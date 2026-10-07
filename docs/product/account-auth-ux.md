# Account, authentication and privacy UX handoff

Status: implementation-ready product/copy specification  
Prepared: 7 October 2026  
Baseline: [German Master 2.0 blueprint](blueprint.md)  
Related: [guest practice UX](guest-practice-ux.md), [learner screens UX](learner-screens-ux.md)

This document completes the ChatGPT account-feature deliverable from
[plan-of-work.md](plan-of-work.md). It defines learner-facing wording and flow
for sign-up, email confirmation, sign-in, password recovery, session expiry,
sign-out, export and account deletion on web and Android.

It is a design/copy handoff. It does not configure SMTP, enable deletion,
change authentication policy or authorize deployment.

## 1. Product decisions

Authentication is optional before first useful practice.

Create/sign-in flows exist to:
- save confirmed progress across devices,
- attach eligible guest attempts only after explicit confirmation,
- recover access,
- export owned learner data,
- sign out safely,
- delete the account and learner data explicitly.

Do not imply that signing in is required to try German Master.

Do not use authentication errors that expose whether an email address is
registered unless the identity provider intentionally returns that information
under an approved anti-enumeration policy.

Never delete local guest work merely because authentication failed, expired or
was canceled.

## 2. Account entry points

Account entry may be opened from:
- guest setup,
- guest summary,
- guest Home,
- the signed-in Account surface,
- an expired-session prompt,
- a saved-work verification prompt.

When guest attempts exist, show before the form:

EN: **Your guest practice stays on this device while you sign in.**  
DE: **Deine Gastübungen bleiben auf diesem Gerät, während du dich anmeldest.**

When a count is available:

EN: **{n} eligible attempts will not be saved to this account automatically.**  
DE: **{n} geeignete Versuche werden nicht automatisch in diesem Konto gespeichert.**

Cancel/back:
- EN: Back to guest practice
- DE: Zurück zur Gastübung

## 3. Sign-in

Title:
- EN: Sign in to German Master
- DE: Bei German Master anmelden

Fields:
- EN: Email
- DE: E-Mail
- EN: Password
- DE: Passwort

Primary:
- EN: Sign in
- DE: Anmelden

Busy:
- EN: Signing in…
- DE: Anmeldung läuft…

Secondary:
- EN: Create account
- DE: Konto erstellen

Recovery:
- EN: Forgot your password?
- DE: Passwort vergessen?

Unconfirmed-account action:
- EN: Resend confirmation email
- DE: Bestätigungs-E-Mail erneut senden

Generic failure:
- EN: Sign-in could not be completed. Check your details and try again.
- DE: Die Anmeldung konnte nicht abgeschlossen werden. Prüfe deine Angaben und versuche es erneut.

Connectivity failure:
- EN: German Master can’t reach the account service right now. Saved practice stays on this device.
- DE: German Master kann den Kontodienst gerade nicht erreichen. Gespeicherte Übungen bleiben auf diesem Gerät.

Do not clear the email field after a failed sign-in. The password field may be
cleared after a failed or completed submission.

## 4. Create account

Title:
- EN: Create your German Master account
- DE: German-Master-Konto erstellen

Supporting copy:
- EN: Save confirmed progress across devices and keep your practice history tied to your account.
- DE: Speichere bestätigten Fortschritt auf mehreren Geräten und verknüpfe deine Übungshistorie mit deinem Konto.

Fields:
- EN: Email
- DE: E-Mail
- EN: Password
- DE: Passwort

If a display name remains part of the product:
- EN: Name (optional)
- DE: Name (optional)

Primary:
- EN: Create account
- DE: Konto erstellen

Busy:
- EN: Creating account…
- DE: Konto wird erstellt…

Switch:
- EN: Already have an account? Sign in
- DE: Du hast bereits ein Konto? Anmelden

Password guidance must reflect the provider's actual enforced policy. Do not
invent stronger requirements in copy than the backend enforces.

Registration failure:
- EN: Your account could not be created. Try again.
- DE: Dein Konto konnte nicht erstellt werden. Versuche es erneut.

If the provider safely exposes a specific validation failure, use specific
field-level copy instead of the generic message.

## 5. Email confirmation

After registration when no authenticated session is returned:

Heading:
- EN: Check your email
- DE: Prüfe deine E-Mails

Body:
- EN: We sent a confirmation link to {email}. Open it to confirm your account, then return to German Master and sign in.
- DE: Wir haben einen Bestätigungslink an {email} gesendet. Öffne ihn, bestätige dein Konto und melde dich danach bei German Master an.

Actions:
- EN: Resend email
- DE: E-Mail erneut senden
- EN: Back to sign in
- DE: Zurück zur Anmeldung

