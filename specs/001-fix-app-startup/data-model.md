# Data Model: Fix App Startup

## Entities

- **QuoteEntity**:
    - `id`: Int (Primary Key)
    - `text`: String
    - `author`: String
    - `isFavorite`: Boolean
- **StartupStatus**: 
    - Internal state tracking to ensure DB is initialized before UI requests data.

## Validation Rules
- All fields in `QuoteEntity` are non-null for consistency.
