# Web accessibility acceptance

German Master 2.0 has a hosted browser acceptance harness for the isolated v2 learner fixture. It is intentionally separate from production routing and from the account-provider staging setup.

Run:

```powershell
npm run build:learner-preview
npx playwright install chromium
node apps/web/scripts/verify-v2-accessibility.mjs
```

The GitHub `Web accessibility acceptance` workflow runs the same check on relevant pull requests and main-branch changes.

## Automated evidence

The harness uses a fresh local API, a fresh browser profile and Chromium at a 320 × 800 CSS-pixel viewport. It verifies:

- exactly one main landmark and one visible page-level `h1` in the tested setup, Home, Topics and practice-feedback states;
- an explicit `lang` on the learner main landmark;
- accessible names for visible enabled buttons, form fields, selects, summaries and links;
- no duplicate IDs or horizontally clipped interactive controls;
- effective interactive targets of at least 44 × 44 CSS px, treating a radio/checkbox label as its effective target;
- no horizontal page overflow at 320 CSS px;
- no horizontal overflow after forcing learner text to 200% of the normal 16px size;
- foundation token contrast of at least 4.5:1 for normal text/background, secondary text/background, text/surface and primary-button text/background;
- a visible focus indicator when keyboard navigation begins;
- keyboard-only setup save, navigation to Topics and back, practice start, and text-answer submission;
- focus transfer to page/question headings and to provisional feedback;
- a live status inside provisional feedback;
- a semantic accessibility-tree snapshot containing the Home heading and navigation;
- no uncaught browser page errors during the exercised journey.

The script deliberately uses the fixture API and unpublished fixture catalog. It does not sign in to Supabase, retrieve credentials, apply schema, deploy, publish content or mutate production.

## Evidence limits

This is a reproducible automated accessibility acceptance subset, **not a claim of complete WCAG conformance**. Automated browser checks cannot replace:

- screen-reader testing with representative assistive technology;
- human review of reading order, wording and cognitive clarity;
- zoom/browser combinations beyond the tested reflow/text-size cases;
- platform-specific high-contrast/forced-colors behavior;
- the independently required design/accessibility approval.

For the web milestone, this closes reproducible keyboard, focus, target-size and narrow/reflow engineering evidence for the exercised v2 journey. Final accessibility acceptance remains open until manual assistive-technology and design review are recorded.
