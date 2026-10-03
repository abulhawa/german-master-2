package com.germanverbmaster.android.di

import android.content.Context
import androidx.core.content.edit
import com.germanverbmaster.android.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.SessionManager
import io.github.jan.supabase.auth.user.UserSession
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SupabaseModule {

    @Provides
    @Singleton
    fun provideSupabaseClient(@ApplicationContext context: Context): SupabaseClient {
        return createSupabaseClient(
            supabaseUrl = BuildConfig.SUPABASE_URL,
            supabaseKey = BuildConfig.SUPABASE_ANON_KEY,
        ) {
            install(Postgrest)
            install(Auth) {
                sessionManager = object : SessionManager {
                    private val sharedPrefs = context.getSharedPreferences("supabase_session", Context.MODE_PRIVATE)
                    private val key = "session"
                    private val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }

                    override suspend fun saveSession(session: UserSession) {
                        sharedPrefs.edit(commit = false) {
                            putString(key, json.encodeToString(session))
                        }
                    }

                    override suspend fun loadSession(): UserSession {
                        val sessionStr = sharedPrefs.getString(key, null) ?: return UserSession(
                            accessToken = "",
                            refreshToken = "",
                            expiresIn = 0,
                            tokenType = "",
                            user = null
                        )
                        return try {
                            json.decodeFromString(sessionStr)
                        } catch (_: Exception) {
                            UserSession(
                                accessToken = "",
                                refreshToken = "",
                                expiresIn = 0,
                                tokenType = "",
                                user = null
                            )
                        }
                    }

                    override suspend fun loadSessionOrNull(): UserSession? {
                        val sessionStr = sharedPrefs.getString(key, null) ?: return null
                        return try {
                            json.decodeFromString<UserSession>(sessionStr)
                        } catch (_: Exception) {
                            null
                        }
                    }

                    override suspend fun deleteSession() {
                        sharedPrefs.edit().remove(key).apply()
                    }
                }
            }
        }
    }
}
