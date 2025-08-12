# 📂 JetDrive (Android)

[![Build Status](https://img.shields.io/badge/build-passing-brightgreen)](https://github.com/nicklauscott/JetDrive-Client)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![Android](https://img.shields.io/badge/platform-Android-green.svg)](https://android.com)
[![API](https://img.shields.io/badge/API-30%2B-orange.svg)](https://android-arsenal.com/api?level=30)

**A fast, secure, and online-first Android client for cloud file management** — JetDrive delivers a seamless, always-connected experience for browsing, uploading, downloading, and managing files stored in the cloud. With live updates, smooth media playback, and background transfers, JetDrive keeps your files in sync the moment they change.

Built with **Kotlin**, **Jetpack Compose**, and **Koin** for dependency injection, JetDrive-Client is optimized for speed, reliability, and real-time collaboration.

---

## 🚀 Features

- **File Browsing & Search** – Navigate directories, preview files, and search quickly across your cloud storage
- **Resumable Transfers** – Pause and resume uploads/downloads without data loss, perfect for large files
- **Offline Caching** – Access previously loaded files without internet connection
- **Real-Time Sync** – Automatic file updates powered by background workers
- **Secure Login** – Google Sign-In integration with Credential Manager API
- **Media Playbook** – Integrated **Media3 ExoPlayer** for seamless video/audio preview
- **Drag & Reorder Transfers** – Prioritize uploads and downloads by rearranging tasks in the transfer queue for optimal bandwidth use
- **Adaptive UI** – Material 3 adaptive layouts optimized for phones, tablets, and foldables
- **Background Operations** – Transfers continue even when app is minimized

---

## 📸 Screenshots

| Login Screen | File Browser | Transfer Queue | Image Preview |
|--------------|--------------|----------------|---------------|
| ![Login](assets/ScreenShot1.jpg) | ![Browser](assets/ScreenShot3.jpg) | ![Transfers](assets/ScreenShot6.jpg) | ![Preview](assets/ScreenShot5.jpg) |

---

## 🎥 Video Demo

[![Watch the demo](assets/ScreenShot2.jpg)](https://raw.githubusercontent.com/nicklauscott/JetDrive-Client/main/assets/Demo.mp4)

*Click the image above to watch JetDrive in action*

---

## 🏗 Architecture

| Layer            | Technology / Approach                                          |
|------------------|----------------------------------------------------------------|
| **Language**     | Kotlin                                                         |
| **UI Framework** | Jetpack Compose + Material 3 Adaptive                          |
| **DI**           | [Koin](https://insert-koin.io/)                                |
| **Networking**   | [Ktor Client](https://ktor.io/) (Core, CIO, Android, Auth, JSON, Logging) |
| **Serialization**| Kotlinx Serialization JSON                                     |
| **Caching**      | Room Database + DataStore Preferences                          |
| **State Mgmt**   | Jetpack ViewModel + Coroutines/StateFlow                       |
| **Background Work** | WorkManager (via Koin integration)                          |
| **Navigation**   | Jetpack Navigation Compose & Navigation 3                      |
| **Auth**         | Google Identity API + AndroidX Credentials                     |
| **Media**        | Media3 ExoPlayer + UI Compose                                  |
| **Image Loading**| Coil & Coil-Compose                                            |

---

## 📱 System Requirements

- **Android Version**: 11.0 (API level 30) or higher
- **Storage**: Minimum 100MB free space
- **RAM**: 4GB recommended for optimal performance
- **Network**: Internet connection required for initial setup and sync
- **Google Account**: Required for authentication

---

## ⚙️ Prerequisites

- **Android Studio**: Giraffe+ (latest recommended)
- **JDK**: 17+
- **Android SDK**: API level 30+
- **Backend**: [JetDrive](https://github.com/nicklauscott/jetdrive) - companion API server
- **Google Cloud Console**: Project with OAuth 2.0 credentials configured

---

## 🔧 Getting Started

### 1. Clone the repository
```bash
git clone https://github.com/nicklauscott/JetDrive-Client.git
cd JetDrive-Client
```

### 2. Configure local.properties
Create a `local.properties` file in the project root and add:
```properties
# Backend Configuration
BASE_URL=https://your-jetdrive-api.com/api/v1

# Google OAuth Configuration
GOOGLE_CLIENT_ID=your_google_oauth_client_id.googleusercontent.com
```

### 3. Set up Google OAuth
1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select existing one
3. Enable the Google Sign-In API
4. Create OAuth 2.0 credentials for Android
5. Add your app's SHA-1 fingerprint
6. Copy the client ID to your `local.properties`

### 4. Build and run
```bash
# Build debug version
./gradlew assembleDebug

# Install on connected device
./gradlew installDebug

# Or open in Android Studio and run directly
```

---

## 📥 Installation

### From Source
Follow the [Getting Started](#-getting-started) instructions above.

### APK Releases
Download the latest APK from the [Releases](https://github.com/nicklauscott/JetDrive-Client/releases) page.

---

## 🔧 Configuration

### Backend Setup
JetDrive requires the companion backend API to function. 

**📡 Backend Repository:** [JetDrive](https://github.com/nicklauscott/jetdrive)

Please refer to the backend repository for complete setup instructions, API documentation, deployment guides, and configuration details.

---

## 🛠 Troubleshooting

### Common Issues

**Build Errors:**
- Ensure you have JDK 17+ installed
- Verify Android SDK is properly configured
- Clean and rebuild: `./gradlew clean build`

**Authentication Issues:**
- Check Google OAuth client ID in `local.properties`
- Verify SHA-1 fingerprint is added to Google Cloud Console
- Ensure Google Sign-In API is enabled

**Network Errors:**
- Confirm backend is running (see [JetDrive](https://github.com/nicklauscott/jetdrive))
- Check backend URL configuration in `local.properties`
- Verify network permissions in AndroidManifest.xml

**Performance Issues:**
- Clear app cache and data
- Ensure device meets minimum system requirements
- Check available storage space

### Getting Help
- Check [Issues](https://github.com/nicklauscott/JetDrive-Client/issues) for known problems
- Create a new issue with detailed error logs

---

## 🤝 Contributing

We welcome contributions! Please read our [Contributing Guide](CONTRIBUTING.md) before submitting pull requests.

### Development Setup
1. Fork the repository
2. Create a feature branch: `git checkout -b feature/amazing-feature`
3. Follow our coding standards and run tests
4. Submit a pull request with clear description

### Code Style
- Follow [Kotlin coding conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Use [ktlint](https://pinterest.github.io/ktlint/) for formatting
- Write meaningful commit messages

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 🙏 Acknowledgments

- **Material Design 3** - for the beautiful design system
- **Jetpack Compose** - for modern Android UI development
- **ExoPlayer** - for reliable media playback
- **Koin** - for clean dependency injection
- **Ktor** - for efficient networking

---

## 📞 Support

- **Issues**: [GitHub Issues](https://github.com/nicklauscott/JetDrive-Client/issues)
- **Discussions**: [GitHub Discussions](https://github.com/nicklauscott/JetDrive-Client/discussions)

---

<div align="center">

**⭐ Star this repository if you find it useful!**

Made with ❤️ by [nicklauscott](https://github.com/nicklauscott)

</div>
