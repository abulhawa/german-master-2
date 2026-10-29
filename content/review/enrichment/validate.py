"""Exact review coverage and inactive patch validation. --sync-queue saves progress.

Complete requires all pinned variants, not a hand-entered count. This is artifact
verification, not a second linguistic review or publication approval.
"""
import argparse
import hashlib
import json
import subprocess
from collections import Counter
from pathlib import Path

OUT = Path(__file__).resolve().parent
ROOT = OUT.parents[2]


def load(name):
    return json.loads((OUT / name).read_text(encoding='utf-8'))


def save(name, value):
    (OUT / name).write_text(json.dumps(value, ensure_ascii=False, indent=2) + '\n', encoding='utf-8')


def require(condition, message):
    if not condition:
        raise ValueError(message)


def validate(sync):
    q, inv = load('queue.json'), load('inventory.json')
    require(len(q['entries']) == len(inv['completed']) == 49, 'Frozen queue must contain 49 batches')
    require(len({e['key'] for e in q['entries']}) == 49, 'Duplicate queue keys')
    require(q['snapshot'] == inv['snapshot'], 'Queue/inventory snapshot mismatch')
    coverage, dispositions, patches, overlays, proposals = [], Counter(), 0, [], {}
    for entry in q['entries']:
        batch = entry['batchId']
        require(entry['key'] == batch + ':' + entry['catalogSha256'] and entry['candidateReady'] is False,
                f'{batch}: invalid queue binding or approval')
        raw = subprocess.check_output(['git', 'show', f"{q['snapshot']}:content/enrichment/batches/{batch}.catalog.json"], cwd=ROOT)
        require(hashlib.sha256(raw.replace(b'\r\n', b'\n')).hexdigest() == entry['catalogSha256'], f'{batch}: hash mismatch')
        source = {v['variantKey']: (t, v) for t in json.loads(raw)['targets'] for v in t['variants']}
        keys, state = set(), 'pending'
        for suffix in ['findings', 'overlay', 'preflight']:
            name = f'{batch}.{suffix}.json'
            if not (OUT / name).exists():
                continue
            artifact = load(name)
            require(artifact['batchId'] == batch and artifact['catalogSha256'] == entry['catalogSha256'] and
                    artifact['snapshot'] == q['snapshot'], f'{name}: incorrect source binding')
            if suffix == 'findings':
                state = artifact['state']
                require(state in ['partial', 'complete'], f'{batch}: invalid state')
                for row in artifact['reviewed']:
                    key = row['variantKey']
                    require(key in source and key not in keys, f'{batch}: unknown/duplicate review key {key}')
                    keys.add(key)
                    target, variant = source[key]
                    e = variant['exercise']
                    require(row['exerciseId'] == e['id'] and row['targetId'] == target['id'], f'{key}: incorrect identity')
                    for field in ['disposition', 'finding', 'rubricAndLanguage', 'provenance', 'levelAndIdentity']:
                        require(isinstance(row.get(field), str) and row[field].strip(), f'{key}: missing {field}')
                    choices = e.get('options') or [o for s in e.get('slots', []) for o in s['options']]
                    assessments = row.get('optionAssessments', [])
                    require(len(assessments) == len(choices) and all(isinstance(a, str) and a.strip() for a in assessments),
                            f'{key}: assess every offered choice')
                    require(all(a.strip().casefold().startswith(o['text'].casefold())
                                for o, a in zip(choices, assessments)),
                            f'{key}: assessment labels must match the actual option text/order')
                    dispositions[row['disposition']] += 1
                require(state != 'complete' or keys == set(source),
                        f'{batch}: incomplete review marked complete; missing {sorted(set(source)-keys)}')
            elif suffix == 'overlay':
                overlays.append(name)
                require(artifact['active'] is False and artifact['candidateReady'] is False, f'{batch}: overlay must stay inactive')
                patched = set()
                for patch in artifact['patches']:
                    key = patch['variantKey']
                    require(key in source and key in keys and key not in patched, f'{key}: missing review or duplicate patch')
                    patched.add(key)
                    _, variant = source[key]
                    e = variant['exercise']
                    require(patch['exerciseId'] == e['id'] and patch['expectedPrompt'] == e['prompt'], f'{key}: patch precondition failed')
                    require(patch['status'] in ['proposed-needs-recheck', 'ai-rechecked-inactive'], f'{key}: invalid inactive proposal state')
                    proposals[(batch, key)] = (entry['catalogSha256'], patch)
                    prompt, completed = patch['proposedPrompt'], patch['proposedCompletedAnswer']
                    require(prompt.strip() and completed.strip() and '___' not in completed, f'{key}: invalid completion')
                    for field in ['proposedInstruction', 'proposedHint', 'proposedExplanation']:
                        if field in patch:
                            require(set(patch[field]) == {'en', 'de'} and
                                    all(isinstance(v, str) and v.strip() for v in patch[field].values()),
                                    f'{key}: {field} requires both language strings')
                    if '___' in e['prompt']:
                        require(prompt.count('___') == 1, f'{key}: preserve one unanswered gap')
                        choices = e.get('options') or e['slots'][0]['options']
                        if 'proposedOptions' in patch:
                            proposed = patch['proposedOptions']
                            require([o['id'] for o in proposed] == [o['id'] for o in choices], f'{key}: preserve option IDs')
                            require(all(o['text'].strip() for o in proposed) and len({o['text'] for o in proposed}) == len(proposed),
                                    f'{key}: empty/duplicate corrected choice')
                            choices = proposed
                        answer = variant['rubric']['acceptedAnswers'][0]
                        answer_id = answer['optionId'] if answer['type'] == 'choice' else answer['selections'][0]['optionId']
                        answer_text = next(o['text'] for o in choices if o['id'] == answer_id)
                        require(prompt.replace('___', answer_text) == completed, f'{key}: corrected key/completion disagree')
                    patches += 1
        if sync:
            entry.update(state=state, reviewedVariantKeys=sorted(keys))
        else:
            require(entry['state'] == state and sorted(entry['reviewedVariantKeys']) == sorted(keys), f'{batch}: queue stale; use --sync-queue')
        coverage.append({'batchId': batch, 'state': state, 'reviewedQuestions': len(keys), 'totalQuestions': len(source), 'candidateReady': False})
    rechecked = set()
    semantic_rechecks = 0
    if (OUT / 'correction-rechecks.json').exists():
        proof = load('correction-rechecks.json')
        require(proof['snapshot'] == q['snapshot'] and proof['active'] is False and
                proof['publicationApproved'] is False and proof['humanReview'] is False,
                'Correction recheck must remain pinned, inactive and AI-assisted')
        for row in proof['entries']:
            identity = (row['batchId'], row['variantKey'])
            require(identity in proposals and identity not in rechecked, 'Unknown or duplicate correction recheck')
            source_hash, patch = proposals[identity]
            actual_hash = hashlib.sha256(json.dumps(patch, ensure_ascii=False, sort_keys=True,
                                        separators=(',', ':')).encode('utf-8')).hexdigest()
            require(row['catalogSha256'] == source_hash and row['exerciseId'] == patch['exerciseId'] and
                    row['proposalSha256'] == actual_hash and patch['status'] == 'ai-rechecked-inactive',
                    f'{identity}: correction changed after AI recheck')
            require(row['decision'] == 'supported-within-stated-task' and row['rationale'].strip(), 'Missing scoped recheck rationale')
            semantic_rechecks += row['scope'] == 'corrected-prompt-options-instruction-hint-explanation-and-completion'
            rechecked.add(identity)
        require(proof['semanticProposalRechecks'] == semantic_rechecks and
                proof['orthographyOnlyRechecks'] == len(rechecked) - semantic_rechecks,
                'Recheck scope counts do not match their entries')
    require(rechecked == {key for key, (_, patch) in proposals.items() if patch['status'] == 'ai-rechecked-inactive'},
            'AI-rechecked proposal state requires exact matching recheck evidence')
    manifest = load('candidate-manifest.json')
    require(manifest['active'] is False and manifest['publicationApproved'] is False and
            manifest['candidateReadyBatches'] == [], 'Candidate must stay inactive and unapproved')
    if sync:
        manifest['overlays'] = sorted(overlays)
        save('candidate-manifest.json', manifest)
    else:
        require(manifest['overlays'] == sorted(overlays), 'Candidate overlay list stale; use --sync-queue')
    summary = {'snapshot': q['snapshot'], 'aiAssistedOnly': True,
               'completedBatches': sum(c['state'] == 'complete' for c in coverage),
               'partialBatches': sum(c['state'] == 'partial' for c in coverage),
               'reviewedQuestions': sum(c['reviewedQuestions'] for c in coverage),
               'totalQuestions': sum(c['totalQuestions'] for c in coverage),
               'inactiveCorrectionProposals': patches, 'aiRecheckedCorrectionProposals': len(rechecked),
               'semanticCorrectionRechecks': semantic_rechecks,
               'orthographyOnlyCorrectionRechecks': len(rechecked) - semantic_rechecks,
               'dispositions': dict(dispositions), 'batches': coverage}
    if sync:
        save('queue.json', q)
        save('coverage.json', summary)
    print(json.dumps({k: v for k, v in summary.items() if k != 'batches'}))


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--sync-queue', action='store_true')
    validate(parser.parse_args().sync_queue)
