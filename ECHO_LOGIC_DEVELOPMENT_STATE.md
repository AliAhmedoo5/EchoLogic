# 🧠 EchoLogic — Full Project Memory

> **"Brain-Off, Logic-On"** — A quote discovery app for Gen Z. Absurd, brainrot, philosophical, and dumb quotes delivered with a bold Neo-Brutalist aesthetic.

---

## 📦 Project Info

| Key | Value |
|-----|-------|
| **App Name** | EchoLogic |
| **Package** | `com.echologic` |
| **Language** | Kotlin |
| **UI Framework** | Jetpack Compose |
| **DI** | Hilt |
| **Local DB** | Room (`echologic-v4.db`) |
| **Cloud** | Firebase (Firestore + Auth + Analytics + AdMob) |
| **Architecture** | MVVM + Repository Pattern |
| **Firebase Project** | `echologic-bbdd7` |
| **Firestore Region** | `nam5` |

---

## 🎨 Themes & Design System

The app has **4 themes**, switchable by Premium users:

| Theme | Description | Colors |
|-------|-------------|--------|
| **Neo-Brutalist** *(default)* | Bold, high-contrast, thick borders | Electric Yellow + Deep Black + Mint Green |
| **OLED Mode** | True black for OLED screens | `#000000` background |
| **Hacker Mode** | Retro terminal aesthetic | Terminal Green on Terminal Dark + Monospaced font |
| **Editorial / Glassmorphism** | Premium clean look | Soft gradients, glass panels |

Design tokens live in `ui/theme/`. Theme is persisted in `SettingsRepository` (SharedPrefs + Firestore sync).

---

## ✅ Features (Implemented)

### 1. Quote Feed (Main Screen)
- Swipe/tap to load a new random quote
- **Offline-first**: quotes seeded locally in Room on first launch
- Background Firestore sync on every launch (pulls new quotes, never overwrites `isFavorited`)
- Category filtering: only shows quotes from user's selected categories
- Auto-refreshes if the current quote's category gets deselected in Settings

### 2. Favorites / Library Screen
- Heart tap saves a quote locally (Room) AND uploads to Firestore (`users/{uid}/favorites/{quoteId}`)
- Library shows all favorited quotes grouped by category
- Fully reactive — updates instantly on favorite/unfavorite via Room Flow

### 3. Google Sign-In / Auth
- Google OAuth via Firebase Authentication
- Auth state drives navigation: `AuthScreen` shown when `currentUser == null`, main nav shown when logged in
- Per-user data: favorites and settings are tied to Firebase UID

### 4. Per-User Data Sync (Firestore)
- On **sign-in**: reset local favorites → re-seed if needed → pull this user's favorites from cloud → pull settings
- On **favorite tap**: save to `users/{uid}/favorites/{quoteId}` with full quote data + timestamp
- On **sign-out**: reset local `isFavorited` flags (quotes stay), clear settings

### 5. Settings Screen
- **Theme selector** (Premium) — 4 themes
- **Category filter** (Premium) — multi-select from dynamic category list
- **Pro upgrade** via Google Play Billing
- **Force unlock** (dev shortcut)
- **Sign out** / **Delete account**
- Shows **logged-in account name** at bottom (or top) of settings

### 6. Dynamic Categories
- Category list is NOT hardcoded — fetched from `metadata/app_config.categories` in Firestore
- Cached locally (SharedPrefs) for offline use
- Default fallback: Logic Loops, Shower Thoughts, Absurd, Deep Dumb, Accidental Philosophy, Brainrot, Celebrity Dumb

### 7. AdMob Integration
- `AdBanner.kt` component ready
- Architecture set up for banner ads; dev mode configured

### 8. Crash Reporter
- `CrashActivity.kt` catches fatal crashes
- Last crash stored in SharedPrefs (`crash_prefs`)
- Shown on next launch with full stack trace + "Clear and Try Again" button

### 9. Firebase Analytics
- Logs `quote_interaction` event on every favorite action

---

