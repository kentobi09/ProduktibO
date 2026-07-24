<p align="center">
  <img src="logo.png" width="128" alt="Produktib O? Logo" />
</p>

# Produktib O? — Minimalist Home Launcher & Lock Screen for Android

> *"Produktib O? — It's productive, make every minute count."*

[![Download APK](https://img.shields.io/badge/📥_Download_APK-v1.2.0-10B981?style=for-the-badge&logo=android&logoColor=white)](https://github.com/kentobi09/ProduktibO/releases/latest)

[**👉 Click here to Download the latest Produktib O? APK (v1.2.0)**](https://github.com/kentobi09/ProduktibO/releases/download/v1.2.0/ProduktibO-v1.2.0.apk)

---

## ✨ Features

- 📱 **Text-Only Minimalist Launcher**: Replaces loud, colorful app icons with clean, distraction-free typography.
- 🔤 **Interactive A-Z Fast-Scroll Bar**: Slide your finger along the A-Z sidebar with a real-time reactive letter indicator for instant navigation.
- 🎯 **First-Install App Onboarding Dialog**: 1-time setup pop-up on first launch to easily choose which apps to display in 10 seconds.
- 🔍 **Space-Saving Search & Settings Search**: Ultra-thin single-line search field on home screen and instant app search inside Settings.
- 👈 **Edge Swipe-to-Back Gesture**: Swipe from the left edge in Settings to return smoothly to the home screen.
- 🔒 **Minimal OLED Lock Screen Overlay**: Pitch-black OLED clock (`72.sp`), date, battery, and quiet text notifications over your lock screen with swipe-up unlock.
- 🛡️ **Anti-Doomscroll Social & Game Shields**: Automatically suppresses social media feeds (**TikTok, X, Facebook, YouTube, Threads, Instagram, Snapchat, Reddit**) and online games (**Mobile Legends, Roblox, PUBG, Genshin, CoD Mobile, Free Fire**).
- 📜 **Quiet Text Notifications**: Swiping down reveals a quiet, monochrome notification list.
- 👆 **Double-Tap Screen Lock**: Turn off your phone screen natively by double-tapping empty space (`AccessibilityService`).
- 🔓 **Fail-Safe Launcher Reset**: Easily change or switch back to your stock launcher anytime directly inside **Settings** -> **Change / Reset Default Launcher**.

---

## 📥 Quick Download & Installation

1. **Download APK**: Click the [**Download APK Badge**](https://github.com/kentobi09/ProduktibO/releases/download/v1.2.0/ProduktibO-v1.2.0.apk) above on your Android phone.
2. **Install**: Tap the downloaded `.apk` file to install it.
3. **Set Default**: Open **Produktib O?** and respond to the one-time system default prompt, or configure anytime in Settings.

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

## 🚀 Building from Source

### Prerequisites

- [Android Studio](https://developer.android.com/studio) (Hedgehog, Iguana, or Ladybug recommended)
- Android Device or Emulator running Android 8.0+

### Steps

1. **Clone the repository**:
   ```bash
   git clone https://github.com/kentobi09/ProduktibO.git
   ```

2. **Open in Android Studio**:
   - Open Android Studio -> **Open** -> Select the `ProduktibO` project folder.

3. **Build and Run**:
   - Connect your Android device via USB (with USB Debugging enabled).
   - Click **Run ('app')** ▶ to compile and install on your phone.

---

## 🔒 Permissions Used

- **`CATEGORY_HOME`**: Allows the app to function as a primary Android Home Launcher.
- **`AccessibilityService`** *(Optional)*: Used exclusively for locking the screen upon double-tapping empty space.
- **`NotificationListenerService`** *(Optional)*: Used to parse incoming notifications into quiet, plain-text alerts.
