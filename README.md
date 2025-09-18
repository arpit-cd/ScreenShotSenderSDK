# ScreenShotSenderSDK

[![](https://jitpack.io/v/cdcountrydelight/ScreenShotSenderSDK.svg)](https://jitpack.io/#cdcountrydelight/ScreenShotSenderSDK)
[![Android](https://img.shields.io/badge/Android-API%2024%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=24)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.2.10-blue.svg?style=flat)](https://kotlinlang.org)
[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](LICENSE)

## Overview

ScreenShotSenderSDK is an Android library that provides an easy-to-use screenshot capture and upload functionality through a floating action button (FAB) overlay. The SDK allows users to capture screenshots of their current app screen and automatically upload them to a configured server endpoint.

## Features

- 📸 **Floating Action Button (FAB)** - Interactive overlay button for screenshot capture
- 🎯 **Drag-and-Drop Positioning** - Move the FAB anywhere on the screen
- 📤 **Automatic Upload** - Background service handles screenshot uploads
- 📊 **Progress Tracking** - Real-time upload status notifications
- 🏗️ **Clean Architecture** - Well-structured code following MVVM pattern
- 🔄 **Robust Error Handling** - Network error handling with retry mechanisms
- 🔔 **Status Notifications** - Keep users informed about upload progress

## Requirements

- Android API 24+ (minSdk 24)
- Kotlin 2.2.10
- JDK 17 (for building)

## Installation

### Step 1: Add JitPack repository

Add JitPack repository to your project's `settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
   repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
   repositories {
      mavenCentral()
      maven { url = uri("https://jitpack.io") }
   }
}
```

### Step 2: Add the dependency

Add the dependency to your module's `build.gradle.kts`:

```kotlin
dependencies {
   implementation("com.github.cdcountrydelight:ScreenShotSenderSDK:Tag")
}
```

Replace `Tag` with the latest version number from the JitPack badge above.

## Usage

### Basic Integration

1. **Initialize and start the SDK:**

```kotlin
import com.cd.screenshotsender.presentation.ScreenShotSenderSDK

class MainActivity : AppCompatActivity() {
   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)

      // Start the SDK with default package name
      ScreenShotSenderSDK.startSDK(this)

      // Or start with custom package name
      ScreenShotSenderSDK.startSDK(this, "com.example.myapp")
   }
}
```

### Important: Async Image Loading Configuration

If you're using async image loading libraries like Coil in your project, you must disable hardware acceleration to ensure screenshots can be captured properly. Hardware-accelerated bitmaps cannot be read for screenshot capture.

**Example with Coil:**

```kotlin
import coil.Coil
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import android.graphics.Bitmap
import android.os.Build

// Configure Coil to disable hardware bitmaps
val customImageLoader = ImageLoader.Builder(this)
   .bitmapConfig(Bitmap.Config.ARGB_8888) // ✅ Disables hardware bitmaps
   .allowHardware(false) // ✅ Disable hardware acceleration
   .components {
      // Add GIF/WebP support if needed
      if (Build.VERSION.SDK_INT >= 28) {
         add(ImageDecoderDecoder.Factory())
      } else {
         add(GifDecoder.Factory())
      }
   }
   .build()

// Set the custom ImageLoader as the default for Coil
Coil.setImageLoader(customImageLoader)
```

**Note:** You can conditionally disable hardware acceleration only when the SDK is active:

```kotlin
.allowHardware(!ScreenShotSenderSDK.isSDKRunning())
```

2. **Stop the SDK when needed:**

```kotlin
override fun onDestroy() {
   super.onDestroy()
   ScreenShotSenderSDK.stopSDK(this)
}
```

3. **Check if SDK is running:**

```kotlin
if (ScreenShotSenderSDK.isSDKRunning()) {
   // SDK is active
}
```

### Handling Overlay Permission

The SDK requires overlay permission to display the floating action button. The SDK will automatically request this permission if not granted:

```kotlin
// The SDK handles permission request automatically
ScreenShotSenderSDK.startSDK(this)
```

If you want to handle the permission result:

```kotlin
override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
   super.onActivityResult(requestCode, resultCode, data)
   if (requestCode == 1234) { // OVERLAY_PERMISSION_REQUEST_CODE
      if (Settings.canDrawOverlays(this)) {
         // Permission granted, SDK will start automatically
      } else {
         // Permission denied
      }
   }
}
```

## Permissions

The SDK requires the following permissions (automatically included in the SDK's manifest):

| Permission | Purpose |
|------------|---------|
| `INTERNET` | Required for uploading screenshots |
| `ACCESS_NETWORK_STATE` | Check network connectivity |
| `SYSTEM_ALERT_WINDOW` | Display floating action button overlay |
| `POST_NOTIFICATIONS` | Show upload progress notifications |
| `FOREGROUND_SERVICE` | Run upload service in the background |
| `FOREGROUND_SERVICE_DATA_SYNC` | Data sync foreground service type |

## API Configuration

The SDK communicates with a backend server to:
1. Register the package name
2. Upload captured screenshots

The API endpoints are configured within the SDK. The upload process includes:
- Automatic retry on network failures
- Progress tracking
- Success/failure notifications

## Architecture

The SDK follows Clean Architecture principles with clear separation of concerns:

### Layers

1. **Presentation Layer**
   - `ScreenShotSenderSDK` - Main SDK interface
   - `ScreenShotSenderService` - Foreground service for managing the overlay
   - `TrackingOverlayManager` - Manages the floating action button
   - `FileUploadTracker` - Tracks upload progress

2. **Domain Layer**
   - Use cases for business logic
   - Repository interfaces
   - Domain models

3. **Data Layer**
   - Network implementation using Retrofit
   - Repository implementations
   - API service definitions

### Key Components

- **Retrofit** - Network communication
- **Coroutines** - Asynchronous operations
- **Flow** - Reactive data streams
- **Foreground Service** - Background operations

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

## Support

For issues, feature requests, or questions, please [create an issue](https://github.com/cdcountrydelight/ScreenShotSenderSDK/issues) on GitHub.