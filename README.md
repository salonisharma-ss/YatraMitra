# YatraMitra

### A Smart, Location-Aware Travel Companion for Android

YatraMitra is an Android application designed to simplify travel and local exploration by combining location-based discovery, interactive maps, personalized place search, favorites, user authentication, and travel expense management into a single platform.

The application integrates Google Maps and Places services for real-time location-based discovery and Firebase for authentication and application services. It is designed to provide users with a convenient and personalized way to discover destinations, restaurants, hotels, attractions, and other points of interest.

---

## Overview

Travelers often rely on multiple applications for discovering places, viewing locations, managing expenses, and saving destinations. YatraMitra brings these essential travel utilities together in one Android application.

The application detects the user's location, provides relevant nearby places, allows keyword-based searches, displays detailed place information, and provides an integrated budget tracker for managing travel expenses.

---

## Key Features

### User Authentication
- Firebase-based authentication
- Email-based OTP verification
- Secure session management
- Personalized user profiles

### Location-Based Discovery
- Detects the user's current location using Fused Location Provider
- Identifies nearby attractions and points of interest
- Supports discovery of restaurants, hotels, and other local destinations
- Provides location-aware results

### Place Search and Information
- Keyword-based place search using Google Places API
- Detailed information for selected locations
- Place ratings and reviews
- Distance calculation from the user's location
- Place images and additional location information

### Interactive Maps
- Google Maps integration
- Visual representation of discovered locations
- Interactive map-based exploration

### Travel Budget Management
- Add and manage travel expenses
- Categorize expenses
- Track total spending
- Offline expense storage using SQLite/Room

### Favorites
- Save frequently visited or preferred locations
- Access saved places through a personalized favorites section

### User Profile
- Manage user information
- Personalized application experience
- Session management

---

## Technology Stack

| Category | Technology |
|----------|------------|
| Language | Java / Kotlin |
| Platform | Android |
| UI & Components | Android Jetpack |
| Authentication | Firebase Authentication |
| Analytics | Firebase Analytics |
| Location | Fused Location Provider |
| Maps | Google Maps SDK for Android |
| Place Discovery | Google Places API |
| Networking | OkHttp |
| JSON Processing | Gson |
| Image Loading | Glide |
| Local Storage | SQLite / Room |
| Email Service | SendGrid API |
| Build System | Gradle |

---

## Application Architecture

YatraMitra follows an organized Android application structure with a focus on modular components and separation of responsibilities.

### High-Level Flow

```text
                    ┌─────────────────────┐
                    │       User          │
                    └──────────┬──────────┘
                               │
                    ┌──────────▼──────────┐
                    │  Authentication     │
                    │ Firebase + OTP      │
                    └──────────┬──────────┘
                               │
                    ┌──────────▼──────────┐
                    │ Location Services   │
                    │ Fused Location      │
                    └──────────┬──────────┘
                               │
              ┌────────────────▼────────────────┐
              │       Place Discovery           │
              │ Google Places + Google Maps     │
              └───────────────┬────────────────┘
                              │
          ┌───────────────────┼───────────────────┐
          │                   │                   │
 ┌────────▼────────┐  ┌───────▼────────┐  ┌───────▼────────┐
 │ Place Details   │  │   Favorites    │  │ Budget Tracker │
 │ Ratings/Photos  │  │ Saved Places   │  │ SQLite / Room  │
 └─────────────────┘  └────────────────┘  └────────────────┘
