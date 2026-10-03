package com.germanverbmaster.android.data.repository

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.germanverbmaster.android.BuildConfig
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.IDToken
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val client: SupabaseClient
) {
    /** Get the current user ID if logged in */
    val currentUserId: String?
        get() = client.auth.currentSessionOrNull()?.user?.id

    val currentUserEmail: String?
        get() = client.auth.currentSessionOrNull()?.user?.email

    val sessionStatus = client.auth.sessionStatus

    suspend fun signOut() {
        try {
            client.auth.signOut()
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error signing out", e)
        }
    }

    suspend fun signInWithGoogle(context: Context): Result<Unit> {
        return try {
            val credentialManager = CredentialManager.create(context)
            
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(BuildConfig.GOOGLE_WEB_CLIENT_ID)
                .setAutoSelectEnabled(true)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = try {
                credentialManager.getCredential(context, request)
            } catch (e: NoCredentialException) {
                Log.e("AuthRepository", "No credentials found", e)
                return Result.failure(e)
            } catch (e: GetCredentialException) {
                Log.e("AuthRepository", "Failed to get credential", e)
                return Result.failure(e)
            }

            val credential = result.credential

            Log.d("AuthRepository", "Received credential type: ${credential.type}")

            val googleIdTokenCredential = try {
                when (credential) {
                    is GoogleIdTokenCredential -> credential
                    is CustomCredential if credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL -> {
                        GoogleIdTokenCredential.createFrom(credential.data)
                    }
                    else -> null
                }
            } catch (e: Exception) {
                Log.e("AuthRepository", "Failed to parse Google ID Token", e)
                null
            }

            if (googleIdTokenCredential != null) {
                val token = googleIdTokenCredential.idToken
                Log.d("AuthRepository", "Signing in to Supabase with ID Token")
                client.auth.signInWith(IDToken) {
                    idToken = token
                    provider = Google
                }
                Result.success(Unit)
            } else {
                val errorMsg = "Received invalid credential type: ${credential.type}"
                Log.e("AuthRepository", errorMsg)
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
