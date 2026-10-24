package com.germanverbmaster.android.learner

import androidx.activity.compose.BackHandler
import android.util.AtomicFile
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.dp
import com.germanverbmaster.android.foundation.*
import com.germanverbmaster.android.foundation.contract.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.encodeToJsonElement
import java.io.File

@Serializable
data class GuestStarterContent(val session:Session,val suggestions:Map<String,Map<String,List<String>>>,val rubrics:List<OfflineRubric>)
@Serializable
data class NativeGuestState(val version:Int=1,val started:Boolean=false,val index:Int=0,val draft:Answer?=null,val order:List<String> = emptyList(),
    val feedback:Evaluation?=null,val graded:Int=0,val correct:Int=0,val assisted:Boolean=false)

/** Separate local guest record. Never supplies an identity, account outbox or confirmed mastery. */
class NativeGuestStore(path:File) {
    private val file=AtomicFile(path)
    fun read():NativeGuestState {
        val raw=try {file.openRead().use {it.readBytes().toString(Charsets.UTF_8)}} catch(_:java.io.FileNotFoundException){return NativeGuestState()}
        return ContractReader.json.decodeFromString<NativeGuestState>(raw).also {
            require(it.version==1 && it.index in 0..5 && it.graded in 0..5 && it.correct in 0..it.graded)
        }
    }
    fun write(state:NativeGuestState) {
        val stream=file.startWrite()
        try {stream.write(ContractReader.json.encodeToString(state).toByteArray());file.finishWrite(stream)}
        catch(error:Exception){file.failWrite(stream);throw error}
    }
}

