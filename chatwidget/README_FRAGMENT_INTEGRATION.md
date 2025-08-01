# ChatWidget SDK - Fragment Integration Guide

## Overview

The ChatWidget SDK now uses a Fragment-based architecture internally to properly handle file uploads and downloads. This ensures that the Activity Result API works correctly for file picker functionality.

## What Changed

### File Upload Fix
- **Previously**: File upload was not working because the `FileUploadManager` couldn't properly register `ActivityResultLauncher` from a custom view context.
- **Now**: The SDK uses `ChatWidgetFragment` internally, which provides the proper Fragment lifecycle for Activity Result API registration.

### Architecture Update
- **New**: `ChatWidgetFragment` - The core fragment that handles WebView, file upload/download, and permissions
- **Updated**: `ChatWidget` - Now acts as a wrapper that internally hosts the `ChatWidgetFragment`
- **Enhanced**: `FileUploadManager` - Now properly works with Fragment-based Activity Result API

## Usage (No Changes Required for Consumers)

### Option 1: Using ChatWidget (Recommended for existing code)
```kotlin
val helloConfig = mapOf(
    "widgetToken" to "your_token",
    "email" to "user@example.com"
)

// In XML
<com.msg91.chatwidget.ChatWidget
    android:id="@+id/chat_widget"
    android:layout_width="match_parent"
    android:layout_height="match_parent" />

// In code
val chatWidget = ChatWidget(
    context = context,
    helloConfig = helloConfig,
    isCloseButtonVisible = false,
    useKeyboardAvoidingView = true
)
```

### Option 2: Using ChatWidgetFragment directly (New option)
```kotlin
val helloConfig = mapOf(
    "widgetToken" to "your_token", 
    "email" to "user@example.com"
)

val fragment = ChatWidgetFragment.newInstance(
    helloConfig = helloConfig,
    widgetColor = "#2196F3",
    isCloseButtonVisible = true,
    useKeyboardAvoidingView = true
)

supportFragmentManager.beginTransaction()
    .replace(R.id.fragment_container, fragment)
    .commit()
```

### In Jetpack Compose
```kotlin
AndroidView(
    factory = { context ->
        ChatWidget(
            context = context,
            helloConfig = helloConfig,
            isCloseButtonVisible = false,
            useKeyboardAvoidingView = true
        )
    }
)
```

## Benefits

1. **File Upload Works**: File picker now works correctly in all scenarios
2. **Backward Compatible**: Existing code continues to work without changes
3. **Self-Contained**: All file upload/download logic is handled internally
4. **No Consumer Setup**: No need for consumers to handle ActivityResult, permissions, or file pickers
5. **Flexible**: Consumers can use either the custom view or fragment directly

## Technical Details

### Automatic Fragment Hosting
When you use `ChatWidget`, it automatically:
1. Finds the nearest `FragmentActivity` from the context
2. Creates a `ChatWidgetFragment` internally
3. Adds the fragment to handle WebView and file operations
4. Falls back to direct WebView if no FragmentActivity is available

### File Upload Flow
1. User taps file upload in chat
2. `CustomWebChromeClient.onShowFileChooser()` is called
3. `FileUploadManager` uses Fragment's `registerForActivityResult()`
4. File picker launches automatically
5. Selected files are passed back to WebView
6. Files are uploaded to chat server

### Requirements
- Host Activity must extend `FragmentActivity` (includes `AppCompatActivity`)
- Android API 21+ (same as before)

## Migration from Previous Version

**No migration required!** The SDK is backward compatible. File upload will now work automatically in existing integrations.

## Known Limitations

- If the host context is not a `FragmentActivity`, the SDK falls back to direct WebView mode where file upload may be limited
- This affects only edge cases where the widget is used in non-standard contexts

## Testing File Upload

After integration, test file upload by:
1. Opening the chat widget
2. Tapping the attachment/file upload button in the chat
3. Selecting a file from the device
4. Verifying the file appears in the chat

The file picker should now open correctly and files should upload successfully.