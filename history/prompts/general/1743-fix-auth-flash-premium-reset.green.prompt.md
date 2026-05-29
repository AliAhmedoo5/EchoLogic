---
id: "1743"
title: "Fix Auth Flash and Premium Reset"
stage: "green"
date: "2026-05-07"
surface: "agent"
model: "Claude Opus 4.6 (Thinking)"
feature: "none"
branch: "main"
user: "pds"
labels: ["auth", "premium", "bugfix", "settings"]
links:
  spec: null
  ticket: null
  adr: null
  pr: null
files:
  - app/src/main/java/com/echologic/data/repository/AuthRepository.kt
  - app/src/main/java/com/echologic/data/repository/SettingsRepository.kt
  - app/src/main/java/com/echologic/data/repository/BillingRepository.kt
  - app/src/main/java/com/echologic/MainActivity.kt
  - FEATURES_AND_ARCHITECTURE.md
  - HURDLES.md
tests: []
---

# Prompt History Record

## Prompt Text
ok, i am having issue, like when i open the app its shows signup button for 2 seconds and than go to main screen, than in settings i am not sure but i think in settings i go pro last time i used, but when i open it after few days, or after sometime, me being pro is not there, it got reset. also i want you to update FEATURES_AND_ARCHITECTURE.md and HURDLES.md

## Response Text
Fixed two bugs:
1. **Auth Screen Flash**: Introduced a sealed `AuthState` class (`Loading`/`Authenticated`/`Unauthenticated`) in `AuthRepository`. `MainActivity` now shows an empty themed background during `Loading` instead of flashing the auth screen.
2. **Premium Reset**: Implemented "Sticky Premium" pattern — `BillingRepository.queryPurchases()` now only upgrades (never downgrades), and `syncSettingsWithCloud()` never overwrites local `isPremium=true` with cloud `isPremium=false`. If local is premium but cloud isn't, the local state is pushed to cloud to heal the inconsistency.

Updated `FEATURES_AND_ARCHITECTURE.md` with new architecture rules #9 (Sticky Premium) and #10 (Tri-State Auth Gate). Updated `HURDLES.md` with Hurdle #19 and #20. Build successful.
