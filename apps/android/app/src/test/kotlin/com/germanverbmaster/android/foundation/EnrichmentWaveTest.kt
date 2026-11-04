package com.germanverbmaster.android.foundation

import com.germanverbmaster.android.foundation.contract.*
import com.germanverbmaster.android.learner.NativeGuestBundle
import com.germanverbmaster.android.learner.NativeGuestState
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.encodeToJsonElement
import org.junit.Assert.*
import org.junit.Test

/** Uses the exact generated shared candidate, without activating production assets. */
class EnrichmentWaveTest {
    private fun bundle() = ContractReader.json.decodeFromString<NativeGuestBundle>(
        requireNotNull(javaClass.classLoader?.getResource("enrichment-wave-1.json")).readText())

    @Test fun sharedCandidateGradesAllNewAnswersAndPreservesFrozenNativeState() {
        val packs=bundle().packs.filter { it.learningContext=="general" }
        val additions=packs.flatMap { pack -> pack.session.questions.map { pack to it } }
            .filter { (_,q) -> q.exercise.id.startsWith("81000000") }
        assertEquals(22, additions.size)
        additions.forEach { (pack,q) ->
            val rubric=pack.rubrics.single { it.exerciseId==q.exercise.id && it.exerciseRevision==q.exercise.revision }
            rubric.acceptedAnswers.forEach { answer ->
                val result=OfflineGrader.grade(q.exercise,rubric,ContractReader.json.encodeToJsonElement<Answer>(answer),emptyList())
                assertEquals("correct",result.outcome)
                val state=NativeGuestState(started=true,session=pack.session.copy(questions=listOf(q)),
                    draft=answer,feedback=result,graded=1,correct=1,saveRequested=true)
                assertEquals(state,ContractReader.json.decodeFromString<NativeGuestState>(ContractReader.json.encodeToString(state)))
            }
        }
    }

    @Test fun concatenatedLevelPoolsUseOneReleaseAndRespectSavedSessionBound() {
        val packs=bundle().packs.filter { it.learningContext=="general" }
        for ((level,count) in listOf("B1" to 77,"B2" to 70)) {
            val selected=packs.filter { it.level==level }
            val questions=selected.flatMap { it.session.questions }
            assertEquals(count,questions.size)
            assertEquals(questions.size,questions.map {it.exercise.id}.toSet().size)
            assertTrue(selected.all {it.session.contentReleaseId=="90000000-0000-4000-8000-000000000001"})
            val session=selected.first().session.copy(questions=questions)
            assertEquals(session,ContractReader.session(ContractReader.json.encodeToString(session)))
            try {
                session.copy(questions=List(126) {questions[it % questions.size]})
                fail("Oversized saved session must be rejected")
            } catch (_:IllegalArgumentException) { /* Generated shared contract rejects 126. */ }
        }
    }
}
