package com.germanverbmaster.android.learner

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.focusable
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import com.germanverbmaster.android.foundation.contract.CompletedAnswer
import com.germanverbmaster.android.R
import com.germanverbmaster.android.foundation.FoundationTokens
import java.util.Locale

@Composable
internal fun studyCopy(german: Boolean): (Int) -> String {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val resources = remember(context, configuration, german) {
        context.createConfigurationContext(Configuration(configuration).apply {
            setLocale(if (german) Locale.GERMAN else Locale.ENGLISH)
        }).resources
    }
    return { resources.getString(it) }
}

@Composable
internal fun StudyHome(cache: LearnerCache, german: Boolean, busy: Boolean, fresh: Boolean,
                       start: () -> Unit, preferences: () -> Unit, refresh: () -> Unit,
                       openTopic: (String) -> Unit, openTarget: (String) -> Unit) {
    val c = studyCopy(german)
    val colors = MaterialTheme.colorScheme
    val available = cache.catalog?.targets?.sumOf { it.availableQuestionCount }
    val needs = cache.targets.count { it.state == "needs_practice" }
    val due = cache.targets.count { it.isDue && it.state != "needs_practice" }
    val hasEvidence = cache.targets.any { it.lastSequence > 0 }
    Text(c(R.string.study_tagline), style = MaterialTheme.typography.headlineLarge,
        modifier = Modifier.semantics { heading() })
    Surface(color = colors.primary, contentColor = colors.onPrimary, shape = RoundedCornerShape(20.dp)) {
        Column(Modifier.fillMaxWidth().padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("${c(R.string.study_practice)} · ${cache.profile?.preferences?.level ?: "B1"}", style = MaterialTheme.typography.labelLarge)
            Text(c(if (!hasEvidence) R.string.study_discover else if (needs + due == 0) R.string.study_quiet else R.string.study_attention),
                style = MaterialTheme.typography.headlineSmall, modifier = Modifier.semantics { heading() })
            if (cache.generatedAt == null) Text(c(R.string.study_snapshot_unavailable))
            else if (!hasEvidence) Text(c(R.string.study_discover_body))
            else {
                Text("${c(R.string.study_needs)}: $needs")
                Text("${c(R.string.study_retention)}: $due")
            }
            if (!fresh && cache.generatedAt != null) Text(c(R.string.study_saved_snapshot))
            Button(onClick = start, enabled = !busy && (cache.practice != null || cache.pending == null && (available ?: 0) > 0),
                colors = ButtonDefaults.buttonColors(containerColor = colors.onPrimary, contentColor = colors.primary),
                modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp)) {
                Text(c(if (cache.practice != null) R.string.study_continue else R.string.study_start))
            }
            if (available == null) {
                Text(c(R.string.study_availability_unknown))
                TextButton(onClick = refresh, enabled = !busy, colors = ButtonDefaults.textButtonColors(contentColor = colors.onPrimary)) { Text(c(R.string.study_retry)) }
            } else if (available == 0) {
                Text(c(R.string.study_no_questions))
                TextButton(onClick = preferences, enabled = !busy, colors = ButtonDefaults.textButtonColors(contentColor = colors.onPrimary)) { Text(c(R.string.study_preferences)) }
            }
        }
    }
    cache.catalog?.topics?.takeIf { it.isNotEmpty() }?.let { topics ->
        Text(c(R.string.study_explore), style = MaterialTheme.typography.titleLarge, modifier = Modifier.semantics { heading() })
        Text(c(R.string.study_explore_body), color = colors.onSurfaceVariant)
        topics.forEach { topic ->
            OutlinedButton(onClick = { openTopic(topic.id) }, enabled = !busy,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.onSurface),
                shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth().heightIn(min = 64.dp)) {
                Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(if (german) topic.title.de else topic.title.en, style = MaterialTheme.typography.titleMedium)
                    Text("${cache.catalog.targets.count { it.topicId == topic.id }} ${c(R.string.study_targets)}", color = colors.onSurfaceVariant)
                }
            }
        }
    }
    cache.targets.filter { it.state == "needs_practice" || it.isDue }.take(3).takeIf { it.isNotEmpty() }?.let { targets ->
        Text(c(R.string.study_next_focus), style = MaterialTheme.typography.titleLarge, modifier = Modifier.semantics { heading() })
        targets.forEach { target ->
            val title = cache.catalog?.targets?.find { it.id == target.targetId }?.title
            LearnerButton(title?.let { if (german) it.de else it.en } ?: c(R.string.study_target), !busy) { openTarget(target.targetId) }
        }
    }
}

@Composable
internal fun StudyFeedback(correct: Boolean, provisional: Boolean, german: Boolean, answer: String?, accepted: String,
                           explanation: String, assisted: Boolean, busy: Boolean, completedAnswer: CompletedAnswer? = null, next: () -> Unit) {
    val c = studyCopy(german)
    val tokens = if (isSystemInDarkTheme()) FoundationTokens.dark else FoundationTokens.light
    val tone = if (correct) tokens.success else tokens.attention
    val focus = remember { FocusRequester() }
    val visibility = remember { BringIntoViewRequester() }
    LaunchedEffect(answer, accepted, correct, provisional) { focus.requestFocus(); visibility.bringIntoView() }
    Surface(shape = RoundedCornerShape(14.dp), color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, tone)) {
        Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text((if (correct) "✓ " else "ⓘ ") + c(if (provisional) {
                if (correct) R.string.study_locally_correct else R.string.study_locally_incorrect
            } else if (correct) R.string.study_correct else R.string.study_incorrect),
                color = tone, style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.bringIntoViewRequester(visibility).focusRequester(focus).focusable()
                    .semantics { liveRegion = androidx.compose.ui.semantics.LiveRegionMode.Polite })
            if (assisted) Text(c(R.string.study_assisted), color = MaterialTheme.colorScheme.onSurfaceVariant)
            answer?.let { Text("${c(R.string.study_your_answer)}: $it") }
            if (completedAnswer == null) Text("${c(R.string.study_accepted)}: $accepted")
            else Text(buildAnnotatedString {
                append("${c(R.string.study_accepted)}: ")
                completedAnswer.parts.forEach { part ->
                    if (part.emphasis) withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(part.text) }
                    else append(part.text)
                }
            })
            Text(explanation)
            if (provisional) Text(c(R.string.study_confirmation_note), color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
    Button(onClick = next, enabled = !busy, modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp)) { Text(c(R.string.study_next)) }
}