## 🏗️ Architecture

```
com.echologic
├── MainActivity.kt           ← Auth gate + NavHost (3 tabs: Feed, Library, Settings)
├── EchoLogicApp.kt           ← Hilt Application class + crash handler
├── CrashActivity.kt          ← Fatal crash display
│
├── data/
│   ├── local/
│   │   ├── AppDatabase.kt    ← Room DB (echologic-v4.db)
│   │   ├── QuoteDao.kt       ← All local quote queries
│   │   ├── QuoteEntity.kt    ← Room entity + Firestore model
│   │   └── QuoteSeeder.kt    ← 200+ hardcoded seed quotes (15 categories)
│   └── repository/
│       ├── AuthRepository.kt     ← Google sign-in, sign-out, per-user sync
│       ├── AuthService.kt        ← Thin wrapper
│       ├── BillingRepository.kt  ← Google Play Billing (premium unlock)
│       ├── QuoteRepository.kt    ← CRUD, Firestore sync, favorites cloud write
│       └── SettingsRepository.kt ← Theme, premium, categories (SharedPrefs + Firestore)
│
├── di/
│   └── AppModule.kt          ← Hilt providers: Room, FirebaseAuth, Firestore, Analytics, GoogleSignIn
│
├── viewmodel/
│   ├── MainViewModel.kt      ← Feed logic, fetchNewQuote, toggleFavorite
│   ├── LibraryViewModel.kt   ← Favorites flow from Room
│   ├── SettingsViewModel.kt  ← Theme, categories, sign-out, billing
│   └── AuthViewModel.kt      ← Auth state (thin)
│
└── ui/
    ├── screens/
    │   ├── MainScreen.kt     ← Quote card, heart animation, swipe
    │   ├── LibraryScreen.kt  ← Favorited quotes grouped by category
    │   ├── SettingsScreen.kt ← All settings + account name display
    │   └── AuthScreen.kt     ← Google Sign-In button
    ├── components/
    │   └── AdBanner.kt       ← AdMob banner component
    └── theme/
        └── Color.kt + Type.kt + Theme.kt
```

---

## ☁️ Firestore Schema

```
quotes/
  {quoteId}/
    id: String
    text: String
    author: String
    category: String
    isPremium: Boolean
    globalLikesCount: Int

metadata/
  app_config/
    categories: [String]   ← dynamic category list

users/
  {uid}/
    favorites/
      {quoteId}/
        id: String
        text: String
        author: String
        category: String
        timestamp: Timestamp
    settings/
      preferences/
        theme: String
        isPremium: Boolean
        selectedCategories: [String]
```

---

## 📚 Quote Categories (15 total — in QuoteSeeder.kt)

| Category | Count | Vibe |
|----------|-------|------|
| Logic Loops | 15 | Self-referential paradoxes |
| Shower Thoughts | 15 | 3am brain observations |
| Absurd | 18 | Random funny one-liners |
| Deep Dumb | 15 | Stupid questions that hit deep |
| Accidental Philosophy | 10 | Profound by accident |
| Brainrot | 10 | Pure Gen Z chaos |
| Celebrity Dumb | 15 | Famous people being funny |
| Aura & Vibe | 14 | Sigma aura energy |
| Mindset Growth | 17 | Goated mindset quotes |
| Skibidi Chaos | 10 | Skibidi-core wisdom |
| Let Him Cook | 9 | Trust the process |
| NPC vs Main Character | 6 | Game metaphors for life |
| Rizz & Social | 5 | Social dynamics |
| Fanum Tax | 9 | Sharing is caring (forced) |
| Crash Out | 9 | Emotional regulation |
| Peace & Detachment | 9 | Chill wisdom |
| Life Simulation | 8 | Reality is a game |

---

## ⚠️ Hurdles & Bugs Solved

### 1. `clearAllTables()` Destroyed the App (Critical)
**Problem**: `signOut()` called `database.clearAllTables()`. This deleted all 200+ quotes from Room. `MainViewModel.init { ensureSeeded() }` only runs once at ViewModel creation (Hilt caches it), so quotes were NEVER restored after sign-out → sign-in. The app showed nothing.

