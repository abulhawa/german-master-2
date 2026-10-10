# German Master branded Supabase Auth email templates

These are ready-to-paste templates for the six standard Supabase Auth email types. They use the existing German Master app icon at:

`https://germanmaster.qortxai.com/icons/icon-512.png`

The brand name is also rendered as text so the email remains identifiable when a mail client blocks remote images. Buttons have a plain URL fallback.

## Apply

1. Open [Production Auth Email Templates](https://supabase.com/dashboard/project/zgmyrpzwgtydwlzponih/auth/templates) for the live project.
2. For each template below, copy the HTML into the **Message body** and set the listed subject.
3. Save each template. Do not change SMTP settings as part of this step.
4. Send a test signup confirmation and password recovery email and verify both on desktop and mobile.

If you're preparing the clean replacement instead, use its dashboard rather than production. These files are documentation only; merging this PR does **not** update Supabase's hosted Auth templates.

## Shared branding

All templates use the same centered white card, German Master icon and name, accessible button text, short security copy, and a fallback link. Keep the content transactional and concise.

## 1. Confirm signup

**Subject:** Confirm your German Master account

```html
<!doctype html>
<html lang="en">
  <body style="margin:0;padding:24px;background:#f5f7fb;font-family:Arial,Helvetica,sans-serif;color:#202938;">
    <table role="presentation" width="100%" cellspacing="0" cellpadding="0" style="max-width:560px;margin:0 auto;background:#ffffff;border:1px solid #e5e9f2;border-radius:16px;">
      <tr><td style="padding:28px 28px 8px;">
        <table role="presentation" cellspacing="0" cellpadding="0"><tr>
          <td style="vertical-align:middle;padding-right:12px;"><img src="https://germanmaster.qortxai.com/icons/icon-512.png" width="48" height="48" alt="German Master logo" style="display:block;border:0;border-radius:10px;"></td>
          <td style="vertical-align:middle;font-size:22px;font-weight:700;color:#202938;">German Master</td>
        </tr></table>
      </td></tr>
      <tr><td style="padding:16px 28px 28px;font-size:16px;line-height:1.6;">
        <h1 style="margin:0 0 16px;font-size:24px;line-height:1.25;">Confirm your email</h1>
        <p style="margin:0 0 20px;">Confirm this email address to finish creating your German Master account.</p>
        <p style="margin:0 0 24px;"><a href="{{ .ConfirmationURL }}" style="display:inline-block;padding:13px 20px;background:#2457c5;color:#ffffff;text-decoration:none;font-weight:700;border-radius:8px;">Confirm email address</a></p>
        <p style="margin:0 0 8px;">If the button doesn't work, open this link:</p>
        <p style="margin:0 0 20px;overflow-wrap:anywhere;font-size:13px;"><a href="{{ .ConfirmationURL }}" style="color:#2457c5;">{{ .ConfirmationURL }}</a></p>
        <p style="margin:0 0 8px;">Or enter this confirmation code in German Master:</p>
        <p style="margin:0 0 20px;font-size:22px;font-weight:700;letter-spacing:3px;">{{ .Token }}</p>
        <p style="margin:0;color:#667085;font-size:13px;">If you didn't create a German Master account, you can ignore this email.</p>
      </td></tr>
    </table>
    <p style="max-width:560px;margin:16px auto 0;text-align:center;color:#667085;font-size:12px;">German Master · <a href="https://germanmaster.qortxai.com" style="color:#667085;">germanmaster.qortxai.com</a></p>
  </body>
</html>
```

## 2. Invite user

**Subject:** You're invited to German Master

```html
<!doctype html>
<html lang="en">
  <body style="margin:0;padding:24px;background:#f5f7fb;font-family:Arial,Helvetica,sans-serif;color:#202938;">
    <table role="presentation" width="100%" cellspacing="0" cellpadding="0" style="max-width:560px;margin:0 auto;background:#ffffff;border:1px solid #e5e9f2;border-radius:16px;">
      <tr><td style="padding:28px 28px 8px;"><table role="presentation" cellspacing="0" cellpadding="0"><tr>
        <td style="vertical-align:middle;padding-right:12px;"><img src="https://germanmaster.qortxai.com/icons/icon-512.png" width="48" height="48" alt="German Master logo" style="display:block;border:0;border-radius:10px;"></td>
        <td style="vertical-align:middle;font-size:22px;font-weight:700;color:#202938;">German Master</td>
      </tr></table></td></tr>
      <tr><td style="padding:16px 28px 28px;font-size:16px;line-height:1.6;">
        <h1 style="margin:0 0 16px;font-size:24px;line-height:1.25;">You're invited</h1>
        <p style="margin:0 0 20px;">You've been invited to join German Master. Accept the invitation to set up your account.</p>
        <p style="margin:0 0 24px;"><a href="{{ .ConfirmationURL }}" style="display:inline-block;padding:13px 20px;background:#2457c5;color:#ffffff;text-decoration:none;font-weight:700;border-radius:8px;">Accept invitation</a></p>
        <p style="margin:0 0 8px;">If the button doesn't work, open this link:</p>
        <p style="margin:0 0 20px;overflow-wrap:anywhere;font-size:13px;"><a href="{{ .ConfirmationURL }}" style="color:#2457c5;">{{ .ConfirmationURL }}</a></p>
        <p style="margin:0;color:#667085;font-size:13px;">If you weren't expecting this invitation, you can ignore this email.</p>
      </td></tr>
    </table>
    <p style="max-width:560px;margin:16px auto 0;text-align:center;color:#667085;font-size:12px;">German Master · <a href="https://germanmaster.qortxai.com" style="color:#667085;">germanmaster.qortxai.com</a></p>
  </body>
</html>
```

## 3. Magic link

**Subject:** Your German Master sign-in link

```html
<!doctype html>
<html lang="en">
  <body style="margin:0;padding:24px;background:#f5f7fb;font-family:Arial,Helvetica,sans-serif;color:#202938;">
    <table role="presentation" width="100%" cellspacing="0" cellpadding="0" style="max-width:560px;margin:0 auto;background:#ffffff;border:1px solid #e5e9f2;border-radius:16px;">
      <tr><td style="padding:28px 28px 8px;"><table role="presentation" cellspacing="0" cellpadding="0"><tr>
        <td style="vertical-align:middle;padding-right:12px;"><img src="https://germanmaster.qortxai.com/icons/icon-512.png" width="48" height="48" alt="German Master logo" style="display:block;border:0;border-radius:10px;"></td>
        <td style="vertical-align:middle;font-size:22px;font-weight:700;color:#202938;">German Master</td>
      </tr></table></td></tr>
      <tr><td style="padding:16px 28px 28px;font-size:16px;line-height:1.6;">
        <h1 style="margin:0 0 16px;font-size:24px;line-height:1.25;">Sign in to German Master</h1>
        <p style="margin:0 0 20px;">Use the secure link below to sign in. You don't need to enter your password.</p>
        <p style="margin:0 0 24px;"><a href="{{ .ConfirmationURL }}" style="display:inline-block;padding:13px 20px;background:#2457c5;color:#ffffff;text-decoration:none;font-weight:700;border-radius:8px;">Sign in</a></p>
        <p style="margin:0 0 8px;">If the button doesn't work, open this link:</p>
        <p style="margin:0 0 20px;overflow-wrap:anywhere;font-size:13px;"><a href="{{ .ConfirmationURL }}" style="color:#2457c5;">{{ .ConfirmationURL }}</a></p>
        <p style="margin:0;color:#667085;font-size:13px;">If you didn't request this sign-in link, you can ignore this email.</p>
      </td></tr>
    </table>
    <p style="max-width:560px;margin:16px auto 0;text-align:center;color:#667085;font-size:12px;">German Master · <a href="https://germanmaster.qortxai.com" style="color:#667085;">germanmaster.qortxai.com</a></p>
  </body>
</html>
```

## 4. Change email address

**Subject:** Confirm your new email address for German Master

```html
<!doctype html>
<html lang="en">
  <body style="margin:0;padding:24px;background:#f5f7fb;font-family:Arial,Helvetica,sans-serif;color:#202938;">
    <table role="presentation" width="100%" cellspacing="0" cellpadding="0" style="max-width:560px;margin:0 auto;background:#ffffff;border:1px solid #e5e9f2;border-radius:16px;">
      <tr><td style="padding:28px 28px 8px;"><table role="presentation" cellspacing="0" cellpadding="0"><tr>
        <td style="vertical-align:middle;padding-right:12px;"><img src="https://germanmaster.qortxai.com/icons/icon-512.png" width="48" height="48" alt="German Master logo" style="display:block;border:0;border-radius:10px;"></td>
        <td style="vertical-align:middle;font-size:22px;font-weight:700;color:#202938;">German Master</td>
      </tr></table></td></tr>
      <tr><td style="padding:16px 28px 28px;font-size:16px;line-height:1.6;">
        <h1 style="margin:0 0 16px;font-size:24px;line-height:1.25;">Confirm your new email</h1>
        <p style="margin:0 0 20px;">Confirm this email address to complete the email change for your German Master account.</p>
        <p style="margin:0 0 24px;"><a href="{{ .ConfirmationURL }}" style="display:inline-block;padding:13px 20px;background:#2457c5;color:#ffffff;text-decoration:none;font-weight:700;border-radius:8px;">Confirm email change</a></p>
        <p style="margin:0 0 8px;">If the button doesn't work, open this link:</p>
        <p style="margin:0 0 20px;overflow-wrap:anywhere;font-size:13px;"><a href="{{ .ConfirmationURL }}" style="color:#2457c5;">{{ .ConfirmationURL }}</a></p>
        <p style="margin:0;color:#667085;font-size:13px;">If you didn't request an email change, secure your account and contact support.</p>
      </td></tr>
    </table>
    <p style="max-width:560px;margin:16px auto 0;text-align:center;color:#667085;font-size:12px;">German Master · <a href="https://germanmaster.qortxai.com" style="color:#667085;">germanmaster.qortxai.com</a></p>
  </body>
</html>
```

## 5. Reset password

**Subject:** Reset your German Master password

```html
<!doctype html>
<html lang="en">
  <body style="margin:0;padding:24px;background:#f5f7fb;font-family:Arial,Helvetica,sans-serif;color:#202938;">
    <table role="presentation" width="100%" cellspacing="0" cellpadding="0" style="max-width:560px;margin:0 auto;background:#ffffff;border:1px solid #e5e9f2;border-radius:16px;">
      <tr><td style="padding:28px 28px 8px;"><table role="presentation" cellspacing="0" cellpadding="0"><tr>
        <td style="vertical-align:middle;padding-right:12px;"><img src="https://germanmaster.qortxai.com/icons/icon-512.png" width="48" height="48" alt="German Master logo" style="display:block;border:0;border-radius:10px;"></td>
        <td style="vertical-align:middle;font-size:22px;font-weight:700;color:#202938;">German Master</td>
      </tr></table></td></tr>
      <tr><td style="padding:16px 28px 28px;font-size:16px;line-height:1.6;">
        <h1 style="margin:0 0 16px;font-size:24px;line-height:1.25;">Reset your password</h1>
        <p style="margin:0 0 20px;">We received a request to reset the password for your German Master account.</p>
        <p style="margin:0 0 24px;"><a href="{{ .ConfirmationURL }}" style="display:inline-block;padding:13px 20px;background:#2457c5;color:#ffffff;text-decoration:none;font-weight:700;border-radius:8px;">Reset password</a></p>
        <p style="margin:0 0 8px;">If the button doesn't work, open this link:</p>
        <p style="margin:0 0 20px;overflow-wrap:anywhere;font-size:13px;"><a href="{{ .ConfirmationURL }}" style="color:#2457c5;">{{ .ConfirmationURL }}</a></p>
        <p style="margin:0;color:#667085;font-size:13px;">If you didn't request a password reset, you can ignore this email. Your password won't change unless you use the link.</p>
      </td></tr>
    </table>
    <p style="max-width:560px;margin:16px auto 0;text-align:center;color:#667085;font-size:12px;">German Master · <a href="https://germanmaster.qortxai.com" style="color:#667085;">germanmaster.qortxai.com</a></p>
  </body>
</html>
```

## 6. Reauthentication

**Subject:** Confirm it's you · German Master

```html
<!doctype html>
<html lang="en">
  <body style="margin:0;padding:24px;background:#f5f7fb;font-family:Arial,Helvetica,sans-serif;color:#202938;">
    <table role="presentation" width="100%" cellspacing="0" cellpadding="0" style="max-width:560px;margin:0 auto;background:#ffffff;border:1px solid #e5e9f2;border-radius:16px;">
      <tr><td style="padding:28px 28px 8px;"><table role="presentation" cellspacing="0" cellpadding="0"><tr>
        <td style="vertical-align:middle;padding-right:12px;"><img src="https://germanmaster.qortxai.com/icons/icon-512.png" width="48" height="48" alt="German Master logo" style="display:block;border:0;border-radius:10px;"></td>
        <td style="vertical-align:middle;font-size:22px;font-weight:700;color:#202938;">German Master</td>
      </tr></table></td></tr>
      <tr><td style="padding:16px 28px 28px;font-size:16px;line-height:1.6;">
        <h1 style="margin:0 0 16px;font-size:24px;line-height:1.25;">Confirm it's you</h1>
        <p style="margin:0 0 20px;">Enter this one-time code in German Master to confirm it's you.</p>
        <p style="margin:0 0 24px;font-size:26px;font-weight:700;letter-spacing:4px;">{{ .Token }}</p>
        <p style="margin:0;color:#667085;font-size:13px;">If you didn't request this code, you can ignore this email.</p>
      </td></tr>
    </table>
    <p style="max-width:560px;margin:16px auto 0;text-align:center;color:#667085;font-size:12px;">German Master · <a href="https://germanmaster.qortxai.com" style="color:#667085;">germanmaster.qortxai.com</a></p>
  </body>
</html>
```

## Final checks

- In **Authentication → Emails → SMTP Settings**, set the sender name to **German Master** and keep the verified sender address you configured.
- In **Authentication → URL Configuration**, set the Site URL to `https://germanmaster.qortxai.com` and add only the exact callback URLs the app needs. Do not use a wildcard redirect for production.
- Test signup confirmation, password reset, magic link, invite, email change, and reauthentication. Confirm the logo loads and every action link reaches the intended app flow.
- If the image is blocked by a mail client, the visible **German Master** wordmark remains in the header.
