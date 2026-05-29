# **EchoLogic: Official Tech Stack Specification (2026)**

## **Core Android Development**

* **Language:** Kotlin 2.1+  
* **Minimum SDK:** API 24 (Android 7.0)  
* **Target SDK:** API 36 (Android 16\)  
* **Build System:** Gradle (Kotlin DSL)

## **UI & Presentation Layer**

* **Framework:** Jetpack Compose  
* **Design System:** Material 3 (Customized for Neo-Brutalism)  
* **Image Loading:** Coil (for potential quote backgrounds/assets)  
* **Navigation:** Compose Navigation Component

## **Data & Storage Layer (Offline-First)**

* **Local DB:** Room Database (SQLite abstraction)  
* **Remote DB:** Cloud Firestore (NoSQL)  
* **Concurrency:** Kotlin Coroutines & Flow  
* **Dependency Injection:** Hilt (Dagger)

## **Infrastructure (Firebase Suite)**

* **Authentication:** Firebase Auth (Google Sign-In Provider)  
* **Cloud Config:** Firebase Remote Config (to toggle features or "Quote of the Day" remotely)  
* **Performance:** Firebase Crashlytics & Performance Monitoring

## **Monetization**

* **Ads:** Google AdMob SDK (Banner & Interstitial)  
* **In-App Purchases:** Google Play Billing Library (v8.3+)

## **CI/CD & Production**

* **Testing:** JUnit 5, Espresso (UI Testing)  
* **Distribution:** Android App Bundle (.aab)  
* **Version Control:** Git (GitHub/GitLab)