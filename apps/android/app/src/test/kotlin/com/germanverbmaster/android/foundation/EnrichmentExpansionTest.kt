package com.germanverbmaster.android.foundation

import com.germanverbmaster.android.foundation.contract.*
import com.germanverbmaster.android.learner.NativeGuestBundle
import com.germanverbmaster.android.learner.NativeGuestState
import com.germanverbmaster.android.learner.selectNativeGuestQuestions
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.encodeToJsonElement
import org.junit.Assert.*
import org.junit.Test

class EnrichmentExpansionTest {
    private val bundle by lazy { ContractReader.json.decodeFromString<NativeGuestBundle>(
        requireNotNull(javaClass.classLoader?.getResource("enrichment-wave-2.json")).readText()) }

    @Test fun everyReviewedNewAnswerGradesCorrectlyOnNative() {
        val newPacks=bundle.packs.filter {it.topicId.startsWith("91000000")}
        assertEquals(2466,newPacks.sumOf {it.session.questions.size})
        assertEquals(54,newPacks.filter {it.learningContext=="work"}.sumOf {it.session.questions.size})
        newPacks.forEach {pack -> pack.session.questions.forEach {q ->
            val rubric=pack.rubrics.single {it.exerciseId==q.exercise.id && it.exerciseRevision==q.exercise.revision}
            rubric.acceptedAnswers.forEach {answer ->
                assertEquals(q.exercise.id,"correct",OfflineGrader.grade(q.exercise,rubric,ContractReader.json.encodeToJsonElement<Answer>(answer),emptyList()).outcome)
            }
        } }
    }

    @Test fun largerGeneralLibraryProducesValidFrozen125QuestionNativeRounds() {
        for ((level,count) in listOf("B1" to 1283,"B2" to 1276)) {
            val packs=bundle.packs.filter {it.level==level && it.learningContext=="general"}
            assertTrue(packs.all {it.session.questions.size<=125})
            val eligible=packs.flatMap {it.session.questions}
            assertEquals(count,eligible.size)
            val questions=selectNativeGuestQuestions(eligible)
            assertEquals(questions.map {it.exercise.id}.toSet(),selectNativeGuestQuestions(eligible,questions).map {it.exercise.id}.toSet())
            val session=packs.first().session.copy(questions=questions)
            assertEquals(125,session.questions.size)
            assertEquals("91000000-0000-4000-8000-000000000001",session.contentReleaseId)
            assertEquals(session,ContractReader.session(ContractReader.json.encodeToString(session)))
            val state=NativeGuestState(started=true,session=session,index=124,saveRequested=true)
            assertEquals(state,ContractReader.json.decodeFromString<NativeGuestState>(ContractReader.json.encodeToString(state)))
        }
    }
}
