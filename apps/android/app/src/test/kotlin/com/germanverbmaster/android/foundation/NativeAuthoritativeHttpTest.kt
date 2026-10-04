package com.germanverbmaster.android.foundation

import com.germanverbmaster.android.learner.*
import com.germanverbmaster.android.foundation.contract.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.*
import org.junit.Assert.*
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
class NativeAuthoritativeHttpTest {
    /** Real TypeScript routes, learning engine and PostgreSQL; no mocked HTTP responses. */
    private class Harness : AutoCloseable {
        private val root = File(requireNotNull(System.getProperty("gm.repoRoot")))
        private val process = ProcessBuilder(System.getProperty("gm.testNode", "node"),
            File(root, "node_modules/tsx/dist/cli.mjs").absolutePath,
            File(root, "services/api/scripts/native-recovery-harness.ts").absolutePath)
            .directory(root).redirectError(ProcessBuilder.Redirect.INHERIT).start()
        private val input = process.outputStream.bufferedWriter()
        private val output = process.inputStream.bufferedReader()
        private val reader = Executors.newSingleThreadExecutor()
        private fun receive(): JsonObject = reader.submit<JsonObject> {
            ContractReader.json.parseToJsonElement(checkNotNull(output.readLine()) { "Harness exited" }).jsonObject
        }.get(30, TimeUnit.SECONDS)
        val api = LocalLearnerApi(receive().getValue("port").jsonPrimitive.int)
        fun command(action: String): JsonObject {
            input.write("{\"action\":\"$action\"}\n"); input.flush()
            return receive()
        }
        override fun close() {
            try {
                input.write("{\"action\":\"close\"}\n"); input.flush()
                if (!process.waitFor(10, TimeUnit.SECONDS)) process.destroyForcibly()
            } finally { process.destroyForcibly(); reader.shutdownNow(); input.close(); output.close() }
        }
    }

    @Test fun expiredCleanedPagesPreserveFrozenWritesAndExplicitRestartReconcilesOnce() = runBlocking {
        for (kind in listOf("attempt", "skip", "session")) Harness().use { harness ->
            val api = harness.api
            val preferences = ProfilePreferences("en", "UTC", "B1", 5)
            val profile = api.save(ProfileRequest("v2", UUID.randomUUID().toString(), 0, preferences))
            val pendingProfile = ProfileRequest("v2", UUID.randomUUID().toString(), profile.revision, preferences.copy(locale = "de"))
            // Server commits are deliberately absent from the persisted client, as after response loss.
            api.save(pendingProfile)
            val request = foundationSessionRequest().copy(questionCount = 1, capabilities = listOf("short_answer@1"))
            val session = api.session(request)
            val draft = AnswerShortAnswer("wrong")
            val pending = foundationAttempt(session, 0, draft, true, UUID.randomUUID().toString())
            val event = ExposureEvent(UUID.randomUUID().toString(), session.questions[0].id,
                session.questions[0].exercise.revision, pending.deviceId, "skip", "2026-10-04T12:00:00Z")
            if (kind == "attempt") assertTrue(api.submit(pending) is AttemptAcknowledgment)
            if (kind == "skip") assertTrue(api.expose(event) is ExposureAccepted)
            val first = api.targets("")
            val practice = if (kind == "session") NativePractice(request) else NativePractice(request,
                deviceId = pending.deviceId, session = session, draft = draft, assisted = true,
                order = listOf("saved-order"), pending = if (kind == "attempt") pending else null,
                exposure = if (kind == "skip") event else null)
            val before = LearnerCache(profile = profile, pending = pendingProfile, practice = practice,
                targets = first.targets, generatedAt = first.generatedAt, syncCursor = first.syncCursor)
            val directory = Files.createTempDirectory("native-real-http").toFile()
            try {
                val file = File(directory, "cache.json")
                AtomicLearnerStore(file).write(before)
                val repo = LearnerRepository(api, AtomicLearnerStore(file))
                val writesBefore = harness.command("stats").getValue("calls").jsonArray.count { it.jsonPrimitive.content.startsWith("POST") }
                harness.command("expire")
                assertTrue(runCatching { repo.refresh() }.isFailure)
                assertEquals(before, repo.state)
                assertEquals(before, AtomicLearnerStore(file).read())
                assertEquals(0, harness.command("stats").getValue("pages").jsonPrimitive.int)
                assertTrue(runCatching { api.sync(requireNotNull(before.syncCursor)) }.exceptionOrNull() is SyncCursorReset)
                val restarted = LearnerRepository(api, AtomicLearnerStore(file))
                restarted.refresh()
                assertEquals(5, restarted.state.targets.size)
                assertEquals(5, restarted.state.targets.map { it.targetId }.distinct().size)
                assertNotEquals(before.syncCursor, restarted.state.syncCursor)
                assertEquals(before.practice, restarted.state.practice)
                assertEquals(before.pending, restarted.state.pending)
                assertEquals(restarted.state, AtomicLearnerStore(file).read())
                assertEquals(writesBefore, harness.command("stats").getValue("calls").jsonArray.count { it.jsonPrimitive.content.startsWith("POST") })
                restarted.refresh() // Replacement cursor works on the real sync route.
                restarted.retry()
                assertNull(restarted.state.pending)
                assertEquals(profile.revision + 1, restarted.state.profile!!.revision)
                when (kind) {
                    "attempt" -> { restarted.answer(); assertNotNull(restarted.state.practice!!.evaluation); assertEquals(1, restarted.state.practice!!.graded) }
                    "skip" -> { restarted.skip(); assertEquals(1, restarted.state.practice!!.skipped) }
                    else -> { restarted.startPractice(); assertEquals(session, restarted.state.practice!!.session) }
                }
                assertEquals(if (kind == "session") 0 else 1, harness.command("stats").getValue("evidence").jsonPrimitive.int)
            } finally { directory.deleteRecursively() }
        }
    }

