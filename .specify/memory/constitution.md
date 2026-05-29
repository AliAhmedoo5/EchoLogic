# EchoLogic Constitution

## Core Principles

### I. MVVM Architecture
The project MUST follow the Model-View-ViewModel (MVVM) architecture to ensure a clean separation of concerns between the UI, business logic, and data layers.

### II. Offline-First
The application MUST be functional offline, using a local Room database as a cache for data fetched from the remote Cloud Firestore database.

### III. Testability
All business logic in ViewModels and Repositories MUST be covered by unit tests. UI tests MUST cover the primary user flows.

### IV. Dependency Injection
The project MUST use Hilt for dependency injection to manage dependencies and improve testability.

### V. Code Style
All code MUST adhere to the official Kotlin style guide to ensure consistency and readability.

## Security
- No hardcoded secrets. API keys and other sensitive information MUST be stored in `local.properties` and accessed via `build.gradle`.

## Governance
- This constitution is the source of truth for all development practices.
- Any deviation from these principles must be justified and documented in the relevant pull request.

**Version**: 1.0.0 | **Ratified**: 2026-04-15 | **Last Amended**: 2026-04-15
