# Architecture & Stability: Lessons Learned (learntbymistakes.md)

This document serves as a "post-mortem" and technical guide for EchoLogic development. It identifies critical pitfalls encountered during the launch optimization phase and provides definitive solutions to prevent regressions.

---

## 1. The "Wildcard Icon" Hang (Performance)
### Symptom
The app launches but stays stuck on the Android Splash Screen (White screen with logo) indefinitely or for 20+ seconds.
### Root Cause
Using wildcard imports for Jetpack Compose Material Icons (e.g., `import androidx.compose.material.icons.filled.*`). 
**Technical Detail**: The `material-icons-extended` library is massive. Wildcard imports force the compiler and runtime to index/load thousands of vector assets into the initial composition, blocking the Main Thread and preventing the first frame from drawing.
### The Fix
**Always** use explicit, singular imports for icons:
```kotlin
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
```

---

## 2. Hilt vs. Firebase Initialization Race (Lifecycle)
### Symptom
Immediate crash on launch with `IllegalStateException: Default FirebaseApp is not initialized`.
### Root Cause
Hilt initializes Singletons (like `FirebaseAuth`) during `super.onCreate()` of the `Application` class. If Firebase is not initialized **before** Hilt's initialization logic starts, the injection will fail.
### The Fix
Initialize Firebase in `attachBaseContext` or at the very top of `onCreate` **before** calling `super.onCreate()`.
```kotlin
override fun attachBaseContext(base: Context) {
    super.attachBaseContext(base)
    FirebaseApp.initializeApp(base)
    // Now Hilt can safely inject Firebase clients
}
```

---

## 3. The "Silent Crash" Trap (Debugging)
### Symptom
App crashes instantly ("opened and closed") but no logs are available because ADB is disconnected or restricted.
### Root Cause
Developer has no visibility into the stack trace, leading to guesswork.
### The Fix
Implement a **Persistent Crash Monitor**. 
1. Register a `Thread.UncaughtExceptionHandler` at the earliest possible entry point (`attachBaseContext`).
2. Intercept the crash, save the stack trace to `SharedPreferences`.
3. Launch a standalone, Hilt-free `CrashActivity` to display the error on-device.

---

## 4. Google Auth: Web Client ID Confusion (Authentication)
### Symptom
Google Sign-In prompt appears, but returns an error `ApiException: 10` or fails to exchange tokens.
### Root Cause
Passing the **Android Client ID** to the `requestIdToken()` method instead of the **Web Client ID**. Firebase Authentication requires the Web Client ID (type 3 in `google-services.json`) to perform the backend exchange.
### The Fix
Always use the `client_id` ending in `.apps.googleusercontent.com` that has `client_type: 3`.

---

## 5. Room Schema Contention (Database)
### Symptom
Crash on launch with `IllegalStateException: Room cannot verify the data integrity`.
### Root Cause
Rapid changes to `QuoteEntity` (adding/removing fields) without incrementing the database version or providing a migration path.
### The Fix
During the development phase, use `fallbackToDestructiveMigration()` and, if things get messy, rename the database file (e.g., `v4.db`) to force a fresh start and avoid cached corruption.

---

## 6. Compose Animation API Shifts (Compatibility)
### Symptom
Compilation error: `None of the following functions can be called with the arguments supplied: togetherWith`.
### Root Cause
Compose 1.7+ changed the `AnimatedContent` transition syntax. The old `with` infix is deprecated/removed in favor of `togetherWith`.
### The Fix
```kotlin
// Correct modern syntax
AnimatedContent(
    targetState = state,
    transitionSpec = { fadeIn() togetherWith fadeOut() }
)
```

---

## Critical Checkpoints for Future Models:
1. **Never** use wildcard imports in UI code.
2. **Never** assume Firebase is auto-initialized if using Hilt Singletons.
3. **Always** check `attachBaseContext` if the app crashes before entering `MainActivity`.
