# 🧠 EchoLogic

> **"Brain off. Logic on."**
> A high-speed, minimalist quote discovery app for Gen Z, delivering hilariously absurd, self-referential, and profound quotes with a striking Neo-Brutalist aesthetic.

[![Platform](https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Firebase](https://img.shields.io/badge/Backend-Firebase-FFCA28?style=for-the-badge&logo=firebase&logoColor=white)](https://firebase.google.com)

---

## 🎨 Design System & Aesthetics

EchoLogic features a unique visual identity designed to stand out. It includes **4 premium themes** that users can toggle:

*   **Neo-Brutalist (Default):** Bold, high-contrast layouts, thick black borders, using a palette of Deep Black (`#121212`), Electric Yellow (`#FFFF00`), and Mint Green (`#BFFFC7`).
*   **OLED Mode:** Sleek, battery-saving true black (`#000000`) theme.
*   **Hacker Mode:** A retro monospaced CLI terminal experience with glowing green typography on dark backgrounds.
*   **Editorial / Glassmorphism:** Clean, soft gradients, and frosted-glass panel layouts for premium users.

---

## 🚀 Key Features

*   **⚡ Instant Quote Feed:** Large, bold typography. Swipe or tap the massive **"Echo"** button to load a new quote instantly.
*   **📶 Offline-First Persistence:** Fully functional offline. Re-seeds a rich library of 200+ local quotes (Room database) on first launch, so there is $0\text{ms}$ loading latency.
*   **☁️ Cloud Sync (Firebase):** Synchronizes favorites (`users/{uid}/favorites/{quoteId}`) and user preferences in real-time when signed in via Firebase Cloud Firestore.
*   **🔐 Google Sign-In:** Seamless one-tap Google OAuth.
*   **💖 Double-Tap to Favorite:** Double-tap on the quote card to save it directly to your library, accompanied by custom micro-animations.
*   **📚 Dynamic Library:** Grouped by 15+ Gen Z categories (e.g., *Logic Loops, Shower Thoughts, Absurd, Accidental Philosophy, NPC vs Main Character, Aura & Vibe*).
*   **💸 Google Play Billing & AdMob:** Premium membership integration to remove banner ads and unlock all themes/categories.
*   **🛡️ Crash Reporter:** Fatal crashes are caught by a custom `CrashActivity`, storing the stack trace and giving users a "Clear & Try Again" option.

---

## 🛠️ Technology Stack

*   **Language:** Kotlin
*   **UI Framework:** Jetpack Compose
*   **Database:** Room DB (`echologic-v4.db`) for offline persistence
*   **Backend:** Firebase Auth, Cloud Firestore, Firebase Analytics, Firebase AdMob
*   **Dependency Injection:** Hilt
*   **Architecture:** MVVM (Model-View-ViewModel) + Repository Pattern
*   **Asynchronous Flow:** Kotlin Coroutines & Reactive Flows

---

## 🏗️ Project Architecture

The codebase follows professional modular MVVM patterns:

```
com.echologic
├── MainActivity.kt           ← Auth Gate & Bottom Bar Navigation (Feed, Library, Settings)
├── EchoLogicApp.kt           ← Application class & custom crash handler installation
├── CrashActivity.kt          ← Error logging UI for app recovery
│
├── data/
│   ├── local/
│   │   ├── AppDatabase.kt    ← Room database builder
│   │   ├── QuoteDao.kt       ← Reactive SQLite database queries
│   │   ├── QuoteEntity.kt    ← Room table mapping
│   │   └── QuoteSeeder.kt    ← Local seed quotes (200+ quotes, 15+ categories)
│   └── repository/
│       ├── AuthRepository.kt     ← Handles login, logout, and cross-account syncing
│       ├── AuthService.kt        ← Auth provider wrapper
│       ├── BillingRepository.kt  ← Google Play Billing system integration
│       ├── QuoteRepository.kt    ← Handles remote Firestore caching and Room syncing
│       └── SettingsRepository.kt ← Local/cloud user configurations and theme preferences
│
├── di/
│   └── AppModule.kt          ← Dagger Hilt module (Auth, Room, Firestore, Analytics)
│
├── viewmodel/
│   ├── MainViewModel.kt      ← Feeds, favorites actions, dynamic card updates
│   ├── LibraryViewModel.kt   ← Live-streamed Favorites from Room flow
│   ├── SettingsViewModel.kt  ← Themes, dyn categories selection, billing, account deletion
│   └── AuthViewModel.kt      ← Google Auth lifecycle management
│
└── ui/
    ├── screens/
    │   ├── MainScreen.kt     ← Core swipe feed layout
    │   ├── LibraryScreen.kt  ← Grouped favorite list UI
    │   ├── SettingsScreen.kt ← Custom settings controls & premium switches
    │   └── AuthScreen.kt     ← Neo-brutalist authentication gate
    ├── components/
    │   └── AdBanner.kt       ← AdMob banner integration
    └── theme/
        └── Color.kt, Type.kt, Theme.kt (Theme-driven design tokens)
```

---

## ⚙️ Getting Started & Setup

### Prerequisites
1.  **Android Studio:** Jellyfish / Ladybug (2024.1+) or newer.
2.  **JDK Version:** JDK 17 configured.
3.  **Firebase Account:** A project set up on Firebase Console.

### 1. Firebase Integration
1.  Register your app package `com.echologic` in your Firebase project.
2.  Download `google-services.json` from the Firebase console and place it into the `app/` directory of the project.
3.  Enable **Google Sign-In** and **Email/Password** inside the Firebase Authentication console.
4.  Create a **Cloud Firestore** database.

### 2. Firestore Security Rules
Configure the following security rules in Firestore:
```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /quotes/{quote} {
      allow read: if true;
      allow write: if false; // Only server/admin writes
    }
    match /metadata/{document} {
      allow read: if true;
    }
    match /users/{userId}/{document=**} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
  }
}
```

### 3. Local Properties Setup
Create a `local.properties` file in the root directory and point to your local Android SDK location:
```properties
sdk.dir=C\:\\Users\\YourUsername\\AppData\\Local\\Android\\Sdk
```

### 4. Build and Run
Build the application locally using Gradle:
```bash
./gradlew assembleDebug
```
Or run it directly from Android Studio onto a connected physical device or emulator.

---

## ⚙️ Developer Guidelines & sacred Rules

*   **No `clearAllTables()` on sign-out:** Doing this deletes all 200+ offline quotes from Room. Use `quoteDao.resetAllFavorites()` instead. Quotes must survive profile changes.
*   **Firestore sync order:** Keep the syncing pipeline strictly ordered as: *reset favorites → re-seed quotes → pull cloud favorites → pull user settings*.
*   **Keep Firestore writes synchronized:** Always use `.await()` for all Firestore document sets/deletes inside repository coroutines; never let writes fail silently in the background.

---

## 📜 License
This project is licensed under the MIT License - see the LICENSE file for details.
