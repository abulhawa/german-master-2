package com.germanverbmaster.android.learner

import com.germanverbmaster.android.foundation.ContractReader
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.SignOutScope
import kotlinx.serialization.json.*
import java.net.URI
import java.util.Base64
import java.util.UUID

interface LearnerAuth {
    suspend fun awaitReady() {}
    fun accessToken(): String?
    suspend fun verifiedSubject(token: String): String
    suspend fun revoke()
}

/** An explicitly supplied dedicated v2 SDK client; no legacy singleton or credential file. */
class SupabaseLearnerAuth(private val client: SupabaseClient) : LearnerAuth {
    override suspend fun awaitReady() { client.auth.awaitInitialization() }
    override fun accessToken() = client.auth.currentSessionOrNull()?.accessToken
    override suspend fun verifiedSubject(token: String): String {
        val user = client.auth.retrieveUser(token)
        check(user.isAnonymous != true) { "Anonymous learner unavailable" }
        return user.id
    }
    override suspend fun revoke() { client.auth.signOut(SignOutScope.LOCAL) }
}

/** JWT parsing is a precondition, never signature verification or learning authority. */
class VerifiedLearnerProvider(private val auth: LearnerAuth, private val projectRef: String,
    private val now: () -> Long = System::currentTimeMillis) {
    @Volatile private var active: LearnerIdentity? = null
    @Volatile private var sessionId: String? = null
    private var generation = 0L
    private var operation = 0L
    init { require(Regex("[a-z]{20}").matches(projectRef)) }
    private data class Credential(val token: String, val subject: String, val sessionId: String, val expiresAt: Long)
    @Synchronized fun invalidate() { operation++; active = null; sessionId = null }
    private fun credential(): Credential {
        val token = requireNotNull(auth.accessToken()) { "Sign in before syncing" }
        check(token.length <= 16384 && token.split('.').size == 3)
        val claims = ContractReader.json.parseToJsonElement(String(Base64.getUrlDecoder().decode(token.split('.')[1]), Charsets.UTF_8)).jsonObject
        fun text(key: String) = requireNotNull(claims[key]).jsonPrimitive.content
        check(text("iss") == "https://$projectRef.supabase.co/auth/v1" && text("aud") == "authenticated" && text("role") == "authenticated")
        val subject = text("sub"); val session = text("session_id")
        check(UUID.fromString(subject).toString() == subject && UUID.fromString(session).toString() == session)
        val expiry = Math.multiplyExact(requireNotNull(claims["exp"]).jsonPrimitive.long, 1000)
        check(expiry > now()) { "Sign in before syncing" }
        return Credential(token,subject,session,expiry)
    }
    private suspend fun verified(): Credential {
        auth.awaitReady()
        val value = credential()
        check(auth.verifiedSubject(value.token) == value.subject && value.expiresAt > now()) { "Account verification unavailable" }
        // A switch while the online check was pending must never become a binding.
        val current = credential()
        check(current.subject == value.subject && current.sessionId == value.sessionId) { "Account changed" }
        return value
    }
    suspend fun bind(): LearnerAccount {
        val ticket = synchronized(this) { invalidate(); operation }
        val value = verified()
        val identity = synchronized(this) {
            check(ticket == operation) { "Sign-in changed" }
            LearnerIdentity(value.subject, ++generation).also { active = it; sessionId = value.sessionId }
        }
        return LearnerAccount(identity) {
            val current = runCatching { credential() }.getOrNull()
            active?.takeIf { current?.subject == it.subject && current.sessionId == sessionId }
        }
    }
    fun api(account: LearnerAccount, origin: String): LearnerApi {
        val url = URI(origin)
        require(url.scheme == "https" && url.host != null && url.rawUserInfo == null && url.rawQuery == null && url.rawFragment == null && url.path in listOf("", "/"))
        return HttpLearnerApi(origin.trimEnd('/'), account.identity.subject, {
            account.assertCurrent()
            val value = verified()
            account.assertCurrent()
            check(value.subject == account.identity.subject)
            value.token
        }, { account.assertCurrent() }, { invalidate() })
    }
    suspend fun revoke(account: LearnerAccount) {
        account.assertCurrent(); verified(); account.assertCurrent()
        auth.revoke() // Failures propagate; never claim a failed revocation succeeded.
        invalidate()
    }
    suspend fun repository(directory: java.io.File, origin: String): LearnerRepository {
        val account = bind()
        return LearnerRepository(api(account,origin),account.store(directory),account) { revoke(account) }
    }
}
