"""Bind completed review inputs to a later structurally checked author snapshot."""
import argparse
import json
from pathlib import Path

OUT = Path(__file__).resolve().parent


def compare(earlier, later):
    def read(commit):
        report = json.loads((OUT / f'structural-{commit[:12]}.json').read_text(encoding='utf-8'))
        if report['snapshot'] != commit or report['canonicalValidatorExitCode'] != 0:
            raise ValueError('Require exact commits with successful structural evidence')
        return {i['batchId']: i for i in report['inputs'] if i['state'] == 'ready-for-review'}
    old, new = read(earlier), read(later)
    unchanged, changed, removed, changed_reports = [], [], [], []
    for batch, row in old.items():
        if batch not in new:
            removed.append(batch)
        elif row['catalogSha256'] == new[batch]['catalogSha256']:
            unchanged.append(batch)
            if row['reportSha256'] != new[batch]['reportSha256']:
                changed_reports.append(batch)
        else:
            changed.append({'batchId': batch, 'earlierCatalogSha256': row['catalogSha256'],
                            'laterCatalogSha256': new[batch]['catalogSha256']})
    report = {'earlierSnapshot': earlier, 'laterSnapshot': later,
              'unchangedCompletedCatalogs': unchanged, 'changedCompletedCatalogs': changed,
              'removedCompletedCatalogs': removed, 'unchangedCatalogsWithChangedAuthorReports': changed_reports,
              'additionalCompletedBatches': sorted(set(new) - set(old)),
              'limitations': ['Unchanged catalog hashes permit reuse of existing review findings only; no new editorial coverage is granted.',
                             'Changed report/provenance claims require separate inspection even if exercise bytes match.',
                             'No moving branch ref, authoring file or existing queue is modified.']}
    (OUT / 'snapshot-comparison.json').write_text(json.dumps(report, ensure_ascii=False, indent=2) + '\n', encoding='utf-8')
    print(json.dumps({'unchangedCatalogs': len(unchanged), 'changedCatalogs': len(changed),
                      'removedCatalogs': len(removed), 'changedReports': len(changed_reports),
                      'additionalCompletedBatches': len(report['additionalCompletedBatches'])}))


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('earlier')
    parser.add_argument('later')
    args = parser.parse_args()
    compare(args.earlier, args.later)
