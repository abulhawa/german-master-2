package com.germanverbmaster.android.foundation

import androidx.compose.runtime.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.germanverbmaster.android.foundation.contract.*
import com.germanverbmaster.android.learner.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.*
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File
import java.nio.file.Files
import java.util.UUID
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class BasicCandidatePreviewTest {
    @get:Rule val compose = createComposeRule()

    @Test fun allHundredTwentyAuthoredVariantsAreSelectableAndGradeAtLargeText() {
        Harness().use { harness ->
            val variants = harness.variants
            assertEquals(120, variants.size)
            var exercise by mutableStateOf(variants.first().first)
            var draft by mutableStateOf<Answer?>(null)
            var order by mutableStateOf<List<String>>(emptyList())
            compose.setContent {
                val density = LocalDensity.current
                CompositionLocalProvider(LocalDensity provides Density(density.density, 2f)) {
                    FoundationTheme { Column(Modifier.width(320.dp).verticalScroll(rememberScrollState())) {
                        LowTypingInput(exercise, draft, order = order, german = true,
                            onDraft = { draft = it }, onOrder = { order = it; draft = AnswerWordOrder(it) })
                    } }
                }
            }
            for ((question, rubric) in variants) {
                compose.runOnIdle { exercise = question; draft = null; order = emptyList() }
                assertFalse(nativeAnswerReady(question, draft))
                when (val answer = rubric.acceptedAnswers.first()) {
                    is AnswerChoice -> {
                        val option = (question as ExerciseChoice).options.single { it.id == answer.optionId }
                        compose.onNodeWithText(option.text).performScrollTo().assertIsDisplayed().assertHeightIsAtLeast(48.dp).performClick()
                    }
                    is AnswerGapChoice -> answer.selections.forEachIndexed { index, selection ->
                        val slot = (question as ExerciseGapChoice).slots.single { it.id == selection.slotId }
                        val text = slot.options.single { it.id == selection.optionId }.text
                        // Finite-form text may occur in both verb gaps. Match its authored slot position.
                        val duplicates = question.slots.flatMap { it.options }.count { it.text == text }
                        val preceding = question.slots.takeWhile { it.id != slot.id }.flatMap { it.options }.count { it.text == text }
                        val node = if (duplicates == 1) compose.onNodeWithText(text) else compose.onAllNodesWithText(text)[preceding]
                        node.performScrollTo().assertIsDisplayed().assertHeightIsAtLeast(48.dp).performClick()
                        if (index < answer.selections.lastIndex) assertFalse(nativeAnswerReady(question, draft))
                    }
                    is AnswerWordOrder -> answer.tokenIds.forEach { id ->
                        val token = (question as ExerciseWordOrder).tokens.single { it.id == id }
                        compose.onNodeWithText(token.text).performScrollTo().assertIsDisplayed()
                            .assertHeightIsAtLeast(48.dp).performClick()
                    }
                    else -> error("Unexpected authored answer")

                }
                compose.runOnIdle {
                    assertTrue(nativeAnswerReady(question, draft))
                    assertEquals(rubric.acceptedAnswers.first(), draft)
                    val result = OfflineGrader.grade(question, rubric, ContractReader.json.encodeToJsonElement(Answer.serializer(), requireNotNull(draft)), emptyList())
                    assertEquals("correct", result.outcome)
                }
            }
        }
    }

    @Test fun everyConvertedTargetUsesRealHttpAndAtomicRestartReplaysWithoutDuplicateEvidence() = runBlocking {
        Harness().use { harness ->
            val directory = Files.createTempDirectory("basic-candidate-native").toFile()
            try {
                val api = harness.api
                api.save(ProfileRequest("v2", UUID.randomUUID().toString(), 0, ProfilePreferences("en", "Europe/Berlin", "B1", 5)))
                val store = AtomicLearnerStore(File(directory, "cache.json"))
                var lose = true
                val submissions = mutableListOf<Attempt>()
                val lossy = object : LearnerApi by api {
                    override suspend fun submit(attempt: Attempt): Acknowledgment {
                        submissions.add(attempt)
                        val receipt = api.submit(attempt)
                        if (lose) { lose = false; error("Accepted response lost") }
                        return receipt
                    }
                }
                var repo = LearnerRepository(lossy, store)
                repo.refresh()
                val targets = harness.variants.map { it.first.targetId }.filter { it.startsWith("10000000-") }.distinct()
                assertEquals(30, targets.size)
                for (target in targets) {
                    repo.startPractice(TargetFocus(target))
                    val exercise = repo.state.practice!!.question.exercise
                    assertTrue(exercise.revision == 3 || exercise.revision == 4)
                    val rubric = harness.variants.single { it.first.id == exercise.id && it.first.revision == exercise.revision }.second
                    repo.draft(rubric.acceptedAnswers.first())
                    if (exercise is ExerciseGapChoice && exercise.slots.size > 1) {
                        val answer = rubric.acceptedAnswers.first() as AnswerGapChoice
                        repo.draft(AnswerGapChoice(answer.selections.take(1)))
                        repo = LearnerRepository(lossy, store)
                        assertFalse(nativeAnswerReady(exercise, repo.state.practice!!.draft))
                        repo.draft(answer)
                    }
                    val savedDraft = repo.state.practice!!.draft
                    repo = LearnerRepository(lossy, store)
                    assertEquals(savedDraft, repo.state.practice!!.draft)
                    if (lose) {
                        assertTrue(runCatching { repo.answer() }.isFailure)
                        val frozen = repo.state.practice!!.pending
                        repo = LearnerRepository(lossy, store)
                        assertEquals(frozen, repo.state.practice!!.pending)
                    }
                    repo.answer()
                    assertEquals("correct", repo.state.practice!!.evaluation!!.outcome)
                    assertEquals(rubric.explanation, repo.state.practice!!.evaluation!!.explanation)
                    repo.continuePractice(); repo.finishPractice()
                    assertEquals(1, repo.state.practice!!.completionReceipt!!.correctCount)
                    repo.discardPractice()
                }
                assertEquals(submissions[0], submissions[1])
                assertEquals(30, harness.command("stats").getValue("evidence").jsonPrimitive.int)
                assertEquals(repo.state, store.read())
            } finally { directory.listFiles()?.forEach { it.delete() }; directory.delete() }
        }
    }

    @Test fun b2TargetsUseAuthoritativeHttpAndRestoreDrafts() = runBlocking {
        Harness().use { harness ->
            val directory = Files.createTempDirectory("basic-b2-candidate-native").toFile()
            try {
                val api = harness.api
                api.save(ProfileRequest("v2", UUID.randomUUID().toString(), 0,
                    ProfilePreferences("en", "Europe/Berlin", "B2", 5)))
                val store = AtomicLearnerStore(File(directory, "cache.json"))
                var repo = LearnerRepository(api, store)
                repo.refresh()
                val variants = harness.variants.filter { it.first.targetId.startsWith("40000000-") }
                val targets = variants.map { it.first.targetId }.distinct()
                assertEquals(60, variants.size)
                assertEquals(30, targets.size)
                for (target in targets) {
                    repo.startPractice(TargetFocus(target))
                    val current = repo.state.practice!!.question.exercise
                    assertTrue(current.revision == 4 || current.revision == 5)
                    assertTrue(current is ExerciseGapChoice)
                    val rubric = variants.single { it.first.id == current.id }.second
                    val answer = rubric.acceptedAnswers.first()
                    repo.draft(answer)
                    repo = LearnerRepository(api, store)
                    assertEquals(answer, repo.state.practice!!.draft)
                    repo.answer()
                    assertEquals("correct", repo.state.practice!!.evaluation!!.outcome)
                    repo.continuePractice()
                    repo.finishPractice()
                    assertEquals(1, repo.state.practice!!.completionReceipt!!.correctCount)
                    repo.discardPractice()
                }
                assertEquals(30, harness.command("stats").getValue("evidence").jsonPrimitive.int)
                assertEquals(repo.state, store.read())
            } finally {
                directory.listFiles()?.forEach { it.delete() }
                directory.delete()
            }
        }
    }

    private class Harness : AutoCloseable {
        private val root = File(requireNotNull(System.getProperty("gm.repoRoot")))
        private val process = ProcessBuilder(System.getProperty("gm.testNode", "node"),
            File(root, "node_modules/tsx/dist/cli.mjs").absolutePath,
            File(root, "services/api/scripts/preview-basic-content.ts").absolutePath, "--stdio")
            .directory(root).redirectError(ProcessBuilder.Redirect.INHERIT).start()
        private val input = process.outputStream.bufferedWriter()
        private val output = process.inputStream.bufferedReader()
        private val reader = Executors.newSingleThreadExecutor()
        private fun receive() = reader.submit<JsonObject> {
            ContractReader.json.parseToJsonElement(checkNotNull(output.readLine()) { "Preview exited" }).jsonObject
        }.get(30, TimeUnit.SECONDS)
        val port = receive().getValue("port").jsonPrimitive.int
        val api = LocalLearnerApi(port, FIXTURE_SUBJECT)
        val variants = command("answers").getValue("variants").jsonArray.map {
            val row = it.jsonObject
            ContractReader.json.decodeFromJsonElement(Exercise.serializer(), row.getValue("exercise")) to
                ContractReader.json.decodeFromJsonElement(OfflineRubric.serializer(), row.getValue("rubric"))
        }
        fun command(action: String): JsonObject {
            input.write("{\"action\":\"$action\"}\n"); input.flush(); return receive()
        }
        override fun close() {
            try { input.write("{\"action\":\"close\"}\n"); input.flush(); if (!process.waitFor(10, TimeUnit.SECONDS)) process.destroyForcibly() }
            finally { process.destroyForcibly(); reader.shutdownNow(); input.close(); output.close() }
        }
    }
}
