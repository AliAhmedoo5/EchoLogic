# **Project Blueprint: EchoLogic**

## **1\. Executive Summary**

**Project Name:** EchoLogic

**Tagline:** "Brain off. Logic on."

**Platform:** Android (Google Play Store)

**Core Concept:** A high-speed, minimalist app providing a curated feed of hilarious, nonsensical, and "dumb" quotes.

**Key Change:** Removed character-specific branding to ensure universal appeal and avoid IP restrictions.

## **2\. Brand & UI/UX Design**

### **2.1 Visual Style**

* **Aesthetic:** "Neo-Brutalism" or "Modern Minimalist."  
* **Palette:** High-contrast colors. Deep Black (\#121212) backgrounds with Electric Yellow (\#FFFF00) or Mint Green (\#BFFFC7) accents.  
* **Motion:** Smooth fade-in/fade-out transitions when refreshing quotes.

### **2.2 Functional Flow**

1. **Instant Access:** One-tap Google Sign-In via Firebase.  
2. **The Feed (Main Screen):**  
   * **Central Quote:** Bold, large typography.  
   * **The "Echo" Button:** A large, tactile button at the bottom center to refresh the quote.  
   * **Double-Tap to Favorite:** Users can double-tap the quote (like Instagram) to save it.  
3. **Monetization (Standard Users):** Small banner ad anchored at the bottom.  
4. **The Library:** A clean list of favorited quotes, categorized by "Vibe" (e.g., Logic, Shower Thoughts, Absurd).

## **3\. Technical Architecture**

### **3.1 The "Offline-First" Stack**

* **UI:** Jetpack Compose (Kotlin).  
* **Local DB:** Room (Persistence for offline reading).  
* **Remote DB:** Cloud Firestore (Master source for new quotes).  
* **Auth:** Firebase Google Auth (User UID used as primary key for favorites).

### **3.2 Data Strategy (Firestore Structure)**

{  
  "quote\_id": "auto-gen",  
  "text": "If your shirt isn't tucked into your pants, then your pants are tucked into your shirt.",  
  "category": "logic-loops",  
  "author": "Anonymous",  
  "premium\_only": false  
}

## **4\. Revenue & Monetization**

### **4.1 Ad Strategy (AdMob)**

* **Banners:** Shown to all free-tier users.  
* **Interstitials:** Shown occasionally (e.g., every 20 refreshes) to encourage the premium upgrade.

### **4.2 In-App Purchase (IAP)**

* **Product:** echologic\_pro\_upgrade (One-time purchase).  
* **Features Unlocked:**  
  * Complete removal of Ads.  
  * Custom Font Selection (Cursive, Bold, Retro, etc.).  
  * "Dark Mode" vs "OLED Black" toggle.

## **5\. Play Store SOPs (Production Readiness)**

To ensure the app is accepted by Google Play and remains compliant in 2026, the following procedures must be followed:

### **5.1 Account & Verification**

* **Merchant Profile:** Setup a Google Payments Profile linked to a local Pakistani bank (e.g., HBL, Meezan) to receive IAP earnings.  
* **Developer Verification:** Complete the mandatory identity verification (CNIC/Passport) and D-U-N-S number (if applicable) required by Google for all new accounts.

### **5.2 The 20-Tester Rule (Crucial)**

* **Requirement:** New personal developer accounts must run a **Closed Test** with at least **20 testers** who have been opted-in for at least **14 days continuously**.  
* **Action:** Create a Google Group for testers (e.g., MAJU classmates) and add their emails to the "Closed Testing" track in the Play Console.

### **5.3 Data Safety & Privacy**

* **Privacy Policy:** Generate a hosted URL stating that the app collects:  
  * Google Account ID (via Firebase Auth).  
  * Interaction data (via Firebase Analytics).  
  * Device identifiers (via AdMob).  
* **Data Safety Form:** Explicitly declare that data is "encrypted in transit" and users can request "account/data deletion."

### **5.4 App Content Rating**

* **Target Audience:** Set to 13+ (due to "dumb" humor) to avoid the strict "Designed for Families" requirements which involve higher scrutiny on ads.

## **6\. Deployment & Compliance (2026)**

* **Target API:** Targets Android 16 (API 36).  
* **App Bundle:** Must be uploaded as .aab (Android App Bundle) with Play Asset Delivery for the JSON quote database if it exceeds 10MB.  
* **Permissions:** Zero sensitive permissions (No Location, No Camera) to reduce rejection risk.

## **7\. Developer Logic (For AI/Gemini CLI)**

**Implementation Note:** Use the **MVVM (Model-View-ViewModel)** architecture.

1. The **ViewModel** should observe the User state.  
2. If User.isPremium \== false, initialize the **AdMob SDK**.  
3. The **Repository** must use Flow to stream quotes from the Room database to the UI, ensuring 0ms latency.