package com.germanverbmaster.android.data.repository

import com.germanverbmaster.android.data.local.entity.WordEntity
import java.util.Locale

internal object BundledWordsCsvParser {
    private const val EXPECTED_COLUMNS = 6

    fun parse(
        csvText: String,
        idBase: Int = 2_000_000,
        level: String = "B2",
        versionTag: String,
    ): List<WordEntity> {
        val rawRows = parseCsvRows(csvText)
        if (rawRows.isEmpty()) return emptyList()

        val header = normalizeToExpectedColumns(rawRows.first()) ?: return emptyList()
        val headerMap = header.mapIndexed { index, value -> normalizeToken(value) to index }.toMap()

        val wordIndex = findHeaderIndex(headerMap, "word", "wort", "lemma") ?: return emptyList()
        val englishIndex = findHeaderIndex(headerMap, "englishtranslation", "translationenglish", "english") ?: return emptyList()
        val posIndex = findHeaderIndex(headerMap, "pos", "postag", "wortart") ?: return emptyList()
        val articleIndex = findHeaderIndex(headerMap, "articleprefix", "article", "prefix", "artikel")
        val exampleDeIndex = findHeaderIndex(headerMap, "examplesentence", "examplede", "beispielsatz", "satz")
        val exampleEnIndex = findHeaderIndex(
            headerMap,
            "englishtranslationsentence",
            "englishsentence",
            "exampleen",
            "sentenceenglish",
        )

        val entities = mutableListOf<WordEntity>()
        val dedupe = HashSet<String>()

        for (row in rawRows.drop(1)) {
            val normalized = normalizeToExpectedColumns(row) ?: continue
            val pos = normalizePos(normalized.getOrNull(posIndex).orEmpty()) ?: continue

            val rawWord = normalized.getOrNull(wordIndex).orEmpty().trim()
            val english = normalized.getOrNull(englishIndex).orEmpty().trim().ifBlank { null } ?: continue

            val (lemma, plural) = splitLemmaAndPlural(rawWord, pos)
            if (lemma.isBlank()) continue

            val articlePrefix = articleIndex?.let { normalized.getOrNull(it).orEmpty() }.orEmpty()
            val exampleDe = exampleDeIndex?.let { normalized.getOrNull(it).orEmpty().trim().ifBlank { null } }
            val exampleEn = exampleEnIndex?.let { normalized.getOrNull(it).orEmpty().trim().ifBlank { null } }
            val dedupeKey = listOf(
                lemma.lowercase(Locale.US),
                pos,
                english.lowercase(Locale.US),
                articlePrefix.lowercase(Locale.US),
                (exampleDe ?: "").lowercase(Locale.US),
                (exampleEn ?: "").lowercase(Locale.US),
            ).joinToString("|")
            if (!dedupe.add(dedupeKey)) continue

            entities += WordEntity(
                id = idBase + entities.size + 1,
                lemma = lemma,
                pos = pos,
                level = level,
                english = english,
                exampleDe = exampleDe,
                exampleEn = exampleEn,
                gender = if (pos == "N") inferGender(articlePrefix) else null,
                plural = if (pos == "N") plural else null,
                separable = null,
                aux = null,
                praeteritum = null,
                partizip2 = null,
                perfekt = null,
                praesensIch = null,
                praesensEr = null,
                comparative = null,
                superlative = null,
                updatedAt = versionTag,
            )
        }

        return entities
    }

    private fun findHeaderIndex(
        normalizedHeaderToIndex: Map<String, Int>,
        vararg candidates: String,
    ): Int? = candidates.firstNotNullOfOrNull { normalizedHeaderToIndex[it] }

    private fun splitLemmaAndPlural(rawWord: String, pos: String): Pair<String, String?> {
        if (pos != "N") return rawWord.trim() to null
        val parts = rawWord.split(",", limit = 2).map { it.trim() }
        val lemma = parts.firstOrNull().orEmpty()
        val plural = parts.getOrNull(1)?.takeIf { it.isNotBlank() && it != "-" }
        return lemma to plural
    }

