# ChatWidget SDK

A modern Android SDK for integrating chat widgets with file upload capabilities.

## Features

- **Self-contained file upload**: No consumer setup required
- **Modern Activity Result API**: Uses the latest Android APIs
- **Fragment compatibility**: Works with both Activities and Fragments
- **Multiple file support**: Handles single and multiple file selections
- **Automatic cleanup**: Proper resource management

## File Upload Implementation

The SDK now includes a completely self-contained file upload system that handles all file selection internally without requiring any setup from the consumer.

### Key Benefits

1. **No Consumer Setup Required**: The file upload functionality is completely handled inside the SDK
2. **Modern API Usage**: Uses `ActivityResultLauncher` instead of deprecated `onActivityResult()`
3. **Fragment Compatible**: Works seamlessly with both Activities and Fragments
4. **Automatic Resource Management**: Proper cleanup of resources when the widget is destroyed

### How It Works

The file upload system consists of three main components:

1. **FileUploadManager**: Handles the modern Activity Result API for file selection
2. **CustomWebChromeClient**: Delegates file chooser requests to the FileUploadManager
3. **ChatWebViewManager**: Manages the overall WebView lifecycle and cleanup

### Implementation Details

#### FileUploadManager
- Uses `ActivityResultLauncher` for modern file selection
- Handles both single and multiple file selections
- Supports all file types through `Intent.ACTION_GET_CONTENT`
- Automatic cleanup of resources

#### CustomWebChromeClient
- Delegates `onShowFileChooser` calls to FileUploadManager
- No direct file handling - completely encapsulated
- Proper error handling and logging

#### Integration
- Automatically initialized when ChatWidget is created
- No additional setup required from consumers
- Works with any Activity or Fragment that extends FragmentActivity

### Usage Example

```kotlin
// Basic usage - file upload works automatically
val chatWidget = ChatWidget(
    context = this,
    helloConfig = mapOf(
        "widgetToken" to "your-token-here"
    )
)

// Add to your layout
parentLayout.addView(chatWidget)
```

### File Upload Features

- **Supported File Types**: All file types (images, documents, videos, etc.)
- **Multiple File Selection**: Users can select multiple files at once
- **File Size Handling**: Handles large files appropriately
- **Error Handling**: Graceful handling of file selection errors
- **Cancellation Support**: Proper handling when user cancels file selection

### Technical Implementation

The file upload system uses the following modern Android APIs:

1. **ActivityResultLauncher**: For handling file selection results
2. **FragmentActivity**: For compatibility with both Activities and Fragments
3. **Intent.ACTION_GET_CONTENT**: For file selection
4. **ClipData**: For handling multiple file selections

### Dependencies

The SDK automatically includes the necessary dependencies:

```kotlin
implementation("androidx.activity:activity-ktx:1.8.2")
implementation("androidx.fragment:fragment-ktx:1.6.2")
```

### Migration from onActivityResult()

If you were previously using `onActivityResult()` for file uploads, this new implementation:

1. **Eliminates the need** for consumer-side file handling
2. **Removes the requirement** for `onActivityResult()` implementation
3. **Provides better encapsulation** of file upload logic
4. **Offers improved error handling** and resource management

### Best Practices

1. **No Manual Setup**: Don't implement any file upload handling in your Activity
2. **FragmentActivity**: Ensure your Activity extends FragmentActivity for compatibility
3. **Automatic Cleanup**: The SDK handles all cleanup automatically
4. **Error Handling**: File upload errors are handled internally with proper logging

### Troubleshooting

If file uploads aren't working:

1. Ensure your Activity extends `FragmentActivity`
2. Check that the WebView has proper permissions
3. Verify that the file chooser is being triggered by the web content
4. Check logs for any error messages from the FileUploadManager

### Logging

The SDK provides comprehensive logging for debugging:

- `[FileUploadManager]`: File selection and handling logs
- `[FileChooser]`: WebView file chooser request logs
- `[ChatWebViewManager]`: WebView lifecycle logs

Enable logging to debug file upload issues:

```kotlin
// Enable debug logging
LogUtil.setDebugEnabled(true)
``` 