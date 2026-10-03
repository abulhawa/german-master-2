package com.germanverbmaster.android.foundation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.dp
import com.germanverbmaster.android.R
import com.germanverbmaster.android.foundation.contract.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun FoundationBackendPreview(api: FoundationApi) {
    val scope = rememberCoroutineScope()
    val request = remember { foundationSessionRequest() }
    val device = remember { UUID.randomUUID().toString() }
    var session by remember { mutableStateOf<Session?>(null) }
    var index by remember { mutableIntStateOf(0) }
    var answer by remember { mutableStateOf<Answer?>(null) }
    var assisted by remember { mutableStateOf(false) }
    var hint by remember { mutableStateOf(false) }
    var pending by remember { mutableStateOf<Attempt?>(null) }
    var evaluation by remember { mutableStateOf<Evaluation?>(null) }
    var busy by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf(false) }
    var rejected by remember { mutableStateOf(false) }

    suspend fun load() {
        busy = true; error = false
        try { session = api.createSession(request) }
        catch (cancel: CancellationException) { throw cancel }
        catch (_: Exception) { error = true }
        finally { busy = false }
    }
    LaunchedEffect(api) { load() }
    Surface(color=MaterialTheme.colorScheme.background, modifier=Modifier.fillMaxSize()) {
        Column(Modifier.safeDrawingPadding().imePadding().verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement=Arrangement.spacedBy(16.dp)) {
            Text(stringResource(R.string.foundation_title), style=MaterialTheme.typography.titleLarge)
            Text(stringResource(R.string.foundation_backend_notice))
            PracticeCard {
                val currentSession = session
                val question = currentSession?.questions?.getOrNull(index)
                if (currentSession == null) {
                    Text(stringResource(R.string.foundation_loading), modifier=Modifier.semantics { heading() })
                    Button(onClick={scope.launch { load() }}, enabled=!busy) { Text(stringResource(R.string.foundation_retry)) }
                } else if (question == null) {
                    Text(stringResource(R.string.foundation_complete), style=MaterialTheme.typography.headlineSmall, modifier=Modifier.semantics { heading(); liveRegion=LiveRegionMode.Polite })
                    Text(stringResource(R.string.foundation_summary))
                } else {
                    val exercise = question.exercise
                    Text(stringResource(R.string.foundation_position,index+1,currentSession.questions.size))
                    Text(exercise.prompt, style=MaterialTheme.typography.headlineSmall, modifier=Modifier.semantics { heading() })
                    // Android resources select interface language; prompts remain German.
                    val german = androidx.compose.ui.platform.LocalConfiguration.current.locales[0].language == "de"
                    Text(if(german) exercise.instruction.de else exercise.instruction.en)
                    if (pending == null && !busy) key(question.id) {
                        ExerciseInput(exercise) { answer = it }
                        TextButton(onClick={ hint=!hint; if(hint) assisted=true }) { Text(stringResource(R.string.foundation_hint)) }
                        if(hint) Text(if(german) exercise.hint.de else exercise.hint.en)
                    } else pending?.let { Text(stringResource(R.string.foundation_answer) + ": " + foundationAnswerText(it.answer, exercise)) }
                    if (evaluation == null) Button(onClick={
                        if (!busy && !rejected) scope.launch {
                            val submitted = pending ?: answer?.let { foundationAttempt(currentSession,index,it,assisted,device) } ?: return@launch
                            pending=submitted; busy=true; error=false
                            try {
                                when(val result=api.submit(submitted)) {
                                    is AttemptAcknowledgment -> evaluation=result.evaluation
                                    is AttemptDuplicate -> evaluation=result.evaluation
                                    is AttemptRejection -> { rejected=true; error=true }
                                }
                            } catch(cancel: CancellationException) { throw cancel }
                            catch(_: Exception) { error=true }
                            finally { busy=false }
                        }
                    }, enabled=answer!=null && !busy && !rejected, modifier=Modifier.fillMaxWidth().heightIn(min=48.dp)) {
                        Text(stringResource(if(busy) R.string.foundation_sending else if(pending!=null) R.string.foundation_retry else R.string.foundation_submit))
                    }
                    evaluation?.let { confirmed ->
                        Text(stringResource(if(confirmed.outcome=="correct") R.string.foundation_correct else R.string.foundation_incorrect), modifier=Modifier.semantics { liveRegion=LiveRegionMode.Polite })
                        if(confirmed.assisted) Text(stringResource(R.string.foundation_assisted))
                        Text(stringResource(R.string.foundation_accepted_answer) + ": " + foundationAnswerText(confirmed.acceptedAnswer,exercise))
                        Text(if(german) confirmed.explanation.de else confirmed.explanation.en)
                        Button(onClick={ index++; answer=null; pending=null; evaluation=null; assisted=false; hint=false; error=false }, modifier=Modifier.fillMaxWidth().heightIn(min=48.dp)) { Text(stringResource(R.string.foundation_continue)) }
                    }
                }
                if(error) Text(stringResource(if(rejected) R.string.foundation_rejected else R.string.foundation_connection_error), modifier=Modifier.semantics { liveRegion=LiveRegionMode.Polite })
            }
        }
    }
}
