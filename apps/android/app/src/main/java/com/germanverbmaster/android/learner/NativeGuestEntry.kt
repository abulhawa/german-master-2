package com.germanverbmaster.android.learner

import androidx.activity.compose.BackHandler
import android.util.AtomicFile
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
    var paused by remember {mutableStateOf(true)}
    var accountPage by remember {mutableStateOf<String?>(null)}
    var closing by remember {mutableStateOf(false)}
    val scroll = rememberScrollState()
    LaunchedEffect(state.index, state.feedback, paused, accountPage, closing) { scroll.scrollTo(0) }
    fun text(en:String,de:String)=if(german)de else en
    fun commit(next:NativeGuestState) {try {store.write(next);state=next;failed=false}catch(_:Exception){failed=true}}
    @Composable fun primary(label:String, enabled:Boolean=true, action:()->Unit) {
        Button(onClick=action,enabled=enabled,modifier=Modifier.fillMaxWidth().heightIn(min=56.dp)){Text(label)}
    }
    val practiceView = loaded.isSuccess && state.started && !paused && accountPage == null
    val practising = practiceView && state.index < content.session.questions.size
    BackHandler(enabled=practiceView || accountPage != null) {
        if(accountPage != null) {
            accountPage=null
            if(state.index>=content.session.questions.size) paused=true
        } else if(practising) closing=true else paused=true
    }
    accountPage?.let { page ->
        Surface(Modifier.fillMaxSize()) {
            Column(Modifier.safeDrawingPadding().verticalScroll(scroll).padding(24.dp),verticalArrangement=Arrangement.spacedBy(16.dp)) {
                Text(when(page) {
                    "Progress" -> text("Progress","Fortschritt")
                    "Downloads" -> text("Downloads","Downloads")
                    else -> text("Account","Konto")
                },Modifier.semantics {heading()},style=MaterialTheme.typography.headlineMedium)
                Text(text("This page requires an account. Sign in or create an account to continue.",
                    "Für diese Seite brauchst du ein Konto. Melde dich an oder erstelle ein Konto, um fortzufahren."),Modifier.semantics {liveRegion=LiveRegionMode.Polite})
                primary(text("Sign in","Anmelden")){account("sign-in")}
                primary(text("Create account","Konto erstellen")){account("register")}
                TextButton(onClick={accountPage=null;if(state.index>=content.session.questions.size)paused=true}){Text(text("Back to practice","Zurück zur Übung"))}
                continueSaved?.let {LearnerButton(text("Continue saved account","Gespeichertes Konto fortsetzen"),true,it)}
            }
        }
        return
    }
    Surface(Modifier.fillMaxSize()) {
        Column(Modifier.safeDrawingPadding().verticalScroll(scroll).padding(24.dp),verticalArrangement=Arrangement.spacedBy(16.dp)) {
            Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween,verticalAlignment=androidx.compose.ui.Alignment.CenterVertically) {
                Text("German Master",Modifier.weight(1f).semantics {heading()},style=if(practising)MaterialTheme.typography.titleLarge else MaterialTheme.typography.headlineMedium)
                if(practiceView) {
                    TextButton(onClick={accountPage="Account"}){Text(text("Account","Konto"))}
                    TextButton(onClick={if(practising)closing=true else paused=true}){Text(text("Close practice","Übung schließen"))}
                }
            }
            if(!practiceView) TextButton(onClick=language){Text("English / Deutsch")}
            if(!practiceView) {
                Text(text("Practise German","Deutsch üben"),Modifier.semantics {heading()},style=MaterialTheme.typography.titleLarge)
                Text(text("Your practice is saved on this device.","Deine Übungen sind auf diesem Gerät gespeichert."))
            }
            if(loaded.isFailure) {
                Text(text("Saved practice could not be read. It has been preserved.","Gespeicherte Übungen konnten nicht gelesen werden und bleiben erhalten."))
            } else if(closing) {
                Text(text("Leave this session?","Diese Übung verlassen?"),Modifier.semantics {heading()})
                Text(text("Your answers and draft stay on this device.","Deine Antworten und dein Entwurf bleiben auf diesem Gerät."))
                primary(text("Save and return Home","Speichern und zur Startseite")){closing=false;paused=true}
                TextButton(onClick={closing=false}){Text(text("Keep practising","Weiter üben"))}
            } else if(!state.started || paused) {
                primary(when {
                    !state.started -> text("Start practice","Übung starten")
                    state.index < content.session.questions.size -> text("Continue session","Übung fortsetzen")
                    else -> text("Repeat the five questions","Die fünf Fragen wiederholen")
                }) {
                    if(!state.started || state.index>=content.session.questions.size) commit(NativeGuestState(started=true))
                    paused=false
                }
            } else if(state.index>=content.session.questions.size) {
                Text(text("Session complete","Übung abgeschlossen"),Modifier.semantics {heading()})
                Text(text("${state.graded} answered · ${content.session.questions.size-state.graded} skipped",
                    "${state.graded} beantwortet · ${content.session.questions.size-state.graded} übersprungen"),Modifier.semantics {liveRegion=LiveRegionMode.Polite})
                Text(text("${state.correct} correct","${state.correct} richtig"))
                primary(text("Finish","Abschließen")){paused=true}
            } else {
                val exercise=content.session.questions[state.index].exercise
                Text(text("Question ${state.index+1} of 5","Aufgabe ${state.index+1} von 5"))
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
                                Row(Modifier.fillMaxWidth().selectable(selected=selected,role=Role.RadioButton,onClick={
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
                                }).heightIn(min=48.dp),verticalAlignment=androidx.compose.ui.Alignment.CenterVertically) {
                                    RadioButton(selected=selected,onClick=null)
                                    Text(option)
                                }
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
                    Text(if(feedback.outcome=="correct")text("Correct","Richtig")else text("Not quite","Noch nicht ganz"),Modifier.semantics {liveRegion=LiveRegionMode.Polite})
                    Text(foundationAnswerText(feedback.acceptedAnswer,exercise))
                    Text(if(german)feedback.explanation.de else feedback.explanation.en)
                    primary(text("Continue","Weiter")){commit(state.copy(index=state.index+1,draft=null,order=emptyList(),feedback=null,assisted=false))}
                }
            }
            if(failed) Text(text("Could not save on this device. Your last saved work is preserved. Try again.","Speichern fehlgeschlagen. Zuletzt gespeicherte Übungen bleiben erhalten. Versuche es erneut."),Modifier.semantics {liveRegion=LiveRegionMode.Polite})
            if(!practiceView) {
                TextButton(onClick={accountPage="Progress"}){Text(text("Progress","Fortschritt"))}
                TextButton(onClick={accountPage="Downloads"}){Text(text("Downloads","Downloads"))}
                TextButton(onClick={accountPage="Account"}){Text(text("Account","Konto"))}
            }
        }
    }
}
