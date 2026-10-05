package com.germanverbmaster.android.foundation

import com.germanverbmaster.android.learner.*
import com.germanverbmaster.android.foundation.contract.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class NativeIdentityDeletionTest {
    private val subject="00000000-0000-4000-8000-000000000020"
    @Test fun receiptLossRestartSessionLossAndCleanupFailureKeepTheBarrier() = runBlocking {
        var current: LearnerIdentity?=LearnerIdentity(subject,1)
        val account=LearnerAccount(requireNotNull(current)) { current }
        val store=object:LearnerStore {
            var cache=LearnerCache(subjectId=subject)
            override fun read()=cache
            override fun write(value:LearnerCache) {cache=value}
        }
        var frozen:IdentityDeletionRecovery?=null;var begins=0;var recovers=0;var clears=0;var failCleanup=true
        val transport=object:IdentityDeletionTransport {
            override suspend fun begin(request:IdentityDeletionRecovery,proof:IdentityDeletionProof):IdentityDeletionResponse {
                begins++;frozen=request;assertEquals(request,store.cache.identityDeletion?.request);error("response lost")
            }
            override suspend fun recover(request:IdentityDeletionRecovery):IdentityDeletionResponse {
                recovers++;assertEquals(frozen,request);return IdentityDeletionCompleted("v2",request.requestId,"2026-10-05T20:00:00Z")
            }
        }
        fun repository()=LearnerRepository(object:LearnerApi {
            override suspend fun profile():LearnerProfile=error("unexpected upload")
            override suspend fun save(request:ProfileRequest):LearnerProfile=error("unexpected upload")
            override suspend fun targets(cursor:String):TargetPage=error("unexpected read")
            override suspend fun sync(cursor:String):SyncPage=error("unexpected read")
            override suspend fun catalog():Catalog=error("unexpected read")
        },store,account).also {
            it.identityDeletionTransport=transport
            it.clearDeletedIdentity={clears++;check(!failCleanup)}
        }
        val first=repository()
        assertTrue(runCatching { first.deleteIdentity(IdentityDeletionProof("learner@example.com","transient")) }.isFailure)
        assertEquals(43,frozen?.recoveryCapability?.length)
        assertTrue(runCatching {first.refresh()}.isFailure)
        current=null
        val second=repository();assertTrue(runCatching {second.deleteIdentity()}.isFailure)
        assertTrue(store.cache.identityDeletion?.receipt is IdentityDeletionCompleted)
        failCleanup=false;repository().deleteIdentity()
        assertTrue(store.cache.identityDeletion?.complete==true)
        assertEquals(1,begins);assertEquals(1,recovers);assertEquals(2,clears)
    }
}