    private fun inferGender(articlePrefix: String): String? {
        val normalized = " ${articlePrefix.lowercase(Locale.US)} "
            .replace("\u00E4", "ae")
            .replace("\u00F6", "oe")
            .replace("\u00FC", "ue")
            .replace("\u00DF", "ss")

        val hasDer = Regex("""\bder\b""").containsMatchIn(normalized)
        val hasDie = Regex("""\bdie\b""").containsMatchIn(normalized)
        val hasDas = Regex("""\bdas\b""").containsMatchIn(normalized)

        return when (listOf(hasDer, hasDie, hasDas).count { it }) {
            0 -> null
            1 -> when {
                hasDer -> "m"
                hasDie -> "f"
                else -> "n"
            }
            else -> buildList {
                if (hasDer) add("der")
                if (hasDie) add("die")
                if (hasDas) add("das")
            }.joinToString("/")
        }
    }

    private fun normalizePos(raw: String): String? {
        val token = normalizeToken(raw)
        return when (token) {
            "v", "verb", "verben" -> "V"
            "n", "noun", "nomen", "substantiv", "subst", "propn", "propernoun" -> "N"
            "adj", "adjective", "adjektiv" -> "Adj"
            "adv", "adverb" -> "Adv"
            "prep", "preposition", "adp", "praeposition" -> "Prep"
            "conj", "conjunction", "konjunktion", "cconj", "sconj" -> "Conj"
            "pron", "pronomen", "pronoun" -> "Pron"
            "int", "intj", "interjektion", "interjection" -> "Int"
            "art", "article", "artikel", "det", "determiner" -> "Art"
            "num", "numerale", "numeral", "zahlwort" -> "Num"
            else -> null
        }
    }

    private fun normalizeToExpectedColumns(row: List<String>): List<String>? {
        val trimmed = row.map { it.trim() }
        return when {
            trimmed.size == EXPECTED_COLUMNS -> trimmed
            trimmed.size > EXPECTED_COLUMNS -> {
                val article = trimmed[0]
                val word = trimmed[1]
                val english = trimmed.subList(2, trimmed.size - 3).joinToString(",").trim()
                val exampleDe = trimmed[trimmed.size - 3]
                val exampleEn = trimmed[trimmed.size - 2]
                val pos = trimmed.last()
                listOf(article, word, english, exampleDe, exampleEn, pos)
            }
            else -> null
        }
    }

    private fun parseCsvRows(csvText: String): List<List<String>> {
        val rows = mutableListOf<List<String>>()
        val row = mutableListOf<String>()
        val field = StringBuilder()
        var inQuotes = false
        var i = 0

        while (i < csvText.length) {
            when (val ch = csvText[i]) {
                '"' -> {
                    if (inQuotes && i + 1 < csvText.length && csvText[i + 1] == '"') {
                        field.append('"')
                        i++
                    } else {
                        inQuotes = !inQuotes
                    }
                }
                ',' -> {
                    if (inQuotes) {
                        field.append(ch)
                    } else {
                        row += field.toString()
                        field.setLength(0)
                    }
                }
                '\n' -> {
                    if (inQuotes) {
                        field.append(ch)
                    } else {
                        row += field.toString()
                        field.setLength(0)
                        if (row.any { it.isNotBlank() }) rows += row.toList()
                        row.clear()
                    }
                }
                '\r' -> {
                    if (inQuotes) field.append(ch)
                }
                else -> field.append(ch)
            }
            i++
        }

        if (field.isNotEmpty() || row.isNotEmpty()) {
            row += field.toString()
            if (row.any { it.isNotBlank() }) rows += row.toList()
        }

        return rows
    }

    private fun normalizeToken(value: String): String = value
        .lowercase(Locale.US)
        .replace("\u00E4", "ae")
        .replace("\u00F6", "oe")
        .replace("\u00FC", "ue")
        .replace("\u00DF", "ss")
        .replace(Regex("[^a-z0-9]"), "")
}
