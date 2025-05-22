package sk.duracik.myaiapplication.ui.components

import android.Manifest
import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import sk.duracik.myaiapplication.util.ImageHelper
import sk.duracik.myaiapplication.R
import java.io.File

@Composable
fun ImagePicker(
    imageUrls: List<String>,
    onImageAdded: (String) -> Unit,
    onImageRemoved: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val imageHelper = remember { ImageHelper(context) }

    var showDialog by remember { mutableStateOf(false) }
    var currentPhotoPath by remember { mutableStateOf<String?>(null) }

    // Launcher pre výber obrázkov z galérie
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            Log.d("ImagePicker", "Image selected from gallery: $uri")
            try {
                val filePath = imageHelper.getFilePathFromUri(uri)
                if (filePath != null) {
                    Log.d("ImagePicker", "Converted URI to file path: $filePath")
                    onImageAdded("file://$filePath")
                } else {
                    Log.w("ImagePicker", "Failed to get file path, using URI directly: $uri")
                    // Fallback for when getFilePathFromUri returns null
                    // Use the URI directly which might work with some image libraries
                    onImageAdded(uri.toString())
                }
            } catch (e: Exception) {
                Log.e("ImagePicker", "Error processing gallery image", e)
                // If there's any exception, try using the URI directly
                onImageAdded(uri.toString())
            }
        } else {
            Log.d("ImagePicker", "No image selected from gallery")
        }
    }

    // Launcher pre fotografovanie
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success && currentPhotoPath != null) {
            onImageAdded("file://$currentPhotoPath")
        }
    }

    // Launchery pre žiadosti o povolenia
    val requestCameraPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            launchCamera(context, imageHelper, cameraLauncher) {
                currentPhotoPath = it
            }
        } else {
            Log.e("ImagePicker", "Camera permission denied")
        }
    }

    // Direct gallery launcher without permission check (as a fallback)
    val directGalleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        // Use the same handler as galleryLauncher
        if (uri != null) {
            Log.d("ImagePicker", "Image selected from gallery (direct): $uri")
            try {
                val filePath = imageHelper.getFilePathFromUri(uri)
                if (filePath != null) {
                    Log.d("ImagePicker", "Converted URI to file path: $filePath")
                    onImageAdded("file://$filePath")
                } else {
                    Log.w("ImagePicker", "Failed to get file path, using URI directly: $uri")
                    onImageAdded(uri.toString())
                }
            } catch (e: Exception) {
                Log.e("ImagePicker", "Error processing gallery image", e)
                onImageAdded(uri.toString())
            }
        } else {
            Log.d("ImagePicker", "No image selected from gallery")
        }
    }

    val requestMediaPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        Log.d("ImagePicker", "Media permission result: $isGranted")
        if (isGranted) {
            Log.d("ImagePicker", "Media permission granted, launching gallery")
            // Using the original galleryLauncher
            galleryLauncher.launch("image/*")
        } else {
            // Try to open gallery anyway, some devices don't require permissions
            Log.w("ImagePicker", "Media permission denied, trying direct launch")
            directGalleryLauncher.launch("image/*")
        }
    }

    Column(modifier = modifier) {
        Text(
            text = "Fotografie rastliny",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Zobrazenie už pridaných obrázkov s možnosťou odstránenia
        if (imageUrls.isNotEmpty()) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(imageUrls, key = { it }) { imageUrl ->
                    ImageWithDeleteButton(
                        imageUrl = imageUrl,
                        onDeleteClick = { onImageRemoved(imageUrl) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Tlačidlo na pridanie novej fotky
        Button(
            onClick = { showDialog = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Pridať fotku"
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Pridať fotku")
        }
    }

    // Dialog pre výber spôsobu pridania fotky
    if (showDialog) {
        androidx.compose.ui.window.Dialog(
            onDismissRequest = { showDialog = false }
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Pridať fotku",
                        style = MaterialTheme.typography.headlineSmall
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Vyberte spôsob pridania fotky",
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // Tlačidlo na fotografovanie
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    showDialog = false
                                    requestCameraPermission.launch(Manifest.permission.CAMERA)
                                }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.PhotoCamera,
                                contentDescription = "Fotografovať",
                                modifier = Modifier
                                    .size(64.dp)
                                    .padding(8.dp)
                            )
                            Text("Fotografovať")
                        }

                        // Tlačidlo na výber z galérie
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    showDialog = false
                                    // Directly launch gallery without permission check
                                    // This works more reliably on most devices
                                    Log.d("ImagePicker", "Clicking gallery button, launching directly")
                                    directGalleryLauncher.launch("image/*")
                                }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.PhotoLibrary,
                                contentDescription = "Z galérie",
                                modifier = Modifier
                                    .size(64.dp)
                                    .padding(8.dp)
                            )
                            Text("Z galérie")
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { showDialog = false },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Zrušiť")
                    }
                }
            }
        }
    }
}

/**
 * Spustenie fotoaparátu
 */
private fun launchCamera(
    context: Context,
    imageHelper: ImageHelper,
    cameraLauncher: androidx.activity.result.ActivityResultLauncher<Uri>,
    onPhotoFile: (String) -> Unit
) {
    try {
        val photoFile = imageHelper.createImageFile()
        val photoURI = imageHelper.getUriForFile(photoFile)
        onPhotoFile(photoFile.absolutePath)
        cameraLauncher.launch(photoURI)
    } catch (ex: Exception) {
        // Chyba pri vytváraní súboru
        ex.printStackTrace()
    }
}

@Composable
fun ImageWithDeleteButton(
    imageUrl: String,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        // Obrázok
        Image(
            painter = rememberAsyncImagePainter(
                model = imageUrl,
                error = painterResource(id = R.drawable.ic_plant_placeholder)
            ),
            contentDescription = null,
            modifier = Modifier
                .size(100.dp)
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(4.dp))
                .padding(2.dp)
        )

        // Tlačidlo na odstránenie
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(4.dp)
        ) {
            IconButton(
                onClick = onDeleteClick,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Odstrániť obrázok",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
