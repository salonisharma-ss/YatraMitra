# 🌍 YatraMitra

### **A Smart, Location-Aware Travel Companion for Android**

> **Discover. Explore. Save. Track. Travel Smarter.**

**YatraMitra** is a smart Android travel companion designed to make **local exploration and travel planning simpler, more personalized, and more convenient**. The application brings location-based discovery, interactive maps, place search, favorites, user authentication, and travel expense management together in a single platform.

By integrating **Google Maps, Google Places, Firebase, and Android location services**, YatraMitra helps users discover nearby attractions, restaurants, hotels, and other points of interest while keeping their favorite destinations and travel expenses organized.

---

## ✨ Why YatraMitra?

Travelers often switch between multiple applications to:

* 📍 Find nearby places
* 🗺️ Explore locations on maps
* 🔎 Search for restaurants, hotels, and attractions
* ⭐ Save favorite destinations
* 💰 Track travel expenses
* 👤 Manage their personal profile

**YatraMitra combines these essential travel utilities into one unified Android application**, reducing the need to rely on multiple apps during a journey.

---

# 🚀 Key Features

## 🔐 1. Secure User Authentication

YatraMitra provides a personalized and secure experience through Firebase-powered authentication.

* Firebase Authentication
* Email-based OTP verification
* Secure session management
* Personalized user profiles
* Authentication-aware application flow

---

## 📍 2. Location-Based Discovery

The application uses the **Fused Location Provider** to determine the user's current location and provide relevant nearby destinations.

Users can discover:

* 🏛️ Tourist attractions
* 🍽️ Restaurants
* 🏨 Hotels
* 📌 Points of interest
* 📍 Other nearby destinations

The location-aware discovery system helps users explore places based on their current surroundings.

---

## 🔎 3. Smart Place Search

Users can search for destinations using keywords through the **Google Places API**.

For a selected location, the application can provide:

* ⭐ Place ratings
* 💬 Reviews
* 📏 Distance from the user
* 🖼️ Place images
* 📍 Location information
* ℹ️ Additional place details

This allows users to evaluate a destination before deciding where to go.

---

## 🗺️ 4. Interactive Google Maps

YatraMitra integrates **Google Maps SDK for Android** to provide an interactive map-based exploration experience.

Users can:

* View discovered locations on a map
* Explore nearby destinations visually
* Interact with map locations
* Connect place discovery with geographical context

---

## ❤️ 5. Favorites & Saved Places

Users can save destinations they are interested in for quick access later.

### Favorites allow users to:

* ❤️ Save preferred locations
* 📌 Quickly access saved destinations
* 🗂️ Maintain a personalized collection of places
* 🔄 Revisit previously discovered locations

---

## 💰 6. Travel Budget & Expense Tracker

Managing expenses is an important part of travel. YatraMitra includes an integrated expense management system.

Users can:

* ➕ Add travel expenses
* 🏷️ Categorize expenses
* 💵 Track total spending
* 📊 Monitor their travel budget
* 💾 Store expenses locally for offline access

Expense data can be managed using **SQLite / Room**, allowing essential budget information to remain available without continuous internet connectivity.

---

## 👤 7. Personalized User Profile

Each user receives a personalized application experience.

The profile module supports:

* User information management
* Personalized experience
* Session management
* Access to saved places and travel-related information

---

# 🛠️ Technology Stack

| Category                         | Technology                  |
| -------------------------------- | --------------------------- |
| **Programming Language**         | Java                        |
| **Platform**                     | Android                     |
| **UI & Architecture Components** | Android Jetpack             |
| **Authentication**               | Firebase Authentication     |
| **Analytics**                    | Firebase Analytics          |
| **Location Services**            | Fused Location Provider     |
| **Maps**                         | Google Maps SDK for Android |
| **Place Discovery**              | Google Places API           |
| **Networking**                   | OkHttp                      |
| **JSON Processing**              | Gson                        |
| **Image Loading**                | Glide                       |
| **Local Database**               | SQLite / Room               |
| **Email Service**                | SendGrid API                |
| **Build System**                 | Gradle                      |

---

# 🏗️ Application Architecture

YatraMitra follows a **modular and organized Android application structure**, separating major responsibilities such as authentication, location services, place discovery, favorites, and expense management.

### 🔄 High-Level Application Flow

