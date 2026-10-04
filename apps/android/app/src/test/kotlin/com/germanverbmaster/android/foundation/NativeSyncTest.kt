package com.germanverbmaster.android.foundation

import com.germanverbmaster.android.learner.*
import com.germanverbmaster.android.foundation.contract.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File
import java.nio.file.Files

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class NativeSyncTest {
    private val old = "00000000-0000-4000-8000-000000000001"
    private val fresh = "00000000-0000-4000-8000-000000000002"
    private val continuation = "00000000-0000-4000-8000-000000000003"
    private val time = "2026-10-04T10:00:00Z"
    private val profile = LearnerProfile("v2", 1, true, ProfilePreferences("en", "UTC", "B1", 5))
    private fun target(id: String, sequence: Int = 0) = ConfirmedTarget(id, "retained-evidence-v1", "new", sequence, 0, false, 0, sequence, emptyList(), false)
    private inner class Api : LearnerApi {
        val pulls = mutableListOf<String>()
        val pages = mutableListOf<String>()
        var writes = 0
        var syncRead: (String) -> SyncPage = { throw SyncCursorReset() }
        var targetRead: (String) -> TargetPage = {
            if (it.isEmpty()) TargetPage("v2", time, listOf(target(fresh)), continuation, fresh)
            else TargetPage("v2", time, emptyList(), "", fresh)
        }
        override suspend fun sync(cursor: String): SyncPage { pulls.add(cursor); return syncRead(cursor) }
        override suspend fun targets(cursor: String): TargetPage { pages.add(cursor); return targetRead(cursor) }
        override suspend fun profile() = profile
        override suspend fun catalog() = Catalog("v2", fresh, "unpublished_local_draft", emptyList(), emptyList())
        override suspend fun save(request: ProfileRequest): LearnerProfile { writes++; return profile }
        override suspend fun submit(attempt: Attempt): Acknowledgment { writes++; error("Unexpected write") }
        override suspend fun expose(event: ExposureEvent): ExposureAcknowledgment { writes++; error("Unexpected write") }
        override suspend fun session(request: SessionRequest): Session { writes++; error("Unexpected write") }
    }
    private class Store(var value: LearnerCache) : LearnerStore {
        var fail = false
        override fun read() = value
        override fun write(value: LearnerCache) { check(!fail); this.value = value }
    }
    private fun cached() = LearnerCache(profile = profile, targets = listOf(target(old)), generatedAt = time, syncCursor = old)

    @Test fun resetReplacesTargetsAtomicallyAndPreservesFrozenWorkThroughRestart() = runBlocking {
        val session = ContractReader.session(requireNotNull(javaClass.classLoader?.getResource("session.json")).readText())
        val answers = ContractReader.attempts(requireNotNull(javaClass.classLoader?.getResource("attempt-batch.json")).readText()).attempts
        for (kind in listOf("attempt", "skip", "draft", "session")) {
            val request = foundationSessionRequest()
            val pending = foundationAttempt(session, 1, answers[1].answer, true, old)
            val practice = if (kind == "session") NativePractice(request) else NativePractice(
                request, deviceId = old, session = session, index = 1, draft = pending.answer,
                assisted = true, order = listOf("saved-token"), graded = 1, correct = 1,
                pending = if (kind == "attempt") pending else null,
                exposure = if (kind == "skip") ExposureEvent(continuation, session.questions[1].id,
                    session.questions[1].exercise.revision, old, "skip", time) else null)
            val before = cached().copy(practice = practice, pending = ProfileRequest("v2", old, 1, profile.preferences))
            val file = File(Files.createTempDirectory("native-sync-$kind").toFile(), "cache.json")
            val store = AtomicLearnerStore(file); store.write(before)
            val api = Api()
            val repo = LearnerRepository(api, store)
            repo.refresh()
            assertEquals(listOf(target(fresh)), repo.state.targets) // Absent old target removed.
            assertEquals(fresh, repo.state.syncCursor)
            assertEquals(before.practice, repo.state.practice)
            assertEquals(before.pending, repo.state.pending)
            assertEquals(0, api.writes)
            val restarted = LearnerRepository(api, AtomicLearnerStore(file))
            assertEquals(repo.state, restarted.state)
            api.syncRead = { SyncPage("v2", emptyList(), it, false) }
            restarted.refresh()
            assertEquals(listOf(old, fresh), api.pulls)
            assertEquals(before.practice, restarted.state.practice)
            assertEquals(before.pending, restarted.state.pending)
        }
    }

    @Test fun failedContinuationPreservesSnapshotThenExplicitRetryStartsFresh() = runBlocking {
        val api = Api(); val store = Store(cached()); val repo = LearnerRepository(api, store)
        val good = api.targetRead
        api.targetRead = { if (it.isNotEmpty()) throw SyncCursorReset() else good(it) }
        assertTrue(runCatching { repo.refresh() }.isFailure)
        assertEquals(cached(), repo.state); assertEquals(cached(), store.value)
        assertEquals(listOf("", continuation), api.pages)
        assertEquals(listOf(old), api.pulls) // No automatic recovery from a page error.
        api.targetRead = good
        LearnerRepository(api, store).refresh()
        assertEquals(fresh, store.value.syncCursor)
        assertEquals(listOf(old, old), api.pulls)
        assertEquals(0, api.writes)
    }

    @Test fun failedSnapshotSaveRetainsCursorForRestartRetry() = runBlocking {
        val api = Api(); val store = Store(cached()); val repo = LearnerRepository(api, store)
        store.fail = true
        assertTrue(runCatching { repo.refresh() }.isFailure)
        assertEquals(cached(), repo.state); assertEquals(cached(), store.value)
        store.fail = false
        LearnerRepository(api, store).refresh()
        assertEquals(listOf(old, old), api.pulls)
        assertEquals(fresh, store.value.syncCursor)
    }

    @Test fun successfulDeltaPageSurvivesLaterResetSnapshotFailure() = runBlocking {
        val api = Api(); val store = Store(cached()); val repo = LearnerRepository(api, store)
        api.syncRead = { if (it == old) SyncPage("v2", listOf(TargetChange(2, "upsert", target(old, 2))), continuation, true) else throw SyncCursorReset() }
        val good = api.targetRead
        api.targetRead = { if (it.isNotEmpty()) error("offline") else good(it) }
        assertTrue(runCatching { repo.refresh() }.isFailure)
        val committed = cached().copy(targets = listOf(target(old, 2)), syncCursor = continuation)
        assertEquals(committed, repo.state); assertEquals(committed, store.value)
        api.targetRead = good
        LearnerRepository(api, store).refresh()
        assertEquals(listOf(old, continuation, continuation), api.pulls)
        assertEquals(listOf(target(fresh)), store.value.targets)
    }

    @Test fun failedDeltaSaveDoesNotAdvanceOrReadNextPage() = runBlocking {
        val api = Api(); val store = Store(cached()); val repo = LearnerRepository(api, store)
        api.syncRead = { SyncPage("v2", listOf(TargetChange(2, "upsert", target(old, 2))), continuation, true) }
        store.fail = true
        assertTrue(runCatching { repo.refresh() }.isFailure)
        assertEquals(cached(), store.value); assertEquals(cached(), repo.state)
        assertEquals(listOf(old), api.pulls); assertTrue(api.pages.isEmpty())
    }

    @Test fun ordinarySyncErrorNeverStartsSnapshotRecovery() = runBlocking {
        val api = Api(); val store = Store(cached()); val repo = LearnerRepository(api, store)
        api.syncRead = { error("offline or unauthorized") }
        assertTrue(runCatching { repo.refresh() }.isFailure)
        assertTrue(api.pages.isEmpty()); assertEquals(cached(), store.value)
    }

    @Test fun deltaPagesMergeNewTargetsWithoutRegressingNewerEvidence() = runBlocking {
        val api = Api(); val store = Store(cached().copy(targets = listOf(target(old, 4)))); val repo = LearnerRepository(api, store)
        api.syncRead = { cursor ->
            if (cursor == old) SyncPage("v2", listOf(TargetChange(5, "upsert", target(fresh, 5))), continuation, true)
            else SyncPage("v2", listOf(TargetChange(3, "upsert", target(old, 3))), fresh, false)
        }
        // A later full-snapshot failure exposes exactly the durably merged delta state.
        api.targetRead = { error("snapshot unavailable") }
        assertTrue(runCatching { repo.refresh() }.isFailure)
        assertEquals(listOf(target(old, 4), target(fresh, 5)), repo.state.targets)
        assertEquals(fresh, repo.state.syncCursor)
        assertEquals(repo.state, store.value)
        assertEquals(listOf(old, continuation), api.pulls)
    }

    @Test fun invalidSnapshotChainsNeverReplaceConfirmedData() = runBlocking {
        for (kind in listOf("repeat", "timestamp", "watermark", "duplicate")) {
            val api = Api(); val store = Store(cached()); val repo = LearnerRepository(api, store)
            val good = api.targetRead
            api.targetRead = { cursor ->
                if (cursor.isEmpty()) good(cursor) else when (kind) {
                    "repeat" -> good(cursor).copy(nextPageCursor = continuation)
                    "timestamp" -> good(cursor).copy(generatedAt = "2026-10-04T11:00:00Z")
                    "watermark" -> good(cursor).copy(syncCursor = old)
                    else -> good(cursor).copy(targets = listOf(target(fresh)))
                }
            }
            assertTrue(runCatching { repo.refresh() }.isFailure)
            assertEquals(cached(), store.value); assertEquals(cached(), repo.state)
        }
    }

    @Test fun repeatingSyncCursorFailsWithoutLoopOrPartialCommit() = runBlocking {
        val api = Api(); val store = Store(cached()); val repo = LearnerRepository(api, store)
        api.syncRead = { SyncPage("v2", emptyList(), it, true) }
        assertTrue(runCatching { repo.refresh() }.isFailure)
        assertEquals(listOf(old), api.pulls); assertEquals(cached(), store.value)
    }

    @Test fun versionOneCacheWithoutCursorLoadsAndUpgradesByFullSnapshot() = runBlocking {
        val file = File(Files.createTempDirectory("native-sync-legacy").toFile(), "cache.json")
        val legacy = cached().copy(syncCursor = null)
        file.writeText(ContractReader.json.encodeToString(legacy))
        assertFalse(file.readText().contains("syncCursor"))
        val api = Api(); val repo = LearnerRepository(api, AtomicLearnerStore(file))
        assertNull(repo.state.syncCursor)
        repo.refresh()
        assertTrue(api.pulls.isEmpty()); assertEquals(fresh, repo.state.syncCursor)
        assertEquals(repo.state, AtomicLearnerStore(file).read())
    }
}
