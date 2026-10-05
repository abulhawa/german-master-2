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
    @Test fun durableDeletionReplaysAfterResponseLossAndBlocksAbandonedWrites() = runBlocking {
        Harness().use { harness ->
            val directory = Files.createTempDirectory("delete-cache").toFile()
            val account = LearnerAccount(LearnerIdentity(FIXTURE_SUBJECT,0)) { LearnerIdentity(FIXTURE_SUBJECT,0) }
            val store = account.store(directory)
            val requests = mutableListOf<PrivacyDeleteRequest>()
            var lose = true
            val transport = object : LearnerApi by harness.api {
                override suspend fun deleteLearner(request: PrivacyDeleteRequest): PrivacyDeleteReceipt {
                    requests.add(request)
                    val receipt = harness.api.deleteLearner(request)
                    if(lose) error("response lost")
                    return receipt
                }
            }
            try {
                harness.api.save(ProfileRequest("v2",UUID.randomUUID().toString(),0,ProfilePreferences("en","UTC","B1",5)))
                val repo = LearnerRepository(transport,store,account)
                repo.refresh(); repo.startPractice(); repo.draft(AnswerShortAnswer("saved draft")); repo.hint()
                val before = repo.state
                assertTrue(runCatching { repo.deleteLearner() }.isFailure)
                assertEquals(before.practice,repo.state.practice); assertNotNull(repo.state.deletion); assertNull(repo.state.deletionReceipt)
                assertTrue(runCatching { repo.syncSavedWork() }.isFailure)
                assertTrue(runCatching { repo.draft(AnswerShortAnswer("changed")) }.isFailure)
                assertTrue(runCatching { repo.refresh() }.isFailure)
                lose = false
                val reopened = LearnerRepository(transport,store,account); reopened.deleteLearner()
                assertEquals(requests[0],requests[1]); assertNotNull(reopened.state.deletionReceipt)
                assertNull(reopened.state.practice); assertNull(reopened.state.profile); assertTrue(reopened.state.targets.isEmpty())
                reopened.deleteLearner(); assertEquals(2,requests.size)
                val terminal = LearnerRepository(transport,store,account)
                assertTrue(runCatching { terminal.startPractice() }.isFailure)
                assertTrue(runCatching { harness.api.profile() }.isFailure)
            } finally { directory.listFiles()?.forEach {it.delete()}; directory.delete() }
        }
    }
    @Test fun ownedExportUsesAuthoritativeHttpAndSyncsOnlyOnExplicitChoice() = runBlocking {
        Harness().use { harness ->
            harness.api.save(ProfileRequest("v2",UUID.randomUUID().toString(),0,ProfilePreferences("en","UTC","B1",5)))
            val file = File(Files.createTempDirectory("export-cache").toFile(),"cache.json")
            try {
                val account = LearnerAccount(LearnerIdentity(FIXTURE_SUBJECT,0)) { LearnerIdentity(FIXTURE_SUBJECT,0) }
                val repo = LearnerRepository(harness.api, AtomicLearnerStore(file),account)
                repo.refresh(); repo.startPractice(); repo.draft(AnswerShortAnswer("saved draft")); repo.hint()
                val before = repo.state
                val confirmed = repo.exportLearner(false)
                assertEquals(FIXTURE_SUBJECT,confirmed.subject); assertEquals("learner-export-v1",confirmed.schemaVersion)
                assertEquals(before,repo.state); assertTrue(confirmed.attempts.isEmpty())
                assertTrue(confirmed.sessions.any { it.id == repo.state.practice?.session?.id })
                val request = ProfileRequest("v2",UUID.randomUUID().toString(),requireNotNull(repo.state.profile).revision,
                    requireNotNull(repo.state.profile).preferences.copy(locale="de"))
                AtomicLearnerStore(file).write(repo.state.copy(pending=request))
                val reopened = LearnerRepository(harness.api,AtomicLearnerStore(file),account)
                assertEquals("en",reopened.exportLearner(false).profile.preferences.locale)
                assertEquals(request,reopened.state.pending)
                val synced = reopened.exportLearner(true)
                assertEquals("de",synced.profile.preferences.locale); assertNull(reopened.state.pending)
                assertEquals(before.practice,reopened.state.practice)
                assertTrue(runCatching { LocalLearnerApi(harness.port,UUID.randomUUID().toString()).exportLearner() }.isFailure)
            } finally { file.delete(); file.parentFile.delete() }
        }
    }
    @Test fun capturedSubjectMismatchCannotReadOrChangeTheAuthoritativeFixture() = runBlocking {
        Harness().use { harness ->
            val original = harness.api.profile()
            val foreign = LocalLearnerApi(harness.port, UUID.randomUUID().toString())
            assertTrue(runCatching {foreign.profile()}.isFailure)
            assertTrue(runCatching {foreign.save(ProfileRequest("v2",UUID.randomUUID().toString(),original.revision,original.preferences.copy(locale="de")))}.isFailure)
            assertEquals(original,harness.api.profile())
        }
    }
    @Test fun preparedPackUsesActualOwnedHttpAndValidatesWholePayloadBeforeReady() = runBlocking {
        Harness().use { harness ->
            val request = foundationSessionRequest().copy(questionCount = 5)
            val pack = harness.api.preparePack(request)
            assertEquals(2,pack.sessions.size); assertEquals(5,pack.rubrics.size)
            assertEquals(10,pack.sessions.flatMap { it.questions }.map { it.id }.distinct().size)
            assertEquals(pack,harness.api.preparePack(request))
            assertTrue(PreparedPackReader.canStart(pack,java.time.Instant.parse(pack.issuedAt)))
            assertFalse(PreparedPackReader.canStart(pack,java.time.Instant.parse(pack.expiresAt)))
            val raw = ContractReader.json.encodeToString(PreparedPack.serializer(),pack)
            assertEquals(pack,PreparedPackReader.read(raw))
            assertTrue(runCatching {PreparedPackReader.read(raw.replace(pack.contentHash,"0".repeat(64)))}.isFailure)
            assertTrue(runCatching {PreparedPackReader.read(raw.replace("deterministic-v1","unknown"))}.isFailure)
            assertEquals(0,harness.command("stats").getValue("evidence").jsonPrimitive.int)
            harness.api.save(ProfileRequest("v2",UUID.randomUUID().toString(),0,ProfilePreferences("en","UTC","B1",5)))
            val memory = object : LearnerStore {
                var cache = LearnerCache()
                var fail = false
                override fun read() = cache
                override fun write(value: LearnerCache) { check(!fail); cache = value }
            }
            var lose = true
            var failReceipt = false
            val requests = mutableListOf<SessionRequest>()
            val lossy = object : LearnerApi by harness.api {
                override suspend fun preparePack(request: SessionRequest): PreparedPack {
                    requests.add(request)
                    val result = harness.api.preparePack(request)
                    if(lose) { lose = false; error("accepted response lost") }
                    if(failReceipt) {failReceipt = false; memory.fail = true}
                    return result
                }
            }
            var repo = LearnerRepository(lossy,memory); repo.refresh(); repo.startPractice(); repo.draft(AnswerShortAnswer("saved")); repo.hint()
            val before = repo.state.practice
            assertTrue(runCatching {repo.prepareReserve()}.isFailure)
            val frozen = requireNotNull(repo.state.packRequest)
            assertNull(repo.state.preparedPack)
            repo = LearnerRepository(lossy,memory); repo.prepareReserve()
            assertEquals(listOf(frozen,frozen),requests)
            assertNull(repo.state.packRequest); assertEquals(before,repo.state.practice)
            assertEquals(frozen.requestId,repo.state.preparedPack!!.packId)
            assertEquals(repo.state,memory.read())
            val firstPack = repo.state.preparedPack
            failReceipt = true
            assertTrue(runCatching {repo.prepareReserve()}.isFailure)
            assertEquals(firstPack,repo.state.preparedPack)
            val secondFrozen = requireNotNull(repo.state.packRequest)
            memory.fail = false; repo = LearnerRepository(lossy,memory); repo.prepareReserve()
            assertEquals(secondFrozen,requests.last()); assertNull(repo.state.packRequest)
            assertEquals(before,repo.state.practice)
            val file = File(Files.createTempDirectory("pack-cache").toFile(),"cache.json")
            AtomicLearnerStore(file).write(repo.state)
            assertEquals(repo.state,AtomicLearnerStore(file).read())
            val stored = file.readText(Charsets.UTF_8)
            file.writeText(stored.replace(requireNotNull(repo.state.preparedPack).contentHash,"0".repeat(64)),Charsets.UTF_8)
            assertTrue(runCatching {AtomicLearnerStore(file).read()}.isFailure)
            assertTrue(file.exists())
            file.delete(); file.parentFile.delete()
            Unit
        }
    }
    @Test fun offlineReserveConsumesBothSessionsAndReplaysOrderedWritesAfterRestartResponseLossAndSaveFailure() = runBlocking {
        Harness().use { harness ->
            val profile = harness.api.save(ProfileRequest("v2",UUID.randomUUID().toString(),0,ProfilePreferences("en","UTC","B1",5)))
            val pendingProfile = ProfileRequest("v2",UUID.randomUUID().toString(),profile.revision,profile.preferences.copy(locale = "de"))
            val catalog = harness.api.catalog()
            val memory = object : LearnerStore {
                var cache = LearnerCache(profile = profile,catalog = catalog)
                var fail = false
                override fun read() = cache
                override fun write(value: LearnerCache) { check(!fail); cache = value }
            }
            var connected = true; var lose = false; var failReceipt = false
            val sent = mutableListOf<Attempt>()
            val api = object : LearnerApi by harness.api {
                override suspend fun submit(attempt: Attempt): Acknowledgment {
                    check(connected); sent.add(attempt); val result = harness.api.submit(attempt)
                    if(lose) {lose = false; error("Accepted response lost")}
                    if(failReceipt) {failReceipt = false; memory.fail = true}
                    return result
                }
                override suspend fun expose(event: ExposureEvent): ExposureAcknowledgment {check(connected); return harness.api.expose(event)}
                override suspend fun complete(sessionId: String, request: SessionCompletionRequest): SessionCompletionReceipt {check(connected); return harness.api.complete(sessionId,request)}
            }
            var repo = LearnerRepository(api,memory); repo.prepareReserve()
            val pack = requireNotNull(repo.state.preparedPack)
            connected = false; memory.fail = true
            assertTrue(runCatching {repo.startOffline(java.time.Instant.parse(pack.issuedAt))}.isFailure)
            assertNull(repo.state.practice); assertTrue(repo.state.consumedPreparedSessions.isEmpty())
            memory.fail = false; repo.startOffline(java.time.Instant.parse(pack.issuedAt))
            val first = requireNotNull(repo.state.practice).session!!.id
            for(index in 0..4) {
                val p = requireNotNull(repo.state.practice); val exercise = p.question.exercise
                val answer = pack.rubrics.single {it.exerciseId == exercise.id && it.exerciseRevision == exercise.revision}.acceptedAnswers.first()
                repo.draft(answer); if(index == 0) repo.hint(); repo.answer()
                assertEquals("correct",repo.state.practice!!.evaluation!!.outcome)
                repo.continuePractice()
                if(index == 2) repo = LearnerRepository(api,memory)
            }
            repo.finishPractice(); repo.discardPractice()
            assertEquals(6,repo.state.completedOffline.single().outbox.size)
            repo.startOffline(java.time.Instant.parse(pack.issuedAt).plusMillis(1)); repo.skip()
            val p = requireNotNull(repo.state.practice)
            val exercise = p.question.exercise
            repo.draft(pack.rubrics.single {it.exerciseId == exercise.id}.acceptedAnswers.first()); repo.hint(); repo.finishPractice(); repo.discardPractice()
            assertEquals(2,repo.state.completedOffline.size)
            assertEquals(2,repo.state.consumedPreparedSessions.size)
            assertTrue(runCatching {repo.startOffline(java.time.Instant.parse(pack.expiresAt))}.isFailure)
            assertTrue(sent.isEmpty())
            val report = ContentReportRequest("v2",UUID.randomUUID().toString(),repo.state.completedOffline.first().session!!.questions.first().id,repo.state.completedOffline.first().session!!.questions.first().exercise.revision,"ambiguous_prompt")
            val download = foundationSessionRequest().copy(questionCount = 5)
            memory.cache = repo.state.copy(pending = pendingProfile,contentReport = report,packRequest = download); repo = LearnerRepository(api,memory)
            val frozen = repo.state.completedOffline
            harness.command("expire-pack"); connected = true; lose = true
            assertTrue(runCatching {repo.syncSavedWork()}.isFailure)
            assertEquals(frozen,repo.state.completedOffline); assertEquals(1,sent.size)
            assertNull(repo.state.pending); assertFalse(repo.state.reportRecorded); assertEquals(download,repo.state.packRequest)
            failReceipt = true; assertTrue(runCatching {repo.syncSavedWork()}.isFailure)
            assertEquals(frozen,repo.state.completedOffline)
            memory.fail = false; repo = LearnerRepository(api,memory); repo.syncSavedWork()
            assertEquals(sent[0],sent[1]); assertEquals(sent[0],sent[2])
            val confirmed = repo.state.completedOffline.first()
            assertTrue(confirmed.outbox.all {it.delivered}); assertEquals(5,confirmed.completionReceipt!!.correctCount)
            val second = frozen.last().session!!.id; repo.syncOffline(second)
            assertEquals("partial",repo.state.completedOffline.last().completionReceipt!!.mode)
            assertEquals(frozen.last().draft,repo.state.completedOffline.last().draft)
            assertNull(repo.state.pending); assertTrue(repo.state.reportRecorded); assertEquals(report,repo.state.contentReport)
            assertNull(repo.state.packRequest); assertEquals(download.requestId,repo.state.preparedPack!!.packId)
            assertEquals(6,harness.command("stats").getValue("evidence").jsonPrimitive.int)
            val file = java.nio.file.Files.createTempDirectory("gm-offline-cache").resolve("learner.json").toFile()
            try { AtomicLearnerStore(file).write(repo.state); assertEquals(repo.state,AtomicLearnerStore(file).read()) }
            finally {file.delete();file.parentFile.delete()}
            Unit
        }
    }
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
        val port = receive().getValue("port").jsonPrimitive.int
        val api = LocalLearnerApi(port, FIXTURE_SUBJECT)
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
                repo.finishPractice()
                val receipt = requireNotNull(repo.state.practice!!.completionReceipt)
                assertEquals("full",receipt.mode); assertEquals(5,receipt.gradedCount); assertEquals(5,receipt.correctCount)
                assertEquals(receipt,api.complete(requireNotNull(summary.session).id,requireNotNull(repo.state.practice!!.completion)))
                repo = LearnerRepository(api,store)
                repo.finishPractice(); assertEquals(receipt,repo.state.practice!!.completionReceipt)
                repo.refresh()
                assertEquals(5, repo.state.targets.sumOf { it.exposureCount })
                assertTrue(repo.state.targets.none { it.state == "mastered" })
                assertEquals(5, harness.command("stats").getValue("evidence").jsonPrimitive.int)
                assertEquals(repo.state, store.read())
            } finally { directory.deleteRecursively() }
        }
    }
}
