# M1 acceptance follow-up — 10 October 2026

## Baseline and engineering evidence

PR #18 is merged as `f1ef138564750594709c56a6fb1330eae71a9ef0`. Its reported PR checks pass, including release readiness and Kotlin CodeQL. The merge commit's Web, Android, web accessibility, repository safety and CodeQL workflows also completed successfully. Earlier pending-PR and blanket unverified implementation statements are historical.

## Fresh AI editorial review

Reviewer: Codex (GPT-6), AI editorial review; no human certification or CEFR assessment is claimed. Read all 60 changed B1/B2 draft targets and their 120 variants, including prompts, options, accepted answers, hints, bilingual descriptions/explanations, completed-answer feedback and context variation. Approval metadata is bound to the schema-normalized target hash, excluding review metadata. Both source catalogs remain `publicationApproved: false`.

Corrections made before approval:

- Five applied B1 article explanations now name the noun in the actual prompt: Anwalt, Anmeldung, Ausflug, Genehmigung and Krankenhaus.
- The nehmen explanation limits `nimm-` to the du/er forms; it no longer incorrectly describes every singular form.
- B2 passive Präteritum and present conditional explanations specify the applicable grammatical persons rather than generalizing across every singular subject.
- B2 relative-pronoun feedback distinguishes antecedent gender from the case determined inside the relative clause.
- Two unreal-past hints now refer to the verb actually shown, rather than obsolete lernen/lesen or kommen/fahren examples.
- The genitive-adjective hint no longer gives the exact `-en` answer.

Each changed exercise uses a new revision. Published content and the five immutable starter revisions are untouched. Regenerated the B1 workbook and combined unpublished candidate, including execution of candidate SQL in isolated in-memory PGlite. The candidate retains 65 targets / 125 exercises. SQL draft review status deliberately remains pending until a separately authorized publication; source editorial approval does not activate live practice.

The offered options have one answer for the explicitly requested form or relationship. These are scaffolded recognition exercises. Context diversity creates opportunities for transfer; it does not demonstrate learner transfer. B2 labels select a practice pack, not a proficiency certification. Some hints teach the relevant rule; assisted evidence remains distinct from unassisted evidence.

## Delivered recovery acceptance

Used the owner-designated account through the deployed web UI. One recovery request delivered an actual email from the German Master sender. Opening its link in the requesting browser redirected to the frontend recovery URL and completed PKCE exchange, showing the new-password form. The owner entered and submitted the new password and confirmed success; the browser then showed signed-in B2 Home with topics loaded.

Opening the consumed email link again produced `otp_expired`, an explicit expired-link explanation and a new-email action, with no password-update form. Following that action opened the recovery request form. This is delivered-email and callback evidence, not administrator-generated confirmation. No auth token, email address, password or callback code is included in this public record.

## Owner acceptance

The owner explicitly accepted both the current web/Android design and manual web screen-reader review on 10 October 2026. Record this as owner acceptance based on their review, not an agent-observed uninterrupted screen-reader journey or complete WCAG certification. TalkBack remains accepted under the 9 October decision; no further TalkBack testing was performed.

## Delivered signup confirmation

The owner authorized a disposable plus-address alias delivering to the same mailbox. Registration through the deployed UI delivered a confirmation email with the frontend root redirect. Opening that email in the requesting browser completed the callback, removed the code from the URL and showed first-time practice setup, rather than the previous account's B2 preferences. Saving the default B1 preferences completed and opened the new account's Home with loaded topics and enabled practice. No administrator-assisted confirmation was used. A private screenshot is retained under ignored `.local/m1-acceptance-2026-10-10/`; the public evidence does not contain account identifiers.

An immediate confirmation resend showed a request error and local cooldown; no successful resend delivery is claimed. The original signup link remained valid and completed confirmation. This is consistent with a provider throttle, but the browser message alone does not establish the exact provider reason. Further successful resend acceptance remains a follow-up, not an unreported pass. The authorized test account is retained; no account deletion was requested or performed.

Final local verification passed: root offline install, generated/type checks, 196 backend tests, 419 web tests, 20 HTTP integration tests, root builds and learner-product build. The final candidate revision-linkage assertion also passes its three targeted tests. Android code was unchanged; no new device or native build acceptance is claimed for this content-only follow-up. M1 is closed under the accepted scope; M2 is next. Publication, deployment and Android/store release remain separate operations.

## Reviewed target hash index

