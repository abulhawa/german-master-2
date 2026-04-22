package com.germanverbmaster.android.ui.common

import java.util.Locale

internal data class NounPresentation(
    val singularDisplay: String,
    val pluralDisplay: String?,
    val speakText: String,
    val genderDisplay: String?,
)

internal object NounFormFormatter {
    fun isNoun(pos: String): Boolean {
        val normalized = pos.trim().uppercase(Locale.ROOT)
        return normalized == "N" || normalized == "NOMEN"
    }

    fun present(
        lemma: String,
        gender: String?,
        plural: String?,
    ): NounPresentation {
        val cleanLemma = lemma.trim()
        val articles = parseArticles(gender)
        val articleLabel = articles.values.joinToString("/")
        val pairLemmas = parseMascFemPair(cleanLemma)
        val hasMascFemPair = pairLemmas != null
        val masculineLemma = pairLemmas?.first ?: cleanLemma
        val feminineLemma = pairLemmas?.second ?: cleanLemma

        val singularDisplay = if (hasMascFemPair) {
            withArticle(normalizePairLemma(cleanLemma), articleLabel.takeIf { it.isNotBlank() })
        } else {
            withArticle(cleanLemma, articleLabel.takeIf { it.isNotBlank() })
        }

        val speakText = if (hasMascFemPair && !articles.hasDer && articles.hasDie) {
            withArticle(feminineLemma, "die")
        } else {
            val preferredArticle = when {
                articles.hasDer -> "der"
                articles.hasDie -> "die"
                articles.hasDas -> "das"
                else -> null
            }
            withArticle(masculineLemma, preferredArticle)
        }

        val pluralDisplay = formatPluralEnding(
            rawPlural = plural,
            hasMascFemPair = hasMascFemPair,
            masculineLemma = masculineLemma,
            feminineLemma = feminineLemma,
        )

        return NounPresentation(
            singularDisplay = singularDisplay,
            pluralDisplay = pluralDisplay,
            speakText = speakText,
            genderDisplay = articles.values.takeIf { it.isNotEmpty() }?.joinToString("/")
                ?: gender?.trim()?.takeIf { it.isNotEmpty() },
        )
    }

    private fun withArticle(lemma: String, article: String?): String {
        val cleanArticle = article?.trim().orEmpty()
        return if (cleanArticle.isEmpty()) lemma else "$cleanArticle $lemma"
    }

    private fun parseMascFemPair(lemma: String): Pair<String, String>? {
        val match = Regex("""^(.+?)\s*/\s*[iI]n\b""").find(lemma) ?: return null
        val masculine = match.groupValues[1].trim()
        if (masculine.isEmpty()) return null
        val feminine = "$masculine" + "in"
        return masculine to feminine
    }

    private fun normalizePairLemma(lemma: String): String {
        val match = Regex("""^(.+?)\s*/\s*[iI]n\b""").find(lemma) ?: return lemma
        return "${match.groupValues[1].trim()}/in"
    }

    private fun formatPluralEnding(
        rawPlural: String?,
        hasMascFemPair: Boolean,
        masculineLemma: String,
        feminineLemma: String,
    ): String? {
        val normalized = rawPlural?.trim()?.takeIf { it.isNotEmpty() } ?: return null
        val pieces = normalized.split("/").map { it.trim() }.filter { it.isNotEmpty() }
        if (pieces.isEmpty()) return null

        if (pieces.size == 1) {
            return toEndingToken(
                token = pieces.first(),
                baseLemma = masculineLemma,
                keepDashPrefix = true,
            )
        }

        return pieces.mapIndexed { index, token ->
            val baseLemma = if (hasMascFemPair && index > 0) feminineLemma else masculineLemma
            toEndingToken(
                token = token,
                baseLemma = baseLemma,
                keepDashPrefix = index == 0,
            )
        }.joinToString(", ")
    }

    private fun toEndingToken(
        token: String,
        baseLemma: String,
        keepDashPrefix: Boolean,
    ): String {
        val raw = token.trim()
        val compact = when {
            raw == "-" -> "-"
            raw.startsWith("-") -> raw
            raw == baseLemma -> "-"
            raw.startsWith(baseLemma) && raw.length > baseLemma.length ->
                "-" + raw.removePrefix(baseLemma)
            else -> raw
        }
        return if (!keepDashPrefix && compact.startsWith("-") && compact.length > 1) {
            compact.removePrefix("-")
        } else {
            compact
        }
    }

    private data class Articles(
        val values: List<String>,
        val hasDer: Boolean,
        val hasDie: Boolean,
        val hasDas: Boolean,
    )

    private fun parseArticles(gender: String?): Articles {
        val normalized = " ${gender?.trim()?.lowercase(Locale.ROOT).orEmpty()} "

        val hasDer = Regex("""\bder\b""").containsMatchIn(normalized) ||
            Regex("""\bm\b""").containsMatchIn(normalized) ||
            Regex("""\br\b""").containsMatchIn(normalized)
        val hasDie = Regex("""\bdie\b""").containsMatchIn(normalized) ||
            Regex("""\bf\b""").containsMatchIn(normalized) ||
            Regex("""\be\b""").containsMatchIn(normalized)
        val hasDas = Regex("""\bdas\b""").containsMatchIn(normalized) ||
            Regex("""\bn\b""").containsMatchIn(normalized) ||
            Regex("""\bs\b""").containsMatchIn(normalized)

        val values = buildList {
            if (hasDer) add("der")
            if (hasDie) add("die")
            if (hasDas) add("das")
        }

        return Articles(
            values = values,
            hasDer = hasDer,
            hasDie = hasDie,
            hasDas = hasDas,
        )
    }
}
