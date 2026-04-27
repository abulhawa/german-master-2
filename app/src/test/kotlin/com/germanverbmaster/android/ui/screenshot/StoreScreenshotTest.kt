package com.germanverbmaster.android.ui.screenshot

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.germanverbmaster.android.data.local.entity.WordEntity
import com.germanverbmaster.android.domain.model.B2Card
import com.germanverbmaster.android.domain.model.B2Category
import com.germanverbmaster.android.domain.model.CardMode
import com.germanverbmaster.android.domain.model.PracticeMode
import com.germanverbmaster.android.domain.model.SessionStats
import com.germanverbmaster.android.domain.model.TaskCard
import com.germanverbmaster.android.ui.b2practice.B2PracticeScreenContent
import com.germanverbmaster.android.ui.b2practice.B2PracticeUiState
import com.germanverbmaster.android.ui.home.HomeScreenContent
import com.germanverbmaster.android.ui.home.HomeUiState
import com.germanverbmaster.android.ui.theme.GermanVerbMasterTheme
import com.germanverbmaster.android.ui.wortschatz.WortschatzScreenContent
import com.germanverbmaster.android.ui.wortschatz.WortschatzTab
import com.germanverbmaster.android.ui.wortschatz.WortschatzUiState
import com.github.takahirom.roborazzi.RoborazziRule
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(
    sdk = [35],
    qualifiers = "w411dp-h891dp-420dpi"
)
class StoreScreenshotTest {
    @get:Rule
    val roborazziRule = RoborazziRule()

    // --- MOCK DATA ---

    private val mockWords = listOf(
        WordEntity(
            id = 1, lemma = "beobachten", pos = "V", level = "B2", english = "to observe",
            exampleDe = "Wir beobachten die Vögel im Wald.", exampleEn = "We observe the birds in the forest."
        ),
        WordEntity(
            id = 2, lemma = "Entscheidung", pos = "N", level = "B1", english = "decision", gender = "f",
            plural = "Entscheidungen", exampleDe = "Das war eine schwere Entscheidung.",
            exampleEn = "That was a difficult decision."
        ),
        WordEntity(
            id = 3, lemma = "verantwortlich", pos = "Adj", level = "B2", english = "responsible",
            exampleDe = "Wer ist dafür verantwortlich?", exampleEn = "Who is responsible for that?"
        ),
        WordEntity(
            id = 4, lemma = "gefallen", pos = "V", level = "A1", english = "to please / to like",
            exampleDe = "Das Buch gefällt mir sehr gut.", exampleEn = "I like the book very much."
        ),
        WordEntity(
            id = 5, lemma = "Nachhaltigkeit", pos = "N", level = "C1", english = "sustainability", gender = "f",
            plural = "-", exampleDe = "Nachhaltigkeit ist heute sehr wichtig.",
            exampleEn = "Sustainability is very important today."
        )
    )

    // Realistic large list for "X / 2000" stats
    private val realisticLargeList = List(2000) { i ->
        if (i < mockWords.size) mockWords[i]
        else mockWords[i % mockWords.size].copy(id = i + 100)
    }

    private val wortschatzBaseState = WortschatzUiState(
        tab = WortschatzTab.LIST,
        selectedLevels = emptySet(),
        selectedPosSet = emptySet(),
        posOptions = listOf("Alle", "V", "N", "Adj", "Adv"),
        searchQuery = "",
        isLoading = false,
        listCards = realisticLargeList,
        drillQueue = mockWords,
        drillIndex = 0,
        drillFlipped = false,
        historicalCorrect = 1248,
        historicalWrong = 89,
        masteredIds = (1..1248).map { it.toString() }.toSet()
    )

