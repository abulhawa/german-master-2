package com.germanverbmaster.android.foundation

import com.germanverbmaster.android.learner.*
import com.germanverbmaster.android.foundation.contract.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File
import java.nio.file.Files

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class LearnerRepositoryTest {
    @Test fun deletionFreezeAndReceiptSaveFailuresPreserveWorkAndFrozenReplay() = runBlocking {
        val identity=LearnerIdentity(FIXTURE_SUBJECT,0)
        val account=LearnerAccount(identity) {identity}
        val base=Api();val store=Store(LearnerCache(subjectId=FIXTURE_SUBJECT,profile=base.current))
        val requests=mutableListOf<PrivacyDeleteRequest>()
        var failReceipt=true
        val api=object : LearnerApi by base {
            override suspend fun deleteLearner(request:PrivacyDeleteRequest):PrivacyDeleteReceipt {
                requests.add(request);store.fail=failReceipt
                return PrivacyDeleteReceipt("v2",request.requestId,FIXTURE_SUBJECT,"deleted","2026-10-05T12:00:00Z")
            }
        }
        val repo=LearnerRepository(api,store,account)
        store.fail=true;assertTrue(runCatching {repo.deleteLearner()}.isFailure);assertTrue(requests.isEmpty());assertNull(repo.state.deletion)
        store.fail=false;assertTrue(runCatching {repo.deleteLearner()}.isFailure);assertNotNull(repo.state.profile);assertNotNull(repo.state.deletion);assertNull(repo.state.deletionReceipt)
        store.fail=false;failReceipt=false
        val reopened=LearnerRepository(api,store,account);reopened.deleteLearner()
        assertEquals(requests[0],requests[1]);assertNull(reopened.state.profile);assertNotNull(reopened.state.deletionReceipt)
    }
    private val prefs = ProfilePreferences("en", "Europe/Berlin", "B1", 5)
    private val id = "00000000-0000-4000-8000-000000000001"
    private val page = TargetPage("v2", "2026-10-04T10:00:00Z", emptyList(), "", id)
    private class Store(var value: LearnerCache) : LearnerStore {
        var fail = false
        override fun read() = value
        override fun write(value: LearnerCache) { check(!fail); this.value = value }
    }
    private inner class Api : LearnerApi {
        var current = LearnerProfile("v2", 0, false, prefs)
        val writes = mutableListOf<ProfileRequest>()
        var loseResponse = false
        var failPage = false
        var paginated = false
        override suspend fun profile() = current
        override suspend fun sync(cursor: String) = SyncPage("v2", emptyList(), cursor, false)
        override suspend fun catalog() = Catalog("v2", id, "unpublished_local_draft", emptyList(), emptyList())
        override suspend fun targets(cursor: String): TargetPage {
            if (failPage && cursor.isNotEmpty()) error("offline")
            return if (paginated && cursor.isEmpty()) page.copy(nextPageCursor = id) else page
        }
        override suspend fun save(request: ProfileRequest): LearnerProfile {
            writes.add(request)
            if (writes.size == 1) current = LearnerProfile("v2", 1, true, request.preferences)
            if (loseResponse) { loseResponse = false; error("accepted response lost") }
            return current
        }
    }
    @Test fun lostResponseRestartRetriesExactFrozenWriteAndReadsLatest() = runBlocking {
        val api = Api()
        val file = File(Files.createTempDirectory("learner-retry").toFile(), "cache.json")
        val repo = LearnerRepository(api, AtomicLearnerStore(file))
        repo.refresh()
        api.loseResponse = true
        assertTrue(runCatching { repo.save(prefs.copy(locale = "de")) }.isFailure)
        assertNotNull(repo.state.pending)
        assertFalse(repo.state.profile!!.setupCompleted)
        val restarted = LearnerRepository(api, AtomicLearnerStore(file))
        assertEquals(repo.state.pending, restarted.state.pending)
        api.current = api.current.copy(revision = 2)
        restarted.retry()
        assertEquals(api.writes[0], api.writes[1])
        assertNull(restarted.state.pending)
        assertEquals(2, restarted.state.profile!!.revision)
        assertEquals(restarted.state, AtomicLearnerStore(file).read())
    }
    @Test fun failedFreezeMakesNoHttpWrite() = runBlocking {
        val api = Api(); val store = Store(LearnerCache(profile = api.current)); val repo = LearnerRepository(api, store)
        store.fail = true
        assertTrue(runCatching { repo.save(prefs) }.isFailure)
        assertTrue(api.writes.isEmpty()); assertNull(repo.state.pending)
    }
    @Test fun failedAcknowledgmentSaveKeepsPendingForRetry() = runBlocking {
        val api = Api(); val store = Store(LearnerCache(profile = api.current)); val repo = LearnerRepository(api, store)
        api.loseResponse = true
        runCatching { repo.save(prefs) }
        val frozen = repo.state.pending
        store.fail = true
        assertTrue(runCatching { repo.retry() }.isFailure)
        assertEquals(frozen, repo.state.pending)
        store.fail = false
        repo.retry()
        assertNull(repo.state.pending)
        assertTrue(api.writes.all { it == frozen })
    }
    @Test fun incompleteSnapshotDoesNotReplaceCacheOrPending() = runBlocking {
        val api = Api(); val store = Store(LearnerCache(profile = api.current)); val repo = LearnerRepository(api, store)
        repo.refresh(); api.loseResponse = true; runCatching { repo.save(prefs) }
        val before = repo.state
        api.paginated = true; api.failPage = true
        assertTrue(runCatching { repo.refresh() }.isFailure)
        assertEquals(before, repo.state); assertEquals(before, store.value)
        api.failPage = false
        repo.refresh()
        assertEquals(before.pending, repo.state.pending)
        repo.reloadProfile(); assertNull(repo.state.pending)
    }
    @Test fun corruptCacheIsPreservedAndInvalidTimezoneDoesNotUpload() = runBlocking {
        val file = File(Files.createTempDirectory("learner-corrupt").toFile(), "cache.json")
        file.writeText("unreadable")
        assertTrue(runCatching { AtomicLearnerStore(file).read() }.isFailure)
        assertEquals("unreadable", file.readText())
        val api = Api(); val repo = LearnerRepository(api, Store(LearnerCache(profile = api.current)))
        assertTrue(runCatching { repo.save(prefs.copy(timezone = "invalid")) }.isFailure)
        assertTrue(api.writes.isEmpty())
    }
}
