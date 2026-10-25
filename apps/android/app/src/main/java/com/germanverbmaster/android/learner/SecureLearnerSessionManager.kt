package com.germanverbmaster.android.learner

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.AtomicFile
import com.germanverbmaster.android.foundation.ContractReader
import io.github.jan.supabase.auth.CodeVerifierCache
import io.github.jan.supabase.auth.FlowType
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.SessionManager
import io.github.jan.supabase.auth.user.UserSession
import io.github.jan.supabase.createSupabaseClient
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import java.io.File
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/** Dedicated app-private encrypted session. Learner caches never contain tokens. */
class SecureLearnerSessionManager(context: Context, projectRef: String) : SessionManager {
    private val alias = "gm-v2-auth-$projectRef"
    private val file = AtomicFile(File(context.noBackupFilesDir,"$alias.session"))
    init { require(Regex("[a-z]{20}").matches(projectRef)) }
    @Synchronized private fun key(create: Boolean = true): SecretKey {
        val store = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        (store.getKey(alias,null) as? SecretKey)?.let { return it }
        check(create) { "Saved sign-in key unavailable" }
        return KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES,"AndroidKeyStore").apply {
            init(KeyGenParameterSpec.Builder(alias,KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM).setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE).build())
        }.generateKey()
    }
    override suspend fun saveSession(session: UserSession) {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE,key())
        val payload = cipher.doFinal(ContractReader.json.encodeToString(session).toByteArray(Charsets.UTF_8))
        val stream = file.startWrite()
        try { stream.write(byteArrayOf(1,cipher.iv.size.toByte()));stream.write(cipher.iv);stream.write(payload);file.finishWrite(stream) }
        catch(error: Exception) {file.failWrite(stream);throw error}
    }
    override suspend fun loadSessionOrNull(): UserSession? {
        val bytes = try {file.openRead().use {it.readBytes()}} catch(_: java.io.FileNotFoundException) {return null}
        check(bytes.size >= 30 && bytes[0] == 1.toByte() && bytes[1] == 12.toByte()) { "Saved sign-in unavailable" }
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE,key(false),GCMParameterSpec(128,bytes.copyOfRange(2,14)))
        return ContractReader.json.decodeFromString(String(cipher.doFinal(bytes.copyOfRange(14,bytes.size)),Charsets.UTF_8))
    }
    override suspend fun loadSession(): UserSession = requireNotNull(loadSessionOrNull()) { "Sign in required" }
    override suspend fun deleteSession() { file.delete() }
}

fun createLearnerAuthClient(context: Context, projectRef: String, publishableKey: String, oauthOnly: Boolean = false) = run {
    require(Regex("[a-z]{20}").matches(projectRef) && publishableKey.startsWith("sb_publishable_"))
    createSupabaseClient("https://$projectRef.supabase.co",publishableKey) {
        install(Auth) {
            sessionManager = SecureLearnerSessionManager(context,projectRef)
            flowType = if(oauthOnly) FlowType.PKCE else FlowType.IMPLICIT
            if(oauthOnly) {
                autoLoadFromStorage=false
                autoSaveToStorage=false
                alwaysAutoRefresh=false
                enableLifecycleCallbacks=false
            }
            codeVerifierCache = SecureLearnerCodeVerifierCache(context,projectRef)
        }
    }
}

/** PKCE survives process recreation, remains outside backups and expires after ten minutes. */
class SecureLearnerCodeVerifierCache(context: Context, projectRef: String) : CodeVerifierCache {
    private val alias = "gm-v2-pkce-$projectRef"
    private val file = AtomicFile(File(context.noBackupFilesDir,"$alias.verifier"))
    init { require(Regex("[a-z]{20}").matches(projectRef)) }
    private fun key(create: Boolean): SecretKey {
        val store = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        (store.getKey(alias,null) as? SecretKey)?.let { return it }
        check(create) { "Sign-in request unavailable" }
        return KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES,"AndroidKeyStore").apply {
            init(KeyGenParameterSpec.Builder(alias,KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM).setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE).build())
        }.generateKey()
    }
    override suspend fun saveCodeVerifier(codeVerifier: String) {
        val cipher=Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE,key(true))
        val payload=cipher.doFinal("${System.currentTimeMillis()}\n$codeVerifier".toByteArray(Charsets.UTF_8))
        val stream=file.startWrite()
        try { stream.write(cipher.iv);stream.write(payload);file.finishWrite(stream) }
        catch(error:Exception){file.failWrite(stream);throw error}
    }
    override suspend fun loadCodeVerifier(): String? {
        val bytes=try {file.openRead().use {it.readBytes()}} catch(_:java.io.FileNotFoundException){return null}
        check(bytes.size>=29) { "Sign-in request unavailable" }
        val cipher=Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE,key(false),GCMParameterSpec(128,bytes.copyOfRange(0,12)))
        val text=String(cipher.doFinal(bytes.copyOfRange(12,bytes.size)),Charsets.UTF_8)
        val age=System.currentTimeMillis()-text.substringBefore('\n').toLong()
        if(age !in 0..600_000) {deleteCodeVerifier();return null}
        return text.substringAfter('\n')
    }
    override suspend fun deleteCodeVerifier() {file.delete()}
}
