# Tasks: EchoLogic App

**Input**: Design documents from `specs/001-echologic-app/`
**Prerequisites**: plan.md, spec.md

**Tests**: Test tasks are included as per standard Android development best practices.

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

## Path Conventions

- All paths are relative to the `app/` directory.

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and basic structure.

- [ ] T001 Create the initial Android project structure in Android Studio.
- [ ] T002 Configure `build.gradle` with all primary dependencies (Jetpack Compose, Room, Hilt, Firebase, etc.).
- [ ] T003 Set up Hilt for dependency injection by creating `EchoLogicApp.kt` and annotating `MainActivity.kt`.
- [ ] T004 [P] Create the basic package structure: `di`, `data`, `ui`, `viewmodel` inside `src/main/java/com/echologic/`.
- [ ] T005 [P] Configure the app's theme in `ui/theme/`.

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented.

- [ ] T006 Set up Firebase integration by adding `google-services.json` to the `app/` directory.
- [ ] T007 Implement the Room database class and DAOs in `data/local/`.
- [ ] T008 [P] Create the `Quote` entity for Room in `data/local/QuoteEntity.kt`.
- [ ] T009 [P] Set up the `Repository` pattern in `data/repository/QuoteRepository.kt`.
- [ ] T010 Implement the core Hilt dependency injection modules in the `di/` package.

---

## Phase 3: User Story 1 - Onboarding and Quote Consumption (Priority: P1) 🎯 MVP

**Goal**: Allow a new user to sign in and view a feed of quotes.

**Independent Test**: A user can successfully sign in with Google and see a quote on the main screen. Tapping the "Echo" button loads a new quote.

### Tests for User Story 1
- [X] T011 [P] [US1] Write a unit test for the main screen's ViewModel in `src/test/java/com/echologic/viewmodel/MainViewModelTest.kt`.
- [X] T012 [P] [US1] Write a UI test for the Google Sign-In flow in `src/androidTest/java/com/echologic/AuthFlowTest.kt`.

### Implementation for User Story 1
- [X] T013 [US1] Implement Google Sign-In logic using Firebase Authentication.
- [X] T014 [US1] Create the main feed screen using Jetpack Compose in `ui/screens/MainScreen.kt`.
- [X] T015 [US1] Implement the `MainViewModel.kt` to fetch quotes from the repository.
- [X] T016 [US1] Implement the "Echo" button functionality to fetch a new quote.
- [X] T017 [US1] Implement the logic to stream quotes from the Room database to the UI using Kotlin Flow.

---

## Phase 4: User Story 2 - Favoriting and Viewing Quotes (Priority: P2)

**Goal**: Allow users to save their favorite quotes and view them in a library.

**Independent Test**: A user can double-tap a quote to favorite it, and it will appear in the library screen.

### Tests for User Story 2
- [X] T018 [P] [US2] Write a unit test for the library ViewModel in `src/test/java/com/echologic/viewmodel/LibraryViewModelTest.kt`.
- [X] T019 [P] [US2] Write a UI test for the favoriting functionality in `src/androidTest/java/com/echologic/FavoritingTest.kt`.

### Implementation for User Story 2
- [X] T020 [US2] Implement the double-tap to favorite functionality on the main feed screen.
- [X] T021 [US2] Create the library screen in `ui/screens/LibraryScreen.kt`.
- [X] T022 [US2] Implement the `LibraryViewModel.kt` to fetch favorited quotes.
- [X] T023 [US2] Update the `User` data model to store a list of favorite quote IDs in Firestore.

---

## Phase 5: User Story 3 - Upgrading to Premium (Priority: P3)

**Goal**: Allow users to remove ads by making a one-time in-app purchase.

**Independent Test**: A user can successfully complete the in-app purchase, and all ads will be removed.

### Tests for User Story 3
- [X] T024 [P] [US3] Write a unit test for the settings/upgrade ViewModel in `src/test/java/com/echologic/viewmodel/SettingsViewModelTest.kt`.
- [X] T025 [P] [US3] Write a UI test for the IAP flow in `src/androidTest/java/com/echologic/IAPTest.kt`.

### Implementation for User Story 3
- [X] T026 [US3] Integrate the Google AdMob SDK and display a banner ad on the main screen for free users.
- [X] T027 [US3] Integrate the Google Play Billing Library.
- [X] T028 [US3] Create a settings or upgrade screen in `ui/screens/SettingsScreen.kt`.
- [X] T029 [US3] Implement the "echologic_pro_upgrade" in-app purchase flow.
- [X] T030 [US3] Implement logic to check the user's premium status and conditionally show/hide ads.
- [X] T031 [US3] Implement the premium features: custom font selection and theme toggling.

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Improvements that affect multiple user stories.

- [X] T032 [P] Implement smooth fade-in/fade-out transitions for quote refreshes.
- [X] T033 Set up Firebase Crashlytics and Performance Monitoring.
- [X] T034 [P] Create and host a privacy policy and fill out the Google Play Data Safety Form.
- [X] T035 Run the quickstart.md validation to ensure the project is easy to set up.
- [X] T036 Prepare the app for release by generating a signed Android App Bundle (.aab).
- [X] T037 Fulfill the 20-tester requirement for the Google Play Store closed test.

## Dependencies & Execution Order

- **Setup (Phase 1)** and **Foundational (Phase 2)** must be completed before any user story work begins.
- All user stories can theoretically be worked on in parallel after Phase 2 is complete, but it is recommended to follow the priority order (US1 -> US2 -> US3).

## Implementation Strategy

1.  **MVP First**: Complete Phase 1, 2, and 3 to deliver the core quote-viewing experience.
2.  **Incremental Delivery**: Add Phase 4 (Favoriting) and then Phase 5 (Monetization).
3.  **Final Polish**: Complete Phase 6 before release.
