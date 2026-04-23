# Implementation Plan - History/Progress Sync

This plan outlines how to sync practice history (including "Schnell-Drill" progress) across devices using the existing Supabase infrastructure.

## Implementation Strategy: Test-First

To ensure stability, I will follow a test-driven approach:
1.  **Baseline Tests**: Enhance/Add unit tests for `PracticeHistoryDao` and `PracticeRepository` *before* modifying them to ensure existing functionality (recording results, observing stats) is stable.
2.  **Incremental Implementation**: Add fields/APIs one by one, updating tests at each step.
3.  **Sync Logic Tests**: Implement `SyncHistoryUseCase` with 100% test coverage for edge cases (no login, network error, conflicting IDs).

## User Review Required

> [!IMPORTANT]
> **Database Migration**: I will need to increment the Room database version and add a migration. Since `remoteId` is new, existing local records will be assigned a random UUID upon first sync.
> **Supabase Schema**: This plan assumes a `practice_history` table exists in Supabase with columns: `id` (UUID), `user_id` (UUID), `task_id`, `lexeme_id`, `lemma`, `pos`, `task_type`, `renderer`, `result`, `submitted_answer`, `correct_answer`, `response_ms`, `cefr_level`, `hints_used`, `submitted_at`.

## Proposed Changes

### 1. Verification of Existing State (Tests)

#### [PracticeHistoryDaoTest.kt](file:///C:/Projects/GermanVerbMaster-Android/app/src/androidTest/kotlin/com/germanverbmaster/android/data/local/dao/PracticeHistoryDaoTest.kt)
- Add tests for `observeTaskTypeStats` and `getDailyAccuracy` to ensure they remain correct after adding the `remoteId` column.

### 2. Data Model & Persistence

#### [PracticeHistoryEntity.kt](file:///C:/Projects/GermanVerbMaster-Android/app/src/main/java/com/germanverbmaster/android/data/local/entity/PracticeHistoryEntity.kt)
- Add `remoteId: String` (default `UUID.randomUUID().toString()`).
- Add `userId: String?` to track which user the record belongs to locally.

#### [AppDatabase.kt](file:///C:/Projects/GermanVerbMaster-Android/app/src/main/java/com/germanverbmaster/android/data/local/db/AppDatabase.kt)
- Increment version to `11`.
- Add migration logic (likely `fallbackToDestructiveMigration()` or a simple `ALTER TABLE` if schema preservation is critical).

#### [PracticeHistoryDao.kt](file:///C:/Projects/GermanVerbMaster-Android/app/src/main/java/com/germanverbmaster/android/data/local/dao/PracticeHistoryDao.kt)
- Add `@Query("SELECT * FROM practice_history WHERE synced = 0 AND userId = :userId")`.
- Add `@Insert(onConflict = OnConflictStrategy.IGNORE)` for merging remote data.

### 3. Remote API & Sync Logic

#### [NEW] [SupabaseHistoryApi.kt](file:///C:/Projects/GermanVerbMaster-Android/app/src/main/java/com/germanverbmaster/android/data/remote/SupabaseHistoryApi.kt)
- `upsert(entries: List<RemoteHistory>)`: Push local changes.
- `fetchUpdatedSince(since: String, userId: String)`: Pull remote changes.

#### [NEW] [SyncHistoryUseCase.kt](file:///C:/Projects/GermanVerbMaster-Android/app/src/main/java/com/germanverbmaster/android/domain/usecase/SyncHistoryUseCase.kt)
- Logic: `if (notLoggedIn) return` -> `Upload Unsynced` -> `Download Remote` -> `Mark Synced`.

#### [SyncWorker.kt](file:///C:/Projects/GermanVerbMaster-Android/app/src/main/java/com/germanverbmaster/android/data/remote/worker/SyncWorker.kt)
- Integrate `SyncHistoryUseCase`.

## Verification Plan

### Automated Tests
- `gradle_build("app:connectedCheck")` to run instrumented Dao tests.
- `gradle_build("app:testDebugUnitTest")` for UseCase and ViewModel tests.

### Manual Verification
- Log in on two devices, complete drills on one, and check "Answer History" and "Mastery Progress" on the other after a sync.
