# FEATURES & ARCHITECTURE

> **EchoLogic** — "Brain off. Logic on." A quote discovery app for Gen Z, built with a bold Neo-Brutalist design and cloud-first data architecture.

---

## 📱 Feature Map

### 1. Quote Feed (Main Screen)
- **Offline-First Engine**: ~200 curated quotes seeded into Room on first launch from `QuoteSeeder.kt`.
- **Random Discovery**: Tap to load a new quote; tracks recent IDs to avoid repeats (rolling window of 20).
- **Category Filtering**: Feed only shows quotes from the user's selected categories (**Now FREE**).
- **Auto-Refresh**: If the user deselects the current quote's category in Settings, the feed immediately loads a new one.
- **Cloud Sync**: On every launch, pulls new/updated quotes from Firestore in the background.
- **Visual Feedback**: Tapping the heart shows a pop animation; dynamic primary color accents used for all badges and buttons.

### 2. Favorites & Library
- **Save Quotes**: Heart tap writes to Room locally (instant) AND uploads to `users/{uid}/favorites/{quoteId}` in Firestore.
- **Advanced Filtering**: Library organizes saved quotes by category/vibe.
- **Search & Sort**: Search by text/author and sort by Date (Newest/Oldest) or Author (A-Z).
- **Offline Access**: Full Room integration — favorites accessible without internet.

### 3. Personalization (Premium)
- **Dynamic Color Accents**: System-wide accent color customization (Yellow, Cyan, Pink, Purple, Green, Orange).
- **Sleek & Swift Animations**: High-performance navigation and interaction transitions designed for a professional feel.
- **Offline-First Sync**: Quote categories and data synchronized via WorkManager and Firebase.
- **Dark Mode Toggle**: Full support for system-wide light/dark aesthetics.
- **High-Retention Daily Echo**: Premium users receive a daily notification with a fresh quote.

### 4. Retention & Community
- **Streak System**: Track daily active days with a dynamic "flame" indicator on the home screen. Streaks are synced to Firestore to prevent progress loss on device reinstall.
- **High-Retention Daily Echo**: Premium users receive a daily notification with a fresh quote. Users can select their preferred delivery time, which is anchored to their local timezone via `WorkManager`.
- **Navigation Transitions**: Horizontal slide + fade with `FastOutSlowInEasing`.
- **Quote Transitions**: Vertical slide + scale + fade for quote switches.
- **Tactile Feedback**: 95% scale-down on button press with high-stiffness spring physics.
- **List Animations**: `animateItemPlacement` for smooth reordering in the Library.
- **"Quote of the Day" Widget**: Premium dark-mode widget for the home screen that updates every 24 hours.
- **Community Submissions**: Users can submit their own quotes for moderation; stores in a global `submissions` collection.

### 5. Google Sign-In / Authentication
- **Google OAuth** via Firebase Authentication.
- **Auth Gate**: `MainActivity` uses a tri-state `AuthState` (`Loading` → `Authenticated` / `Unauthenticated`). During `Loading`, an empty themed background is shown — eliminating the "sign-up flash" for already-authenticated users.
- **Per-Session Sync**: On every sign-in, local state is reset and this user's cloud data (favorites, accent color, categories) is restored.

### 6. Per-User Cloud Sync (Firestore)
- On **sign-in**: `resetAllFavorites()` → `ensureSeeded()` → `mergeFavoritesWithCloud(uid)` → `syncSettingsWithCloud(uid)` → `syncStreakFromCloud(uid)`.
- On **favorite tap**: writes to `users/{uid}/favorites/{quoteId}` with full metadata.
- **Settings Persistence**: Accent color, dark mode, daily notification toggles, and notification time preferences are synced to the cloud.
- **Streak Persistence**: `currentStreak`, `longestStreak`, and `lastActiveDate` are synced to cloud on every update and merged on login.

---

## 🏗️ Technical Architecture

### Pattern: MVVM + Repository + Hilt DI

```
com.echologic
├── MainActivity.kt           ← Auth gate + NavHost (3 tabs: Feed, Library, Settings)
├── EchoLogicApp.kt           ← HiltWorker support + global crash handler setup
│
├── data/
│   ├── local/
│   │   ├── AppDatabase.kt        ← Room DB (echologic-v4.db)
│   │   ├── QuoteDao.kt           ← All Room queries
│   │   ├── QuoteEntity.kt        ← Room entity + Firestore POJO
│   │   └── QuoteSeeder.kt        ← 200+ curated seed quotes
│   └── repository/
│       ├── AuthRepository.kt     ← Auth & sync orchestration
│       ├── QuoteRepository.kt    ← Quotes CRUD, Firestore sync
│       ├── SettingsRepository.kt ← Dynamic colors, categories, premium flag
│       └── StreakRepository.kt   ← Daily active day tracking
│
├── worker/
│   └── QuoteWorker.kt            ← Background task for daily notifications
│
├── widget/
│   └── QuoteWidgetProvider.kt    ← AppWidgetProvider for home screen
│
├── viewmodel/
│   ├── MainViewModel.kt      ← Feed logic, fetchNewQuote(), toggleFavorite()
│   ├── LibraryViewModel.kt   ← Reactive filtering, search, and sorting
│   ├── SettingsViewModel.kt  ← Appearance, notifications, billing, cloud sync
│
└── ui/
    ├── screens/
    │   ├── MainScreen.kt     ← Quote card, heart animation, streak indicator
    │   ├── LibraryScreen.kt  ← Searchable favorites grouped by vibe
    │   ├── SettingsScreen.kt ← Appearance picker, categories, pro-features
    │   └── AuthScreen.kt     ← Google Sign-In button
    ├── theme/
    │   ├── Color.kt          ← Color tokens & category color logic
    │   ├── Type.kt           ← Typography (Outfit / Inter)
    │   └── Theme.kt          ← Dynamic EchoLogicTheme with accent injection
```

