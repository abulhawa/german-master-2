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
| B2 | Passiv im Präsens | `3b42a2c16a075e09f52bae358a0b723519838ae2d5548faf529d36ec6cf2a38b` |
| B2 | Passiv im Präteritum | `6382818dbc3601215d81a2b6d03291c6cb093030ca5ddb4dbde0be97597332af` |
| B2 | Passiv im Perfekt | `56af56bba0f6a925b7d9159a5df5f0729d0b2b0faa758c3eeec498d607bb8668` |
| B2 | Passiv mit Modalverb | `026aa6584fc42c4b4ba30d4918093fb8ec231738a8b415475bd8587fc2d8dfca` |
| B2 | Irreale Bedingungen mit hätte | `bfa60953d9e88eb6d15f62e7ac4e04fdaf60ebc026133a99772a83d4456f9fb5` |
| B2 | Höfliche Bitten mit könnte | `bb08b96e6d93c3aad4760bf18974d031e1288109a5a71ed014b58bcc691bc2db` |
| B2 | Irreale Vergangenheit mit hätte | `bc39de2777232ed871cd1cf800418b7d9b8cf1e9e6da608aaceecd9e1cea22eb` |
| B2 | Irreale Vergangenheit mit wäre | `b4202310d9af32de7808591a6da4d876308115a3b816467b5bd27fb38f3ec243` |
| B2 | Gegensatz mit obwohl | `7cde5a37fe0b446c843c864e9af7a07f5c2e3b3d93a3e56c87c84f840f3520d1` |
| B2 | Begründung mit da | `dabd5d484b00f7b99a2c2be9f94f99def2660e0d6f03d21dc54bca04760fbf6f` |
| B2 | Folge mit sodass | `5106c68835b20ec50cf07d7fb814bedb160bf8975a594495daa6b524bb1f56a0` |
| B2 | Absicht mit um … zu | `74e622f32f19cb732992bca95ea43cf926a0eab4616d38a8a4993010070df4fb` |
| B2 | Infinitiv mit zu bei trennbaren Verben | `e21a318f12fcaf1e5aa5a8e460ade15519b94bad90faf9b9365076b9964a92cb` |
| B2 | Relativpronomen im Akkusativ | `7421342b62327ab9358e183d09139efb6eb8c4944f47c11f972d32f22f65b478` |
| B2 | Relativpronomen im Dativ | `e12349d32dfa20e83635a3bb307685ba68ae0fa08c2c4b2f10d31bc1649cf65b` |
| B2 | Relativpronomen im Genitiv | `55870e213b73b8467c32b62d6cebb724adc9c75c261b13db8bc8edb4be655ab1` |
| B2 | Genitiv nach trotz | `0950c489b53109d7807b82daf853ffc6a0e87beae7d6738b553bfae5d6359002` |
| B2 | Genitiv nach während | `6c118f36f078a955bb63aa3831076016feb24bb5bb1f2e51ee8d6a44713ce56d` |
| B2 | Adjektive im Genitiv | `c4d7851d8733adf7913a4dac09096bf1608262976102958ea6faaccb833bb59c` |
| B2 | Adjektive ohne Artikel | `258176d52ac6eb50f263dead250a715968d1233e0bf9de2e0c8dfb97d7218812` |
| B2 | Abhängen von | `597826e74a0fd420c70e111c7c53b74d19b9e6858d37bb01d5002c345f1c55ed` |
| B2 | Teilnehmen an | `fc9bd642452959bda207d7d3a0fbd129ac1a833ba868dfba4fa214ec3787f2fe` |
| B2 | Überzeugen von | `09d9be40202f40394d5a4be50c46b25904a18532f986829ecf3e05cfebd20855` |
| B2 | Sich befassen mit | `8f3630beb055532d3cbe11e932fd02e6325005bded4bda7a372cec2f98f0334a` |
| B2 | Sich ergeben aus | `0858579283dd7b865346d686a30dc9bbe960c31f898d6995d310acd43f0b2d85` |
| B2 | Einen Antrag stellen | `f6f91a83ffec30d8eceb45aadda696af89a80ad95e33ee64b62f1e51616352a7` |
| B2 | Eine Entscheidung treffen | `a143e5c07b0bf83af07693d45b5d98add24bc28b16c73bc921635732f37b2f13` |
| B2 | Verantwortung übernehmen | `b5a0e048eb561376015c89fd9a0cf25cb678646b3fb39991a51d5cd43f3e8919` |
| B2 | Eine Frist einhalten | `aed8cf4ec16b25350277824dc5025481241bdafff015a3f2ca017288cb9e11e8` |
| B2 | Voraussetzungen erfüllen | `68c0098f932276ef43ae673d31443a511b5b5f533286b1470857fcf1b8290c11` |
