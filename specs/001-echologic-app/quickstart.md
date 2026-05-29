# Quickstart: EchoLogic App

This guide provides the essential steps to get the EchoLogic Android project running on a local machine.

## 1. Prerequisites

*   **Android Studio**: The latest stable version (Iguana or newer recommended).
*   **Java Development Kit (JDK)**: Version 17 or higher.
*   **Firebase Account**: A Google account with access to the Firebase console.

## 2. Firebase Setup

1.  **Create a Firebase Project**: Go to the [Firebase Console](https://console.firebase.google.com/) and create a new project.
2.  **Register Your App**:
    *   Within your new Firebase project, add a new Android app.
    *   Use `com.echologic` as the package name.
    *   Download the `google-services.json` file and place it in the `app/` directory of the project.
3.  **Enable Authentication**:
    *   In the Firebase Console, go to the **Authentication** section.
    *   Enable the **Google Sign-In** provider.
4.  **Set up Firestore**:
    *   Go to the **Firestore Database** section and create a new database.
    *   Start in **test mode** for initial development (this allows open access).
    *   Create a collection named `quotes` and add a few sample documents that match the structure defined in `data-model.md`.

## 3. Local Environment Setup

1.  **Clone the Repository**:
    ```bash
    git clone <repository-url>
    cd <repository-directory>
    ```
2.  **Open in Android Studio**:
    *   Open Android Studio and select "Open an Existing Project".
    *   Navigate to the cloned repository and select the root directory.
3.  **Sync Gradle**:
    *   Android Studio will automatically start a Gradle sync. Wait for it to complete. This will download all the required dependencies.

## 4. Running the App

1.  **Select a Device**:
    *   Connect a physical Android device via USB or create an Android Virtual Device (AVD) using the AVD Manager in Android Studio.
2.  **Build and Run**:
    *   Click the "Run 'app'" button (the green play icon) in the Android Studio toolbar.
    *   The app will be built, installed, and launched on your selected device.

## 5. Development Workflow

*   **Architecture**: The project follows the MVVM architecture. All new UI should be built with Jetpack Compose.
*   **Code Style**: Adhere to the official Kotlin style guide.
*   **Testing**: Run unit tests from the command line with `./gradlew test` and instrumentation tests via Android Studio.
