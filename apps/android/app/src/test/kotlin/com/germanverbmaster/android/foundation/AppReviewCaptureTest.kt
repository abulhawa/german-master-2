package com.germanverbmaster.android.foundation

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import com.github.takahirom.roborazzi.captureRoboImage
import androidx.test.core.app.ApplicationProvider
import com.germanverbmaster.android.foundation.contract.*
import com.germanverbmaster.android.learner.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
import org.junit.*
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner.Parameters
import org.robolectric.ParameterizedRobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.io.File

/** Opt-in visual inventory. All account, progress and transport data are local fixtures. */
@RunWith(ParameterizedRobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk=[35],qualifiers="w412dp-h915dp-420dpi")
class AppReviewCaptureTest(private val scenario:String) {
    @get:Rule val compose=createComposeRule()
    companion object {
        @JvmStatic @Parameters(name="{0}") fun cases():List<Array<String>> {
            if(System.getProperty("gm.reviewOutput")==null)return listOf(arrayOf("disabled"))
            val formats=listOf("short_answer","choice","cloze","word_order","multi_slot","gap_choice","matching")
            val core=formats.flatMap { f->listOf("pre","selected","hint","correct","incorrect","pending","rejected").map {"question/$f/$it"} }+
                listOf("word_order","multi_slot","gap_choice","matching").map {"question/$it/partial"}+
                listOf("page/home","page/home-empty","page/home-resume","page/progress","page/progress-empty","page/topics","page/topic","page/target","page/target-mastered","page/target-unavailable","page/account","page/setup","page/onboarding","page/offline-account","page/local-only",
                    "control/export","control/signout","control/signout-confirm","control/deletion","control/deletion-confirm",
                    "summary/full","summary/partial","summary/pending","offline/pre","dialog/close","dialog/report","dialog/discard",
                    "auth/sign-in","auth/register","auth/reset","auth/sign-in-error","auth/register-sent","auth/reset-sent","auth/reset-error","auth/sign-in-busy")+
                (0..5).map {"guest/$it"}
            val extra=formats.map {"question/$it/pre"}+listOf("page/home","page/progress","page/account","page/setup","auth/sign-in","auth/register","auth/reset")
            return (core.map {arrayOf("en-light/$it")}+extra.map {arrayOf("de-light/$it")}+
                extra.map {arrayOf("en-dark/$it")}+formats.map {arrayOf("en-large/question/$it/pre")}).filter {System.getProperty("gm.reviewFilter")?.let {pattern->Regex(pattern).containsMatchIn(it[0])}?:true}
        }
    }
    private fun resource(name:String)=requireNotNull(javaClass.classLoader?.getResource(name)).readText()
    private val context get()=ApplicationProvider.getApplicationContext<Context>()
    private fun guest()=ContractReader.json.decodeFromString<GuestStarterContent>(context.assets.open("starter.json").bufferedReader().use {it.readText()})
    private fun profile(german:Boolean,setup:Boolean=true)=LearnerProfile("v2",0,setup,ProfilePreferences(if(german)"de" else "en","Europe/Berlin","B1",5))
    private fun repo(cache:LearnerCache,authenticated:Boolean=true):LearnerRepository {
        val store=object:LearnerStore {var value=cache;override fun read()=value;override fun write(value:LearnerCache){this.value=value}}
        val api=object:LearnerApi {
            override suspend fun profile()=requireNotNull(cache.profile)
            override suspend fun save(request:ProfileRequest)=profile()
            override suspend fun catalog()=requireNotNull(cache.catalog)
            override suspend fun targets(cursor:String)=TargetPage("v2","2026-10-20T00:00:00Z",cache.targets,"","00000000-0000-4000-8000-000000000999")
            override suspend fun sync(cursor:String)=SyncPage("v2",emptyList(),cursor,false)
        }
        return LearnerRepository(api,store,revoke=if(authenticated)({})else null).also {it.identityDeletionEnabled=authenticated}
    }
    private fun cache(german:Boolean,empty:Boolean=false):LearnerCache {
        val catalog=ContractReader.json.decodeFromString<Catalog>(resource("catalog.json"))
        val base=ContractReader.json.decodeFromString<TargetPage>(resource("target-page.json")).targets.first()
        val targets=if(empty)emptyList() else catalog.targets.mapIndexed {i,t->base.copy(targetId=t.id,state=listOf("needs_practice","improving","mastered")[i%3],qualifyingCheckCount=if(i%3==2)3 else i%2,everMastered=i%3==2,isDue=i%2==0)}
        return LearnerCache(profile=profile(german),catalog=catalog,targets=targets,generatedAt="2026-10-20T00:00:00Z")
    }
    private fun click(text:String) {val node=compose.onAllNodes(hasText(text) and hasClickAction()).onFirst();runCatching {node.performScrollTo()};node.performClick();compose.waitForIdle()}
    private fun wrong(exercise:Exercise,answer:Answer):Answer=when(answer) {
        is AnswerShortAnswer->AnswerShortAnswer("Berufen")
        is AnswerChoice->AnswerChoice((exercise as ExerciseChoice).options.first {it.id!=answer.optionId}.id)
        is AnswerCloze->answer.copy(values=answer.values.map {it.copy(text="im")})
        is AnswerMultiSlot->answer.copy(values=answer.values.map {it.copy(text="arbeiten")})
        is AnswerWordOrder->answer.copy(tokenIds=answer.tokenIds.reversed())
        is AnswerGapChoice->answer.copy(selections=answer.selections.map {s->s.copy(optionId=(exercise as ExerciseGapChoice).slots.single {it.id==s.slotId}.options.first {it.id!=s.optionId}.id)})
        is AnswerMatching->answer.copy(pairs=answer.pairs.mapIndexed {i,p->p.copy(rightId=answer.pairs[(i+1)%answer.pairs.size].rightId)})
    }
    @Test fun capture()=runBlocking {
        Assume.assumeTrue(System.getProperty("gm.reviewOutput")!=null)
        val parts=scenario.split('/');val variant=parts[0];val kind=parts[1];val name=parts[2];val state=parts.getOrElse(3){"default"}
        val german=variant.startsWith("de");val dark=variant.endsWith("dark")
        RuntimeEnvironment.setQualifiers("w412dp-h915dp-"+(if(dark)"night"else"notnight")+"-420dpi")
        if(variant.endsWith("large")) {val config=context.resources.configuration;config.fontScale=1.5f;@Suppress("DEPRECATION") context.resources.updateConfiguration(config,context.resources.displayMetrics)}
        val folder=File(System.getProperty("gm.reviewOutput"),"rendered-fixtures/$variant").apply {mkdirs()}
        val stem=parts.drop(1).joinToString("--")
        var action:String?=null
        when(kind) {
            "question","dialog","summary","offline"->{
                val g=guest();val modern=ContractReader.session(resource("practice-formats-session.json"))
                val questions=(modern.questions+g.session.questions).distinctBy {ContractReader.json.encodeToJsonElement<Exercise>(it.exercise).jsonObject.getValue("type").jsonPrimitive.content}
                val rubrics=ContractReader.json.decodeFromString<List<OfflineRubric>>(resource("practice-formats-rubrics.json"))+g.rubrics
                val q=questions.firstOrNull {ContractReader.json.encodeToJsonElement<Exercise>(it.exercise).jsonObject.getValue("type").jsonPrimitive.content==name}?:questions.first()
                val session=g.session.copy(questions=listOf(q));val rubric=rubrics.single {it.exerciseId==q.exercise.id&&it.exerciseRevision==q.exercise.revision}
                val accepted=rubric.acceptedAnswers.first();val bad=wrong(q.exercise,accepted)
                var p=NativePractice(foundationSessionRequest(),session=session)
                if(state!="pre"&&kind=="question")p=p.copy(draft=if(state=="incorrect"||state=="rejected")bad else accepted,order=(accepted as? AnswerWordOrder)?.tokenIds.orEmpty())
                if(state=="partial")p=p.copy(draft=when(accepted){is AnswerGapChoice->accepted.copy(selections=accepted.selections.take(1));is AnswerMatching->accepted.copy(pairs=accepted.pairs.take(1));is AnswerMultiSlot->accepted.copy(values=accepted.values.take(1));else->null},order=(accepted as? AnswerWordOrder)?.tokenIds?.take(2).orEmpty())
                if(state=="hint")p=p.copy(assisted=true)
                if(state=="correct"||state=="incorrect") {val result=OfflineGrader.grade(q.exercise,rubric,ContractReader.json.encodeToJsonElement<Answer>(requireNotNull(p.draft)),emptyList());p=p.copy(evaluation=result,graded=1,correct=if(result.outcome=="correct")1 else 0)}
                if(state=="pending")p=p.copy(pending=foundationAttempt(session,0,accepted,false,p.deviceId))
                if(state=="rejected")p=p.copy(rejected=true)
                if(kind=="summary") {
                    val req=SessionCompletionRequest("v2","00000000-0000-4000-8000-000000000555",if(name=="partial")"partial"else"full")
                    val partial=name=="partial"
                    p=p.copy(session=g.session,index=if(partial)2 else 5,graded=if(partial)1 else 4,skipped=1,correct=if(partial)1 else 3,completion=req,completionReceipt=if(name=="pending")null else SessionCompletionReceipt("v2",req.requestId,g.session.id,req.mode,5,if(partial)1 else 4,1,if(partial)1 else 3,"2026-10-20T00:00:00Z"))
                }
                if(kind=="offline") {val pack=PreparedPackReader.read(resource("practice-formats-pack.json"));p=NativePractice(foundationSessionRequest(),session=pack.sessions.first(),offlinePack=pack)}
                val repo=repo(cache(german).copy(practice=p))
                compose.setContent {FoundationTheme {Surface(Modifier.fillMaxSize()){Column(Modifier.verticalScroll(rememberScrollState()).padding(24.dp),verticalArrangement=Arrangement.spacedBy(16.dp)){NativePracticeView(p,german,false,{},repo,{}, {},{}, {})}}}}
                if(kind=="dialog")action=when(name){"close"->if(german)"Übung schließen"else"Close practice";"report"->if(german)"Problem mit der Übung melden"else"Report an exercise problem";else->if(german)"Optionen für gespeicherte Übungen"else"Saved practice options"}
            }
            "auth"->{
                val mode=name.substringBefore('-').let {if(it=="sign")"sign-in"else it}
                compose.setContent {FoundationTheme {NativeAccountEntry(mode,german,{}, {}) {_,_,_->if(name.endsWith("error"))error("local capture failure");if(name.endsWith("busy"))kotlinx.coroutines.awaitCancellation()}}}
                if(name.endsWith("sent")||name.endsWith("error")||name.endsWith("busy")) {
                    compose.onNodeWithText(if(german)"E-Mail"else"Email").performTextInput("review@example.test")
                    if(mode!="reset")compose.onNodeWithText(if(german)"Passwort"else"Password").performTextInput("synthetic-review-password")
                    if(mode=="register")compose.onNodeWithText(if(german)"Passwort bestätigen"else"Confirm password").performTextInput("synthetic-review-password")
                    action=if(mode=="reset")if(german)"E-Mail zum Zurücksetzen senden"else"Send reset email" else if(mode=="register")if(german)"Konto erstellen"else"Create account" else if(german)"Anmelden"else"Sign in"
                }
            }
            "guest"->{
                val index=name.toInt();val file=File(context.cacheDir,"capture-guest-$scenario".replace('/','-'))
                NativeGuestStore(file).write(NativeGuestState(started=index>0,index=if(index==0)0 else index-1))
                compose.setContent {FoundationTheme {NativeGuestEntry(file,german,{}, {})}}
            }
            "control"->{
                val repo=repo(cache(german))
                compose.setContent {FoundationTheme {Surface(Modifier.fillMaxSize()){Column(Modifier.verticalScroll(rememberScrollState()).padding(24.dp),verticalArrangement=Arrangement.spacedBy(16.dp)){
                    when {name=="export"->NativePrivacyExport(repo,german,false){};name.startsWith("signout")->NativePrivacySignOut(repo,german,false){};else->NativeIdentityDeletionControl(repo,german,false){}}
                }}}}
                if(name=="signout-confirm")action=if(german)"Lokale Daten entfernen und abmelden…"else"Remove local work and sign out…"
                if(name=="deletion-confirm")action=if(german)"Konto löschen…"else"Delete account…"
            }
            else->{
                var c=cache(german,name.endsWith("empty"))
                if(name=="onboarding")c=c.copy(profile=profile(german,false))
                if(name=="offline-account")c=c.copy(preparedPack=PreparedPackReader.read(resource("practice-formats-pack.json")))
                if(name=="home-resume")c=c.copy(practice=NativePractice(foundationSessionRequest(),session=guest().session))
                if(name=="target-unavailable")c=c.copy(catalog=c.catalog!!.copy(targets=c.catalog!!.targets.map {it.copy(availableQuestionCount=0)}))
                val repo=repo(c)
                compose.setContent {FoundationTheme {LearnerShell(repo,{},name=="local-only")}}
                compose.waitUntil(10000){compose.onAllNodes(hasText(if(german)"Konto"else"Account") and hasClickAction()).fetchSemanticsNodes().isNotEmpty()}
                when {
                    name.startsWith("progress")->click(if(german)"Lernstand"else"Progress")
                    name=="topics"||name=="topic"||name.startsWith("target")->{
                        click(if(german)"Themen"else"Topics")
                        if(name!="topics") {click(if(german)c.catalog!!.topics.first().title.de else c.catalog!!.topics.first().title.en)
                            if(name.startsWith("target")){val t=if(name=="target-mastered")c.catalog!!.targets[2]else c.catalog!!.targets.first();click((if(german)t.title.de else t.title.en)+" · "+t.level)}}
                    }
                    name=="account"||name=="offline-account"||name=="setup"->{click(if(german)"Konto"else"Account");if(name=="setup")click(if(german)"Einstellungen ändern"else"Edit preferences")}
                }
            }
        }
        compose.waitForIdle()
        action?.let {click(it)}
        if(kind=="dialog"&&name=="discard")click(if(german)"Lokale Sitzung verwerfen…"else"Discard local session…")
        val roots=compose.onAllNodes(isRoot()).fetchSemanticsNodes()
        if(roots.size==1&&compose.onAllNodes(hasScrollAction()).fetchSemanticsNodes().isNotEmpty()) {
            compose.onAllNodes(hasScrollAction()).onFirst().performSemanticsAction(androidx.compose.ui.semantics.SemanticsActions.ScrollBy){it(0f,-100000f)}
            compose.waitForIdle()
        }
        fun save(node:SemanticsNodeInteraction,file:File) {node.captureRoboImage(file)}
        save(compose.onAllNodes(isRoot()).onLast(),File(folder,"$stem--viewport.png"))
        val scrolls=compose.onAllNodes(hasScrollAction()).fetchSemanticsNodes()
        if(roots.size==1&&scrolls.isNotEmpty()) {
            compose.onAllNodes(hasScrollAction()).onFirst().performTouchInput {swipeUp(durationMillis=300)}
            compose.onAllNodes(hasScrollAction()).onFirst().performTouchInput {swipeUp(durationMillis=300)}
            compose.waitForIdle();save(compose.onRoot(),File(folder,"$stem--lower.png"))
        }
        File(folder,"$stem.json").writeText(buildJsonObject {put("scenario",scenario);put("source","Robolectric native rendering of production Compose components with local fixtures");put("widthDp",412);put("heightDp",915);put("fontScale",if(variant.endsWith("large"))1.5 else 1.0);put("locale",if(german)"de"else"en");put("theme",if(dark)"dark"else"light")}.toString())
    }
}
