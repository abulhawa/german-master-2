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
    var paused by remember {mutableStateOf(false)}
    val scroll = rememberScrollState()
    LaunchedEffect(state.index, state.feedback, paused) { scroll.scrollTo(0) }
    fun text(en:String,de:String)=if(german)de else en
    fun commit(next:NativeGuestState) {try {store.write(next);state=next;failed=false}catch(_:Exception){failed=true}}
    @Composable fun primary(label:String, enabled:Boolean=true, action:()->Unit) {
        Button(onClick=action,enabled=enabled,modifier=Modifier.fillMaxWidth().heightIn(min=56.dp)){Text(label)}
    }
    val practising = loaded.isSuccess && state.started && state.index < content.session.questions.size && !paused
    BackHandler(enabled=practising) { paused=true }
    Surface(Modifier.fillMaxSize()) {
        Column(Modifier.safeDrawingPadding().verticalScroll(scroll).padding(24.dp),verticalArrangement=Arrangement.spacedBy(16.dp)) {
            Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween,verticalAlignment=androidx.compose.ui.Alignment.CenterVertically) {
                Text("German Master",Modifier.weight(1f).semantics {heading()},style=if(practising)MaterialTheme.typography.titleLarge else MaterialTheme.typography.headlineMedium)
                if(practising) TextButton(onClick={paused=true}){Text(text("Pause","Pause"))}
            }
            if(!practising) TextButton(onClick=language){Text("English / Deutsch")}
            if(!practising) {
                Text(text("Practise German. No account needed.","Deutsch üben. Ohne Konto."),style=MaterialTheme.typography.titleLarge)
            }
            if(loaded.isFailure) {
                Text(text("Saved guest work could not be read. It has been preserved.","Gespeicherte Gastübungen konnten nicht gelesen werden und bleiben erhalten."))
            } else if(!state.started || paused) {
                primary(if(state.started)text("Continue practice","Weiter üben")else text("Start practising","Jetzt üben")){commit(state.copy(started=true));paused=false}
            } else if(state.index>=content.session.questions.size) {
                Text(text("Starter complete: ${state.correct} of ${state.graded} correct.","Einstieg abgeschlossen: ${state.correct} von ${state.graded} richtig."),Modifier.semantics {liveRegion=LiveRegionMode.Polite})
                primary(text("Practise again","Noch einmal üben")){commit(NativeGuestState(started=true))}
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
            if(!practising) {
                Text(text("An account saves online progress across devices. Earlier guest results stay on this device.","Ein Konto speichert Online-Fortschritt geräteübergreifend. Frühere Gastergebnisse bleiben auf diesem Gerät."))
                TextButton(onClick={account("register")}){Text(text("Create account","Konto erstellen"))}
                TextButton(onClick={account("sign-in")}){Text(text("Sign in","Anmelden"))}
                TextButton(onClick={account("reset")}){Text(text("Forgot password?","Passwort vergessen?"))}
                continueSaved?.let {LearnerButton(text("Continue saved account practice","Gespeicherte Kontoübungen fortsetzen"),true,it)}
            }
        }
    }
}
