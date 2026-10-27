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
import java.util.UUID
import java.time.Instant

@Serializable
data class GuestStarterContent(val session:Session,val suggestions:Map<String,Map<String,List<String>>>,val rubrics:List<OfflineRubric>)
@Serializable
data class NativeGuestPack(val level:String,val learningContext:String,val topicId:String,val title:LocalizedText,val session:Session,val rubrics:List<OfflineRubric>,val suggestions:Map<String,Map<String,List<String>>>)
@Serializable
data class NativeGuestBundle(val packs:List<NativeGuestPack>)
@Serializable
data class NativeGuestState(val version:Int=1,val started:Boolean=false,val index:Int=0,val draft:Answer?=null,val order:List<String> = emptyList(),
    val feedback:Evaluation?=null,val graded:Int=0,val correct:Int=0,val assisted:Boolean=false,
    val session:Session?=null,val attempts:List<GuestAttempt> = emptyList(),val deviceId:String=UUID.randomUUID().toString(),
    val saveRequested:Boolean=false,val saveSubject:String?=null,val skippedIndices:List<Int> = emptyList(),val attemptIndices:List<Int> = emptyList())

/** Separate local guest record. Never supplies an identity, account outbox or confirmed mastery. */
class NativeGuestStore(private val path:File) {
    companion object {private val temporary=mutableMapOf<String,NativeGuestState>()}
    private val pending=AtomicFile(File(path.path+".save"))
    val legacy get()=path.exists()
    fun read():NativeGuestState {
        temporary[path.path]?.let{return it}
        val source=if(pending.baseFile.exists())pending else AtomicFile(path)
        val value=try {ContractReader.json.decodeFromString<NativeGuestState>(source.openRead().use {it.readBytes().toString(Charsets.UTF_8)})}
            catch(_:java.io.FileNotFoundException){NativeGuestState()}
        require(value.version==1 && value.index>=0 && value.graded>=0 && value.correct in 0..value.graded)
        temporary[path.path]=value;return value
    }
    fun write(state:NativeGuestState) {
        if(state.saveRequested) {
            val stream=pending.startWrite()
            try {stream.write(ContractReader.json.encodeToString(state).toByteArray());pending.finishWrite(stream)}
            catch(error:Exception){pending.failWrite(stream);throw error}
        }
        temporary[path.path]=state
    }
    fun discard(){pending.delete();AtomicFile(path).delete();temporary[path.path]=NativeGuestState()}
    fun authorize(){write(read().copy(saveRequested=true))}
}

