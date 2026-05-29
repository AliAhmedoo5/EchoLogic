# HURDLES & SOLUTIONS

> This document tracks every real technical challenge encountered during EchoLogic development — root causes, symptoms, and exact fixes applied.

---

## 1. Favorites Vanishing After App Restart (Race Condition in Cloud Sync)
**Symptom**: Saved quotes would disappear every time the app restarted.
**Root Cause**: `refreshQuotes()` used `REPLACE` strategy, overwriting local `isFavorited` flags with non-favorited cloud data.
**Fix**: Split sync into `insertIgnore` and `updateCloudFields()` to preserve the local `isFavorited` state.

## 2. userId Always `null` — Favorites Never Saved to Firebase
**Symptom**: Tapping heart worked locally but never synced to Firestore.
**Root Cause**: `MainViewModel` used `WhileSubscribed(5000)` on a private flow that no Composable observed, keeping it at `null`.
**Fix**: Read `auth.currentUser?.uid` directly at the moment of the action inside the Repository/ViewModel.

## 3. `clearAllTables()` on Sign-Out Destroyed the App
**Symptom**: Feed went blank after signing back in.
**Root Cause**: Sign-out deleted the entire quote library. Seed logic only runs once on ViewModel creation.
**Fix**: Replaced with `quoteDao.resetAllFavorites()`. Global data must survive account switches.

## 4. Wrong Account's Favorites Showing After Account Switch
**Symptom**: User B sees User A's favorites.
**Root Cause**: Local Room data wasn't cleared before merging new user's cloud data.
**Fix**: Strict `syncUserData()` sequence: Wipe local flags → Pull cloud state.

## 5. Firestore Writes Were Fire-and-Forget
**Symptom**: Cloud writes occasionally failed silently.
**Root Cause**: Missing `.await()` on Task objects inside suspend functions.
**Fix**: Mandatory `.await()` on all Firebase operations to ensure exceptions are caught.

## 6. Hilt-WorkWorker "WorkerFactory not initialized"
**Symptom**: Daily notifications crashed the app on boot or background trigger.
**Root Cause**: Hilt requires a custom `Configuration` in the `Application` class to inject dependencies into `ListenableWorker`.
**Fix**: Implemented `Configuration.Provider` in `EchoLogicApp` and updated `AndroidManifest.xml` to disable default WorkManager initialization.

## 7. Home Screen Widget Update Latency
**Symptom**: Quote widget stayed on "Logic off" even after background sync.
**Root Cause**: `AppWidgetManager` updates are throttled by the OS (30min minimum).
**Fix**: Implemented a custom `onReceive` broadcast to force-refresh the widget immediately after a successful quote sync or manual refresh.

## 8. StateFlow Lifecycle Jitter in Library Search
**Symptom**: Keyboard would hide or search results would flicker while typing.
**Root Cause**: The search Flow was being re-created on every recomposition.
**Flow Fix**: Moved the `combine(searchQuery, selectedCategory, sortOrder)` logic into the ViewModel's `init` block using `stateIn` to maintain a single stable Flow instance.

## 9. Duplicate Color Ambiguity in Theme Overhaul
**Symptom**: Build failed with "Overload resolution ambiguity" for `ElectricYellow` and `DeepBlack`.
**Root Cause**: During the transition to a dynamic accent picker, color constants were defined in both `Color.kt` and `Theme.kt`.
**Fix**: Consolidated all color tokens into `Color.kt` and removed redundant definitions from `Theme.kt`.

## 10. POST_NOTIFICATIONS Permission on Android 13+
**Symptom**: Daily Echo feature worked on older devices but never showed notifications on Pixel 7/8.
**Root Cause**: Android 13 (API 33) introduced a runtime permission for notifications.
**Fix**: Added runtime permission check in `SettingsScreen` when toggling notifications, ensuring the user grants permission before `WorkManager` is scheduled.

## 11. Compose `animateItem` vs. BOM Versions
**Symptom**: Build failed with "Unresolved reference: animateItem".
**Root Cause**: `animateItem()` was introduced in Compose 1.7.0, but the project uses BOM 2024.06.00 (Compose 1.6.x).
**Fix**: Reverted to `animateItemPlacement()` and added `@OptIn(ExperimentalFoundationApi::class)`.

## 12. Coroutine Scope Access in `LaunchedEffect`
**Symptom**: Build failed with "Unresolved reference: launch" inside a `LaunchedEffect` block.
**Root Cause**: While `LaunchedEffect` provides a scope, `launch` is an extension function on `CoroutineScope` and was not implicitly available without import or explicit receiver.
**Fix**: Added `import kotlinx.coroutines.launch` and wrapped parallel animations in `coroutineScope { launch { ... } }` for clarity and safety.

## 13. Fatal Lint Error: `RemoveWorkManagerInitializer`
**Symptom**: `assembleRelease` failed even though `assembleDebug` passed.
**Root Cause**: When implementing `Configuration.Provider` (for HiltWork), Android's release-stage lint checks strictly enforce the removal of the default `WorkManagerInitializer` to prevent startup race conditions.
**Fix**: Added an explicit `<provider>` node to `AndroidManifest.xml` targeting `androidx.startup.InitializationProvider` and used `tools:node="remove"` on the `WorkManagerInitializer` meta-data.

