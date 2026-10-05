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
fun NativePrivacySignOut(repository: LearnerRepository,german:Boolean,blocked:Boolean,onUpdated:()->Unit) {
    val scope = rememberCoroutineScope()
    var confirming by remember { mutableStateOf(false) }
    var busy by remember { mutableStateOf(false) }
    var failed by remember { mutableStateOf(false) }
    fun label(en:String,de:String)=if(german) de else en
    fun run(resume:Boolean,remove:Boolean=false) {
        if(busy||blocked) return
        busy=true;failed=false
        scope.launch {
            try { withContext(Dispatchers.IO) { if(resume) repository.resumeLocalFixture() else repository.signOut(remove) } }
            catch(cancel:kotlinx.coroutines.CancellationException) { throw cancel }
            catch(_:Exception) { failed=true }
            finally { busy=false;onUpdated() }
        }
    }
    Column(verticalArrangement=Arrangement.spacedBy(16.dp)) {
        Text(if(repository.authenticatedAccount) label("Sign out","Abmelden") else label("Sign out of local preview","Aus lokaler Vorschau abmelden"),Modifier.semantics { heading() })
        if(repository.state.signedOut) {
            Text(label("Local learner access is paused. Practice and normal sync are blocked. Retained work belongs only to this learner.","Lokaler Lernzugriff pausiert. Üben und normale Synchronisierung sind gesperrt. Behaltene Daten gehören nur zu diesem Lerner."))
            if(repository.state.localRemovalPending || repository.state.authRevocationPending) LearnerButton(label("Finish saved sign-out","Gespeichertes Abmelden abschließen"),!busy&&!blocked) { run(false,true) }
            else LearnerButton(if(repository.authenticatedAccount) label("Continue after signing in to this account","Nach Anmeldung bei diesem Konto fortsetzen") else label("Resume the same local fixture learner","Denselben lokalen Beispiel-Lerner fortsetzen"),!busy&&!blocked) { run(true) }
        } else {
            Text(if(repository.authenticatedAccount) label("Sync saved requests and retain drafts for this account, or remove its local work. Then revoke this device's sign-in session. Local removal does not delete server data.","Synchronisiere gespeicherte Anfragen und behalte Entwürfe für dieses Konto, oder entferne seine lokalen Daten. Danach wird die Anmeldung dieses Geräts widerrufen. Lokale Entfernung löscht keine Serverdaten.") else label("Sync saved requests and retain unsubmitted drafts for this learner, or explicitly remove local work. Local removal does not delete server data. This fixture does not manage a host sign-in account.","Synchronisiere gespeicherte Anfragen und behalte nicht abgegebene Entwürfe für diesen Lerner, oder entferne ausdrücklich lokale Daten. Lokale Entfernung löscht keine Serverdaten. Diese Vorschau verwaltet kein Anmeldekonto."))
            LearnerButton(label("Sync saved work and sign out","Synchronisieren und abmelden"),!busy&&!blocked) { run(false) }
            if(!confirming) LearnerButton(label("Remove local work and sign out…","Lokale Daten entfernen und abmelden…"),!busy&&!blocked) { confirming=true }
            else {
                Text(label("Remove this learner's downloads, drafts and unsynced work from this device?","Downloads, Entwürfe und nicht synchronisierte Vorgänge dieses Lerners von diesem Gerät entfernen?"))
                LearnerButton(label("Confirm local removal and sign out","Lokale Entfernung und Abmelden bestätigen"),!busy&&!blocked) { run(false,true) }
                LearnerButton(label("Keep my local work","Meine lokalen Daten behalten"),!busy) { confirming=false }
            }
        }
        if(failed) Text(label("Sign-out could not finish. Sync failure keeps your work. Retry explicitly.","Abmelden nicht abgeschlossen. Bei Synchronisierungsfehlern bleiben die Daten erhalten. Versuche es ausdrücklich erneut."),Modifier.semantics { liveRegion=LiveRegionMode.Polite })
    }
}

@Composable
fun NativePrivacyDeletion(repository: LearnerRepository, german: Boolean, blocked: Boolean, onUpdated: () -> Unit) {
    val scope = rememberCoroutineScope()
    var confirming by remember { mutableStateOf(false) }
    var busy by remember { mutableStateOf(false) }
    var failed by remember { mutableStateOf(false) }
    fun label(en: String,de: String) = if(german) de else en
    val pending = repository.state.deletion != null
    val confirmed = repository.state.deletionReceipt != null
    val complete = repository.state.deletionLocalComplete
    fun remove() {
        if (busy || blocked) return
        busy = true; failed = false
        scope.launch {
            try { withContext(Dispatchers.IO) { repository.deleteLearner() } }
            catch (cancel: kotlinx.coroutines.CancellationException) { throw cancel }
            catch (_: Exception) { failed = true }
            finally { busy = false; onUpdated() }
        }
    }
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(label("Delete local service learner data","Lerndaten im lokalen Dienst löschen"),Modifier.semantics { heading() })
        Text(if(complete) label("Learner data deleted from the local service and this device. This learner cannot resume practice.","Lerndaten im lokalen Dienst und auf diesem Gerät gelöscht. Dieser Lerner kann nicht weiterüben.")
            else if(confirmed) label("Deletion confirmed. This learner cannot resume practice. Finish local removal if needed.","Löschung bestätigt. Dieser Lerner kann nicht weiterüben. Schließe bei Bedarf die lokale Entfernung ab.")
            else if(pending) label("Deletion saved. Practice and sync are blocked. Local work remains until confirmation. Retry explicitly.","Löschanfrage gespeichert. Üben und Synchronisieren sind gesperrt. Lokale Daten bleiben bis zur Bestätigung erhalten. Versuche es ausdrücklich erneut.")
            else label("Deletes this learner's data from the local service and this device, including unsynced work. Does not delete a host sign-in account. Export first if you want a copy.","Löscht die Lerndaten im lokalen Dienst und auf diesem Gerät, einschließlich nicht synchronisierter Vorgänge. Löscht kein Anmeldekonto. Exportiere zuerst eine Kopie, falls gewünscht."))
        if(pending) { if(!complete) LearnerButton(if(confirmed) label("Finish local removal","Lokale Entfernung abschließen") else label("Retry saved deletion","Gespeicherte Löschung erneut senden"),!busy && !blocked) { remove() } }
        else if(!confirming) LearnerButton(label("Delete learner data…","Lerndaten löschen…"),!busy && !blocked) { confirming = true }
        else {
            Text(label("Confirm removal of all confirmed data and unsynced work? Once saved, the deletion request blocks practice and normal sync.","Alle bestätigten Daten und nicht synchronisierten Vorgänge löschen? Die gespeicherte Löschanfrage sperrt Üben und normale Synchronisierung."))
            LearnerButton(label("Confirm deletion of learner data","Löschen der Lerndaten bestätigen"),!busy && !blocked) { remove() }
            LearnerButton(label("Keep my data","Meine Daten behalten"),!busy) { confirming = false }
        }
        if(failed) Text(label("Deletion or local removal failed. The saved request remains. Retry explicitly.","Löschung oder lokale Entfernung fehlgeschlagen. Die gespeicherte Anfrage bleibt erhalten. Versuche es ausdrücklich erneut."),Modifier.semantics { liveRegion = LiveRegionMode.Polite })
    }
}

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
