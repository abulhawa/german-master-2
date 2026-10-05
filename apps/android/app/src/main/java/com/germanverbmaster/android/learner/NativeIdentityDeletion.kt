package com.germanverbmaster.android.learner

import com.germanverbmaster.android.foundation.ContractReader
import com.germanverbmaster.android.foundation.contract.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import java.net.HttpURLConnection
import java.net.URI

@Serializable
data class NativeIdentityDeletion(val request: IdentityDeletionRecovery, val receipt: IdentityDeletionResponse? = null, val complete: Boolean = false)

interface IdentityDeletionTransport {
    suspend fun begin(request: IdentityDeletionRecovery, proof: IdentityDeletionProof): IdentityDeletionResponse
    suspend fun recover(request: IdentityDeletionRecovery): IdentityDeletionResponse
}

/** Capability recovery is session-free and read-only. Neither proof nor tokens are persisted. */
internal class HttpIdentityDeletion(private val origin: String, private val account: LearnerAccount,
    private val credential: suspend () -> String,
    private val open: (URI) -> HttpURLConnection = {it.toURL().openConnection() as HttpURLConnection}) : IdentityDeletionTransport {
    init {
        val uri = URI(origin)
        require(uri.scheme == "https" && uri.host != null && uri.rawUserInfo == null && uri.rawQuery == null && uri.rawFragment == null && uri.path in listOf("", "/"))
    }
    override suspend fun begin(request: IdentityDeletionRecovery, proof: IdentityDeletionProof): IdentityDeletionResponse {
        account.assertCurrent(); val token = credential(); account.assertCurrent()
        return send("begin",ContractReader.json.encodeToString(IdentityDeletionBegin("v2",request.requestId,request.recoveryCapability,"delete_identity",proof)),token)
    }
    override suspend fun recover(request: IdentityDeletionRecovery) = send("status",ContractReader.json.encodeToString(request),null)
    private suspend fun send(route: String, body: String, token: String?): IdentityDeletionResponse = withContext(Dispatchers.IO) {
        val connection = open(URI("${origin.trimEnd('/')}/v2/me/identity-deletion:$route"))
        try {
            connection.instanceFollowRedirects=false;connection.connectTimeout=10000;connection.readTimeout=15000
            connection.requestMethod="POST";connection.doOutput=true
            connection.setRequestProperty("Content-Type","application/json");connection.setRequestProperty("Cache-Control","no-store")
            token?.let { connection.setRequestProperty("Authorization","Bearer $it");connection.setRequestProperty("X-Learner-Subject",account.identity.subject) }
            connection.outputStream.use { it.write(body.toByteArray(Charsets.UTF_8)) }
            check(connection.responseCode==200) { "Identity deletion unavailable" }
            val raw=ContractReader.json.parseToJsonElement(connection.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() })
            ContractShape.checkIdentityDeletionResponse(raw)
            ContractReader.json.decodeFromJsonElement(IdentityDeletionResponse.serializer(),raw)
        } finally { connection.disconnect() }
    }
}
