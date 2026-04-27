# Redesign Grammar Accessibility

Make grammar reference tables easily accessible from any practice or tab by moving them into a global Bottom Sheet and splitting them so each table is shown on its own "screen".

## Proposed Changes

### Domain Models & Data

#### [B2Card.kt](file:///C:/Projects/GermanVerbMaster-Android/app/src/main/java/com/germanverbmaster/android/domain/model/B2Card.kt)

- [DELETE] Remove `GRAMMAR` from `B2Category` enum.

#### [B2ContentData.kt](file:///C:/Projects/GermanVerbMaster-Android/app/src/main/java/com/germanverbmaster/android/ui/b2practice/B2ContentData.kt)

- No logic changes needed here, as `grammarTables` will still be used by the new bottom sheet.

---

### UI Components

#### [NEW] [GrammarBottomSheet.kt](file:///C:/Projects/GermanVerbMaster-Android/app/src/main/java/com/germanverbmaster/android/ui/components/GrammarBottomSheet.kt)

- Implement a `ModalBottomSheet` containing a `HorizontalPager`.
- Each page will render one `GrammarTable` using the existing `GrammarTableCard` logic.
- Add pager indicators (dots) and "Previous/Next" buttons or swipe instructions.

#### [B2PracticeScreen.kt](file:///C:/Projects/GermanVerbMaster-Android/app/src/main/java/com/germanverbmaster/android/ui/b2practice/B2PracticeScreen.kt)

- [DELETE] Remove `GrammarTabContent` and its usage when `category == B2Category.GRAMMAR`.
- [DELETE] `GrammarTableCard` (move it to a shared component or keep it in the new bottom sheet file).

---

### Navigation & Global UI

#### [NavGraph.kt](file:///C:/Projects/GermanVerbMaster-Android/app/src/main/java/com/germanverbmaster/android/navigation/NavGraph.kt)

- Add a state to track if the Grammar Bottom Sheet is open.
- Add a `FloatingActionButton` (FAB) or a specific icon in the `Scaffold`'s `TopAppBar` or `BottomBar` to trigger the bottom sheet.
- Integrate `GrammarBottomSheet` within the `Scaffold` content.

## Verification Plan

### Automated Tests
- Run `./gradlew app:assembleDebug` to ensure all references to `B2Category.GRAMMAR` are cleaned up.

### Manual Verification
- Open the app and verify that the "Grammatik" tab is gone from the B2 Practice screen.
- Verify that a "Grammar" button (FAB or similar) is visible on all main screens.
- Click the "Grammar" button and verify the `ModalBottomSheet` opens.
- Verify that you can swipe through the grammar tables one by one.
- Verify that each table is correctly formatted and legible.