@Composable
fun NativeGuestEntry(path:File,german:Boolean,language:()->Unit,account:(String)->Unit,continueSaved:(()->Unit)?=null) {
    val context=LocalContext.current
    val content=remember {ContractReader.json.decodeFromString<GuestStarterContent>(context.assets.open("starter.json").bufferedReader().use {it.readText()})}
    val store=remember(path){NativeGuestStore(path)}
    val loaded=remember(store){runCatching {store.read()}}
    var state by remember(store){mutableStateOf(loaded.getOrDefault(NativeGuestState()))}
    var failed by remember {mutableStateOf(false)}
    var screen by rememberSaveable {mutableStateOf("home")}
    var returnTo by rememberSaveable {mutableStateOf("home")}
    var closing by remember {mutableStateOf(false)}
    val scroll=rememberScrollState()
    LaunchedEffect(state.index,state.feedback,screen,closing) {scroll.scrollTo(0)}
    fun text(en:String,de:String)=if(german)de else en
    fun commit(next:NativeGuestState):Boolean = try {store.write(next);state=next;failed=false;true}catch(_:Exception){failed=true;false}
    @Composable fun primary(label:String,enabled:Boolean=true,action:()->Unit) {
        StudyPrimary(label,enabled,action)
    }
    @Composable fun section(content:@Composable ColumnScope.()->Unit) {
        StudyPanel(content)
    }
    val active=state.started && state.index<content.session.questions.size
    val focused=screen=="practice"
    fun start() {
        if(loaded.isFailure)return
        if(active || commit(NativeGuestState(started=true))) {closing=false;screen="practice"}
    }
    val startLabel=when {
        !state.started -> text("Start practice","Übung starten")
        active -> text("Continue session","Übung fortsetzen")
        else -> text("Repeat the five questions","Die fünf Fragen wiederholen")
    }
    fun openAccount() {returnTo=screen;screen="account"}
    fun backToPractice() {screen=if(active && returnTo=="practice")"practice" else "home"}
    BackHandler(enabled=screen!="home") {
        when {
            focused && active -> closing=true
            screen=="downloads" -> screen="account"
            screen=="account" && returnTo=="practice" && active -> screen="practice"
            else -> screen="home"
        }
    }
    Scaffold(modifier=Modifier.fillMaxSize(),contentWindowInsets=WindowInsets.safeDrawing,containerColor=MaterialTheme.colorScheme.background,
        bottomBar={if(!focused) LearnerNavigation(screen,german) {returnTo="home";screen=it}}) {insets->
        Column(Modifier.padding(insets).widthIn(max=720.dp).fillMaxWidth().verticalScroll(scroll).padding(horizontal=24.dp,vertical=16.dp),verticalArrangement=Arrangement.spacedBy(20.dp)) {
            if(!focused) StudyHeader(german,{openAccount()})
            else Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween,verticalAlignment=androidx.compose.ui.Alignment.CenterVertically) {
                TextButton(onClick={if(active)closing=true else screen="home"}){Text(text("Close practice","Übung schließen"))}
                if(active) Text(text("Question ${state.index+1} of 5","Aufgabe ${state.index+1} von 5"),style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant)
                TextButton(onClick={openAccount()}){Text(text("Account","Konto"))}
            }
            when(screen) {
                "home" -> {
                    StudyEyebrow(text("YOUR PRACTICE","DEINE ÜBUNG"))
                    StudyPageTitle(text("A little German.\nA little more confidence.","Ein wenig Deutsch.\nEin Stück sicherer."))
                    Text(text("Make time for a short, focused session.","Nimm dir Zeit für eine kurze, gezielte Übung."),color=MaterialTheme.colorScheme.onSurfaceVariant)
                    section {
                        StudyEyebrow(text("B1 · 5 questions · Mixed practice","B1 · 5 Aufgaben · Gemischte Übung"))
                        Text(text("Build your foundations","Festige deine Grundlagen"),style=MaterialTheme.typography.titleLarge)
                        Text(text("Articles, plurals and the patterns that hold a sentence together.","Artikel, Pluralformen und die Muster, die einen Satz zusammenhalten."),color=MaterialTheme.colorScheme.onSurfaceVariant,style=MaterialTheme.typography.bodyMedium)
                        primary(startLabel,loaded.isSuccess){start()}
                        Text(text("Your practice is saved on this device.","Deine Übungen sind auf diesem Gerät gespeichert."),style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween,verticalAlignment=androidx.compose.ui.Alignment.CenterVertically) {
                        StudyEyebrow(text("IN THIS SESSION","IN DIESER ÜBUNG"))
                        TextButton(onClick={screen="topics"}){Text(text("View topics","Themen ansehen"))}
                    }
                    Text(text("Noun plurals · Dative articles · Prepositions · Word order · Verb forms","Pluralformen · Dativartikel · Präpositionen · Wortstellung · Verbformen"),color=MaterialTheme.colorScheme.onSurfaceVariant,style=MaterialTheme.typography.bodyMedium)
                }
                "topics" -> {
                    StudyEyebrow(text("EXPLORE · B1","ENTDECKEN · B1"))
                    StudyPageTitle(text("Grammar foundations","Grammatik-Grundlagen"))
                    Text(text("Five patterns. One short B1 session.","Fünf Muster. Eine kurze B1-Übung."),color=MaterialTheme.colorScheme.onSurfaceVariant)
                    Column {
                        listOf("Noun plurals" to "Pluralformen","Dative articles" to "Dativartikel","Prepositions" to "Präpositionen","Word order" to "Wortstellung","Verb forms" to "Verbformen").forEach { (en,de)->
                            StudyTopicRow(text(en,de))
                        }
                    }
                    primary(startLabel,loaded.isSuccess){start()}
                }
                "progress","account","downloads" -> {
                    StudyPageTitle(when(screen){"progress"->text("Progress","Fortschritt");"downloads"->text("Downloads","Downloads");else->text("Settings","Einstellungen")})
                    if(screen=="account") {
                        StudyAppearanceSettings(german,language)
                        Text(text("Account","Konto"),style=MaterialTheme.typography.titleLarge)
                    }
                    section {
                        Text(if(screen=="account") text("Sign in to save and sync practice across devices.","Melde dich an, um Übungen geräteübergreifend zu speichern und zu synchronisieren.") else text("This page requires an account. Sign in or create an account to continue.","Für diese Seite brauchst du ein Konto. Melde dich an oder erstelle ein Konto, um fortzufahren."),Modifier.semantics {liveRegion=LiveRegionMode.Polite})
                        primary(text("Sign in","Anmelden")){account("sign-in")}
                        OutlinedButton(onClick={account("register")},modifier=Modifier.fillMaxWidth().heightIn(min=56.dp)){Text(text("Create account","Konto erstellen"))}
                        TextButton(onClick={backToPractice()}){Text(text("Back to practice","Zurück zur Übung"))}
                        continueSaved?.let {LearnerButton(text("Continue saved account","Gespeichertes Konto fortsetzen"),true,it)}
                    }
                    if(screen=="account") OutlinedButton(onClick={screen="downloads"},modifier=Modifier.fillMaxWidth().heightIn(min=48.dp)){Text(text("Downloads","Downloads"))}
                }
                "practice" -> if(closing) {
                    section {
                        Text(text("Leave this session?","Diese Übung verlassen?"),Modifier.semantics {heading()},style=MaterialTheme.typography.titleLarge)
                        Text(text("Your answers and draft stay on this device.","Deine Antworten und dein Entwurf bleiben auf diesem Gerät."))
                        primary(text("Save and return Home","Speichern und zur Startseite")){closing=false;screen="home"}
                        TextButton(onClick={closing=false}){Text(text("Keep practising","Weiter üben"))}
                    }
                } else if(state.index>=content.session.questions.size) {
                    section {
                        Text(text("Session complete","Übung abgeschlossen"),Modifier.semantics {heading()},style=MaterialTheme.typography.headlineSmall)
                        Text(text("${state.graded} answered · ${content.session.questions.size-state.graded} skipped","${state.graded} beantwortet · ${content.session.questions.size-state.graded} übersprungen"),Modifier.semantics {liveRegion=LiveRegionMode.Polite})
                        Text(text("${state.correct} correct","${state.correct} richtig"))
                        primary(text("Finish","Abschließen")){screen="home"}
                    }
                } else if(loaded.isSuccess) {
                val exercise=content.session.questions[state.index].exercise
                LinearProgressIndicator(progress={state.index.toFloat()/content.session.questions.size},modifier=Modifier.fillMaxWidth().height(4.dp),trackColor=MaterialTheme.colorScheme.outlineVariant)
                PracticeHeading(exercise.prompt,exercise.id)
                if(exercise !is ExerciseShortAnswer) Text(if(german)exercise.instruction.de else exercise.instruction.en)
                val feedback=state.feedback
                if(feedback==null) {
                    if(exercise is ExerciseChoice || exercise is ExerciseWordOrder || exercise is ExerciseGapChoice || exercise is ExerciseMatching) {
                        LowTypingInput(exercise,state.draft,state.order,german,
                            {commit(state.copy(draft=it))},{commit(state.copy(order=it,draft=if(it.size==(exercise as? ExerciseWordOrder)?.tokens?.size)AnswerWordOrder(it) else null))})
                    } else {
                        val suggestions=content.suggestions.getValue(exercise.id)
                        suggestions.forEach {(slot,options)->
                            Text(if(slot=="answer")text("Choose an answer","Wähle eine Antwort")else when(exercise) {
                                is ExerciseCloze -> exercise.slots.first {it.id==slot}.label
                                is ExerciseMultiSlot -> exercise.slots.first {it.id==slot}.label
                                else -> slot
                            })
                            options.forEach {option->
                                val selected=when(val answer=state.draft) {
                                    is AnswerShortAnswer -> answer.text==option
                                    is AnswerCloze -> answer.values.any {it.slotId==slot && it.text==option}
                                    is AnswerMultiSlot -> answer.values.any {it.slotId==slot && it.text==option}
                                    else -> false
                                }
                                ChoiceOption(option,selected,choose={
                                    val answer:Answer=when(exercise) {
                                        is ExerciseShortAnswer -> AnswerShortAnswer(option)
                                        is ExerciseCloze -> AnswerCloze(listOf(SlotValue(slot,option)))
                                        is ExerciseMultiSlot -> {
                                            val existing=(state.draft as? AnswerMultiSlot)?.values.orEmpty()
                                            AnswerMultiSlot(exercise.slots.map { field ->
                                                SlotValue(field.id,if(field.id==slot)option else existing.find {it.slotId==field.id}?.text.orEmpty())
                                            })
                                        }
                                        else -> error("Unsupported guest suggestion exercise")
                                    };commit(state.copy(draft=answer))
                                })
                            }
                        }
                    }
                    primary(text("Check answer","Antwort prüfen"),nativeAnswerReady(exercise,state.draft)) {
                        val rubric=content.rubrics.single {it.exerciseId==exercise.id&&it.exerciseRevision==exercise.revision}
                        val evaluation=OfflineGrader.grade(exercise,rubric,ContractReader.json.encodeToJsonElement<Answer>(requireNotNull(state.draft)),if(state.assisted)listOf("hint")else emptyList())
                        commit(state.copy(feedback=evaluation,graded=state.graded+1,correct=state.correct+if(evaluation.outcome=="correct")1 else 0))
                    }
                    TextButton(onClick={commit(state.copy(assisted=true))}){Text(text("Hint","Hinweis"))}
                    if(state.assisted) Text(if(german)exercise.hint.de else exercise.hint.en)
                    TextButton(onClick={commit(state.copy(index=state.index+1,draft=null,order=emptyList(),assisted=false))}){Text(text("Skip","Überspringen"))}
                } else {
                    StudyFeedback(feedback.outcome=="correct",true,german,
                        state.draft?.let {foundationAnswerText(it,exercise)},foundationAnswerText(feedback.acceptedAnswer,exercise),
                        if(german)feedback.explanation.de else feedback.explanation.en,false,feedback.completedAnswer) {
                        commit(state.copy(index=state.index+1,draft=null,order=emptyList(),feedback=null,assisted=false))
                    }
                }
                }
            }
            if(loaded.isFailure) Text(text("Saved practice could not be read. It has been preserved.","Gespeicherte Übungen konnten nicht gelesen werden und bleiben erhalten."),Modifier.semantics {liveRegion=LiveRegionMode.Polite})
            if(failed) Text(text("Could not save on this device. Your last saved work is preserved. Try again.","Speichern fehlgeschlagen. Zuletzt gespeicherte Übungen bleiben erhalten. Versuche es erneut."),Modifier.semantics {liveRegion=LiveRegionMode.Polite})
        }
    }
}
