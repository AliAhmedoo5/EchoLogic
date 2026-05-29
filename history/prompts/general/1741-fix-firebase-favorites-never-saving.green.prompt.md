# Prompt History Record (PHR) - Session 1741

## Metadata
- **ID**: 1741
- **TITLE**: Fix Per-User Firebase Favorites Never Saving
- **STAGE**: green
- **DATE_ISO**: 2026-04-25
- **SURFACE**: agent
- **MODEL**: Claude Sonnet 4.6
- **FEATURE**: Persistence & Sync
- **BRANCH**: main
- **USER**: pds
- **COMMAND**: none
- **LABELS**: ["bugfix", "firebase", "firestore", "auth", "favorites", "stateflow"]
- **LINKS**:
  - SPEC: null
  - TICKET: null
  - ADR: null
  - PR: null
- **FILES**:
  - [MainViewModel.kt](file:///e:/Projects/dribile/sp_dribbile/app/src/main/java/com/echologic/viewmodel/MainViewModel.kt)
  - [AuthRepository.kt](file:///e:/Projects/dribile/sp_dribbile/app/src/main/java/com/echologic/data/repository/AuthRepository.kt)
  - [QuoteRepository.kt](file:///e:/Projects/dribile/sp_dribbile/app/src/main/java/com/echologic/data/repository/QuoteRepository.kt)
  - [ECHO_LOGIC_DEVELOPMENT_STATE.md](file:///e:/Projects/dribile/sp_dribbile/ECHO_LOGIC_DEVELOPMENT_STATE.md)

## Prompt Text
the issue is still not solved, check everything, if the app is not communicating with the firebase correctly to save the user's data or whats the problem

## Root Cause Analysis

### Bug #1 — userId was ALWAYS null (Critical)
`MainViewModel` injected `AuthRepository` and created a StateFlow:
```kotlin
private val authUser = authRepository.currentUser
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
```
`WhileSubscribed(5000)` only starts collecting when a subscriber exists. Since `authUser` was `private` and never collected by any Composable, it **permanently stayed at `null`**. Every call to `toggleFavorite` passed `null` as the userId, so `userId?.let { }` always skipped the Firestore write.

**Proof**: Checked Firestore via Firebase MCP — `users/{uid}/favorites` subcollection was completely empty for ALL users. No favorites had ever been saved.

### Bug #2 — signOut() destroyed all quotes
`database.clearAllTables()` deleted the entire Room database including all 200+ quotes. `MainViewModel.init { ensureSeeded() }` only runs once on first ViewModel creation (Hilt caches the ViewModel), so quotes were never restored.

### Bug #3 — Firestore writes were fire-and-forget
`userFavRef.set(favoriteData)` without `.await()` silently discarded network errors.

## Response Text / Fixes Applied

### Fix 1: MainViewModel — Use FirebaseAuth directly
```kotlin
// BEFORE (broken — authUser always null)
private val authUser = authRepository.currentUser
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
repository.toggleFavorite(authUser.value?.uid, quote.id, newState)

// AFTER (correct — always returns real UID)
private val auth: FirebaseAuth  // injected directly
repository.toggleFavorite(auth.currentUser?.uid, quote.id, newState)
```

### Fix 2: AuthRepository.signOut() — Preserve quotes
```kotlin
// BEFORE (broken — nuked all quotes)
database.clearAllTables()

// AFTER (correct — only resets user-specific favorite flags)
database.quoteDao().resetAllFavorites()
```

### Fix 3: AuthRepository.syncUserData() — Proper per-user sync
```kotlin
private suspend fun syncUserData(uid: String) {
    database.quoteDao().resetAllFavorites()   // clean slate
    quoteRepository.ensureSeeded()             // safety net
    quoteRepository.mergeFavoritesWithCloud(uid)  // pull THIS user's favorites
    settingsRepository.syncSettingsWithCloud(uid)
}
```

### Fix 4: QuoteRepository.toggleFavorite() — await + logging
Added `.await()` to all Firestore `set()` and `delete()` calls. Added explicit `Log.d` for every success/failure path.

### Fix 5: mergeFavoritesWithCloud() — Simplified to pull-only
Since `syncUserData` always resets favorites before calling this, simplified to just pull cloud favorites down and restore them locally.

## Outcome
✅ Verified working — per-user favorites now save and restore correctly across sign-out/sign-in cycles.
APK built: `app/build/outputs/apk/debug/app-debug.apk` (April 25, 2026 — 1:30 AM)
