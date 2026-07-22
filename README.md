<p align="center">
  <img src="logo.png" width="128" alt="Produktib O? Logo" />
</p>

# Produktib O? — Minimalist Text Launcher & Lock Screen for Android

> *"Produktib O? — It's productive, make every minute count."*

[![Download APK](https://img.shields.io/badge/📥_Download_APK-v1.1.0-10B981?style=for-the-badge&logo=android&logoColor=white)](https://github.com/kentobi09/ProduktibO/releases/latest)

[**👉 Click here to Download the latest Produktib O? APK (v1.1.0)**](https://github.com/kentobi09/ProduktibO/releases/download/v1.1.0/ProduktibO-v1.1.0.apk)

---

## ✨ Features

- 📱 **Text-Only Minimalist Launcher**: Replaces loud, colorful app icons with clean, distraction-free typography.
- 🔒 **Minimal OLED Lock Screen Overlay**: Pitch-black OLED clock (`72.sp`), date, battery, and quiet text notifications over your lock screen with swipe-up unlock.
- 🛡️ **Anti-Doomscroll Social Shield**: Automatically suppresses high-friction social feeds (**TikTok, X/Twitter, Facebook, YouTube, Threads, Instagram, Snapchat, Reddit**).
- 🎮 **Addictive Online Games Shield**: Auto-detects and suppresses high-dopamine mobile & online games (**Mobile Legends, Roblox, PUBG, Genshin, CoD Mobile, Free Fire**, and all Play Store games).
- 📜 **Quiet Text Notifications**: Swiping down reveals a quiet, monochrome notification list—no red badges, flashing cards, or distracting thumbnail previews.
- 👆 **Double-Tap Screen Lock**: Turn off your phone screen natively by double-tapping empty space (`AccessibilityService`), enabled by default.
- 🔋 **Immersive OLED UI**: Hides system clutter in edge-to-edge OLED dark mode with in-app battery percentage and time indicators.
- 🔓 **Fail-Safe Launcher Reset**: Easily change or switch back to your stock launcher anytime directly inside **Settings** -> **Change / Reset Default Launcher**.

---

## 📥 Installation Tutorial

### Step 1: Turn Off Play Protect

1. Open **Google Play Store** on your Android phone
2. Tap your **Profile icon** (top-right corner)
3. Go to **Settings** → **Google Play Protect**
4. Toggle **Play Protect scanning** to **OFF**
5. Confirm the prompt

### Step 2: Download & Install Produktib O?

1. Click the [**Download APK Badge**](https://github.com/kentobi09/ProduktibO/releases/download/v1.1.0/ProduktibO-v1.1.0.apk) at the top of this page
2. Once downloaded, tap the `.apk` file in your downloads
3. Tap **"Install"** — the app will install without warnings
4. Open **Produktib O?** and set it as your default launcher

### Step 3: Turn Play Protect Back On (Highly Recommended)

1. Return to **Google Play Store** → **Profile icon** → **Settings** → **Google Play Protect**
2. Toggle **Play Protect scanning** back to **ON**
3. ✅ Your phone is now secure again while keeping Produktib O? installed

### 🔄 Why Turn Off Play Protect During Installation?

- **Play Protect** scans all apps and may flag sideloaded APKs as unverified
- Temporarily disabling it allows smooth installation from GitHub
- **Re-enabling it** after installation restores your phone's security shield
- This is the **best practice** for sideloading apps safely

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
   - Open Android Studio → **Open** → Select the `ProduktibO` project folder.

3. **Build and Run**:
   - Connect your Android device via USB (with USB Debugging enabled).
   - Click **Run ('app')** ▶ to compile and install on your phone.

---

## 🔒 Permissions Used

- **`CATEGORY_HOME`**: Allows the app to function as a primary Android Home Launcher.
- **`AccessibilityService`** *(Optional)*: Used exclusively for locking the screen upon double-tapping empty space.
- **`NotificationListenerService`** *(Optional)*: Used to parse incoming notifications into quiet, plain-text alerts.
- **`QUERY_ALL_PACKAGES`**: Required to display installed applications in the minimalist text drawer.