    @Test fun preferredFifteenStartsAvailableMixedPracticeAndResumesToConfirmedSummary() = runBlocking {
        Harness().use { harness ->
            val api = harness.api
            api.save(ProfileRequest("v2", UUID.randomUUID().toString(), 0, ProfilePreferences("en", "UTC", "B1", 15)))
            val fixtures = ContractReader.session(requireNotNull(javaClass.classLoader?.getResource("session.json")).readText())
            val answers = ContractReader.attempts(requireNotNull(javaClass.classLoader?.getResource("attempt-batch.json")).readText()).attempts
            val directory = Files.createTempDirectory("native-mixed-http").toFile()
            try {
                val store = AtomicLearnerStore(File(directory, "cache.json"))
                var repo = LearnerRepository(api, store)
                repo.refresh(); repo.startPractice()
                assertEquals(5, repo.state.practice!!.request.questionCount)
                assertEquals(5, repo.state.practice!!.session!!.questions.size)
                for (index in 0..4) {
                    if (index == 2) repo = LearnerRepository(api, store)
                    val question = repo.state.practice!!.question
                    val fixtureIndex = fixtures.questions.indexOfFirst { it.exercise.id == question.exercise.id }
                    repo.draft(answers[fixtureIndex].answer)
                    if (index == 0) {
                        repo.hint()
                        val beforeReport = repo.state.practice
                        repo.reportProblem("ambiguous_prompt")
                        assertTrue(repo.state.reportRecorded)
                        val report = requireNotNull(repo.state.contentReport)
                        assertEquals(question.id, report.sessionQuestionId)
                        assertEquals(question.exercise.revision, report.exerciseRevision)
                        assertEquals("recorded", api.report(report).status)
                        repo = LearnerRepository(api, store)
                        assertEquals(beforeReport, repo.state.practice)
                        assertTrue(repo.state.reportRecorded)
                        assertEquals(0, harness.command("stats").getValue("evidence").jsonPrimitive.int)
                    }
                    repo.answer()
                    assertEquals("correct", repo.state.practice!!.evaluation!!.outcome)
                    assertEquals(index == 0, repo.state.practice!!.evaluation!!.assisted)
                    repo.continuePractice()
                }
                val summary = LearnerRepository(api, store).state.practice!!
                assertEquals(5, summary.index); assertEquals(5, summary.graded); assertEquals(5, summary.correct)
                assertEquals(0, summary.skipped)
                repo.refresh()
                assertEquals(5, repo.state.targets.sumOf { it.exposureCount })
                assertTrue(repo.state.targets.none { it.state == "mastered" })
                assertEquals(5, harness.command("stats").getValue("evidence").jsonPrimitive.int)
                assertEquals(repo.state, store.read())
            } finally { directory.deleteRecursively() }
        }
    }
}
