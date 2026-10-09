# German Master M1: GPT-6 AI editorial review

Date: 9 October 2026

**Reviewer:** ChatGPT GPT-6, AI editorial review. **Owner policy:** independent human German sign-off is not required for M1. This record does not claim human certification or CEFR proficiency validation.

## Scope and method

Read and checked all 30 B1 plus 30 B2 authored targets, two variants per target (120 draft exercises), plus the five original published starter exercise revisions. For each draft: grammar/orthography, conjugation/case, answer/rubric agreement, alternate valid answers, distractor quality, contextual naturalness, hints and accidental answer disclosure, and explanation/level framing. Source `review` metadata for all 60 targets contains a dated AI editor and matching SHA-256 of content excluding the review object. Validation rejects a modified target carrying an obsolete approved hash. Published starter revisions stay immutable; they are a historical fixture, not covered by the 60 new approvals.

## Defects corrected

- Previously corrected: twelve B2 variants had hints that repeated the keyed response; prior commit `86dd4e9` removed direct disclosure.
- B1 accusative after `für`, two variants: rephrased an overly suggestive hint containing the article that also served as the correct option.
- B2 no-article dative adjective: `Mit zuverlässigen Kollegen` admitted both a singular weak noun reading (`zuverlässigem Kollegen`) and plural (`zuverlässigen Kollegen`). Replaced with unmistakably plural `Mit zuverlässigen Kolleginnen aus anderen Abteilungen ...`; keyed option remains `zuverlässigen`.
- B2 dative adjective explanation: `Interesse` is neuter. Explained `-em` in the masculine **and neuter** singular and `-en` in the plural.

## Outcome and limitations

All 60 draft targets have GPT-6 AI editorial approval, with 120 reviewed variants. B2 means a scaffolded intermediate pack and does not assert that each isolated grammatical form is exclusive to B2. The five legacy published starter revisions were inspected, but not retroactively rewritten. A legacy cloze sentence, `Ich fahre mit dem Bus ___ Büro`, can also be completed naturally with `zum Büro` when interpreted as travel toward the building; its accepted `ins/in das` answers intentionally assess movement inside. Consider clarifying that exercise in a **new immutable revision** before treating it as unconstrained production assessment; do not mutate published revision 1.

This is language-content review only. It does **not** test native device screen readers, visual design, production publishing, teacher certification, German proficiency, or delayed retention. M1's remaining native accessibility/design acceptance and separate production approvals are unchanged.

## Target hash index (AI reviewed)