    private val homeBaseState = HomeUiState(
        mode = PracticeMode.ALL,
        cefrLevel = "B2",
        currentTask = TaskCard(
            taskId = "t1",
            lexemeId = "l1",
            lemma = "empfehlen",
            pos = "V",
            taskType = "conjugate_form",
            renderer = "default",
            cefrLevel = "B2",
            prompt = mapOf("person" to "du", "tense" to "Präsens", "instructions" to "Konjugiere das Verb:"),
            solution = mapOf("form" to "empfiehlst"),
            translation = "to recommend"
        ),
        stats = SessionStats(correct = 15, incorrect = 3),
        isLoading = false
    )

    private val b2BaseState = B2PracticeUiState(
        category = B2Category.VERBEN_PRAEP,
        mode = CardMode.DE_TO_EN,
        shuffle = true,
        queue = listOf(
            B2Card("1", B2Category.VERBEN_PRAEP, "abhängen von", "to depend on", preposition = "von + Dat.", example = "Es hängt vom Wetter ab.", topic = "Natur"),
            B2Card("2", B2Category.VERBEN_PRAEP, "sich freuen auf", "to look forward to", preposition = "auf + Akk.", example = "Ich freue mich auf den Urlaub.", topic = "Freizeit")
        ),
        currentIndex = 0,
        isFlipped = false,
        correct = 24,
        wrong = 2,
        isLoading = false
    )

    // --- SHELL ---

    @Composable
    private fun StoreShell(darkTheme: Boolean = false, selectedTab: Int = 0, content: @Composable (Modifier) -> Unit) {
        GermanVerbMasterTheme(darkTheme = darkTheme) {
            Scaffold(
                bottomBar = {
                    NavigationBar {
                        NavigationBarItem(
                            selected = selectedTab == 0,
                            onClick = {},
                            icon = { Icon(Icons.AutoMirrored.Filled.MenuBook, "Wortschatz") },
                            label = { Text("Wortschatz") }
                        )
                        NavigationBarItem(
                            selected = selectedTab == 1,
                            onClick = {},
                            icon = { Icon(Icons.Default.School, "Lernen") },
                            label = { Text("Lernen") }
                        )
                        NavigationBarItem(
                            selected = selectedTab == 2,
                            onClick = {},
                            icon = { Icon(Icons.Default.Stars, "Üben") },
                            label = { Text("Üben") }
                        )
                        NavigationBarItem(
                            selected = selectedTab == 3,
                            onClick = {},
                            icon = { Icon(Icons.Default.AccountCircle, "Profil") },
                            label = { Text("Profil") }
                        )
                    }
                }
            ) { innerPadding ->
                Surface(
                    modifier = Modifier.padding(innerPadding),
                    color = androidx.compose.material3.MaterialTheme.colorScheme.background
                ) {
                    content(Modifier)
                }
            }
        }
    }

    // --- TESTS ---

    @Test
    fun capture_1_wortschatz_list_light() {
        captureRoboImage("../play-store-listing/1_wortschatz_list_light.png") {
            StoreShell(darkTheme = false, selectedTab = 0) {
                WortschatzScreenContent(
                    state = wortschatzBaseState,
                    onTriggerSync = {}, onSelectTab = {}, onUpdateSearchQuery = {}, onToggleLevel = {}, onTogglePos = {},
                    onNavigateToHistory = {}, onNavigateToWordDetail = {}, onFlip = {}, onMarkCorrect = {}, onMarkWrong = {},
                    onRestartDrill = {}, onSpeak = {}
                )
            }
        }
    }

    @Test
    fun capture_2_wortschatz_drill_light() {
        captureRoboImage("../play-store-listing/2_wortschatz_drill_light.png") {
            StoreShell(darkTheme = false, selectedTab = 0) {
                WortschatzScreenContent(
                    state = wortschatzBaseState.copy(tab = WortschatzTab.DRILL),
                    onNavigateToHistory = {}, onSpeak = {}, onFlip = {}, onMarkCorrect = {}, onMarkWrong = {},
                    onRestartDrill = {}, onTriggerSync = {}, onSelectTab = {}, onUpdateSearchQuery = {},
                    onToggleLevel = {}, onTogglePos = {}, onNavigateToWordDetail = {}
                )
            }
        }
    }