---

## ☁️ Firestore Schema

```
quotes/
  {quoteId}/
    id              : String
    text            : String
    author          : String
    category        : String
    isPremium       : Boolean
    globalLikesCount: Int

submissions/        ← Global user-generated content
  {submissionId}/
    text            : String
    author          : String
    submittedBy     : String (UID)
    timestamp       : Timestamp

users/
  {uid}/
    favorites/
      {quoteId}/
        ... (full quote metadata)
    settings/
      preferences/
        isPremium           : Boolean
        isDarkMode          : Boolean
        accentColor         : Long (Int value)
        selectedCategories  : [String]
```

---

## 📦 Build & Deployment

### 1. Release Artifacts
- **Release APK**: Signed and optimized (19.1 MB).
- **Debug APK**: Full development build (26.5 MB).
- **Signing**: Configured with `keystore.jks` in the `app/` module.

### 2. ProGuard / R8
- Mandatory minification enabled for release builds.
- Critical `keep` rules for Hilt and Room entities maintained in `proguard-rules.pro`.

### 3. CI/CD Readiness
- **Fatal Lint Checks**: Release builds now explicitly handle fatal lint errors related to on-demand WorkManager initialization.
- **Gradle Version**: Using Gradle 8.x with stable Compose compiler plugins.

---

## 🔒 Architecture Rules (Non-Negotiable)

1. **Quotes are Global Data**: Never delete quotes on sign-out; only reset `isFavorited`.
2. **Cloud-First Settings**: Preferences (Accent, Dark Mode) MUST sync to Firestore to provide a consistent cross-device experience.
3. **Optimistic UI**: Favorites update locally instantly via Flow, with background `.await()` for cloud sync.
4. **Zero-Crash Initialization**: 
   - Firebase MUST be initialized *before* `super.onCreate()` in `EchoLogicApp` (specifically, inside `attachBaseContext`).
   - WorkManager `Configuration` MUST use `EntryPointAccessors` to avoid `lateinit` races with Hilt.
   - All 3rd-party SDKs must have `try-catch` wrappers during startup.
5. **Runtime-Safe Resources**: Use `getIdentifier` for generated resources like `default_web_client_id` to prevent `Resources.NotFoundException` due to package/namespace mismatches.
6. **No Wildcard Imports**: Never use wildcard imports for Jetpack Compose icons (e.g., `import androidx.compose.material.icons.filled.*`), as this will cause an infinite hang on the splash screen due to massive asset loading.
7. **Room Database Initialization**: Always include `.fallbackToDestructiveMigration()` in the Room database builder during development to prevent immediate crashes when testing un-versioned schema changes.
8. **Valid Application Icons**: Never use XML `<vector>` drawables directly for `android:icon` or `android:roundIcon` in the `AndroidManifest.xml` (e.g., `@drawable/ic_app_logo`), as it will crash many Android launchers. Always use `@mipmap/ic_launcher` which points to an adaptive icon or raster fallback.
9. **Sticky Premium**: Premium status must never be downgraded by automated systems (billing queries, cloud sync). `BillingRepository.queryPurchases()` only calls `setPremium(true)` on confirmed purchases. `syncSettingsWithCloud()` only applies cloud premium if it's `true` or local is already `false`. If local is `true` and cloud is `false`, push local to cloud.
10. **Tri-State Auth Gate**: Never use a `null`-check on `FirebaseUser?` to gate the auth screen. Use a sealed `AuthState` (`Loading` / `Authenticated` / `Unauthenticated`) to prevent the sign-in screen from flashing on cold start.

11. **Timezone-Anchored Background Work**: Never use a generic `PeriodicWorkRequest` for exact-time daily tasks. Always calculate the `initialDelay` based on the user's local timezone target time and use `ExistingPeriodicWorkPolicy.UPDATE` to ensure the schedule survives reboots and user time changes.
12. **Max-Merge for Streaks**: When syncing local-first incremental data (like streaks) from the cloud upon sign-in, use a max-merge strategy (`maxOf(cloud, local)`). Never overwrite a higher local value with a lower cloud value to protect user progress.

---

*Last Updated: May 7, 2026 (Notifications, Streaks, & Icon)*
