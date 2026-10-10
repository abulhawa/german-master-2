package com.germanverbmaster.android.learner

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.germanverbmaster.android.foundation.contract.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun NativeIdentityDeletionControl(repository: LearnerRepository, german: Boolean, blocked: Boolean, onUpdated: () -> Unit) {
    var confirm by remember { mutableStateOf(false) }
    var busy by remember { mutableStateOf(false) }
    var failed by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val scope=rememberCoroutineScope()
    val marker=repository.state.identityDeletion
    fun label(en: String,de: String)=if(german)de else en
    fun run(proof: IdentityDeletionProof? = null) {
        if(busy||blocked)return
        busy=true;failed=false;password=""
        scope.launch {
            try {withContext(Dispatchers.IO) {repository.deleteIdentity(proof)};confirm=false}
            catch(cancel:kotlinx.coroutines.CancellationException) {throw cancel}
            catch(_:Exception) {failed=true}
            finally {busy=false;onUpdated()}
        }
    }
    Column(verticalArrangement=Arrangement.spacedBy(16.dp)) {
        Text(label("Delete account","Konto löschen"),Modifier.semantics {heading()})
        Text(if(marker?.complete==true)label("Your account and its saved work on this device have been deleted.","Dein Konto und seine gespeicherten Übungen auf diesem Gerät wurden gelöscht.")
            else if(marker!=null)label("Deletion saved. Practice and sync are blocked. Check status or explicitly reauthenticate to retry delivery.","Löschung gespeichert. Üben und Synchronisieren gesperrt. Prüfe den Status oder bestätige deine Anmeldung, um die Löschung erneut zu senden.")
            else label("Deletes your account, saved learning data and unsynced work. Download your data first if you want a copy.","Löscht dein Konto, gespeicherte Lerndaten und noch nicht synchronisierte Übungen. Lade zuerst deine Daten herunter, wenn du eine Kopie möchtest."))
        if(marker!=null) LearnerButton(if(marker.receipt is IdentityDeletionCompleted)label("Finish local removal","Lokale Entfernung abschließen") else label("Check saved deletion status","Status der gespeicherten Löschung prüfen"),!busy&&!blocked) {run()}
        if(marker?.complete!=true && marker?.receipt !is IdentityDeletionCompleted) {
            if(!confirm)LearnerButton(if(marker==null)label("Delete account…","Konto löschen…") else label("Reauthenticate and retry deletion","Anmeldung bestätigen und Löschung erneut senden"),!busy&&!blocked) {confirm=true}
            else {
                Text(label("Confirm deletion with your current email and password.","Bestätige die Löschung mit deiner aktuellen E-Mail und deinem Passwort."))
                OutlinedTextField(email,{email=it.take(320)},label={Text(label("Email","E-Mail"))},singleLine=true,enabled=!busy,modifier=Modifier.fillMaxWidth())
                OutlinedTextField(password,{password=it.take(4096)},label={Text(label("Current password","Aktuelles Passwort"))},singleLine=true,visualTransformation=PasswordVisualTransformation(),enabled=!busy,modifier=Modifier.fillMaxWidth())
                LearnerButton(label("Confirm account deletion","Kontolöschung bestätigen"),!busy&&!blocked&&email.isNotBlank()&&password.isNotEmpty()) {run(IdentityDeletionProof(email,password))}
                LearnerButton(label("Keep my account","Mein Konto behalten"),!busy) {confirm=false;password=""}
            }
        }
        if(failed)Text(label("Deletion or cleanup failed. The saved request remains. Retry explicitly.","Löschung oder Entfernung fehlgeschlagen. Die gespeicherte Anfrage bleibt. Versuche es ausdrücklich erneut."),Modifier.semantics {liveRegion=LiveRegionMode.Polite})
    }
}
