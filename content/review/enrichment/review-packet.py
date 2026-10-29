"""Print compact immutable editorial inputs without repeating author boilerplate.

All exercise/rubric fields and source records are retained. Author claims remain
claims; neither a packet nor the existing author review establishes correctness.
"""
import argparse
import hashlib
import json
import subprocess
from pathlib import Path

ROOT = Path(__file__).resolve().parents[3]
PIN = '671e9d19f4f5a61d7916ed96b3b41433b768fd39'


def packet(batch, snapshot):
    if not __import__('re').fullmatch(r'legacy-\d{4}', batch):
        raise ValueError('Expected planned batch ID legacy-NNNN')
    def raw(path):
        return subprocess.check_output(['git', 'show', f'{snapshot}:{path}'], cwd=ROOT)
    catalog_raw = raw(f'content/enrichment/batches/{batch}.catalog.json')
    catalog = json.loads(catalog_raw)
    report = json.loads(raw(f'content/enrichment/batches/{batch}.report.json'))
    inputs = json.loads(raw(f'content/enrichment/inputs/{batch}.json'))
    catalog_hash = hashlib.sha256(catalog_raw.replace(b'\r\n', b'\n')).hexdigest()
    if report['state'] != 'ready-for-review' or catalog_hash != report['catalogSha256']:
        raise ValueError('Only completed, hash-matched batches are editorial inputs')
    records = {row['sourceId']: row for row in inputs['records']}
    print(json.dumps({'snapshot': snapshot, 'batchId': batch, 'catalogSha256': catalog_hash,
                      'untrustedAuthorClaims': {k: report[k] for k in ['aiReviewer', 'reviewedAt', 'reviewNotes']},
                      'targetCount': len(catalog['targets']),
                      'questionCount': sum(len(t['variants']) for t in catalog['targets'])}, ensure_ascii=False))
    for target in catalog['targets']:
        rows = [r for r in report['rows'] if target['id'] in r['targetIds']]
        source = [{'sourceId': row['sourceId'], 'sourceHash': row['sourceHash'],
                   'record': records[row['sourceId']]['record'],
                   'untrustedAuthorCorrections': row['corrections']} for row in rows]
        metadata = {k: v for k, v in target.items() if k != 'variants'}
        print(json.dumps({'target': metadata, 'sources': source}, ensure_ascii=False))
        for variant in target['variants']:
            # The long repeated author's self-assessment is not another review.
            value = {k: v for k, v in variant.items() if k != 'ambiguityNotes'}
            print(json.dumps(value, ensure_ascii=False))


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('batch')
    parser.add_argument('--snapshot', default=PIN)
    args = parser.parse_args()
    packet(args.batch, args.snapshot)