## 14. "FirebaseApp not initialized" Race Condition
**Symptom**: App crashes on startup with `Default FirebaseApp is not initialized`.
**Root Cause**: Hilt performs member injection inside `super.onCreate()`. If repositories depend on Firebase, but `FirebaseApp.initializeApp()` is called after `super.onCreate()`, injection fails. Conversely, if Firebase triggers WorkManager before Hilt is ready, `workerFactory` is uninitialized.
**Fix**: 
1. Moved `FirebaseApp.initializeApp(base)` to `attachBaseContext()` to ensure it runs before `super.onCreate()`.
2. Used `EntryPointAccessors` inside `workManagerConfiguration` to fetch `workerFactory` on-demand, bypassing `lateinit` race conditions.

## 15. Resources.NotFoundException for Google Web Client ID
**Symptom**: App crashes when initializing Google Sign-In.
**Root Cause**: `google-services` plugin generates resources based on `applicationId`, which may differ from the `namespace`. Referencing `R.string.default_web_client_id` directly can fail if the package mismatch exists.
**Fix**: Use `context.resources.getIdentifier("default_web_client_id", "string", context.packageName)` for a runtime safe lookup.

## 16. The "Wildcard Icon" Hang
**Symptom**: The app launches but stays stuck on the Android Splash Screen indefinitely.
**Root Cause**: Using wildcard imports for Jetpack Compose Material Icons (e.g., `import androidx.compose.material.icons.filled.*`). This forced the compiler and runtime to load thousands of vector assets into memory.
**Fix**: Replaced wildcard imports with explicit, singular imports for only the required icons.

## 17. Room Schema Contention
**Symptom**: Crash on launch with `IllegalStateException: Room cannot verify the data integrity`.
**Root Cause**: Rapid changes to `QuoteEntity` without incrementing the database version or providing a migration path.
**Fix**: Added `.fallbackToDestructiveMigration()` to the Room database builder to ensure a clean state when schema mismatches occur.

## 18. Vector Drawable App Icon Crash
**Symptom**: The app fails to open or crashes immediately upon installation/launch on the phone.
**Root Cause**: `AndroidManifest.xml` used an XML Vector Drawable (`@drawable/ic_app_logo`) with gradients for `android:icon`. Vector drawables cannot be used directly as application icons and cause `Resources$NotFoundException` or parsing errors on many launchers.
**Fix**: Reverted `android:icon` and `android:roundIcon` in the manifest to use proper raster/adaptive icons (`@mipmap/ic_launcher` and `@mipmap/ic_launcher_round`).

## 19. Auth Screen Flash on App Open
**Symptom**: When opening the app, the "Sign Up" button appears for ~2 seconds, then transitions to the main screen.
**Root Cause**: `MainActivity` observed `authRepository.currentUser` as a `Flow<FirebaseUser?>` with `initial = null`. Since Firebase's `AuthStateListener` fires asynchronously (after a few hundred ms), the UI initially saw `null` → showed `AuthScreen` → then received the cached credential → switched to main nav.
**Fix**: Introduced a sealed `AuthState` class with three states: `Loading`, `Unauthenticated`, and `Authenticated`. The initial state is `Loading` (shows an empty background), which transitions to the correct state only *after* the `AuthStateListener` has fired. The auth screen is never shown to already-signed-in users.

## 20. Premium ("Go Pro") Status Resets After App Restart
**Symptom**: User activates Pro mode (via debug tap or purchase), but on the next app launch, the "GO PRO" card reappears and premium features are locked again.
**Root Cause**: Two independent subsystems were both overwriting the local `isPremium` flag on every cold start:
1. **`BillingRepository.queryPurchases()`**: Called on billing client connection. If the Play Console product `premium_upgrade_one_time` doesn't exist or the purchase hasn't been acknowledged, it returned zero purchases → `setPremium(false)`, wiping the local flag.
2. **`syncSettingsWithCloud()`**: Called in `AuthRepository.syncUserData()`. If the Firestore `users/{uid}/settings/preferences` document was empty or had `isPremium: false` (e.g., the upload never happened), it blindly overwrote the local `true` with `false`.
**Fix**: Implemented a "Sticky Premium" pattern across both systems:
- **BillingRepository**: Changed `queryPurchases()` to upgrade-only — it only calls `setPremium(true)` when a valid purchase is found, and never downgrades.
- **SettingsRepository.syncSettingsWithCloud()**: Cloud can upgrade (`false`→`true`) but never downgrade (`true`→`false`). If local is `true` but cloud is `false`, the local state is preserved and pushed to cloud to heal the inconsistency.
- **No cloud doc**: If no preferences document exists in Firestore, local settings are uploaded instead of defaulting to non-premium.

## 21. Notifications Unreliable and Streaks Reset on Reinstall
**Symptom**: Daily notifications either didn't appear on Android 13+, fired at random times like 3 AM, or disappeared after an app restart. Additionally, user streaks were lost if the app was reinstalled.
**Root Cause**: 
1. `POST_NOTIFICATIONS` was never explicitly requested via ActivityResultContracts.
2. `PeriodicWorkRequest` had no `initialDelay` anchored to a specific time, allowing WorkManager to schedule immediately and flex.
3. Notification preferences and Streak data were kept strictly in local `SharedPreferences` and not included in Firestore sync.
**Fix**: 
- Added a `TimePicker` to calculate a precise `initialDelay` for local timezone.
- Migrated `isDailyNotificationEnabled`, `currentStreak`, and `lastActiveDate` to the cloud sync pipeline with a max-merge strategy for streaks (restoring highest streak from cloud).
- Re-enqueued WorkManager on every app startup using the cached preferences.

---

*EchoLogic — Brain off. Logic on. ⚡*
*Last Updated: May 7, 2026 (Notifications, Streaks, & Icon)*
