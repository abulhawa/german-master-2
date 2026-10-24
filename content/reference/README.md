# Grammar reference

`grammar-reference.json` is the single reference-content input for GM2 web and
native Android. It contains the 16 existing German tables from the imported
Android `B2ContentData.grammarTables`, grouped into 14 menu topics. The three
adjective-declension tables remain together, as in the old reference menu.

Provenance: `apps/android/app/src/main/java/com/germanverbmaster/android/ui/b2practice/B2ContentData.kt`
at monorepo revision `9150b76`. Tables, notes, rules, examples, common mistakes,
additional sections and coverage markers are preserved without rewriting them.
English/German menu titles are presentation metadata; the reference material
remains German. This is a content-preserving import, not a new linguistic review.

Web imports this JSON directly. Android bundles the same file through
`prepareFoundationAssets`. `check:generated` validates its structure, and native
tests compare every imported table to the preserved legacy content. Reference
browsing never writes attempts, assigns mastery or changes exercise revisions.