**Fix**: Replaced `clearAllTables()` with `quoteDao.resetAllFavorites()`. Quotes are global data and must survive account switches. Only the `isFavorited` flag is user-specific.

---

### 2. `userId` Was Always `null` — Favorites NEVER Saved to Cloud (Critical)
**Problem**: `MainViewModel` used:
```kotlin
private val authUser = authRepository.currentUser
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
```
`WhileSubscribed(5000)` only starts collecting when a Composable subscribes. Since `authUser` was `private` (never observed by UI), it **permanently stayed at `null`**. Every `toggleFavorite()` passed `null` userId, so the Firestore write was always silently skipped.

**Proof**: Checked Firestore via Firebase MCP — `users/{uid}/favorites` was completely empty for ALL users ever.

**Fix**: Injected `FirebaseAuth` directly into `MainViewModel` and used `auth.currentUser?.uid` inline — always the real value.

---

### 3. Firestore Writes Were Fire-and-Forget (Silently Failing)
**Problem**: `userFavRef.set(favoriteData)` and `.delete()` had no `.await()`, so errors were swallowed silently and the coroutine didn't wait for the write to complete.

**Fix**: Added `.await()` to all Firestore writes in `toggleFavorite()`.

---

### 4. Favorites Wiped on Cloud Sync (Race Condition)
**Problem**: `refreshQuotes()` used `insertOrReplace` strategy which overwrote the local `isFavorited = true` with `isFavorited = false` from the cloud master list (global quotes don't have user favorites).

**Fix**: Split into two operations — `insertIgnore` for new quotes, then `updateCloudFields()` which only updates `text`, `author`, `category`, `globalLikesCount` — never touches `isFavorited`.

---

### 5. Wrong User's Favorites Showing After Switch
**Problem**: Sign-out didn't clear local favorites. Sign-in with Account B showed Account A's local favorites.

**Fix**: `syncUserData()` now calls `resetAllFavorites()` first (clean slate), then `ensureSeeded()`, then `mergeFavoritesWithCloud(uid)` to pull only THIS user's favorites from Firestore.

---

### 6. `StateFlow` Import Issues & Missing `stateIn`
**Problem**: Several build failures from missing `kotlinx.coroutines.flow.StateFlow`, `SharingStarted`, `stateIn` imports in `SettingsViewModel.kt` and `SettingsRepository.kt`.

**Fix**: Added explicit imports to each file.

---

### 7. Dynamic Categories Breaking `SettingsScreen` Compose UI
**Problem**: After switching from hardcoded to dynamic `availableCategories` StateFlow, `SettingsScreen.kt` wasn't collecting the new Flow, causing a compile error.

**Fix**: Added `val availableCategories by viewModel.availableCategories.collectAsState()` in the screen.

---

## 🔒 Architecture Rules (DO NOT BREAK)

1. **Never `clearAllTables()` on sign-out** — quotes are global, only reset `isFavorited`
2. **Never use `stateIn(WhileSubscribed)` for auth state in private fields** — use `FirebaseAuth.currentUser` directly for point-in-time reads
3. **Always `.await()` on Firestore writes** inside suspend functions — never fire-and-forget
4. **`updateCloudFields()`** is the only way to update quotes from the cloud — it never touches `isFavorited`
5. **`syncUserData()` order is sacred**: reset → re-seed → pull favorites → pull settings

---

## 📦 Latest Build

| Key | Value |
|-----|-------|
| **Status** | ✅ Stable — per-user Firebase sync working |
| **APK path** | `app/build/outputs/apk/debug/app-debug.apk` |
| **Last built** | April 25, 2026 — 1:30 AM |
| **Room DB version** | v4 (`echologic-v4.db`) |
| **Firestore rules** | Open until May 21, 2026 — tighten before production |

---

*Last Updated: April 25, 2026*
