# File Upload Integration Guide

## Overview

The ChatWidget SDK includes **completely self-contained file upload functionality** that requires **zero setup** from the consumer. This implementation uses the modern Activity Result API with intelligent fallback mechanisms and is fully compatible with both Activities and Fragments.

### ⚡ Latest Improvements

- **🔄 Smart Registration**: Intelligent ActivityResultLauncher registration with retry mechanisms
- **🛡️ Robust Fallbacks**: Multiple fallback strategies when primary registration fails
- **📱 Fragment-Aware**: Better lifecycle handling for fragment-based implementations
- **🔧 Self-Healing**: Automatic recovery from registration failures
- **🎯 Zero-Config**: Truly zero setup required from consumers

## 🚀 Quick Start

### For Jetpack Compose

```kotlin
@Composable
fun ChatScreen() {
    AndroidView(
        factory = { context ->
            ChatWidget(
                context = context,
                helloConfig = mapOf(
                    "widgetToken" to "your-token-here"
                )
            )
        },
        modifier = Modifier.fillMaxSize()
    )
}
```

### For Traditional Views

```kotlin
class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val chatWidget = ChatWidget(
            context = this,
            helloConfig = mapOf(
                "widgetToken" to "your-token-here"
            )
        )
        
        setContentView(chatWidget)
    }
}
```

## ✨ Key Features

- **🔄 Zero Setup**: No manual file handling required
- **📱 Modern API**: Uses ActivityResultLauncher internally
- **🎯 Self-Contained**: All file logic handled within the SDK
- **📁 Multi-File Support**: Handles single and multiple file selections
- **🛡️ Error Handling**: Comprehensive error handling and logging
- **🧹 Auto Cleanup**: Proper resource management

## 🔧 Technical Implementation

### Enhanced Architecture

```
ChatWidget
├── ChatWebViewManager
│   ├── CustomWebChromeClient
│   │   └── FileUploadManager
│   │       ├── Smart Registration System
│   │       ├── Fallback Mechanisms
│   │       └── Lifecycle Awareness
│   └── WebView Configuration
└── File Upload Handling
```

### Core Components

1. **FileUploadManager**: Smart ActivityResultLauncher management with fallbacks
2. **CustomWebChromeClient**: Enhanced file chooser delegation with error handling
3. **ChatWebViewManager**: WebView lifecycle management
4. **TempFilePickerFragment**: Fallback fragment for edge cases

### Smart Registration System

```kotlin
// Enhanced implementation with retry mechanisms:
private fun tryRegisterLauncher(): Boolean {
    // Try fragment-based registration first
    if (fragment?.isAdded == true && fragment.lifecycle.currentState.isAtLeast(Lifecycle.State.CREATED)) {
        fileChooserLauncher = fragment.registerForActivityResult(...)
        return true
    }
    
    // Fallback to activity-based registration
    return tryRegisterWithActivity()
}

// Automatic retry on failure
private fun retryRegistration(): Boolean {
    registrationAttempted = false
    return tryRegisterLauncher()
}
```

### Fallback Mechanisms

1. **Fragment Registration**: Preferred method when fragment is available
2. **Activity Registration**: Fallback when fragment isn't ready
3. **Temporary Fragment**: Creates headless fragment for file picking
4. **Direct Activity Launch**: Last resort with timeout handling

## 📋 Requirements

### Minimum Requirements

- **Android API**: 24+ (Android 7.0+)
- **Activity Type**: Must extend `FragmentActivity` or `ComponentActivity`
- **Dependencies**: Automatically included in the SDK

### Dependencies Added

```kotlin
implementation("androidx.activity:activity-ktx:1.8.2")
implementation("androidx.fragment:fragment-ktx:1.6.2")
```

## 🎯 Usage Examples

### Basic Implementation

```kotlin
// Simple usage - file upload works automatically
val chatWidget = ChatWidget(
    context = this,
    helloConfig = mapOf("widgetToken" to "your-token")
)
```

### With Custom Configuration

```kotlin
val chatWidget = ChatWidget(
    context = this,
    helloConfig = mapOf(
        "widgetToken" to "your-token",
        "email" to "user@example.com",
        "name" to "User Name"
    ),
    widgetColor = "#2196F3",
    isCloseButtonVisible = true,
    useKeyboardAvoidingView = true
)
```

### In Jetpack Compose

```kotlin
@Composable
fun ChatWidgetScreen() {
    AndroidView(
        factory = { context ->
            ChatWidget(
                context = context,
                helloConfig = mapOf("widgetToken" to "your-token")
            )
        },
        modifier = Modifier.fillMaxSize()
    )
}
```

## 🔍 Debugging

### Enable Logging

