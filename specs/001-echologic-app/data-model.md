# Data Model: EchoLogic App

This document defines the data entities for the EchoLogic application, based on the feature specification.

## 1. Quote

Represents a single quote entry. This will be used for both the remote Firestore collection and the local Room database entity.

**Entity Name**: `Quote`

**Fields**:

| Field         | Type    | Description                                  | Constraints      |
|---------------|---------|----------------------------------------------|------------------|
| `id`          | String  | Unique identifier for the quote (UUID)       | Primary Key, Not Null |
| `text`        | String  | The full text of the quote.                  | Not Null, Not Empty |
| `category`    | String  | The category or "vibe" of the quote.         | Not Null         |
| `author`      | String  | The author of the quote. Defaults to "Anonymous". | Not Null         |
| `isPremium`   | Boolean | If `true`, this quote is reserved for premium users. | Not Null         |

**Relationships**:
*   A **User** can have many favorited **Quotes**.

## 2. User

Represents an authenticated user of the application.

**Entity Name**: `User`

**Fields**:

| Field         | Type    | Description                                  | Constraints      |
|---------------|---------|----------------------------------------------|------------------|
| `uid`         | String  | The unique ID from Firebase Authentication.  | Primary Key, Not Null |
| `isPremium`   | Boolean | If `true`, the user has the premium upgrade. | Not Null, Default `false` |
| `favorites`   | List<String> | A list of `Quote` IDs that the user has favorited. | -                |

**Relationships**:
*   A **User** can have many `favorites`, which correspond to **Quote** entities.

## 3. Favorite (Join Table/Entity - Conceptual)

While not necessarily a separate table in a NoSQL structure like Firestore (it's a list in the `User` document), in a relational model (like Room, conceptually), this represents the many-to-many relationship between Users and Quotes.

**Entity Name**: `Favorite`

**Fields**:

| Field         | Type    | Description                                  | Constraints      |
|---------------|---------|----------------------------------------------|------------------|
| `userId`      | String  | The `uid` of the User.                       | Foreign Key (User) |
| `quoteId`     | String  | The `id` of the Quote.                       | Foreign Key (Quote) |
