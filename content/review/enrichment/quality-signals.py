"""Deterministic triage, not linguistic approval. Run after validate-snapshot.py."""
import argparse
import json
import re
import unicodedata
from collections import Counter
from pathlib import Path

ROOT = Path(__file__).resolve().parents[3]
OUT = Path(__file__).resolve().parent


def normalized(text):
    return ' '.join(unicodedata.normalize('NFC', text).casefold().split())


def inspect(snapshot):
    source = ROOT / '.local/enrichment-validation' / snapshot / 'content/enrichment/batches'
    require = source.is_dir()
    if not require:
        raise ValueError('Run validate-snapshot.py first for this exact full commit')
    findings, distributions = [], []
    formats, positions = Counter(), Counter()
    questions = 0
    separable = re.compile(r'\bzu (abmildern|abholen|abarbeiten|ankommen|vorschlagen|einhalten|mitteilen|aufgeben|ausfüllen|weitergeben)\b')
    for path in sorted(source.glob('*.catalog.json')):
        catalog = json.loads(path.read_text(encoding='utf-8'))
        batch = path.name.removesuffix('.catalog.json')
        batch_positions = Counter()
        for target in catalog['targets']:
            for variant in target['variants']:
                exercise, rubric = variant['exercise'], variant['rubric']
                questions += 1
                formats[exercise['type']] += 1
                ref = {'batchId': batch, 'variantKey': variant['variantKey'], 'exerciseId': exercise['id']}
                prompt = exercise['prompt']
                completed = ''.join(p['text'] for p in rubric['completedAnswer']['parts'])
                groups = [exercise['options']] if exercise['type'] == 'choice' else [s['options'] for s in exercise.get('slots', [])]
                for options in groups:
                    if len({normalized(o['text']) for o in options}) != len(options):
                        findings.append({**ref, 'kind': 'duplicate-option-text', 'prompt': prompt, 'options': options})
                answer = rubric['acceptedAnswers'][0]
                selected = None
                if answer['type'] == 'choice':
                    options = exercise['options']
                    selected = next((i, o['text']) for i, o in enumerate(options) if o['id'] == answer['optionId'])
                elif answer['type'] == 'gap_choice' and len(exercise['slots']) == 1:
                    options = exercise['slots'][0]['options']
                    selected = next((i, o['text']) for i, o in enumerate(options) if o['id'] == answer['selections'][0]['optionId'])
                if selected:
                    positions[selected[0]] += 1
                    batch_positions[selected[0]] += 1
                    if prompt.count('___') == 1 and normalized(prompt.replace('___', selected[1])) != normalized(completed):
                        findings.append({**ref, 'kind': 'rendered-answer-mismatch', 'prompt': prompt,
                                         'acceptedText': selected[1], 'storedCompletion': completed})
                if '___' in completed:
                    findings.append({**ref, 'kind': 'unfilled-completed-answer', 'storedCompletion': completed})
                for marker in ['\ufffd', 'â€', 'Ã¼', 'Ã¶', 'Ã¤', 'ÃŸ']:
                    if marker in prompt or marker in completed:
                        findings.append({**ref, 'kind': 'encoding-marker', 'marker': marker, 'prompt': prompt})
                match = separable.search(completed)
                if match:
                    findings.append({**ref, 'kind': 'separable-zu-infinitive-needs-review',
                                     'matchedText': match.group(), 'storedCompletion': completed})
                explanation = rubric['explanation']
                english_rule = re.search(r'(separat\w*[^.!?]{0,100}main clauses?|main clauses?[^.!?]{0,100}separat)',
                                         explanation['en'], re.IGNORECASE)
                german_rule = re.search(r'(trenn\w*[^.!?]{0,100}Haupts(?:atz|ätzen)|Haupts(?:atz|ätzen)[^.!?]{0,100}(?:trenn|getrennt))',
                                        explanation['de'], re.IGNORECASE)
                qualified = re.search(r'modal|infinitiv|\bfinite\b|\bfinit\w*\b',
                                      explanation['en'] + ' ' + explanation['de'], re.IGNORECASE)
                if (english_rule or german_rule) and not qualified:
                    findings.append({**ref, 'kind': 'broad-main-clause-prefix-rule-needs-review',
                                     'prompt': prompt, 'explanation': explanation})
                if re.search(r'not (?:a )?(?:non.?stop|return)', explanation['en'], re.IGNORECASE) and 'Anschlussflug' in explanation['en']:
                    findings.append({**ref, 'kind': 'connecting-flight-property-contrast-needs-review',
                                     'prompt': prompt, 'explanation': explanation})
        distributions.append({'batchId': batch, 'correctOptionPositionZeroBased': dict(batch_positions),
                              'questions': sum(batch_positions.values())})
    report = {'snapshot': snapshot, 'questionsScanned': questions, 'formats': dict(formats),
              'correctOptionPositionZeroBased': dict(positions), 'signals': findings,
              'batchAnswerPositions': distributions,
              'limitations': ['Programmatic triage only; no semantic ambiguity, naturalness, CEFR or rights approval.',
                             'Position counts describe saved choice order. Client rendering behavior must be checked before interpreting learner impact.',
                             'Separable-infinitive patterns are a small explicit list; absence of a hit is not proof of correct German.']}
    (OUT / f'quality-signals-{snapshot[:12]}.json').write_text(json.dumps(report, ensure_ascii=False, indent=2) + '\n', encoding='utf-8')
    print(json.dumps({'snapshot': snapshot, 'questions': questions, 'positions': dict(positions),
                      'signals': dict(Counter(f['kind'] for f in findings))}))


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--snapshot', required=True)
    inspect(parser.parse_args().snapshot)
