# UI Element Tracking SDK

A reusable Android SDK that provides automatic UI element tracking with a floating action button (FAB) overlay service.

## Features

- **Floating Action Button Overlay**: Shows real-time tracking status
- **Automatic Element Detection**: Tracks UI elements marked with `trackElement` modifiers
- **Visual Feedback**: Different states (Capturing, Ready to Send, Sending, Success, Error)
- **Screenshot Capture**: Automatically captures screenshots with tracked elements
- **Clean Architecture**: MVVM pattern with separation of concerns
- **Non-intrusive**: Minimal overlay that doesn't interfere with app UI

## Installation

Add the UIElementManager module to your project.

## Permissions

Add these permissions to your `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
```

## Usage

### 1. Initialize the SDK

```kotlin
UIElementTrackingSDK.initialize(
    context = context,
    config = UIElementTrackingSDK.Config(
        baseUrl = "http://your-server.com/",
        autoCapture = true,
        captureTimeoutMs = 3000L,
        showNotification = true
    )
)
```

### 2. Check Overlay Permission

```kotlin
if (UIElementTrackingSDK.hasOverlayPermission(context)) {
    // Permission granted, start tracking
} else {
    // Request permission
    UIElementTrackingSDK.requestOverlayPermission(activity)
}
```

### 3. Start Tracking

```kotlin
val started = UIElementTrackingSDK.startTracking(context, screenName = "home_screen")
```

### 4. Mark UI Elements for Tracking

Use the tracking modifiers in your Compose UI:

```kotlin
// For non-clickable elements
Text(
    text = "Hello World",
    modifier = Modifier.trackElement("home_screen.text.greeting")
)

// For clickable elements
Button(
    onClick = { /* action */ },
    modifier = Modifier.trackClickableElement("home_screen.button.submit")
) {
    Text("Submit")
}
```

### 5. Wrap Screens with UIElementProvider

```kotlin
UIElementProvider(
    screenName = "home_screen",
    elementTracker = uiElementViewModel
) {
    // Your screen content
}
```

### 6. Stop Tracking

```kotlin
UIElementTrackingSDK.stopTracking(context)
```

## How It Works

1. **Start Service**: When tracking starts, a foreground service is launched with an overlay FAB
2. **Element Registration**: As users navigate, UI elements marked with tracking modifiers are automatically registered
3. **State Management**: The FAB shows different states:
   - **Capturing** (with spinner): Elements are being tracked
   - **Ready to Send** (send icon): All elements captured, tap to send
   - **Sending** (spinner): Uploading data
   - **Success** (checkmark): Upload successful
   - **Error** (warning): Upload failed
4. **Screenshot & Upload**: When user taps send, a screenshot is captured and sent with element data
5. **Continuous Tracking**: Service continues running for next screen or can be stopped

## Architecture

- **UIElementTrackingSDK**: Main entry point
- **UIElementTrackingService**: Foreground service managing the overlay
- **TrackingOverlayManager**: Manages the FAB overlay window
- **UIElementTrackingStateManager**: Handles tracking state logic
- **TrackingFABOverlay**: Compose UI for the FAB
- **UIElementViewModel**: Core ViewModel for element tracking

## States

```kotlin
sealed class TrackingState {
    object Idle                           // Not tracking
    data class Capturing(count: Int)      // Tracking elements
    data class Captured(count: Int)       // Ready to send
    object Sending                        // Uploading
    data class Success(message: String)   // Upload successful
    data class Error(message: String)     // Error occurred
}
```

## Example Integration

```kotlin
@Composable
fun HomeScreen() {
    val context = LocalContext.current
    
    Button(
        onClick = {
            if (UIElementTrackingSDK.hasOverlayPermission(context)) {
                UIElementTrackingSDK.startTracking(context)
            } else {
                // Request permission
            }
        }
    ) {
        Text("Start UI Tracking")
    }
}
```