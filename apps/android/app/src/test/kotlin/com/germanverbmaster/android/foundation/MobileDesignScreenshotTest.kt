package com.germanverbmaster.android.foundation

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.germanverbmaster.android.learner.*
import com.germanverbmaster.android.foundation.contract.*
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.io.File

/** Focused evidence for the owner-requested mobile redesign; never store artwork. */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w393dp-h852dp-notnight-420dpi")
class MobileDesignScreenshotTest {
    @get:Rule val compose = createComposeRule()
    @get:Rule val folder = TemporaryFolder()
    private fun mount(german: Boolean = false, largeText: Boolean = false, practice: Boolean = false) {
        val path = File(folder.root, "practice")
        if(practice) {
            val context=androidx.test.core.app.ApplicationProvider.getApplicationContext<android.content.Context>()
            val starter=ContractReader.json.decodeFromString<GuestStarterContent>(context.assets.open("starter.json").bufferedReader().use {it.readText()})
            NativeGuestStore(path).write(NativeGuestState(started=true,session=starter.session))
        }
        compose.setContent {
            CompositionLocalProvider(LocalDensity provides Density(LocalDensity.current.density, if(largeText) 1.6f else 1f)) {
                FoundationTheme { NativeGuestEntry(path, german, {}, {}) }
            }
        }
    }
    private fun capture(name: String) { compose.onRoot().captureRoboImage("build/outputs/mobile-design/$name.png") }

    @Test fun homeLight() { mount(); capture("home-light") }
    @Test @Config(qualifiers = "w393dp-h852dp-night-420dpi")
    fun homeDark() { mount(); capture("home-dark") }
    @Test @Config(qualifiers = "w320dp-h720dp-notnight-320dpi")
    fun narrowGermanHome() { mount(true); capture("home-german-320") }
    @Test fun selectedAnswer() {
        mount(practice=true); compose.onNodeWithText("Continue session").performScrollTo().performClick()
        compose.onNodeWithText("Berufe").performScrollTo().performClick()
        capture("practice-selected")
    }
    @Test fun incorrectFeedback() {
        mount(practice=true); compose.onNodeWithText("Continue session").performScrollTo().performClick()
        compose.onNodeWithText("Berufen").performScrollTo().performClick()
        compose.onNodeWithText("Check answer").performScrollTo().performClick()
        compose.onNodeWithText("Not quite").performScrollTo().assertExists()
        capture("practice-incorrect")
    }
    @Test fun topics() {
        mount(); compose.onNode(hasText("Topics") and hasClickAction()).performClick(); capture("topics")
    }
    @Test fun accountGate() {
        mount(); compose.onNode(hasText("Settings") and hasClickAction()).performClick(); capture("account")
    }
    @Test fun explicitAppearanceChoices() {
        val context=androidx.test.core.app.ApplicationProvider.getApplicationContext<android.content.Context>()
        val prefs=context.getSharedPreferences("gm-v2-appearance",android.content.Context.MODE_PRIVATE)
        val previous=prefs.getString("theme","system")
        try {
            mount(); compose.onNode(hasText("Settings") and hasClickAction()).performClick()
            compose.onNodeWithText("Dark").performScrollTo().performClick(); capture("settings-dark")
            compose.onNodeWithText("Light").performScrollTo().performClick(); capture("settings-light")
        } finally {prefs.edit().putString("theme",previous).commit()}
    }
    @Test fun largeTextKeepsStartReachable() {
        mount(true, true)
        compose.onNodeWithText("Übung starten").performScrollTo().assertIsDisplayed()
        capture("home-large-text")
    }
    @Test fun signIn() {
        compose.setContent { FoundationTheme { NativeAccountEntry("sign-in", false, {}, {}) { _,_,_->error("No network") } } }
        capture("sign-in")
    }
    private fun mountAccount() {
        val id = "00000000-0000-4000-8000-000000000001"
        val profile = LearnerProfile("v2", 0, true, ProfilePreferences("en", "UTC", "B1", 5))
        val catalog = Catalog("v2", id, "unpublished_local_draft", listOf(CatalogTopic(id, LocalizedText("Grammar", "Grammatik"))),
            listOf(CatalogTarget(id, id, LocalizedText("Dative articles", "Dativartikel"), LocalizedText("Articles after mit", "Artikel nach mit"), "B1", 5)))
        val api = object : LearnerApi {
            override suspend fun profile() = profile
            override suspend fun save(request: ProfileRequest) = profile
            override suspend fun sync(cursor: String) = SyncPage("v2", emptyList(), cursor, false)
            override suspend fun catalog() = catalog
            override suspend fun targets(cursor: String) = TargetPage("v2", "2026-10-24T10:00:00Z", emptyList(), "", id)
        }
        val store = object : LearnerStore {
            var cache = LearnerCache(profile = profile, catalog = catalog)
            override fun read() = cache
            override fun write(value: LearnerCache) { cache = value }
        }
        val repo = LearnerRepository(api, store)
        compose.setContent { FoundationTheme { LearnerShell(repo) } }
        compose.waitUntil(10000) { repo.state.generatedAt != null }
    }
    @Test fun accountHome() { mountAccount(); capture("account-home") }
    @Test fun accountProgress() {
        mountAccount(); compose.onNode(hasText("Progress") and hasClickAction()).performClick(); capture("account-progress")
    }
    @Test fun loginProvidersAndHome() {
        compose.setContent { FoundationTheme {
            NativeAccountEntry("sign-in",false,{}, {},socialSignIn={}) {_,_,_->}
        } }
        capture("login-providers-home")
    }
    @Test @Config(qualifiers = "w320dp-h720dp-night-320dpi")
    fun narrowGermanLoginProvidersAndHome() {
        compose.setContent { FoundationTheme {
            NativeAccountEntry("register",true,{}, {},socialSignIn={}) {_,_,_->}
        } }
        capture("login-providers-home-german-dark-320")
    }

}