| Level | Target | Content SHA-256 |
| --- | --- | --- |
| B1 | Plural: der Antrag | `69d93ca7e7e1676505e2066e50d72c09774d8b95b82a49433c26625da95a6228` |
| B1 | Plural: die Rechnung | `cb173cc77d163548d62d559e129a5e688440f171bce1d203a828a358c0390f1d` |
| B1 | Plural: der Termin | `fb1417be082a98872d2fe72d9221b290270c3554d19f5af16b7df88ec588b538` |
| B1 | Plural: das Gespräch | `599d3b75aadb84e8652a08f32a7c687ce2abd8f7a38622a792170a8b2574eb95` |
| B1 | Plural: der Vertrag | `9d512b7d7b0d353258574fb2f21fc696c928b8aa599e1243bef27f61ffe73475` |
| B1 | Plural: die Erfahrung | `209ae513b175ef914ee0ad01fd40be67d5cbc3b79ecbe34af3981643b104ce3e` |
| B1 | Plural: das Angebot | `7f23c5ed80e8352665455e04e596759db6348de2bd7e8f463d14a660c585275e` |
| B1 | Plural: die Entscheidung | `25efea61e84578a423b3325bdf437f94d208ca7ce4ace7bde1b388e7c1fc8858` |
| B1 | Plural: die Voraussetzung | `f75d4ad0e4ac8065821ed84c63a9d335a54036ac7289eb8868656bc4f0d6787d` |
| B1 | Plural: der Vorschlag | `ba4e56601a1583634ca9357dde7d03ab8dd614699ac00729198daa9646feb8e1` |
| B1 | Dativartikel nach „mit“ | `0fdeea580300dd04dc267ae0eb261b0847c48899ead5438a4f508fbb9452f0d6` |
| B1 | Dativartikel nach „bei“ | `8efa423fbb9bb06c711e316ed7d6e9d604bdca09ce84f9891c49ed082a3678b1` |
| B1 | Akkusativartikel nach „für“ | `badda04c0b88745403f74c1fec806a7a94a6a45dae148a267cecda2abefe56e4` |
| B1 | Akkusativartikel nach „ohne“ | `d0273e098655276f7e31af8a51b00f14b140288fc3614244aaa6b88bd9247995` |
| B1 | Dativartikel nach „aus“ | `78fdb4e1066107dead36f18c09cc5bf79e33ee0d5d3348fb6826c7c24d3b44f7` |
| B1 | Adjektivendung: Maskulinum im Nominativ nach „der“ | `57762ed69360a13aa8f013d23b3389f858c2d430a063bd47f89e6f5ba382b411` |
| B1 | Adjektivendung: Femininum im Nominativ nach „die“ | `455049fb5acc52fb04420da7a9693199641ab4732b73442c4ad52d7e399d8192` |
| B1 | Adjektivendung: Neutrum im Nominativ nach „das“ | `7f0f853d430432ad0c2189347355ec49155d18ef40c247b6809aede56ea2a9ff` |
| B1 | Adjektivendung: Maskulinum im Akkusativ nach „den“ | `5b2dbb8d0202664863353780394893217960179878ef5ac6dc71f19bb7be8b50` |
| B1 | Adjektivendung: Dativ nach bestimmtem Artikel | `4ac52751faff45f61cf100b6d9c34a950843bc17f9bef048d66bddcdf5c917b9` |
| B1 | Präsens: fahren (du/er) | `b1cf74d139f410ba792e54567a5301b6e8cd147bc7d5d8b0e27233d6beccdbe3` |
| B1 | Präsens: lesen (du/er) | `f849f49ff6c73e3822b8922068cc6c73c4eeb54245e76df108f7b946912e7360` |
| B1 | Präsens: geben (du/er) | `369da24132e77cb0955538c7625651f103f8c0a9c08dd11eb260a59229dc0cf8` |
| B1 | Präsens: nehmen (du/er) | `416277077f50d6805fcd5be195a226ede47aac0242104693f10c71f86fb2d6ab` |
| B1 | Präsens: sprechen (du/er) | `2914d2e3cb99b9f9f46278d1f056a7ca8ff2e9ff1b7ff5fd8661567b4f05f776` |
| B1 | Nebensatz mit „weil“ | `f7b951d9dda441132c6aed3b2e9b66dd6a9ee3abb0071e9dfddb510e5361f0b8` |
| B1 | Nebensatz mit „dass“ | `a6ca086b93b3363424f9049a6cb01dfae409be15d8a494dc66bf11a2efa075f0` |
| B1 | Nebensatz mit „obwohl“ | `59c2aae19ec4d1babe3cd959b4281909f841a06a41d44cb505192eced7ec0643` |
| B1 | Nebensatz mit „wenn“ | `6b7ddf9f72ffb12e4056b28b3c8491d6bce98381af4443b7d39e22f4672cde74` |
| B1 | Indirekte Frage mit „ob“ | `288e8e6332237d0d9b44e74b9d6470119a0f97624685965dd6567689d633d6e6` |
| B2 | Passiv im Präsens | `bd1781242e8eaf8c4d98ddaf31e1267f58a4385292f23e98416093822701a071` |
| B2 | Passiv im Präteritum | `de83c738bdea7e20633bb3e8abf2b506fb0d88c53d1b9a4992f38d4e69b8c96a` |
| B2 | Passiv im Perfekt | `46282d7ae258c15f9aedbfde76a39ec5a9e3d6a5ba37bab0f827e900a528b39d` |
| B2 | Passiv mit Modalverb | `4967806bcdcc51a59b0bd1af35170a9d23fa5b472d0831bcda13d79b8912d0e4` |
| B2 | Irreale Bedingungen mit hätte | `20d4449643214c68a241bb819ec7ce095c52e1b32e746f2e7ba9756bb86192e3` |
| B2 | Höfliche Bitten mit könnte | `6b3d2d0cdc5142025b8a0a99fb20af2bf385ddd89800bff10b303fe2ca0b9dd5` |
| B2 | Irreale Vergangenheit mit hätte | `563ba5e97324267caf84151dc5cd0fecc5324e15698d330201bdf0476f4778bc` |
| B2 | Irreale Vergangenheit mit wäre | `36feddfd8b545e5ac1cfc13681840ac6cd3b0f54b5c7cc169555289a7faf8cb9` |
| B2 | Einen Gegensatz ausdrücken | `b27c22a110f2819f7c6882e34bf456a7e17355fe8b39a55cd5a84f47986f501e` |
| B2 | Einen Grund nennen | `4e726e094c5feef118bdde77fbfe62a17664f7589e2d764c37b6f3edd65ccffd` |
| B2 | Eine Folge ausdrücken | `0f462842876b5e67366639e98fd04c1c16a5d7d61339efe4e4180de2f82df400` |
| B2 | Absicht mit um … zu | `827a5174cdbcfcd8ee4952014dc795ac1e6d2bda1a9cbcf1c30c0a8ca9217633` |
| B2 | Infinitiv mit zu bei trennbaren Verben | `4647ba93a9a8f6e01cc613c41b91e086c9fae1b1fbb06255c4e890f48f311165` |
| B2 | Relativpronomen im Akkusativ | `c297745f79d3b70550da9949d620791a965afb8a9320ab92c2ccecedc047e566` |
| B2 | Relativpronomen im Dativ | `9897c3937c0391cc96f7941f91edde683493da7888d8a2c035f1d0e73fbeaa6b` |
| B2 | Relativpronomen im Genitiv | `403a841c1f5fcd605cd03c00a1947c19069f7b1e066ebe33fe48bc04500613b7` |
| B2 | Genitiv nach trotz | `71431a687a168c11bf568c54c6dc4f2951a54fd3c04cd3c9a2a8f41a3aafb73f` |
| B2 | Genitiv nach während | `45babf3a05025aa49b365b0bacf95176f35f654e9e884079e85915acc07aea87` |
| B2 | Adjektive im Genitiv | `cedd02be09d11437ad3fc17f9d4272a9689efae7995566b585779b12aa3e2dac` |
| B2 | Adjektive ohne Artikel | `56640ae7c4104628abcc4d8c2e1adc70a2aa92b830981d9449b010733476e21b` |
| B2 | Präposition bei abhängen | `473e65cfa7325132cbd669cf741179359929220913942fbea686e68fdfefea37` |
| B2 | Präposition bei teilnehmen | `eb7c0e2b30c6b9dcb545b441d56add97e7c7a008c6f5d6f981752a8da1343a72` |
| B2 | Präposition bei überzeugen | `1d9aec783586552305ab4852df172ce3b5a190342ab6e0933afd609b03e84dde` |
| B2 | Präposition bei sich befassen | `2e7c200983ef544d0a7db6d9e98a552c0e88643b2e11722b9378f5b6139acffc` |
| B2 | Präposition bei sich ergeben | `9ea292d63ec668f171133812a6fed2afd712b3c2adb08d45f20e20de07db89fa` |
| B2 | Einen Antrag stellen | `459a211934b6875347f5fe38b7cba52e80fec60dc9a82d93b209451587025b2d` |
| B2 | Eine Entscheidung treffen | `5d3daf369ea77cf8222fe82090ae0897c9010e85dd7ca79d0b3cfc170da31185` |
| B2 | Verantwortung übernehmen | `3df1f2cc51957d0da198c8bf4a013bc6850bd971cff24d1ed32bdd4649f43468` |
| B2 | Eine Frist einhalten | `0e5f9d6894485ee4185f32d124ff7f50fea44c695af4999c7fac7784b4eed3b1` |
| B2 | Voraussetzungen erfüllen | `8db9010ddffaad188ec7e732c86e6df27ca08af7ee169c5a47d2c0985a61be4a` |