Resend success:
- EN: Confirmation email sent. Check your inbox and spam folder.
- DE: Bestätigungs-E-Mail gesendet. Prüfe deinen Posteingang und Spam-Ordner.

Resend failure:
- EN: The confirmation email could not be sent right now. Try again later.
- DE: Die Bestätigungs-E-Mail konnte gerade nicht gesendet werden. Versuche es später erneut.

Do not claim delivery merely because the provider accepted the request. Use
"sent" only after the configured provider reports successful submission, and
retain operational evidence separately.

### Confirmation-link return

Success:
- EN: Email confirmed. You can sign in now.
- DE: E-Mail bestätigt. Du kannst dich jetzt anmelden.

Already confirmed:
- EN: This email is already confirmed. Sign in to continue.
- DE: Diese E-Mail ist bereits bestätigt. Melde dich an, um fortzufahren.

Expired/invalid link:
- EN: This confirmation link is no longer valid. Request a new email.
- DE: Dieser Bestätigungslink ist nicht mehr gültig. Fordere eine neue E-Mail an.

## 6. Password recovery

Entry:
- EN: Forgot your password?
- DE: Passwort vergessen?

Recovery title:
- EN: Reset your password
- DE: Passwort zurücksetzen

Body:
- EN: Enter the email for your German Master account. If a reset can be sent, you’ll receive a link by email.
- DE: Gib die E-Mail-Adresse deines German-Master-Kontos ein. Wenn ein Zurücksetzen möglich ist, erhältst du einen Link per E-Mail.

Field:
- EN: Email
- DE: E-Mail

Primary:
- EN: Send reset link
- DE: Link zum Zurücksetzen senden

Success, anti-enumeration-safe:
- EN: If an account can receive a reset email, a link has been sent. Check your inbox and spam folder.
- DE: Wenn für dieses Konto eine E-Mail zum Zurücksetzen gesendet werden kann, wurde ein Link verschickt. Prüfe deinen Posteingang und Spam-Ordner.

Failure:
- EN: The reset request could not be completed right now. Try again later.
- DE: Die Anfrage zum Zurücksetzen konnte gerade nicht abgeschlossen werden. Versuche es später erneut.

### New password screen

Title:
- EN: Choose a new password
- DE: Neues Passwort festlegen

Fields:
- EN: New password
- DE: Neues Passwort
- EN: Confirm new password
- DE: Neues Passwort bestätigen

Mismatch:
- EN: The passwords do not match.
- DE: Die Passwörter stimmen nicht überein.

Primary:
- EN: Save new password
- DE: Neues Passwort speichern

Success:
- EN: Password updated. Sign in with your new password.
- DE: Passwort aktualisiert. Melde dich mit deinem neuen Passwort an.

Expired link:
- EN: This reset link is no longer valid. Request a new one.
- DE: Dieser Link zum Zurücksetzen ist nicht mehr gültig. Fordere einen neuen an.

Local practice remains untouched throughout password recovery.

## 7. Session expired or account verification required

Do not discard practice.

Inline status:
- EN: Sign in again to sync. Your saved practice remains on this device.
- DE: Melde dich erneut an, um zu synchronisieren. Deine gespeicherten Übungen bleiben auf diesem Gerät.

Primary:
- EN: Sign in
- DE: Anmelden

Secondary when safe local work exists:
- EN: Continue saved practice
- DE: Gespeicherte Übungen fortsetzen

Verification in progress:
- EN: Verifying your account…
- DE: Dein Konto wird geprüft…

Verification failure:
- EN: Sign in to this account to continue syncing. Saved learner work remains on this device.
- DE: Melde dich bei diesem Konto an, um weiter zu synchronisieren. Gespeicherte Lerndaten bleiben auf diesem Gerät.

Do not silently bind local data to a different authenticated subject.

## 8. Signed-in Account surface

Heading:
- EN: Account
- DE: Konto

Show:
- email,
- interface language,
- session-length preference,
- theme,
- sync/download status entry,
- privacy/export entry,
- sign-out action,
- delete-account action.

Do not show internal role names, UUIDs, access tokens or provider metadata in
the learner UI.

If email is unconfirmed:
- EN: Confirm your email to finish setting up this account.
- DE: Bestätige deine E-Mail, um die Einrichtung dieses Kontos abzuschließen.

Action:
- EN: Resend confirmation
- DE: Bestätigung erneut senden

## 9. Sign out

Sign-out must distinguish two choices when local account-bound work exists.

Title:
- EN: Sign out
- DE: Abmelden

