"""Literal source-example overlap triage. Similarity is not infringement evidence."""
import argparse
import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[3]
OUT = Path(__file__).resolve().parent


def norm(text):
    return ' '.join(text.split())


def inspect(snapshot):
    directory = ROOT / '.local/enrichment-validation' / snapshot / 'content/enrichment'
    matches, checked = [], 0
    for path in sorted((directory / 'batches').glob('*.catalog.json')):
        batch = path.name.removesuffix('.catalog.json')
        catalog = json.loads(path.read_text(encoding='utf-8'))
        report = json.loads(path.with_name(batch + '.report.json').read_text(encoding='utf-8'))
        inputs = json.loads((directory / 'inputs' / (batch + '.json')).read_text(encoding='utf-8'))
        records = {r['sourceId']: r for r in inputs['records']}
        for target in catalog['targets']:
            linked = [r for r in report['rows'] if target['id'] in r['targetIds']]
            for variant in target['variants']:
                checked += 1
                completion = ''.join(p['text'] for p in variant['rubric']['completedAnswer']['parts'])
                for row in linked:
                    source = records[row['sourceId']]['record']
                    examples = {source.get('example_de', ''), *[e.get('de', '') for e in source.get('examples', [])]}
                    for example in sorted(examples):
                        # Preserve case and punctuation: only whitespace is normalized.
                        if len(example.split()) >= 5 and len(example) >= 25 and norm(example) in norm(completion):
                            matches.append({'batchId': batch, 'variantKey': variant['variantKey'],
                                'exerciseId': variant['exercise']['id'], 'sourceId': row['sourceId'],
                                'sourceHash': row['sourceHash'], 'sourceExample': example,
                                'completedAnswer': completion, 'kind': 'literal-linked-example-overlap'})
    evidence = {'snapshot': snapshot, 'questionsChecked': checked,
        'matchedVariants': len({m['variantKey'] for m in matches}), 'matches': matches,
        'method': 'Case- and punctuation-sensitive substring match after whitespace normalization, >=5 words and >=25 characters, linked lexical source only.',
        'limitations': ['Literal overlap does not prove copying, ownership, infringement or an absence of permission.',
                       'No match does not prove originality; paraphrases and cross-row/cross-corpus reuse are not measured.',
                       'Short common lexical examples are excluded by this triage threshold.']}
    (OUT / f'provenance-signals-{snapshot[:12]}.json').write_text(json.dumps(evidence, ensure_ascii=False, indent=2) + '\n', encoding='utf-8')
    print(json.dumps({'questionsChecked': checked, 'matchedVariants': evidence['matchedVariants'], 'literalMatches': len(matches)}))


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--snapshot', required=True)
    inspect(parser.parse_args().snapshot)