| Level | Target | SHA-256 of reviewed content |
| --- | --- | --- |
| B1 | Plural: der Antrag | `6017a5b43890a480fdf54f90cf85442b4da8302ba97a7ab4973316e9962fb9d7` |
| B1 | Plural: die Rechnung | `8402cfe9fec48896b8bee44a787076ecfb1d32e1a6bb52db03d333bd939b127a` |
| B1 | Plural: der Termin | `45ac18662c7cff0e3fe4e019a28fb6fe535bf924db38210864a43d0d61a9961b` |
| B1 | Plural: das Gespräch | `7a9123328dd810847fae7be4691134c04730305d139783fc39248d804cf5a6ad` |
| B1 | Plural: der Vertrag | `1c8075fc0090a8fa85c288c84735aac278db2ac38df77660c167526aa45699c1` |
| B1 | Plural: die Erfahrung | `ad3143936ca9e3d120c46397a98b52129697521af208320a3dc38c586ca13652` |
| B1 | Plural: das Angebot | `ae4660eba5ce8eb51d4da0929cfa1e6e2890f7c025ada771238d600281440aba` |
| B1 | Plural: die Entscheidung | `1b21224058808b2658500466278b20769ec3bc6f2495edac85a07da01506fb1a` |
| B1 | Plural: die Voraussetzung | `61700bb828d16a89c57592dc3d1ed71a683b05fc08542590b07efe9eb3e2b367` |
| B1 | Plural: der Vorschlag | `4aca1333c8f87a7003fc78d9e0f83ab94469502db772f8adea8aeb077353563f` |
| B1 | Dativartikel nach „mit“ | `7c4c1fc924f5b49df585b0ebba26a54e90ed56b4c00ae708f9044db4c507d18c` |
| B1 | Dativartikel nach „bei“ | `05b8deded4c1004208e3cca0d44f6a7be54f860104d5d0ec5368726c2b11f5a7` |
| B1 | Akkusativartikel nach „für“ | `b87d576dc52bea0f1feadc24b636e60e9d1c95315c3a708b20a8a89daa14dfe7` |
| B1 | Akkusativartikel nach „ohne“ | `7b4c56ccd06c51583c094041cf4127063aaacc0602b264dafbcfd1e855cdf7b9` |
| B1 | Dativartikel nach „aus“ | `5e99de0a26a422c7fb0b50551fa8c7f219bfe526241154f316939540a2e6086d` |
| B1 | Adjektivendung: Maskulinum im Nominativ nach „der“ | `566fca9b915aac35bef3380e9e29236f3153a901126ef3d9f5221515fa45ab10` |
| B1 | Adjektivendung: Femininum im Nominativ nach „die“ | `6c8a276eb3e644b7b76620c1957ac8b4f5c3a6b351b634e165cb97aab4a05451` |
| B1 | Adjektivendung: Neutrum im Nominativ nach „das“ | `677ec710c57a74a8f2ebd66384624ee6b5e966a6d014e12b889db682a812a4e8` |
| B1 | Adjektivendung: Maskulinum im Akkusativ nach „den“ | `8bdeaf0138c9c856712853f5c733998efa428490808058621ac21885fbfc2ad6` |
| B1 | Adjektivendung: Dativ nach bestimmtem Artikel | `2827190edba3bc4ab911dbda6171ad8e2f1b779bdec88787dc47d2cf9795bb86` |
| B1 | Präsens: fahren (du/er) | `7f40f83d39c66f71c4488318f198492895eb47f15f95a5bef160383bef445035` |
| B1 | Präsens: lesen (du/er) | `1101b7f2be839db334e2161c1536dcacb5f447acbfd182b4e76267ce0ca0f9af` |
| B1 | Präsens: geben (du/er) | `bc0c2feaf57c09f2a3c72c6527ab035d2e71daf8635f50fb39c9fd3eb7512e96` |
| B1 | Präsens: nehmen (du/er) | `e23fe98b36eaa3172d9163aa360a33a4f98d5ba4d6c91bae0bf81ec17ef8695e` |
| B1 | Präsens: sprechen (du/er) | `29cdfebfe065610f81f4cda6984938d12afef22c681c30f144d6d11d425e441e` |
| B1 | Nebensatz mit „weil“ | `6a831b4165c1c0c550d0bc06c0ccfb38e48c71ae1638108b26011474154bfcd7` |
| B1 | Nebensatz mit „dass“ | `a13ed62d1c6010bc59ac1a63482e61c060f727da9f2c872347db77039832d166` |
| B1 | Nebensatz mit „obwohl“ | `44349f9b0efaa9fb61a1f0133bd07db119e444052c43485332a59ad7a3626044` |
| B1 | Nebensatz mit „wenn“ | `d6c0468cf13bf17e0ba052e1c7ce560cd8b646d48487397c1767db3bbd195239` |
| B1 | Indirekte Frage mit „ob“ | `d3d07ba4070afcce369c77d5c32a9837be96c831c2b7446224bd2be0af6d4378` |
| B2 | Passiv im Präsens | `218dce010f29d68750d9334a6340cbbe6019d3b15134a6869e86b83094768a31` |
| B2 | Passiv im Präteritum | `1985189dc5eab421c9a1657ffff6a76d56cd2d3b75e1be337ec90f3e3a440ac2` |
| B2 | Passiv im Perfekt | `49720cdb573770fcbb90c76c76f95d901e15ac7f49e6856e355381058834bb07` |
| B2 | Passiv mit Modalverb | `648b9966ad3ea40e5ebfd08c90506f50d266af73af1161056baee91522ca4605` |
| B2 | Irreale Bedingungen mit hätte | `78e79ea9cb5675582bcef5e6f952e41b4b966b8d28320a83ec0df34b9dacc237` |
| B2 | Höfliche Bitten mit könnte | `b66049833faffc669ef644b98b6edf2901f18af89f516086e3c99933d852ac03` |
| B2 | Irreale Vergangenheit mit hätte | `1253decbe4bae37af587c94c0b73ad62379eed4ed132048e91465e7200993b4c` |
| B2 | Irreale Vergangenheit mit wäre | `e7b14a76973def16fbd84154e1da0aa5a690a6a1b01653caaa49cae90dfadbce` |
| B2 | Gegensatz mit obwohl | `e8d0cf2d3a378d2d88d9afe022f541a3af02539bfad8b0c97a57595adb316145` |
| B2 | Begründung mit da | `2481ea29c1c5c1c2323e043157b43701906c1f140f4c445b07c49bb7f49194cd` |
| B2 | Folge mit sodass | `4a90f0129c4a7643088bb8bf34becbe32a905fffaa5b4281d9c7b92086a562a4` |
| B2 | Absicht mit um … zu | `2cd6d51a2253aeaed71c5c182fbad42692271b621c58289daa17bae2037b8427` |
| B2 | Infinitiv mit zu bei trennbaren Verben | `142c7ae18f1c87d8036b1509dcb52d801ca7e3e23735cdfd0d9669d42c346deb` |
| B2 | Relativpronomen im Akkusativ | `d0ebc1e2c9577db284d26574c77da4edd3d3a76d0a840441db0276fd0d7897cf` |
| B2 | Relativpronomen im Dativ | `9f25404233cea04c5adfba424ce54e68dabe2379510341e53c94679b4c9acd36` |
| B2 | Relativpronomen im Genitiv | `6a44df2f66fd83bc044cd5ccd4cc3a970fb9b0e7af6c718e4bcf958381f01e7c` |
| B2 | Genitiv nach trotz | `35d5d0af7dfd7ea560840ce779f26abc98be7bad8d13c936a504ba87fd26f673` |
| B2 | Genitiv nach während | `371c9e27d4b74d6794c1bf7a2969998f2b241cd20354a5622b19828e96dc90c4` |
| B2 | Adjektive im Genitiv | `fac2f307c8f87ab181e067f5ce349ff2f95af65a8dfbd17aa285b373b6d64c8e` |
| B2 | Adjektive ohne Artikel | `4adee8c6ebb5390ee8c57fb5b9f8d3a23e4a7dc7decb3956adda6b269b1447de` |
| B2 | Abhängen von | `ec05dab4c602ba7a66ae96c7c0ced3f67703cc599c8ffc2628ebf224f31c66e0` |
| B2 | Teilnehmen an | `70ae0d4fe1c411ebd887822b3af2ebbdf7fc51de66a48a07cbde7c81bd9e3c04` |
| B2 | Überzeugen von | `d1819476163862101e6d43238fb0858650e8c5c99b5f75f2ee0ff4e3171dacc9` |
| B2 | Sich befassen mit | `db921737b045bbaf1d3420384659421f9b7ca1d4ea257fb5958a603f1e24b831` |
| B2 | Sich ergeben aus | `cf4991bcec27247e9a313a2ec785a7e979db57f4d1ab40f6c4264babff9b19d1` |
| B2 | Einen Antrag stellen | `6d07f09e6f09a5a1299f6528767f93452e75a0a2d21dda14e3946fd629708556` |
| B2 | Eine Entscheidung treffen | `3e04226ab68b2a61649ccda393e58586c57235ef293f264c14e9308f52a7acaa` |
| B2 | Verantwortung übernehmen | `17196b4bb595c0089dbb4a970d6b4ad501d5e7d27aea2f5204a0edb4d44c8658` |
| B2 | Eine Frist einhalten | `c009e142fecb506b4cff35c632193722767f4d41f8250bf1a3427b0cd35a5520` |
| B2 | Voraussetzungen erfüllen | `febf918f6255a6d11c28e1f62d7f27fd7a26dd8ae641186bfb26e571d40bddee` |
