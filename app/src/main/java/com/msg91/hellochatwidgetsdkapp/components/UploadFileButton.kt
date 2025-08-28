package com.msg91.hellochatwidgetsdkapp.components

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
//import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

class UploadFileButton(context: Context) : Button(context) {
    
    private var selectedFileUri: Uri? = null
    
    init {
        // Set the button text
        text = "Upload File"
        
        // Set button styling programmatically
        textSize = 16f
        setPadding(48, 32, 48, 32)
        
        // Set click listener
        setOnClickListener {
//            Toast.makeText(context, "Button clicked!", Toast.LENGTH_SHORT).show()
            launchFilePicker()
        }
    }
    
    private fun launchFilePicker() {
        try {
//            Toast.makeText(context, "Starting file picker process...", Toast.LENGTH_SHORT).show()
            
            val activity = context as? FragmentActivity
            if (activity != null) {
//                Toast.makeText(context, "Activity found: ${activity::class.simpleName}", Toast.LENGTH_SHORT).show()
                
                // Create and add headless fragment to handle file picking
                val fragment = FilePickerFragment { uri ->
                    handleFileSelection(uri)
                }
                
//                Toast.makeText(context, "Fragment created, adding to manager...", Toast.LENGTH_SHORT).show()
                
                activity.supportFragmentManager
                    .beginTransaction()
                    .add(fragment, "file_picker_fragment_${System.currentTimeMillis()}")
                    .commitAllowingStateLoss()
                    
//                Toast.makeText(context, "Fragment transaction committed", Toast.LENGTH_SHORT).show()
            } else {
//                Toast.makeText(context, "Context is not FragmentActivity: ${context::class.simpleName}", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
//            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }
    
    fun handleFileSelection(uri: Uri?) {
        selectedFileUri = uri
        if (uri != null) {
            // Get file name for display
            val fileName = getFileName(uri)
//            Toast.makeText(context, "File selected: $fileName", Toast.LENGTH_LONG).show()
            
            // Update button text to show selected file
            text = "File: $fileName"
            
            // Process the selected file
            processSelectedFile(uri)
        } else {
//            Toast.makeText(context, "No file selected", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun getFileName(uri: Uri): String {
        return try {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val nameIndex = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                    if (nameIndex >= 0) {
                        it.getString(nameIndex) ?: "Unknown file"
                    } else {
                        "Unknown file"
                    }
                } else {
                    "Unknown file"
                }
            } ?: uri.lastPathSegment ?: "Unknown file"
        } catch (e: Exception) {
            uri.lastPathSegment ?: "Unknown file"
        }
    }
    
    private fun processSelectedFile(uri: Uri) {
        // This is where you can add your file processing logic
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val fileSize = inputStream?.available() ?: 0
            inputStream?.close()
            
//            Toast.makeText(
//                context,
//                "File ready for processing (Size: ${fileSize} bytes)",
//                Toast.LENGTH_SHORT
//            ).show()
            
            // TODO: Add your specific file processing logic here
            // Example: uploadToServer(uri), processFileContent(uri), etc.
            
        } catch (e: Exception) {
//            Toast.makeText(context, "Error processing file: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    
    // Public methods for external access
    fun getSelectedFileUri(): Uri? = selectedFileUri
    
    fun resetSelection() {
        selectedFileUri = null
        text = "Upload File"
    }
    
    companion object {
        /**
         * Creates a self-contained upload file button.
         */
        fun create(context: Context): UploadFileButton {
            return UploadFileButton(context)
        }
    }
}

/**
 * Headless fragment that handles file picker activity results.
 * This fragment has no UI and automatically removes itself after handling the result.
 */
class FilePickerFragment(
    private val onResult: (Uri?) -> Unit
) : Fragment() {
    
    private lateinit var filePickerLauncher: ActivityResultLauncher<String>
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Register activity result launcher
        filePickerLauncher = registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            // Handle the result
            onResult(uri)
            
            // Remove this fragment after handling result
            parentFragmentManager.beginTransaction()
                .remove(this)
                .commitAllowingStateLoss()
        }
        
        // Launch file picker immediately when fragment is created
        launchFilePicker()
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Return null - this is a headless fragment with no UI
        return null
    }
    
    private fun launchFilePicker() {
        try {
            // Launch file picker for all file types
            filePickerLauncher.launch("*/*")
        } catch (e: Exception) {
//            Toast.makeText(requireContext(), "Error launching file picker: ${e.message}", Toast.LENGTH_SHORT).show()
            // Remove fragment on error
            parentFragmentManager.beginTransaction()
                .remove(this)
                .commitAllowingStateLoss()
        }
    }
}