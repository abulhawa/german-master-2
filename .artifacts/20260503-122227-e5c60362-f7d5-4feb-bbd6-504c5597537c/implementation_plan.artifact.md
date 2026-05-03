# ML Kit Translation Integration

Integrate Google ML Kit for on-device translation to improve the accuracy of German-English translations in the app. This includes a "Round-Trip" verification strategy and debug-only database updates.

## User Review Required

> [!IMPORTANT]
> - ML Kit requires downloading language models (~30MB per language). We will implement a check to ensure this happens gracefully.
> - Database updates are restricted to `DEBUG` builds as requested.

## Proposed Changes

### Build Configuration

#### [build.gradle.kts](file:///C:/Projects/GermanVerbMaster-Android/app/build.gradle.kts)
- Add `com.google.mlkit:translate` dependency.

---

### Data Layer

#### [NEW] [TranslationManager.kt](file:///C:/Projects/GermanVerbMaster-Android/app/src/main/java/com/germanverbmaster/android/data/util/TranslationManager.kt)
- Create a singleton manager for ML Kit translations.
- Implement `translateDeToEn` and `translateEnToDe`.
- Implement `verifyWithRoundTrip` logic to estimate translation reliability.

---

### UI / ViewModels

#### [WordDetailViewModel.kt](file:///C:/Projects/GermanVerbMaster-Android/app/src/main/java/com/germanverbmaster/android/ui/worddetail/WordDetailViewModel.kt)
- Inject `TranslationManager`.
- Add state for AI-suggested translations.
- Add logic to trigger translation/verification.
- Implement debug-only DB update call.

#### [WordDetailScreen.kt](file:///C:/Projects/GermanVerbMaster-Android/app/src/main/java/com/germanverbmaster/android/ui/worddetail/WordDetailScreen.kt)
- Display AI-suggested translation if available.
- Add a "Refresh with AI" button (visible if translation is suspect or missing).
- Show "Update DB" button only in debug builds.

---

## Verification Plan

### Automated Tests
- N/A (ML Kit requires native libraries and models, hard to unit test without mocks).

### Manual Verification
1. Open a word with a known incorrect translation.
2. Trigger "AI Translate".
3. Verify the "Round-Trip" logic catches stable translations.
4. Verify that in `DEBUG` build, the "Update DB" button appears and works.
5. Verify that in `RELEASE` build (simulated), the DB update button is hidden.
