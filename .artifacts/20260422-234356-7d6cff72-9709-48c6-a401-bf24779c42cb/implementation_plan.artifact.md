# Fix Persistent Wortschatz Progress

The user reported that Wortschatz progress is stored per session and often resets to zero, despite having a history of answers. Research revealed several issues:
1. **Tab Switch Reset**: Switching between "Wortliste" and "Schnell-Drill" resets the drill state.
2. **Filter Reset**: Changing filters (Level/POS) unconditionally resets the drill progress in preferences, even if the user was just browsing the list.
3. **Unstable Shuffle**: The drill queue is reshuffled on every app restart, making the saved index point to incorrect words.
4. **Session-only Progress**: The progress bar only reflects the current session's index, not the overall mastery from history.

## Proposed Changes

### 1. Data Layer: Persistent Mastery Tracking

Add a way to observe the set of mastered words from history.

#### [PracticeHistoryDao.kt](file:///C:/Projects/GermanVerbMaster-Android/app/src/main/java/com/germanverbmaster/android/data/local/dao/PracticeHistoryDao.kt)
- Add `@Query("SELECT DISTINCT taskId FROM practice_history WHERE result = 'correct'")` to observe mastered task IDs.

#### [PracticeRepository.kt](file:///C:/Projects/GermanVerbMaster-Android/app/src/main/java/com/germanverbmaster/android/data/repository/PracticeRepository.kt)
- Add `observeCorrectTaskIds(): Flow<Set<String>>`.

### 2. Preferences: Stable Shuffle

#### [LexemeRepository.kt](file:///C:/Projects/GermanVerbMaster-Android/app/src/main/java/com/germanverbmaster/android/data/repository/LexemeRepository.kt) (where `SyncPreferences` is defined)
- Add `DRILL_SEED` key and accessors to `SyncPreferences` to persist the shuffle seed.

### 3. ViewModel: Fix Logic & Persistence

#### [WortschatzViewModel.kt](file:///C:/Projects/GermanVerbMaster-Android/app/src/main/java/com/germanverbmaster/android/ui/wortschatz/WortschatzViewModel.kt)
- **Stable Shuffle**: Use the persisted seed for `shuffled(Random(seed))`.
- **Conditional Reset**: Only reset the drill state if the filters actually changed AND the user is in Drill mode, or provide a way to change list filters without affecting the drill until it's restarted.
- **Tab Persistence**: In `selectTab`, don't call `buildDrill()` if a queue already exists.
- **Mastery Observation**: Observe `practiceRepository.observeCorrectTaskIds()` and update a new `masteredCount` in the UI state.
- **Progress Calculation**: Update `drillProgress` to be based on `masteredCount` / `totalCount` OR show both session and mastery progress.

### 4. UI: Improved Progress Feedback

#### [WortschatzScreen.kt](file:///C:/Projects/GermanVerbMaster-Android/app/src/main/java/com/germanverbmaster/android/ui/wortschatz/WortschatzScreen.kt)
- Update the progress indicator to show mastery progress.
- Ensure session stats (Correct/Wrong) remain visible for the current session.

---

## Verification Plan

### Automated Tests
- Create `WortschatzViewModelTest.kt` to verify:
    - `selectTab` does not reset progress.
    - `observeWords` restores index correctly with a stable seed.
    - Mastery count updates correctly when `SubmitAnswerUseCase` is called.

### Manual Verification
1. Start a Drill, answer a few words correctly.
2. Switch to Wortliste tab and back. Verify progress is preserved.
3. Restart the app. Verify progress and the current word are preserved.
4. Change a filter. Verify the progress resets (intended as filters change the scope).
5. Open History to confirm answers are recorded, then check if Wortschatz progress reflects those historical answers.
