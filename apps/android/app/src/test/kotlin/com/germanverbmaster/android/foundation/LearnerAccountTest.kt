package com.germanverbmaster.android.foundation

import com.germanverbmaster.android.learner.*
import com.germanverbmaster.android.foundation.contract.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.nio.file.Files
import java.util.UUID

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class LearnerAccountTest {
    @Test fun expirySwitchAndStaleReceiptKeepEachSubjectsQueueAndDraftUntilExplicitReauthentication() = runBlocking {
        val a = UUID.randomUUID().toString(); val b = UUID.randomUUID().toString()
        var current: LearnerIdentity? = LearnerIdentity(a,0)
        val accountA = LearnerAccount(requireNotNull(current)) { current }
        val accountB = LearnerAccount(LearnerIdentity(b,1)) { current }
        val directory = Files.createTempDirectory("learner-accounts").toFile()
        val storeA = accountA.store(directory); val storeB = accountB.store(directory)
        val session = ContractReader.session(requireNotNull(javaClass.classLoader?.getResource("session.json")).readText())
        val preferences = ProfilePreferences("en","UTC","B1",5)
        val profile = LearnerProfile("v2",0,true,preferences)
        val pending = ProfileRequest("v2",UUID.randomUUID().toString(),0,preferences.copy(locale="de"))
        val report = ContentReportRequest("v2",UUID.randomUUID().toString(),session.questions[0].id,session.questions[0].exercise.revision,"other")
        val practice = NativePractice(foundationSessionRequest(),session=session,draft=AnswerShortAnswer("saved draft"),assisted=true)
        val frozen = LearnerCache(subjectId=a,profile=profile,pending=pending,practice=practice,contentReport=report)
        storeA.write(frozen)
        val writes = mutableListOf<ProfileRequest>(); val reports = mutableListOf<ContentReportRequest>(); var afterSave: () -> Unit = {}
        val api = object : LearnerApi {
            override suspend fun profile() = profile.copy(revision=1,preferences=pending.preferences)
            override suspend fun save(request: ProfileRequest): LearnerProfile { writes.add(request); afterSave(); return profile() }
            override suspend fun report(request: ContentReportRequest): ContentReportReceipt { reports.add(request); return ContentReportReceipt("v2",request.reportId,"recorded") }
            override suspend fun targets(cursor: String) = error("unused")
            override suspend fun sync(cursor: String) = error("unused")
            override suspend fun catalog() = error("unused")
        }
        try {
            val repoA = LearnerRepository(api,storeA,accountA)
            current = null; assertTrue(runCatching {repoA.syncSavedWork()}.isFailure); assertTrue(writes.isEmpty())
            current = LearnerIdentity(b,1)
            val repoB = LearnerRepository(api,storeB,accountB)
            assertNull(repoB.state.pending); assertNull(repoB.state.practice); assertEquals(b,repoB.state.subjectId)
            assertTrue(runCatching {repoA.syncSavedWork()}.isFailure); assertTrue(writes.isEmpty())
            assertTrue(runCatching {LearnerRepository(api,storeA,accountB)}.isFailure)
            assertEquals(frozen,storeA.read())
            current = LearnerIdentity(a,0); afterSave = {current = LearnerIdentity(b,1)}
            assertTrue(runCatching {repoA.syncSavedWork()}.isFailure)
            assertEquals(frozen,repoA.state); assertTrue(reports.isEmpty())
            current = LearnerIdentity(a,2); afterSave = {}
            assertTrue(runCatching {repoA.syncSavedWork()}.isFailure); assertEquals(1,writes.size)
            val renewed = LearnerAccount(requireNotNull(current)) {current}
            val reopened = LearnerRepository(api,renewed.store(directory),renewed); reopened.syncSavedWork()
            assertEquals(listOf(pending,pending),writes); assertEquals(listOf(report),reports)
            assertNull(reopened.state.pending); assertTrue(reopened.state.reportRecorded); assertEquals(practice,reopened.state.practice)
            assertNull(storeB.read().pending); assertNull(storeB.read().practice)
        } finally { directory.listFiles()?.forEach {it.delete()}; directory.delete() }
    }
}
