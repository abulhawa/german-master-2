package com.germanverbmaster.android.learner

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.dp
import com.germanverbmaster.android.foundation.ContractReader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString

@Composable
fun NativePrivacyExport(repository: LearnerRepository, german: Boolean, blocked: Boolean, onUpdated: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var busy by remember { mutableStateOf(false) }
    var pending by remember { mutableStateOf<String?>(null) }
    var status by remember { mutableStateOf<String?>(null) }
    fun label(en: String, de: String) = if (german) de else en
    val destination = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
        val data = pending
        pending = null
        if (uri == null || data == null) { busy = false }
        else scope.launch {
            try {
                withContext(Dispatchers.IO) {
                    requireNotNull(context.contentResolver.openOutputStream(uri, "wt")).use { it.write(data.toByteArray(Charsets.UTF_8)) }
                }
                status = label("Export saved. Keep it private; it contains your answers and preferences.", "Export gespeichert. Bewahre ihn vertraulich auf; er enthält deine Antworten und Einstellungen.")
            } catch (_: Exception) {
                status = label("Export could not be saved. Retry explicitly.", "Export konnte nicht gespeichert werden. Versuche es erneut.")
            } finally { busy = false }
        }
    }
    fun export(sync: Boolean) {
        if (busy || blocked) return
        busy = true; status = null
        scope.launch {
            try {
                try {
                    pending = withContext(Dispatchers.IO) { ContractReader.json.encodeToString(repository.exportLearner(sync)) }
                } finally { onUpdated() }
                destination.launch("german-master-learner-export.json")
            } catch (_: Exception) {
                pending = null; busy = false
                status = label("Export failed. Saved work remains available. Retry explicitly.", "Export fehlgeschlagen. Gespeicherte Daten bleiben erhalten. Versuche es erneut.")
            }
        }
    }
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(label("Privacy and account", "Datenschutz und Konto"), Modifier.semantics { heading() })
        Text(label("Export contains confirmed server data. Sync first to include pending answers and preferences. Unsubmitted drafts stay on this device. Choose where to save the file.", "Der Export enthält bestätigte Serverdaten. Synchronisiere zuerst ausstehende Antworten und Einstellungen. Nicht abgegebene Entwürfe bleiben auf diesem Gerät. Wähle den Speicherort für die Datei."))
        LearnerButton(label("Save confirmed data", "Bestätigte Daten speichern"), !busy && !blocked) { export(false) }
        LearnerButton(label("Sync saved work and save data", "Gespeicherte Vorgänge synchronisieren und Daten speichern"), !busy && !blocked) { export(true) }
        status?.let { Text(it, Modifier.semantics { liveRegion = LiveRegionMode.Polite }) }
    }
}
