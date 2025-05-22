package sk.duracik.myaiapplication.util

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Pomocná trieda na správu fotografií a prácu so súbormi
 */
class ImageHelper(private val context: Context) {

    /**
     * Vytvorí nový súbor pre fotografiu v adresári aplikácie
     */
    fun createImageFile(): File {
        // Vytvorenie unikátneho názvu súboru
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val imageFileName = "JPEG_${timeStamp}_"

        // Získanie adresára pre uloženie fotografií
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        // Vytvorenie dočasného súboru
        return File.createTempFile(
            imageFileName,
            ".jpg",
            storageDir
        )
    }

    /**
     * Získa URI pre súbor fotografií, ktoré môžu byť zdieľané s kamerou
     */
    fun getUriForFile(file: File): Uri {
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }

    /**
     * Konvertuje URI z galérie na cestu k súboru
     * Používa sa najmä pre URI z Content Providera
     */
    fun getFilePathFromUri(uri: Uri): String? {
        try {
            // Add logging to help with debugging
            Log.d("ImageHelper", "Getting file path from uri: $uri")

            val inputStream = context.contentResolver.openInputStream(uri) ?: run {
                Log.e("ImageHelper", "Failed to open input stream for URI: $uri")
                return null
            }

            // Create a unique file name
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
            val imageFile = File(
                context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "JPEG_${timeStamp}_.jpg"
            )

            // Ensure the parent directory exists
            imageFile.parentFile?.mkdirs()

            // Copy the file with proper buffer handling
            try {
                inputStream.use { input ->
                    imageFile.outputStream().use { output ->
                        val buffer = ByteArray(4 * 1024) // 4KB buffer
                        var bytesRead: Int
                        while (input.read(buffer).also { bytesRead = it } != -1) {
                            output.write(buffer, 0, bytesRead)
                        }
                        output.flush()
                    }
                }

                Log.d("ImageHelper", "Successfully copied image to: ${imageFile.absolutePath}")
                return imageFile.absolutePath
            } catch (e: Exception) {
                Log.e("ImageHelper", "Error while copying the file", e)
                return null
            }
        } catch (e: Exception) {
            Log.e("ImageHelper", "Error handling image URI", e)
            return null
        }
    }
}
