package com.germanverbmaster.android.learner

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.*

/** Passwords are transient; only explicit submission contacts the identity provider. */
@Composable
fun NativeAccountEntry(mode: String, german: Boolean, language: () -> Unit,
                       navigate: (String?) -> Unit, back: (() -> Unit)? = null, socialSignIn: (suspend (String) -> Unit)? = null, submit: suspend (String,String,String) -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember(mode) { mutableStateOf("") }
    var confirmation by remember(mode) { mutableStateOf("") }
    var busy by remember { mutableStateOf(false) }
    var failed by remember(mode) { mutableStateOf(false) }
    var sent by remember(mode) { mutableStateOf(false) }
    var cooldown by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    fun text(en:String,de:String)=if(german) de else en
    LaunchedEffect(cooldown) { if(cooldown) {delay(60_000);cooldown=false} }
    BackHandler(enabled=!busy) { if(back!=null)back() else navigate(null) }
    val title=when(mode) {
        "register" -> text("Create account","Konto erstellen")
        "reset" -> text("Reset password","Passwort zurücksetzen")
        else -> text("Sign in","Anmelden")
    }
    Surface(Modifier.fillMaxSize(),color=MaterialTheme.colorScheme.background) {
        Column(Modifier.safeDrawingPadding().imePadding().verticalScroll(rememberScrollState()).padding(24.dp),verticalArrangement=Arrangement.spacedBy(16.dp)) {
            Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween,verticalAlignment=androidx.compose.ui.Alignment.CenterVertically) {
                TextButton(onClick={navigate(null)},enabled=!busy){Text(text("Home","Startseite"))}
                TextButton(onClick=language,enabled=!busy){Text("English / Deutsch")}
            }
            StudyPageTitle(title)
            Text(text("Save online progress across devices. Earlier practice stays on this device.","Speichere Online-Fortschritt geräteübergreifend. Bisherige Übungen bleiben auf diesem Gerät."),color=MaterialTheme.colorScheme.onSurfaceVariant,style=MaterialTheme.typography.bodyMedium)
            if(NativeReleaseCapabilities.socialSignIn && mode!="reset" && socialSignIn!=null) {
                listOf("google" to text("Continue with Google","Mit Google fortfahren"),
                    "azure" to text("Continue with Microsoft","Mit Microsoft fortfahren")).forEach { (provider,label) ->
                    OutlinedButton(onClick={
                        busy=true;failed=false;password="";confirmation=""
                        scope.launch {
                            try {socialSignIn(provider)}
                            catch(cancel:CancellationException){throw cancel}
                            catch(_:Exception){failed=true}
                            finally {busy=false}
                        }
                    },enabled=!busy,modifier=Modifier.fillMaxWidth()) { LoginProviderIcon(provider);Spacer(Modifier.width(8.dp));Text(label) }
                }
                Text(text("Or use email","Oder mit E-Mail"))
            }
            OutlinedTextField(email,{email=it},label={Text(text("Email","E-Mail"))},singleLine=true,enabled=!busy,
                keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Email),modifier=Modifier.fillMaxWidth())
            if(mode!="reset") {
                OutlinedTextField(password,{password=it},label={Text(text("Password","Passwort"))},singleLine=true,enabled=!busy,
                    visualTransformation=PasswordVisualTransformation(),modifier=Modifier.fillMaxWidth())
                if(mode=="register") {
                    Text(text("Use at least 8 characters.","Verwende mindestens 8 Zeichen."))
                    OutlinedTextField(confirmation,{confirmation=it},label={Text(text("Confirm password","Passwort bestätigen"))},singleLine=true,enabled=!busy,
                        visualTransformation=PasswordVisualTransformation(),modifier=Modifier.fillMaxWidth())
                }
            } else Text(text("Open the email link to choose a new password in your browser, then return here to sign in.","Öffne den E-Mail-Link und wähle im Browser ein neues Passwort. Melde dich anschließend hier an."))
            val valid=android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches() &&
                (mode=="reset" || password.isNotEmpty()) && (mode!="register" || password.length>=8 && password==confirmation)
            StudyPrimary(if(mode=="reset") text("Send reset email","E-Mail zum Zurücksetzen senden") else title,!busy&&(!cooldown||mode=="sign-in")&&valid) {
                if(!busy&&(!cooldown||mode=="sign-in")) {
                    busy=true;failed=false;sent=false
                    val address=email.trim();val secret=password
                    scope.launch {
                        try {submit(mode,address,secret);sent=true}
                        catch(cancel:CancellationException){throw cancel}
                        catch(_:Exception){failed=true}
                        finally {password="";confirmation="";busy=false;if(mode!="sign-in")cooldown=true}
                    }
                }
            }
            if(busy) Text(text("Please wait…","Bitte warten…"),Modifier.semantics {liveRegion=LiveRegionMode.Polite})
            if(sent&&mode!="sign-in") Text(if(mode=="reset") text("If an account matches this email, a reset link has been requested. Check your inbox and spam folder.","Falls zu dieser Adresse ein Konto existiert, wurde ein Link angefordert. Prüfe Posteingang und Spamordner.") else text("Check your email to confirm your account, then return here to sign in. If you already have an account, sign in or reset your password.","Prüfe deine E-Mails und bestätige dein Konto. Melde dich danach hier an. Hast du bereits ein Konto, melde dich an oder setze dein Passwort zurück."),Modifier.semantics {liveRegion=LiveRegionMode.Polite})
            if(failed) Text(text("Could not complete this request. Check your details and connection, then try again. Your saved work is preserved.","Die Anfrage konnte nicht abgeschlossen werden. Prüfe deine Angaben und Verbindung und versuche es erneut. Gespeicherte Übungen bleiben erhalten."),Modifier.semantics {liveRegion=LiveRegionMode.Polite})
            if(cooldown) Text(text("Wait a minute before requesting another email.","Warte eine Minute, bevor du eine weitere E-Mail anforderst."))
            if(mode!="sign-in") TextButton(onClick={navigate("sign-in")},enabled=!busy){Text(text("Back to sign in","Zurück zur Anmeldung"))}
            if(mode=="sign-in") {
                TextButton(onClick={navigate("register")},enabled=!busy){Text(text("Create account","Konto erstellen"))}
                TextButton(onClick={navigate("reset")},enabled=!busy){Text(text("Forgot password?","Passwort vergessen?"))}
            }
        }
    }
}
