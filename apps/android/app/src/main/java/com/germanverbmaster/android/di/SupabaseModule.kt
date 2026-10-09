package com.germanverbmaster.android.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import javax.inject.Singleton

/**
 * Retain the binding for compiled imported legacy classes, but fail closed.
 * German Master 2.0 creates its dedicated Auth client outside this module.
 */
@Module
@InstallIn(SingletonComponent::class)
object SupabaseModule {
    @Provides
    @Singleton
    fun provideSupabaseClient(): SupabaseClient =
        error("Legacy Supabase provider is disabled in German Master 2.0")
}
