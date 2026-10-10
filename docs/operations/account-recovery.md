# Account recovery implementation

Status: implemented, unverified, 10 October 2026. No live emails, account writes or provider configuration changes were performed during implementation.

- The sign-in screen exposes password reset and signup-confirmation resend with localized pending/success/failure states and a local resend cooldown. Responses avoid promising that an email address exists.
- Email redirects use the frontend origin. Recovery uses `/?auth=recovery`; confirmation uses `/`. The provider's redirect allowlist must permit the deployed frontend recovery URL before release. Configuration inspection and delivered-email acceptance are deferred.
- The SDK handles PKCE exchange. A URL selects the recovery screen but cannot authorize a password update. `PASSWORD_RECOVERY` supplies the recovery subject; `getUser` must match before `updateUser`. Learner bindings stay invalidated throughout recovery; guest attachment is never automatic. Completing recovery resumes normal verified binding.
- Expired or failed links show a new-email route instead of falling back to an already signed-in account. Reloading a consumed link without a fresh recovery event requires a new link. Open recovery emails in the same browser that requested them to preserve the PKCE verifier.
- Saved learner data and guest work are not deleted by recovery. Passwords stay in component memory and are cleared after submission. No credentials enter the fix list or logs.

Deferred: mocked SDK ordering/races, different-account events during recovery, expired or consumed links, reload, password policy failures, rate limiting, screen-reader focus, real email delivery and provider redirect configuration. All remain acceptance requirements.

Implementation references: [resetPasswordForEmail](https://supabase.com/docs/reference/javascript/auth-resetpasswordforemail), [resend](https://supabase.com/docs/reference/javascript/auth-resend), [updateUser](https://supabase.com/docs/reference/javascript/auth-updateuser). Existing PKCE configuration is preserved.
