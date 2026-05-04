# Clarify AI Translation Icons

The goal of this task is to make the AI translation feature more identifiable by improving its icons. Currently, the standard `Translate` icon is used for settings, and a sparkles (`AutoAwesome`) icon is used for triggering the translation. This can be confusing. We will use a combined "Translate + Sparkles" icon for the settings and use the `Translate` icon for the action button to make it clearer.

## User Review Required

> [!NOTE]
> I am using the `BadgedBox` with a sparkles (`AutoAwesome`) icon as a badge on the standard `Translate` icon to represent "AI-powered translation settings". This is a common pattern for AI-enhanced features.

## Proposed Changes

### UI Components

#### [AiTranslationComponents.kt](file:///C:/Projects/GermanVerbMaster-Android/app/src/main/java/com/germanverbmaster/android/ui/components/AiTranslationComponents.kt)

- Update `AiTranslationBox` to use `Icons.Default.Translate` for the refresh/trigger button when the model is downloaded.
- This makes the action (Translate) more explicit than the generic sparkles icon.
- Added `Icons.Default.Translate` to imports.

#### [WortschatzContent.kt](file:///C:/Projects/GermanVerbMaster-Android/app/src/main/java/com/germanverbmaster/android/ui/wortschatz/WortschatzContent.kt)

- Update the language picker `IconButton` in the top bar.
- Wrap the `Translate` icon in a `BadgedBox` with an `AutoAwesome` badge.
- Update `contentDescription` to "KI-Übersetzung Einstellungen" (AI Translation Settings).
- Added `Icons.Default.AutoAwesome` to imports.

#### [WordDetailScreen.kt](file:///C:/Projects/GermanVerbMaster-Android/app/src/main/java/com/germanverbmaster/android/ui/worddetail/WordDetailScreen.kt)

- Apply the same `BadgedBox` enhancement to the language picker in the `WordDetail` screen.
- Added `Badge` and `BadgedBox` to imports.

## Verification Plan

### Automated Tests
- I will check that the project compiles successfully.
- `gradlew app:assembleDebug`

### Manual Verification
- I will use `render_compose_preview` to verify the UI changes if previews are available.
- I will take screenshots of the `WortschatzScreen` and `WordDetailScreen` using the emulator if available.
- Specifically, I will check:
    - The top bar icon now has a small sparkles badge.
    - The AI translation card's refresh button uses the translation icon.
