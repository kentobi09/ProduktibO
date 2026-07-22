# Produktib O? — Minimalist Text Launcher for Android

> *"Produktib O? — It's productive, make every minute count."*

**Produktib O?** (derived from the Tagalog word *produktibo* meaning *"It's productive!"* in a persuasive, affirming tone) is an aesthetic minimalist text launcher for Android designed to eliminate doomscrolling, reduce dopamine triggers, and maximize intentionality.

---

## ✨ Features

- 📱 **Text-Only Minimalist Launcher**: Replaces loud, colorful app icons with clean, distraction-free typography.
- 🛡️ **Anti-Doomscroll Shield**: Toggleable master filter that automatically suppresses high-friction social feeds (**TikTok, X/Twitter, Facebook, YouTube, Threads, Instagram, Snapchat, Reddit**).
- 📜 **Plain Text Notifications**: Swiping down from the top reveals a quiet, monochrome notification list—no red badges, flashing cards, or distracting thumbnail previews.
- 🔒 **Opt-In Double-Tap to Lock**: Enable in Settings to turn off your phone screen natively with a quick double-tap on empty space (`AccessibilityService`).
- 🔋 **Immersive OLED UI**: Hides system clutter in edge-to-edge OLED dark mode with in-app battery percentage and time indicators.
- 🔓 **Fail-Safe Launcher Reset**: Easily change or switch back to your stock launcher anytime directly inside **Settings** -> **Change / Reset Default Launcher**.

---

## 🛠 Tech Stack

- **Language**: Kotlin 1.9
- **UI Framework**: Jetpack Compose (Material 3)
- **State Management**: Kotlin Coroutines & StateFlow
- **Storage**: Jetpack DataStore Preferences
- **Minimum SDK**: Android 8.0 (API 26)
- **Target SDK**: Android 14 (API 34)
- **Build System**: Gradle with Kotlin DSL (`.gradle.kts`)

---

## 🚀 Getting Started

### Prerequisites

- [Android Studio](https://developer.android.com/studio) (Hedgehog, Iguana, or Ladybug recommended)
- Android Device or Emulator running Android 8.0+

### Installation

1. **Clone the repository**:
   ```bash
   git clone https://github.com/kentobi09/ProduktibO.git
   ```

2. **Open in Android Studio**:
   - Open Android Studio -> **Open** -> Select the `ProduktibO` project folder.

3. **Build and Run**:
   - Connect your Android device via USB (with USB Debugging enabled).
   - Click **Run ('app')** ▶ to compile and install on your phone.
   - Tap **Set Default** on the home screen banner to select **Produktib O?** as your main launcher.

---

## 🔒 Permissions Used

- **`CATEGORY_HOME`**: Allows the app to function as a primary Android Home Launcher.
- **`AccessibilityService`** *(Optional)*: Used exclusively for locking the screen upon double-tapping empty space.
- **`NotificationListenerService`** *(Optional)*: Used to parse incoming notifications into quiet, plain-text alerts.
- **`QUERY_ALL_PACKAGES`**: Required to display installed applications in the minimalist text drawer.

---

## 📄 License

Distributed under the MIT License. See `LICENSE` for details.
