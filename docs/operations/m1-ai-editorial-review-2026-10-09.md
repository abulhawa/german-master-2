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
| B2 | Passiv im Präsens | `e3287d09c98c9fc7bb94148e7203c7d9c550a86260d54ba86315d152c7187149` |
| B2 | Passiv im Präteritum | `9b3f0ccd97b702454624d2ae904cbdfa4f6eb935ec28afcf9673b9cacb785cfc` |
| B2 | Passiv im Perfekt | `c805c8f240aea7b192dd00b51868475c891721b615721e1bccadffa3a1959586` |
| B2 | Passiv mit Modalverb | `2840767f5d86823c0942e2156741ca7f6e964691316b3c2fd466bb3c17d77c26` |
| B2 | Irreale Bedingungen mit hätte | `51f14091cbaf4a8808893f077b100b50f2d67b3c2d602517ed25610fccff6659` |
| B2 | Höfliche Bitten mit könnte | `e9cd2f429008f27b01b0515ef2b4818f520203d8f1e1cb5b0ee4aa47718a370b` |
| B2 | Irreale Vergangenheit mit hätte | `cc7c56299d9ed104f6200f65d05a544a8ef2310bde75b4a67de1eb2040c0c973` |
| B2 | Irreale Vergangenheit mit wäre | `0d9324967c1130f37a18c013685449ebb171ba4145f5abcc7c31b6fc152b31e3` |
| B2 | Gegensatz mit obwohl | `9657a66b362fb1b1a16e159591997f6a6c443270194869716d0df1c395353bed` |
| B2 | Begründung mit da | `86f0b07985cdd4d309161ca30dda830bb2e2273681b2a73cbeb6df161f4e94e7` |
| B2 | Folge mit sodass | `09ddf77f491b06b4b55ed107dbd2d0ff7a0accd414898d5188fc25eebb1826c3` |
| B2 | Absicht mit um … zu | `d08a49a06ae520731eae7043396f1f14dc49af85c64f52a13f705bf270fda7c8` |
| B2 | Infinitiv mit zu bei trennbaren Verben | `9aa26c79bed31cd267fd10e53dfd02ab24b6e3bab988c3a9651bb9152b5a3c8d` |
| B2 | Relativpronomen im Akkusativ | `daace6b7b59df841da41b2ba169a28142c10bfcd015ad06566a2d994e93aeb15` |
| B2 | Relativpronomen im Dativ | `7170d01e4ccd81cdbaff12a58315a8c64883e2cae0589fc618573e5f4dc421be` |
| B2 | Relativpronomen im Genitiv | `9c7cd5004ef02e06fd817bdb223e9dcaf71de081b30ac8919ba8b76e48e00be6` |
| B2 | Genitiv nach trotz | `3d7c0302a18ba429917f8d4334fcc65b975ee8eff480857739bfe6e9114b5109` |
| B2 | Genitiv nach während | `6b85b4abd37063d8e6b440afc056e0584537e92fb41cd3d74c9b6e48b516a3f8` |
| B2 | Adjektive im Genitiv | `bae2e8cbd0f9f91488d9f8a8338d33db204b2828ad52916e800943c50959bdb2` |
| B2 | Adjektive ohne Artikel | `1ae4dcc1f8da4b4fbb7af676e2a37c624c011fbfff3bc39abb27574554adf7ff` |
| B2 | Abhängen von | `71a37284f9556f26e90e79ddf6144a5dd8e2f1159577f353f57f640a3b36a101` |
| B2 | Teilnehmen an | `8bcbaf80de2c9f6cae9ffa70152a049fe3d449116f8d1dc6e2b841f6a9a73c95` |
| B2 | Überzeugen von | `ce390351e9924b7adfee0a9d26d201d71f9fe8cddb882ac41359997fb09b11fe` |
| B2 | Sich befassen mit | `ff351d8ec9a541a9f4bf489e258df725a10dc48bad6dd3497ec8d784b5b2a057` |
| B2 | Sich ergeben aus | `886c268e4637033a2428efe18213df49a9a3661c2dfa460eb8e209ddec5cab1a` |
| B2 | Einen Antrag stellen | `0dd6f23d8d985c1f3119323d2b96c95469f5006730bc5c32a40fc61fda1d4d9c` |
| B2 | Eine Entscheidung treffen | `1006acf2a32d032be7bf30fa0e39a387071d0c83b775972924752ecdda0e4a90` |
| B2 | Verantwortung übernehmen | `3ef96fb8eb5fefab36cd5e2d1ef0039bbc8f31ce5339b3d4a9090db7c2645a43` |
| B2 | Eine Frist einhalten | `512a2ec6eb5a3cd45ad8e07c03b3050795015784380521c2b79b39a868742273` |
| B2 | Voraussetzungen erfüllen | `5b93004e3866c735f55d84c14a7cdc9113ca238f093483d07253bb7748ac95a5` |