```kotlin
// Enable debug logging to see file upload activity
LogUtil.setDebugEnabled(true)
```

### Log Tags to Monitor

- `[FileUploadManager]`: File selection and handling
- `[FileChooser]`: WebView file chooser requests
- `[ChatWebViewManager]`: WebView lifecycle events

### Common Log Messages

```
[FileUploadManager] ===== File chooser requested =====
[FileUploadManager] File chooser launcher registered successfully
[FileChooser] ===== onShowFileChooser called =====
[FileUploadManager] File selection result: -1
[FileUploadManager] Single file selected: content://...
```

## 🛠️ Troubleshooting

### File Upload Not Working

1. **Check Activity Type**
   ```kotlin
   // ✅ Correct
   class MainActivity : FragmentActivity()
   
   // ❌ Incorrect
   class MainActivity : Activity()
   ```

2. **Verify Permissions**
   ```xml
   <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
   ```

3. **Check WebView Configuration**
   - File access is automatically enabled
   - JavaScript is enabled by default
   - Mixed content is allowed

### Enhanced Diagnostics

The improved FileUploadManager provides detailed logging for troubleshooting:

```
[FileUploadManager] ===== File chooser requested =====
[FileUploadManager] Fragment available: true
[FileUploadManager] File chooser launcher: false
[FileUploadManager] No launcher available, attempting registration...
[FileUploadManager] Attempting to register launcher with fragment
[FileUploadManager] Fragment lifecycle state: STARTED
[FileUploadManager] Fragment is added: true
[FileUploadManager] File chooser launcher registered successfully
[FileUploadManager] File chooser launched successfully
```

### Common Issues & Solutions

1. **"Could not get Activity from context"**
   - ✅ **Auto-Fixed**: Smart registration now tries multiple approaches
   - Ensure Activity extends `FragmentActivity`
   - Check that context is properly passed

2. **"Registration already attempted, skipping retry"**
   - ✅ **Auto-Fixed**: Registration resets on cleanup for reuse
   - The SDK now automatically retries registration when needed

3. **File chooser not opening**
   - ✅ **Auto-Fixed**: Multiple fallback mechanisms implemented
   - Verify WebView has proper permissions
   - Check that file input is triggered from web content

4. **"Fragment not ready for registration"**
   - ✅ **Auto-Fixed**: Lazy registration waits for proper lifecycle
   - SDK automatically retries when fragment becomes ready

5. **Files not uploading**
   - Check network connectivity
   - Verify file size limits
   - Ensure proper MIME types

### New Self-Healing Features

- **Automatic Registration Retry**: If initial registration fails, SDK retries when file chooser is needed
- **Multiple Fallback Strategies**: Fragment → Activity → Temporary Fragment → Direct Launch
- **Lifecycle Awareness**: Waits for proper fragment/activity state before registration
- **Resource Cleanup**: Automatic cleanup allows reuse after view destruction

## 🔄 Migration Guide

### From onActivityResult()

**Before (Manual Setup):**
```kotlin
class MainActivity : AppCompatActivity() {
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // ❌ Manual file handling required
        chatWidget?.onActivityResult(requestCode, resultCode, data)
    }
}
```

**After (Zero Setup):**
```kotlin
class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val chatWidget = ChatWidget(context = this, helloConfig = config)
        // ✅ File upload works automatically!
    }
}
```

## 📚 Best Practices

1. **Use FragmentActivity**: Always extend `FragmentActivity` for compatibility
2. **No Manual Handling**: Don't implement any file upload callbacks
3. **Let SDK Handle It**: File upload is triggered automatically from WebView
4. **Enable Logging**: Use `LogUtil.setDebugEnabled(true)` for debugging
5. **Proper Cleanup**: The SDK handles cleanup automatically

## 🔒 Security Considerations

- File access is limited to the WebView context
- No file system access outside the WebView
- Proper permission handling for file access
- Secure file chooser implementation

## 📞 Support

If you encounter issues:

1. Check logs for `[FileUploadManager]` and `[FileChooser]` messages
2. Ensure Activity extends `FragmentActivity`
3. Verify file access permissions
4. Test with simple file types first

## 🎉 Summary

The enhanced file upload functionality is now:
- ✅ **Completely self-contained**
- ✅ **Zero setup required**
- ✅ **Modern API usage**
- ✅ **Fragment compatible**
- ✅ **Automatic cleanup**
- ✅ **Comprehensive error handling**
- ✅ **Smart registration system**
- ✅ **Multiple fallback strategies**
- ✅ **Self-healing capabilities**
- ✅ **Enhanced lifecycle awareness**
- ✅ **Robust error recovery**

The SDK now handles even the most challenging edge cases automatically. No additional code is needed - file uploads work reliably out of the box in all scenarios! 