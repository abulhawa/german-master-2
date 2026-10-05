package com.germanverbmaster.android.learner

internal data class ProviderCopy(val local: String,val signIn: String,val resume: String,val retry: String,val checking: String,val failed: String)
internal fun providerCopy(locale: String) = if(locale=="de") ProviderCopy(
    "Gespeicherte Übungen sind verfügbar. Vor dem Synchronisieren anmelden.","Anmelden oder Konto prüfen","Gespeicherte Übungen fortsetzen",
    "Kontoprüfung erneut versuchen","Dein Konto wird geprüft…","Melde dich bei diesem Konto an, um fortzufahren. Gespeicherte Lerndaten bleiben auf diesem Gerät."
) else ProviderCopy(
    "Saved practice is available. Sign in before syncing.","Sign in or verify account","Continue saved practice",
    "Retry account verification","Verifying your account…","Sign in to this account to continue. Saved learner work remains on this device."
)
