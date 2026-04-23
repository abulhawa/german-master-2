# Implementation Plan - History/Progress Sync

This plan outlines how to sync practice history (including "Schnell-Drill" progress) across devices using the existing Supabase infrastructure.

## User Review Required

> [!IMPORTANT]
> This sync requires users to be signed in (Google login).
> The local `PracticeHistoryEntity` will need a new `remoteId` field (UUID) to uniquely identify records across devices.

## Proposed Changes

### Data Model & Persistence

#### [PracticeHistoryEntity.kt](file:///C:/Projects/GermanVerbMaster-Android/app/src/main/java/com/germanverbmaster/android/data/local/entity/PracticeHistoryEntity.kt)
- Add `remoteId: String` field (default to a new UUID for local-only records).
- Add it to the `@Entity` definition and update Room database version.

#### [PracticeHistoryDao.kt](file:///C:/Projects/GermanVerbMaster-Android/app/src/main/java/com/germanverbmaster/android/data/local/dao/PracticeHistoryDao.kt)
- Add `@Query("SELECT * FROM practice_history WHERE synced = 0")` if not present.
- Add `@Query("UPDATE practice_history SET synced = 1 WHERE localId IN (:ids)")`.
- Add `@Insert(onConflict = OnConflictStrategy.IGNORE)` for merging remote data.

---

### Remote API (Supabase)

#### [NEW] [SupabaseHistoryApi.kt](file:///C:/Projects/GermanVerbMaster-Android/app/src/main/java/com/germanverbmaster/android/data/remote/SupabaseHistoryApi.kt)
- Define `RemoteHistory` DTO matching the Supabase table schema.
- Implement `upsert(entries: List<RemoteHistory>)`.
- Implement `fetchUpdatedSince(since: String, userId: String)`.

---

### Sync Logic

#### [SyncPreferences.kt](file:///C:/Projects/GermanVerbMaster-Android/app/src/main/java/com/germanverbmaster/android/data/repository/SyncPreferences.kt)
- Add `HISTORY_LAST_SYNC` key and getter/setter.

#### [NEW] [SyncHistoryUseCase.kt](file:///C:/Projects/GermanVerbMaster-Android/app/src/main/java/com/germanverbmaster/android/domain/usecase/SyncHistoryUseCase.kt)
- Coordinate the upload of unsynced local records.
- Fetch new records from Supabase since the last sync.
- Update local database and sync preferences.

#### [SyncWorker.kt](file:///C:/Projects/GermanVerbMaster-Android/app/src/main/java/com/germanverbmaster/android/data/remote/worker/SyncWorker.kt)
- Call `SyncHistoryUseCase` alongside existing sync logic.

## Verification Plan

### Automated Tests
- `SyncHistoryUseCaseTest`: Mock repository and API to verify that unsynced records are uploaded and remote records are merged.
- `PracticeHistoryDaoTest`: Verify that `unsynced()` and `markSynced()` work as expected with the new `remoteId` field.

### Manual Verification
1.  Sign in on Device A.
2.  Complete a few "Schnell-Drill" sessions.
3.  Trigger manual sync (or wait for worker).
4.  Sign in on Device B.
5.  Verify that the "mastered" counts and history list reflect Device A's progress.