@Composable
fun NativeGuestEntry(path:File,german:Boolean,language:()->Unit,account:(String)->Unit,continueSaved:(()->Unit)?=null) {
    val context=LocalContext.current
    val legacyContent=remember {ContractReader.json.decodeFromString<GuestStarterContent>(context.assets.open("starter.json").bufferedReader().use {it.readText()})}
    val store=remember(path){NativeGuestStore(path)}
    val loaded=remember(store){runCatching {store.read()}}
    val preferences=remember {context.getSharedPreferences("gm-v2-practice-preferences",android.content.Context.MODE_PRIVATE)}
    var level by remember {mutableStateOf(preferences.getString("level","B1")!!)}
    var learningContext by remember {mutableStateOf(if(NativeReleaseCapabilities.workplacePractice)preferences.getString("context","general")!! else "general")}
    var selectedTopic by remember {mutableStateOf<String?>(null)}
    val bundle=remember {ContractReader.json.decodeFromString<NativeGuestBundle>(context.assets.open("practice.json").bufferedReader().use{it.readText()})}
    val packs=bundle.packs.filter {it.level==level && it.learningContext==learningContext}
    var state by remember(store){mutableStateOf(loaded.getOrDefault(NativeGuestState()))}
    var failed by remember {mutableStateOf(false)}
    var screen by rememberSaveable {mutableStateOf(if(state.saveRequested&&state.started)"practice" else "home")}
    var returnTo by rememberSaveable {mutableStateOf("home")}
    var closing by remember {mutableStateOf(store.legacy)}
    var destination by remember {mutableStateOf("home")}
    val content=GuestStarterContent(state.session ?: legacyContent.session,legacyContent.suggestions + bundle.packs.flatMap {it.suggestions.entries}.associate {it.toPair()},bundle.packs.flatMap {it.rubrics}+legacyContent.rubrics)
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
        if(active) { screen="practice";closing=false;return }
        val available=packs.filter {selectedTopic==null || it.topicId==selectedTopic}
        val questions=available.flatMap {it.session.questions}.shuffled().take(5).map {it.copy(id=UUID.randomUUID().toString())}
        if(questions.isNotEmpty() && commit(NativeGuestState(started=true,session=available.first().session.copy(id=UUID.randomUUID().toString(),questions=questions)))) {closing=false;screen="practice"}
    }
    val startLabel=when {
        !state.started -> text("Start practice","Übung starten")
        active -> text("Continue session","Übung fortsetzen")
        else -> text("Repeat the five questions","Die fünf Fragen wiederholen")
    }
    fun leave(next:String) {if(focused&&active){destination=next;closing=true}else{if(state.started && !active){store.discard();state=NativeGuestState()};screen=next}}
    fun openAccount() {returnTo=screen;leave("account")}
    fun save(mode:String) {try {store.authorize();state=store.read();account(mode)}catch(_:Exception){failed=true}}
    fun discard() {try {store.discard();state=NativeGuestState();closing=false;screen=destination}catch(_:Exception){failed=true}}
    if(closing) {
        AlertDialog(onDismissRequest={closing=false;screen=if(state.started)"practice" else "home"},
            title={Text(text("Save your progress?","Fortschritt speichern?"))},
            text={Column {Text(text("Without an account, this practice will be lost when you leave.","Ohne Konto geht diese Übung beim Verlassen verloren."))
                TextButton(onClick={save("register")}){Text(text("Create account","Konto erstellen"))}
                TextButton(onClick={save("sign-in")}){Text(text("Sign in","Anmelden"))}
                if(failed)Text(text("Could not save. Try again.","Speichern fehlgeschlagen. Versuche es erneut."))}},
            confirmButton={TextButton(onClick={discard()}){Text(text("Leave without saving","Ohne Speichern verlassen"))}},
            dismissButton={TextButton(onClick={closing=false;screen=if(state.started)"practice" else "home"}){Text(text("Keep practising","Weiter üben"))}})
    }
    fun backToPractice() {screen=if(active && returnTo=="practice")"practice" else "home"}
    BackHandler(enabled=screen!="home") {
        when {
            focused && active -> {destination="home";closing=true}
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
                TextButton(onClick={leave("home")}){Text(text("Close practice","Übung schließen"))}
                if(active) Text(text("Question ${state.index+1} of 5","Aufgabe ${state.index+1} von 5"),style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant)
                TextButton(onClick={openAccount()}){Text(text("Account","Konto"))}
            }
            if(focused) GrammarReferenceButton(german)
            when(screen) {
                "home" -> {
                    StudyEyebrow(text("YOUR PRACTICE","DEINE ÜBUNG"))
                    StudyPageTitle(text("A little German.\nA little more confidence.","Ein wenig Deutsch.\nEin Stück sicherer."))
                    Text(text("Make time for a short, focused session.","Nimm dir Zeit für eine kurze, gezielte Übung."),color=MaterialTheme.colorScheme.onSurfaceVariant)
                    section {
                        StudyEyebrow(level+text(" · 5 questions · "," · 5 Aufgaben · ")+if(learningContext=="work")text("German for work","Deutsch im Beruf")else text("General German","Allgemeines Deutsch"))
                        Text(text("Build your foundations","Festige deine Grundlagen"),style=MaterialTheme.typography.titleLarge)
                        Text(text("Articles, plurals and the patterns that hold a sentence together.","Artikel, Pluralformen und die Muster, die einen Satz zusammenhalten."),color=MaterialTheme.colorScheme.onSurfaceVariant,style=MaterialTheme.typography.bodyMedium)
                        primary(startLabel,loaded.isSuccess){start()}
                        Text(text("Without an account, leaving practice or closing the app loses your work.","Ohne Konto geht deine Übung beim Verlassen oder Schließen der App verloren."),style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween,verticalAlignment=androidx.compose.ui.Alignment.CenterVertically) {
                        StudyEyebrow(text("IN THIS SESSION","IN DIESER ÜBUNG"))
                        TextButton(onClick={screen="topics"}){Text(text("View topics","Themen ansehen"))}
                    }
                    Text(text("Noun plurals · Dative articles · Prepositions · Word order · Verb forms","Pluralformen · Dativartikel · Präpositionen · Wortstellung · Verbformen"),color=MaterialTheme.colorScheme.onSurfaceVariant,style=MaterialTheme.typography.bodyMedium)
                }
                "topics" -> {
                    StudyPageTitle(text("Topics","Themen"))
                    packs.forEach {pack -> OutlinedButton(onClick={selectedTopic=pack.topicId}){Text((if(selectedTopic==pack.topicId)"* " else "")+(if(german)pack.title.de else pack.title.en))} }
                    primary(text("Start practice","Übung starten"),loaded.isSuccess){start()}
                }
                "progress","account","downloads" -> {
                    StudyPageTitle(when(screen){"progress"->text("Progress","Fortschritt");"downloads"->text("Downloads","Downloads");else->text("Settings","Einstellungen")})
                    if(screen=="account") {
                        StudyAppearanceSettings(german,language)
                        LearnerButton(text("Level","Niveau")+": "+level,true){level=if(level=="B1")"B2" else "B1";selectedTopic=null;preferences.edit().putString("level",level).apply()}
                        if(NativeReleaseCapabilities.workplacePractice) LearnerButton(text("Practice","Übung")+": "+(if(learningContext=="work")text("German for work","Deutsch im Beruf")else text("General German","Allgemeines Deutsch")),true){learningContext=if(learningContext=="general")"work" else "general";selectedTopic=null;preferences.edit().putString("context",learningContext).apply()}
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
                "practice" -> if(state.index>=content.session.questions.size) {
                    section {
                        Text(text("Session complete","Übung abgeschlossen"),Modifier.semantics {heading()},style=MaterialTheme.typography.headlineSmall)
                        Text(text("${state.graded} answered · ${content.session.questions.size-state.graded} skipped","${state.graded} beantwortet · ${content.session.questions.size-state.graded} übersprungen"),Modifier.semantics {liveRegion=LiveRegionMode.Polite})
                        Text(text("${state.correct} correct","${state.correct} richtig"))
                        primary(text("Save progress","Fortschritt speichern")){save("register")}
                        TextButton(onClick={destination="home";discard()}){Text(text("Finish","Abschließen"))}
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
                        val suggestions=content.suggestions[exercise.id]
                        if(suggestions==null) when(exercise) {
                            is ExerciseShortAnswer -> AnswerField(text("Answer","Antwort"),(state.draft as? AnswerShortAnswer)?.text.orEmpty()) {commit(state.copy(draft=AnswerShortAnswer(it)))}
                            is ExerciseCloze,is ExerciseMultiSlot -> {
                                val slots=if(exercise is ExerciseCloze)exercise.slots else (exercise as ExerciseMultiSlot).slots
                                val values=when(val answer=state.draft){is AnswerCloze->answer.values;is AnswerMultiSlot->answer.values;else->emptyList()}
                                slots.forEach {slot->AnswerField(slot.label,values.find {it.slotId==slot.id}?.text.orEmpty()) {value->
                                    val next=slots.map {field->SlotValue(field.id,if(field.id==slot.id)value else values.find {it.slotId==field.id}?.text.orEmpty())}
                                    commit(state.copy(draft=if(exercise is ExerciseCloze)AnswerCloze(next)else AnswerMultiSlot(next)))
                                }}
                            }
                            else -> error("Unsupported typed exercise")
                        }
                        else suggestions.forEach {(slot,options)->
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
                        val rubric=content.rubrics.first {it.exerciseId==exercise.id&&it.exerciseRevision==exercise.revision}
                        val evaluation=OfflineGrader.grade(exercise,rubric,ContractReader.json.encodeToJsonElement<Answer>(requireNotNull(state.draft)),if(state.assisted)listOf("hint")else emptyList())
                        val attempt=GuestAttempt(UUID.randomUUID().toString(),content.session.contentReleaseId,exercise.id,exercise.revision,requireNotNull(state.draft),if(state.assisted)listOf("hint")else emptyList(),Instant.now().toString(),state.attempts.size)
                        commit(state.copy(feedback=evaluation,graded=state.graded+1,correct=state.correct+if(evaluation.outcome=="correct")1 else 0,attempts=state.attempts+attempt,attemptIndices=state.attemptIndices+state.index))
                    }
                    TextButton(onClick={commit(state.copy(assisted=true))}){Text(text("Hint","Hinweis"))}
                    if(state.assisted) Text(if(german)exercise.hint.de else exercise.hint.en)
                    TextButton(onClick={commit(state.copy(index=state.index+1,draft=null,order=emptyList(),assisted=false,skippedIndices=state.skippedIndices+state.index))}){Text(text("Skip","Überspringen"))}
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
