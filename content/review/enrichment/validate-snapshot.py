"""Run the unchanged canonical validator in an isolated, commit-pinned export.

Dependencies are supplied by a local existing checkout; no install or network is
performed. Only tracked source is exported. Cached exports are disposable and
never become authoring inputs. Reports contain portable evidence, not host paths.
"""
import argparse
import hashlib
import io
import json
import os
import shutil
import subprocess
import zipfile
from collections import Counter, defaultdict
from pathlib import Path

ROOT = Path(__file__).resolve().parents[3]
OUT = Path(__file__).resolve().parent


def git(*args):
    return subprocess.check_output(['git', *args], cwd=ROOT)


def digest(data):
    return hashlib.sha256(data.replace(b'\r\n', b'\n')).hexdigest()


def save(path, value):
    path.write_text(json.dumps(value, ensure_ascii=False, indent=2) + '\n', encoding='utf-8')


def audit(snapshot, dependency_root, node):
    commit = git('rev-parse', f'{snapshot}^{{commit}}').decode().strip()
    # Workspace package links in node_modules point to the dependency checkout.
    # Verify their sources match the pinned export before using those links.
    dependency_commit = subprocess.check_output(['git', 'rev-parse', 'HEAD'], cwd=dependency_root).decode().strip()
    for package in ['contracts', 'packages/learning-engine']:
        if git('rev-parse', f'{commit}:{package}') != subprocess.check_output(
                ['git', 'rev-parse', f'{dependency_commit}:{package}'], cwd=dependency_root):
            raise RuntimeError(f'Dependency workspace source differs: {package}')
        if subprocess.check_output(['git', 'status', '--porcelain', '--', package], cwd=dependency_root):
            raise RuntimeError(f'Dependency workspace has uncommitted package changes: {package}')
    export = ROOT / '.local' / 'enrichment-validation' / commit
    export.mkdir(parents=True, exist_ok=True)
    paths = ['content/enrichment', 'content/drafts', 'tools/content',
             'services/api/scripts/validate-enrichment.ts', 'packages', 'contracts',
             'apps/web/data', 'apps/web/db/verb-lists', 'package.json']
    archive = git('archive', '--format=zip', commit, *paths)
    with zipfile.ZipFile(io.BytesIO(archive)) as z:
        z.extractall(export)
    modules = export / 'node_modules'
    dependency_modules = dependency_root / 'node_modules'
    if not modules.exists():
        if not dependency_modules.is_dir():
            raise RuntimeError('Existing root dependencies required; no network install performed')
        if os.name == 'nt':
            # A junction needs no developer-mode symlink privilege on Windows.
            script = "New-Item -ItemType Junction -Path '" + str(modules).replace("'", "''") + "' -Value '" + str(dependency_modules).replace("'", "''") + "' | Out-Null"
            subprocess.run(['powershell', '-NoProfile', '-Command', script], check=True, capture_output=True)
        else:
            modules.symlink_to(dependency_modules, target_is_directory=True)
    cli = dependency_modules / 'tsx' / 'dist' / 'cli.mjs'
    run = subprocess.run([node, str(cli), 'services/api/scripts/validate-enrichment.ts'],
                         cwd=export, capture_output=True, text=True, encoding='utf-8')
    reports = sorted((export / 'content/enrichment/batches').glob('*.report.json'))
    base = {}
    for path in sorted((export / 'content/drafts').glob('*.json')):
        catalog = json.loads(path.read_text(encoding='utf-8'))
        if isinstance(catalog, dict) and isinstance(catalog.get('targets'), list):
            for target in catalog['targets']:
                base[target['id']] = target
    targets = dict(base)
    normalized_objectives = defaultdict(list)
    normalized_titles = defaultdict(list)
    counts = Counter()
    levels = Counter()
    input_hashes = []
    candidates = []
    for path in reports:
        report = json.loads(path.read_text(encoding='utf-8'))
        catalog_path = path.with_name(path.name.replace('.report.json', '.catalog.json'))
        raw = catalog_path.read_bytes()
        catalog = json.loads(raw)
        input_hashes.append({'batchId': report['batchId'], 'state': report['state'],
                             'catalogSha256': digest(raw), 'reportSha256': digest(path.read_bytes())})
        counts[report['state']] += 1
        for row in report['rows']:
            counts['source:' + row['decision']] += 1
        for target in catalog['targets']:
            counts['targets'] += 1
            counts['questions'] += len(target['variants'])
            levels[target['level']] += len(target['variants'])
            targets[target['id']] = target
            title = target['title']['de']
            lemma = title.removesuffix(' im Kontext').strip().casefold()
            candidates.append({'batchId': report['batchId'], 'targetId': target['id'], 'lemma': lemma})
    for target in targets.values():
        normalized_objectives[' '.join(target['objective'].casefold().split())].append(target['id'])
        normalized_titles[' '.join(target['title']['de'].casefold().split())].append(target['id'])
    # These are exact normalized strings, not a semantic duplicate certification.
    duplicate_objectives = [{'objective': k, 'targetIds': v} for k, v in normalized_objectives.items() if len(v) > 1]
    duplicate_titles = [{'title': k, 'targetIds': v} for k, v in normalized_titles.items() if len(v) > 1]
    by_lemma = defaultdict(list)
    for item in candidates:
        by_lemma[item['lemma']].append(item)
    repeats = [v for v in by_lemma.values() if len(v) > 1]
    evidence = {
        'snapshot': commit, 'canonicalValidatorExitCode': run.returncode,
        'canonicalValidatorStdout': run.stdout.strip(), 'canonicalValidatorStderr': run.stderr.strip(),
        'validatorSha256': digest((export / 'services/api/scripts/validate-enrichment.ts').read_bytes()),
        'learningEngineValidatorSha256': digest((export / 'packages/learning-engine/src/content-review.ts').read_bytes()),
        'counts': dict(counts), 'questionLevels': dict(levels), 'inputs': input_hashes,
        'exactNormalizedObjectiveCollisions': duplicate_objectives,
        'exactNormalizedGermanTitleCollisions': duplicate_titles,
        'repeatedLemmaCandidates': repeats,
        'limitations': ['Structural and exact-string evidence only; no semantic, linguistic, CEFR or rights approval.',
                       'Topic and learning-context mapping must be resolved before an inactive candidate can become publishable.',
                       'Existing four draft corpora are the canonical cross-corpus context; published database content was not queried.']
    }
    # Avoid leaking local paths from an unexpected validator error to a public file.
    if run.returncode:
        evidence['canonicalValidatorStderr'] = 'Validation failed; inspect the private local execution output.'
        evidence['canonicalValidatorStdout'] = run.stdout.replace(str(export), '<isolated-export>')
    save(OUT / f'structural-{commit[:12]}.json', evidence)
    print(json.dumps({'snapshot': commit, 'validatorExitCode': run.returncode,
                      'counts': dict(counts), 'levels': dict(levels),
                      'objectiveCollisions': len(duplicate_objectives), 'titleCollisions': len(duplicate_titles),
                      'repeatedLemmas': len(repeats)}))
    if run.returncode:
        print(run.stderr)
        raise SystemExit(run.returncode)


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--snapshot', required=True)
    parser.add_argument('--dependency-root', type=Path, required=True)
    parser.add_argument('--node', default=shutil.which('node'))
    args = parser.parse_args()
    audit(args.snapshot, args.dependency_root.resolve(), args.node)