```text
                         ┌──────────────────────┐
                         │        👤 USER       │
                         └───────────┬──────────┘
                                     │
                                     ▼
                    ┌─────────────────────────────┐
                    │     🔐 AUTHENTICATION       │
                    │     Firebase + Email OTP    │
                    └──────────────┬──────────────┘
                                   │
                                   ▼
                    ┌─────────────────────────────┐
                    │      📍 LOCATION SERVICES   │
                    │   Fused Location Provider   │
                    └──────────────┬──────────────┘
                                   │
                                   ▼
             ┌──────────────────────────────────────────┐
             │          🌍 PLACE DISCOVERY              │
             │       Google Places + Google Maps        │
             └────────────────────┬─────────────────────┘
                                  │
              ┌───────────────────┼───────────────────┐
              │                   │                   │
              ▼                   ▼                   ▼
      ┌───────────────┐   ┌───────────────┐   ┌────────────────┐
      │ 📌 PLACE      │   │ ❤️ FAVORITES  │   │ 💰 BUDGET      │
      │    DETAILS    │   │ Saved Places  │   │    TRACKER     │
      │ Ratings/Photo │   │               │   │ SQLite / Room  │
      └───────────────┘   └───────────────┘   └────────────────┘
              │                   │                   │
              └───────────────────┼───────────────────┘
                                  │
                                  ▼
                    ┌─────────────────────────────┐
                    │     👤 PERSONALIZED        │
                    │       TRAVEL EXPERIENCE    │
                    └─────────────────────────────┘
```

---

# 🔄 How YatraMitra Works

### **1. Authenticate**

The user signs in through Firebase authentication and completes the required OTP verification.

### **2. Detect Location**

The application accesses the user's location through the **Fused Location Provider**.

### **3. Discover Places**

Nearby destinations are retrieved using Google Places services based on location and search requirements.

### **4. Explore**

Users can view destinations through interactive Google Maps and explore detailed place information.

### **5. Save**

Interesting destinations can be added to the user's **Favorites** for future reference.

### **6. Manage Expenses**

Users can record and categorize their travel expenses using the integrated **Budget Tracker**.

### **7. Personalize**

Authentication and profile features allow the application to provide a personalized travel experience.

---

# 🧩 Core Modules

```text
YatraMitra
│
├── 🔐 Authentication
│   ├── Firebase Authentication
│   ├── OTP Verification
│   └── Session Management
│
├── 📍 Location Services
│   └── Fused Location Provider
│
├── 🔎 Place Discovery
│   ├── Google Places API
│   ├── Keyword Search
│   └── Nearby Places
│
├── 🗺️ Maps
│   └── Google Maps SDK
│
├── 📌 Place Details
│   ├── Ratings
│   ├── Reviews
│   ├── Distance
│   └── Images
│
├── ❤️ Favorites
│   └── Saved Places
│
├── 💰 Budget Tracker
│   ├── Add Expense
│   ├── Categories
│   └── Total Spending
│
└── 👤 User Profile
    ├── User Information
    └── Personalized Experience
```

---

# 🌟 Key Highlights

| Feature                         | Benefit                                               |
| ------------------------------- | ----------------------------------------------------- |
| 📍 **Location-Aware Discovery** | Finds relevant destinations around the user           |
| 🗺️ **Interactive Maps**        | Makes geographical exploration easier                 |
| 🔎 **Place Search**             | Quickly searches destinations using keywords          |
| ⭐ **Place Information**         | Helps users understand a destination before visiting  |
| ❤️ **Favorites**                | Saves places for future access                        |
| 💰 **Expense Tracking**         | Helps manage travel spending                          |
| 🔐 **Firebase Authentication**  | Provides personalized and secure access               |
| 💾 **Offline Storage**          | Keeps expense information available locally           |
| 📱 **All-in-One Experience**    | Combines multiple travel utilities in one application |

---

# 🎯 Project Objective

The primary objective of **YatraMitra** is to develop an integrated travel companion that combines **location intelligence, destination discovery, map-based exploration, personalization, and expense management** into a single Android application.

The project demonstrates the practical use of modern Android technologies and third-party APIs to create a **real-world, location-aware mobile application** focused on improving the overall travel experience.

---

# 🔮 Future Enhancements

The platform can be further extended with features such as:

* 🤖 AI-powered destination recommendations
* 🧭 Personalized trip planning
* 🌦️ Weather-based travel suggestions
* 🗺️ Multi-destination itinerary creation
* 👥 Social travel and destination sharing
* 📊 Advanced expense analytics
* 🔔 Smart travel notifications
* 🌐 Multi-language support
* 📴 Improved offline map and destination support

---

## 🏁 Conclusion

**YatraMitra** aims to transform the way users discover and manage their travel experiences by bringing **location-based discovery, interactive maps, destination information, favorites, authentication, and expense tracking** into one unified Android platform.

> **YatraMitra — Your Journey. Your Places. Your Budget. Your Companion.** 🌍✨