Body:
- EN: You can keep this account’s saved work on this device, or remove its local work before signing out. Removing local work does not delete server data.
- DE: Du kannst die gespeicherten Daten dieses Kontos auf diesem Gerät behalten oder sie vor dem Abmelden lokal entfernen. Das Entfernen lokaler Daten löscht keine Serverdaten.

Primary safe default:
- EN: Sync saved work and sign out
- DE: Gespeicherte Daten synchronisieren und abmelden

Secondary destructive-local action:
- EN: Remove local work and sign out…
- DE: Lokale Daten entfernen und abmelden…

Local-removal confirmation:
- EN: Remove this account’s downloads, drafts and unsynced work from this device?
- DE: Downloads, Entwürfe und nicht synchronisierte Vorgänge dieses Kontos von diesem Gerät entfernen?

Destructive:
- EN: Remove local work and sign out
- DE: Lokale Daten entfernen und abmelden

Cancel:
- EN: Keep my local work
- DE: Lokale Daten behalten

If sync fails:
- EN: Sign-out was not completed because saved work could not be synced. Your work is still on this device.
- DE: Die Abmeldung wurde nicht abgeschlossen, weil gespeicherte Daten nicht synchronisiert werden konnten. Deine Daten bleiben auf diesem Gerät.

After sign-out with retained local work:
- EN: Signed out. Saved work remains on this device for this account.
- DE: Abgemeldet. Gespeicherte Daten bleiben auf diesem Gerät diesem Konto zugeordnet.

## 10. Export learner data

Section:
- EN: Your data
- DE: Deine Daten

Heading:
- EN: Download your learner data
- DE: Lerndaten herunterladen

Body:
- EN: The export contains confirmed server data. Sync saved work first if you want pending answers and preferences included. Unsubmitted drafts remain only on this device.
- DE: Der Export enthält bestätigte Serverdaten. Synchronisiere gespeicherte Daten zuerst, wenn ausstehende Antworten und Einstellungen enthalten sein sollen. Nicht abgegebene Entwürfe bleiben nur auf diesem Gerät.

Primary:
- EN: Sync and download
- DE: Synchronisieren und herunterladen

Secondary:
- EN: Download confirmed data only
- DE: Nur bestätigte Daten herunterladen

Success:
- EN: Export downloaded. Keep it private; it contains your answers and preferences.
- DE: Export heruntergeladen. Bewahre ihn vertraulich auf; er enthält deine Antworten und Einstellungen.

Failure:
- EN: Export failed. Your saved work remains available.
- DE: Export fehlgeschlagen. Deine gespeicherten Daten bleiben erhalten.

## 11. Delete account

This action means deletion of the actual sign-in identity plus owned learner
data. It is distinct from:
- removing local work,
- deleting guest practice from one device,
- deleting only learner data while retaining a provider identity.

### Entry

Heading:
- EN: Delete account
- DE: Konto löschen

Body:
- EN: This deletes your sign-in account, confirmed learner data and saved work on this device. Download an export first if you want a copy.
- DE: Dadurch werden dein Anmeldekonto, bestätigte Lerndaten und gespeicherte Daten auf diesem Gerät gelöscht. Lade vorher einen Export herunter, wenn du eine Kopie behalten möchtest.

Action:
- EN: Delete account…
- DE: Konto löschen…

### Fresh-password confirmation

Heading:
- EN: Confirm account deletion
- DE: Kontolöschung bestätigen

Body:
- EN: Enter your current email and password. Once the deletion request is saved, practice and sync are blocked until deletion is resolved.
- DE: Gib deine aktuelle E-Mail-Adresse und dein Passwort ein. Sobald die Löschanfrage gespeichert ist, bleiben Üben und Synchronisieren gesperrt, bis die Löschung abgeschlossen ist.

Fields:
- EN: Email
- DE: E-Mail
- EN: Current password
- DE: Aktuelles Passwort

Destructive:
- EN: Confirm account deletion
- DE: Kontolöschung bestätigen

Cancel:
- EN: Keep my account
- DE: Mein Konto behalten

Do not keep the password in durable storage.

### Pending/ambiguous deletion

Heading:
- EN: Account deletion is pending
- DE: Kontolöschung ist noch offen

Body:
- EN: Practice and sync are blocked while German Master confirms the saved deletion request. Your local data stays in place until deletion is confirmed.
- DE: Üben und Synchronisieren bleiben gesperrt, während German Master die gespeicherte Löschanfrage bestätigt. Deine lokalen Daten bleiben erhalten, bis die Löschung bestätigt ist.

Actions:
- EN: Check deletion status
- DE: Löschstatus prüfen
- EN: Reauthenticate and retry
- DE: Erneut anmelden und Löschung wiederholen

Never advise the learner to create a second deletion request merely because the
network response was lost.

