package com.germanverbmaster.android.foundation

import com.germanverbmaster.android.foundation.contract.*
import kotlinx.serialization.json.*
import java.security.MessageDigest
import java.time.Instant

/** Whole-pack integrity and linkage; this does not grade or confirm learner evidence. */
object PreparedPackReader {
    private fun canonical(value: JsonElement): String = when(value) {
        is JsonObject -> value.toSortedMap().entries.joinToString(",", "{", "}") { (key,item) -> "${JsonPrimitive(key)}:${canonical(item)}" }
        is JsonArray -> value.joinToString(",", "[", "]") { canonical(it) }
        else -> value.toString()
    }
    fun read(source: String): PreparedPack {
        val raw = ContractReader.json.parseToJsonElement(source)
        ContractShape.checkPreparedPack(raw)
        val pack = ContractReader.json.decodeFromJsonElement(PreparedPack.serializer(),raw)
        require(Regex("[0-9a-f]{64}").matches(pack.contentHash))
        require(Instant.parse(pack.expiresAt) > Instant.parse(pack.issuedAt))
        val hash = MessageDigest.getInstance("SHA-256").digest(canonical(JsonObject(raw.jsonObject.filterKeys { it != "contentHash" })).toByteArray(Charsets.UTF_8))
            .joinToString("") { String.format(java.util.Locale.ROOT,"%02x",it.toInt() and 255) }
        require(hash == pack.contentHash) { "Pack hash mismatch" }
        require(pack.sessions.map { it.id }.distinct().size == pack.sessions.size)
        val questions = pack.sessions.flatMap { it.questions }
        require(questions.map { it.id }.distinct().size == questions.size)
        require(pack.sessions.all { it.contentReleaseId == pack.contentReleaseId })
        val rubrics = pack.rubrics.associateBy { it.exerciseId to it.exerciseRevision }
        require(rubrics.size == pack.rubrics.size)
        require(rubrics.keys == questions.map { it.exercise.id to it.exercise.revision }.toSet())
        questions.forEach { question ->
            val exercise = question.exercise
            requireNotNull(rubrics[exercise.id to exercise.revision]).acceptedAnswers.forEach { answer ->
                OfflineGrader.validate(exercise, answer)
            }
        }
        return pack
    }
    fun canStart(pack: PreparedPack, now: Instant): Boolean = now >= Instant.parse(pack.issuedAt) && now < Instant.parse(pack.expiresAt)
}
