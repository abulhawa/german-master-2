# Implementation Plan - Clutter Reduction and Selection Removal

This plan addresses two requests:
1. Clearing text selection when tapping outside of selectable areas.
2. Reducing UI clutter by removing the "KI is not 100% sure" confidence message.

## Proposed Changes

### [Translation Components]

#### [AiTranslationComponents.kt](file:///C:/Projects/GermanVerbMaster-Android/app/src/main/java/com/germanverbmaster/android/ui/components/AiTranslationComponents.kt)

- **Clutter Reduction**: Remove the `if (result is TranslationManager.TranslationResult.LowConfidence)` block in `TranslationResultView` that displays the "Die KI ist sich bei dieser Übersetzung nicht zu 100% sicher" message.
- **Visual Cleanup**: Remove the same low-confidence warning block from `AiTranslationBox`.

### [ViewModels]

#### [WortschatzViewModel.kt](file:///C:/Projects/GermanVerbMaster-Android/app/src/main/java/com/germanverbmaster/android/ui/wortschatz/WortschatzViewModel.kt)

- Add `clearSelection()` function to increment `selectionKey`. This forces a recomposition of `SelectionContainer`s which effectively clears the current selection.

#### [WordDetailViewModel.kt](file:///C:/Projects/GermanVerbMaster-Android/app/src/main/java/com/germanverbmaster/android/ui/worddetail/WordDetailViewModel.kt)

- Add `selectionKey` state (as a `MutableStateFlow`).
- Add `clearSelection()` function to increment `selectionKey`.

### [Screens]

#### [WortschatzContent.kt](file:///C:/Projects/GermanVerbMaster-Android/app/src/main/java/com/germanverbmaster/android/ui/wortschatz/WortschatzContent.kt)

- Add `onClearSelection: () -> Unit` to `WortschatzScreenContent` parameters.
- Add `import androidx.compose.foundation.gestures.detectTapGestures` and `import androidx.compose.ui.input.pointer.pointerInput`.
- Wrap the `PullToRefreshBox` content with a `Box` (or apply to the existing `PullToRefreshBox`) a `pointerInput` modifier that calls `onClearSelection()` on tap.

#### [WortschatzScreen.kt](file:///C:/Projects/GermanVerbMaster-Android/app/src/main/java/com/germanverbmaster/android/ui/wortschatz/WortschatzScreen.kt)

- Pass `onClearSelection = viewModel::clearSelection` to `WortschatzScreenContent`.

#### [WordDetailScreen.kt](file:///C:/Projects/GermanVerbMaster-Android/app/src/main/java/com/germanverbmaster/android/ui/worddetail/WordDetailScreen.kt)

- Collect `selectionKey` from ViewModel.
- Apply `pointerInput` with `detectTapGestures` to the root `Column` inside `Scaffold` to trigger `viewModel.clearSelection()`.

## Verification Plan

### Manual Verification
- **Clutter Reduction**:
    - Trigger an AI translation in Wortschatz or Word Detail.
    - Verify that no "Low Confidence" / "100% sicher" message appears, even if the result was previously marked as low confidence.
- **Selection Removal**:
    - Select some text in Wortschatz or Word Detail.
    - Tap on an empty area of the screen.
    - Verify that the selection handles and background disappear.