### Server identity deleted, local cleanup pending

Heading:
- EN: Account deleted
- DE: Konto gelöscht

Body:
- EN: The account and server data were deleted. Finish removing saved work from this device.
- DE: Konto und Serverdaten wurden gelöscht. Entferne jetzt noch die gespeicherten Daten von diesem Gerät.

Action:
- EN: Finish local removal
- DE: Lokale Entfernung abschließen

### Complete

- EN: Account deletion confirmed and local work removed.
- DE: Kontolöschung bestätigt und lokale Daten entfernt.

Do not offer normal sign-in to the deleted identity.

## 12. Guest-practice deletion

This is device-local and must remain clearly separate from account deletion.

Title:
- EN: Delete guest practice from this device?
- DE: Gastübungen von diesem Gerät löschen?

Body:
- EN: This removes guest sessions and attempts that have not been saved to an account. This can’t be undone.
- DE: Dadurch werden Gastübungen und Versuche gelöscht, die noch nicht in einem Konto gespeichert wurden. Das kann nicht rückgängig gemacht werden.

Destructive:
- EN: Delete from device
- DE: Vom Gerät löschen

Cancel:
- EN: Cancel
- DE: Abbrechen

## 13. Downloads and sync wording

Heading:
- EN: Downloads and sync
- DE: Downloads und Synchronisierung

Healthy:
- EN: Up to date
- DE: Aktuell

Pending:
- EN: {n} saved items are waiting to sync
- DE: {n} gespeicherte Vorgänge warten auf die Synchronisierung

Offline:
- EN: Offline. Saved work will sync when you reconnect.
- DE: Offline. Gespeicherte Daten werden synchronisiert, sobald du wieder online bist.

Retry:
- EN: Retry sync
- DE: Synchronisierung erneut versuchen

Prepared session:
- EN: Prepared practice available offline
- DE: Vorbereitete Übung offline verfügbar

Downloading:
- EN: Preparing offline practice…
- DE: Offline-Übung wird vorbereitet…

Ready:
- EN: Available offline
- DE: Offline verfügbar

Insufficient space:
- EN: There isn’t enough free space to save this practice for offline use.
- DE: Es ist nicht genug freier Speicher vorhanden, um diese Übung offline zu speichern.

Auth required:
- EN: Sign in to prepare personalized offline practice.
- DE: Melde dich an, um persönliche Offline-Übungen vorzubereiten.

Do not describe provisional local answers as "synced" or "confirmed".

## 14. Error-language rules

Prefer consequence-oriented copy:
- what failed,
- what is still safe,
- what the learner can do next.

Avoid:
- raw provider errors,
- HTTP codes,
- database language,
- “unknown error”,
- “your data may be lost” unless loss is actually established.

Examples:

EN: **Sync failed. Your saved work is still on this device. Try again.**  
DE: **Synchronisierung fehlgeschlagen. Deine gespeicherten Daten bleiben auf diesem Gerät. Versuche es erneut.**

EN: **Account service is unavailable. You can continue prepared practice offline.**  
DE: **Der Kontodienst ist nicht verfügbar. Du kannst vorbereitete Übungen offline fortsetzen.**

## 15. Accessibility requirements

- Form errors are associated with the relevant input and announced once.
- Busy states do not move focus unexpectedly.
- Confirmation dialogs receive focus at the heading and return focus after cancel.
- Destructive actions are not the default focused action.
- Password managers can identify email/current-password/new-password fields.
- Resend and reset actions expose busy/disabled state.
- A completed sign-out or deletion state is announced without re-reading the whole page.
- Account screens reflow at 200% web zoom and Android large font scale.
- Copy never relies on color or icon alone for verification/error state.

## 16. Implementation checklist

The account-copy handoff is complete when clients:
- allow guest-first use without account pressure,
- expose create account and sign in with the same meaning on web/Android,
- provide delivered-email confirmation states when SMTP is configured,
- provide resend confirmation,
- provide password-recovery request and new-password flows,
- preserve local work across auth failure/expiry,
- never auto-attach guest attempts after login,
- distinguish sign-out from local data removal,
- distinguish local/guest removal from account deletion,
- require fresh proof for identity deletion,
- recover a saved deletion request without duplicate destructive requests,
- expose export wording that distinguishes confirmed server data from unsynced drafts,
- use the same English/German terminology across account, sync and privacy surfaces.

## 17. Deliberate non-features

Do not add:
- mandatory social login,
- “continue with Google” unless the production provider is intentionally configured,
- Microsoft login until configured and accepted,
- automatic guest attachment,
- security questions,
- account deletion by a single unconfirmed tap,
- raw technical auth errors,
- pressure copy such as “Create an account or lose your progress”.

