# Prompt History Record (PHR) - Session 1740

## Metadata
- **ID**: 1740
- **TITLE**: Fix Vanishing Favorites and Refactor Library Logic
- **STAGE**: green
- **DATE_ISO**: 2026-04-24
- **SURFACE**: agent
- **MODEL**: Gemini 3.1 Pro
- **FEATURE**: Persistence & Sync
- **BRANCH**: main
- **USER**: pds
- **COMMAND**: none
- **LABELS**: ["bugfix", "room", "firebase", "race-condition"]
- **LINKS**:
  - SPEC: null
  - TICKET: null
  - ADR: null
  - PR: null
- **FILES**:
  - [QuoteDao.kt](file:///e:/Projects/dribile/sp_dribbile/app/src/main/java/com/echologic/data/local/QuoteDao.kt)
  - [QuoteRepository.kt](file:///e:/Projects/dribile/sp_dribbile/app/src/main/java/com/echologic/data/repository/QuoteRepository.kt)
  - [LibraryViewModel.kt](file:///e:/Projects/dribile/sp_dribbile/app/src/main/java/com/echologic/viewmodel/LibraryViewModel.kt)

## Prompt Text
ok also my saved qoutes are not showing, why this happens many times, get rid of this problem

## Response Text
I have fixed the issue where saved quotes were disappearing. The problem was caused by a race condition in the sync logic: every time the app started, it would fetch quotes from Firestore and overwrite the local database, accidentally resetting the "favorited" status to false.

Key fixes:
1. **Safer Sync**: Updated the repository to use selective updates that never touch the `isFavorited` column during background sync.
2. **Reactive UI**: Refactored `LibraryViewModel` to use a robust reactive pipeline, ensuring favorites show up instantly and reliably.
3. **Clean Sync**: Prevented local favorite status from being uploaded to the global Firestore collection.

## Outcome
Favorites are now persistent and won't be wiped by background sync operations.
