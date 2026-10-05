package com.germanverbmaster.android.foundation

import com.germanverbmaster.android.learner.*
import com.germanverbmaster.android.foundation.contract.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import java.util.Base64

class VerifiedLearnerProviderTest {
    private val project = "zgmyrpzwgtydwlzponih"
    private val owner = "00000000-0000-4000-8000-000000000020"
    private inner class Auth : LearnerAuth {
        var subject = owner
        var session = owner
        var offline = false
        var revocations = 0
        override fun accessToken() = "e30." + Base64.getUrlEncoder().withoutPadding().encodeToString(
            """{"iss":"https://$project.supabase.co/auth/v1","aud":"authenticated","role":"authenticated","sub":"$subject","session_id":"$session","exp":10}""".toByteArray()) + ".c2ln"
        override suspend fun verifiedSubject(token: String): String { check(!offline); return subject }
        override suspend fun revoke() { revocations++; check(!offline) }
    }
    @Test fun expirySwitchAndRenewalInvalidateCapturedBindings() = runBlocking {
        val auth = Auth(); var time = 1000L
        val provider = VerifiedLearnerProvider(auth,project) { time }
        val first = provider.bind(); first.assertCurrent()
        auth.subject = "00000000-0000-4000-8000-000000000021"
        assertTrue(runCatching { first.assertCurrent() }.isFailure)
        auth.subject = owner; auth.session = "00000000-0000-4000-8000-000000000022"
        assertTrue(runCatching { first.assertCurrent() }.isFailure)
        val renewed = provider.bind(); assertTrue(runCatching { first.assertCurrent() }.isFailure)
        renewed.assertCurrent(); time = 10000L
        assertTrue(runCatching { renewed.assertCurrent() }.isFailure)
        assertTrue(runCatching { provider.bind() }.isFailure)
    }
    @Test fun onlineFailureAndRevocationFailureNeverClaimSuccess() = runBlocking {
        val auth = Auth(); val provider = VerifiedLearnerProvider(auth,project) { 1000L }
        auth.offline = true; assertTrue(runCatching { provider.bind() }.isFailure)
        auth.offline = false; val account = provider.bind()
        val store = object : LearnerStore {
            var cache = LearnerCache(subjectId = owner)
            override fun read() = cache
            override fun write(value: LearnerCache) { cache = value }
        }
        val api = object : LearnerApi {
            override suspend fun profile(): LearnerProfile = error("unused")
            override suspend fun save(request: ProfileRequest): LearnerProfile = error("unused")
            override suspend fun targets(cursor: String): TargetPage = error("unused")
            override suspend fun sync(cursor: String): SyncPage = error("unused")
            override suspend fun catalog(): Catalog = error("unused")
        }
        var failRevoke = true
        val revoke: suspend () -> Unit = { if(failRevoke) error("offline") else provider.revoke(account) }
        val repo = LearnerRepository(api,store,account,revoke)
        assertTrue(runCatching { repo.signOut(true) }.isFailure)
        assertTrue(store.cache.authRevocationPending); assertTrue(store.cache.signedOut)
        assertTrue(runCatching { repo.resumeLocalFixture() }.isFailure)
        failRevoke = false
        LearnerRepository(api,store,account,revoke).signOut(false)
        assertFalse(store.cache.authRevocationPending); assertEquals(1,auth.revocations)
        assertTrue(runCatching { account.assertCurrent() }.isFailure)
    }
    @Test fun rejectsInsecureOriginsAndWrongProjectBeforeAnyLearningRequest() = runBlocking {
        val auth = Auth(); val provider = VerifiedLearnerProvider(auth,project) { 1000L }; val account = provider.bind()
        for(origin in listOf("http://api.example","https://name@api.example","https://api.example/path","https://api.example?key=value")) {
            assertTrue(runCatching { provider.api(account,origin) }.isFailure)
        }
        assertTrue(runCatching { VerifiedLearnerProvider(auth,"aaaaaaaaaaaaaaaaaaaa") { 1000L }.bind() }.isFailure)
    }
}