    @Test
    fun capture_3_home_practice_light() {
        captureRoboImage("../play-store-listing/3_home_practice_light.png") {
            StoreShell(darkTheme = false, selectedTab = 2) {
                HomeScreenContent(
                    state = homeBaseState,
                    onSetMode = {}, onSetCefrLevel = {}, onRefresh = {}, onNavigateToHistory = {},
                    onSubmitResult = { _, _, _, _, _ -> },
                    onSpeak = {}
                )
            }
        }
    }

    @Test
    fun capture_4_lernen_b2_light() {
        captureRoboImage("../play-store-listing/4_lernen_b2_light.png") {
            StoreShell(darkTheme = false, selectedTab = 1) {
                B2PracticeScreenContent(
                    state = b2BaseState,
                    onSetCategory = {}, onSetMode = {}, onToggleShuffle = {}, onNavigateToHistory = {},
                    onRestart = {}, onFlip = {}, onMarkCorrect = {}, onMarkWrong = {}
                )
            }
        }
    }

    @Test
    fun capture_5_home_practice_dark() {
        captureRoboImage("../play-store-listing/5_home_practice_dark.png") {
            StoreShell(darkTheme = true, selectedTab = 2) {
                HomeScreenContent(
                    state = homeBaseState.copy(
                        currentTask = homeBaseState.currentTask!!.copy(
                            lemma = "Entscheidung",
                            taskType = "noun_case_declension",
                            prompt = mapOf("case" to "Akkusativ", "gender" to "f", "instructions" to "Bilde die richtige Form:"),
                            solution = mapOf("answer" to "eine Entscheidung"),
                            translation = "a decision"
                        )
                    ),
                    onSetMode = {}, onSetCefrLevel = {}, onRefresh = {}, onNavigateToHistory = {},
                    onSubmitResult = { _, _, _, _, _ -> },
                    onSpeak = {}
                )
            }
        }
    }

    @Test
    fun capture_6_wortschatz_list_dark() {
        captureRoboImage("../play-store-listing/6_wortschatz_list_dark.png") {
            StoreShell(darkTheme = true, selectedTab = 0) {
                WortschatzScreenContent(
                    state = wortschatzBaseState.copy(searchQuery = "beo"),
                    onTriggerSync = {}, onSelectTab = {}, onUpdateSearchQuery = {}, onToggleLevel = {}, onTogglePos = {},
                    onNavigateToHistory = {}, onNavigateToWordDetail = {}, onFlip = {}, onMarkCorrect = {}, onMarkWrong = {},
                    onRestartDrill = {}, onSpeak = {}
                )
            }
        }
    }

    @Test
    fun capture_7_wortschatz_drill_dark_flipped() {
        captureRoboImage("../play-store-listing/7_wortschatz_drill_dark_flipped.png") {
            StoreShell(darkTheme = true, selectedTab = 0) {
                WortschatzScreenContent(
                    state = wortschatzBaseState.copy(tab = WortschatzTab.DRILL, drillFlipped = true),
                    onNavigateToHistory = {}, onSpeak = {}, onFlip = {}, onMarkCorrect = {}, onMarkWrong = {},
                    onRestartDrill = {}, onTriggerSync = {}, onSelectTab = {}, onUpdateSearchQuery = {},
                    onToggleLevel = {}, onTogglePos = {}, onNavigateToWordDetail = {}
                )
            }
        }
    }

    @Test
    fun capture_8_lernen_cards_dark() {
        captureRoboImage("../play-store-listing/8_lernen_cards_dark.png") {
            StoreShell(darkTheme = true, selectedTab = 1) {
                B2PracticeScreenContent(
                    state = b2BaseState.copy(category = B2Category.VERBEN_PRAEP),
                    onSetCategory = {}, onSetMode = {}, onToggleShuffle = {}, onNavigateToHistory = {},
                    onRestart = {}, onFlip = {}, onMarkCorrect = {}, onMarkWrong = {}
                )
            }
        }
    }
}
