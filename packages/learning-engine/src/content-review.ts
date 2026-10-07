import { z } from 'zod';
import { createHash } from 'node:crypto';
import { ExerciseSchema, AnswerSchema } from '@german-master/contracts';
import { grade } from './index';

const localized = z.object({ en: z.string().trim().min(1), de: z.string().trim().min(1) }).strict();
const rubric = z.object({ normalizationVersion: z.literal('de-nfc-trim-v1'),
  acceptedAnswers: z.array(AnswerSchema).min(1), explanation: localized }).strict();
const review = z.object({ status: z.enum(['pending', 'changes-requested', 'approved']),
  reviewer: z.string().trim().min(1).nullable(), date: z.string().regex(/^\d{4}-\d{2}-\d{2}$/).nullable(), notes: z.string(), reviewedHash: z.string().regex(/^[a-f0-9]{64}$/).nullable() }).strict();
const catalogSchema = z.object({ schemaVersion: z.literal(1), status: z.literal('agent-authored-draft'),
  publicationApproved: z.literal(false), targets: z.array(z.object({ id: z.string().uuid(), title: localized,
    objective: z.string().trim().min(1), category: z.string().trim().min(1), level: z.enum(['B1','B2']),
    provenance: z.string().trim().min(1), review,
    variants: z.array(z.object({ variantKey: z.string().trim().min(1), contextKey: z.string().trim().min(1),
      transferKey: z.string().trim().min(1).nullable(), exercise: ExerciseSchema, rubric,
      ambiguityNotes: z.string().trim().min(1) }).strict()).min(2)
  }).strict()).min(30) }).strict();

/** Editorial validation only. Never publishes or feeds the live fixture selector. */
export function validateDraftCatalog(input: unknown) {
  const catalog = catalogSchema.parse(input);
  const targetIds = new Set<string>();
  const exerciseIds = new Set<string>();
  const variantKeys = new Set<string>();
  const forms = new Set<string>();
  const targetHashes = new Map<string,string>();
  for (const target of catalog.targets) {
    const {review: _review, ...content} = target;
    const contentHash = createHash('sha256').update(JSON.stringify(content)).digest('hex');
    targetHashes.set(target.id,contentHash);
    if (targetIds.has(target.id)) throw new Error('duplicate target');
    targetIds.add(target.id);
    if (target.review.status === 'approved' && (!target.review.reviewer || !target.review.date || !target.review.notes.trim() || target.review.reviewedHash !== contentHash))
      throw new Error('approval requires independent reviewer, date, checklist notes and matching content hash');
    const contexts = new Set<string>();
    const prompts = new Set<string>();
    for (const variant of target.variants) {
      const e = variant.exercise;
      if (e.targetId !== target.id) throw new Error('wrong target linkage');
      if (exerciseIds.has(e.id) || variantKeys.has(variant.variantKey)) throw new Error('duplicate exercise or variant identity');
      exerciseIds.add(e.id); variantKeys.add(variant.variantKey); forms.add(e.type);
      if (contexts.has(variant.contextKey) || prompts.has(e.prompt.normalize('NFC').trim())) throw new Error('variants require distinct contexts and prompts');
      contexts.add(variant.contextKey); prompts.add(e.prompt.normalize('NFC').trim());
      if (e.type === 'choice' || e.type === 'word_order' || e.type === 'cloze' || e.type === 'multi_slot') {
        const linked = e.type === 'choice' ? e.options : e.type === 'word_order' ? e.tokens : e.slots;
        if (new Set(linked.map(item => item.id)).size !== linked.length) throw new Error('duplicate input identity');
      } else if (e.type === 'gap_choice') {
        if (new Set(e.slots.map(slot => slot.id)).size !== e.slots.length) throw new Error('duplicate input identity');
        for (const slot of e.slots)
          if (new Set(slot.options.map(option => option.id)).size !== slot.options.length) throw new Error('duplicate input identity');
      } else if (e.type === 'matching') {
        if (new Set(e.left.map(item => item.id)).size !== e.left.length ||
            new Set(e.right.map(item => item.id)).size !== e.right.length)
          throw new Error('duplicate input identity');
      }
      for (const answer of variant.rubric.acceptedAnswers)
        if (grade(e, variant.rubric, answer, []).outcome !== 'correct') throw new Error('rubric conformance failed');
    }
  }
  for (const form of ['short_answer','choice','cloze','word_order','multi_slot'])
    if (!forms.has(form)) throw new Error(`missing initial form: ${form}`);
  return { catalog, targetHashes, targets: targetIds.size, variants: exerciseIds.size,
    independentlyApproved: catalog.targets.filter(t => t.review.status === 'approved').length };
}
