package dev.katiebarnett.decktagram.presentation

import android.content.ContentValues
import android.content.Context
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.camera.core.ImageCapture
import androidx.lifecycle.*
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.katiebarnett.decktagram.R
import dev.katiebarnett.decktagram.data.storage.UserPreferencesManager
import dev.katiebarnett.decktagram.util.CrashlyticsConstants.KEY_CAMERA_SAVE_PHOTO_COUNT
import dev.katiebarnett.decktagram.util.CrashlyticsConstants.KEY_STORE_IMAGES_IN_GALLERY
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val userPreferencesManager: UserPreferencesManager,
    private val crashlytics: FirebaseCrashlytics
) : ViewModel() {
    
    companion object {
        // TODO change this to ISO format
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    }
    
    var gameId: Long? = null

    private val _loading = MutableLiveData<Boolean>(false)
    val loading: LiveData<Boolean>
        get() = _loading

    private val _snackbar = MutableLiveData<String?>()
    val snackbar: LiveData<String?>
        get() = _snackbar

    private val imageCollection =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }
    
    var storeImagesInGallery: Boolean = false
    
    var internalAppFilePath: String? = null
    
    private val _imageBuffer = MutableLiveData<List<String>>(listOf())
    val imageBuffer: LiveData<List<String>>
        get() = _imageBuffer
    
    val lastCapturedImage = Transformations.map(imageBuffer) {
        it.lastOrNull()
    }
    
    val imageBufferCount = Transformations.map(imageBuffer) {
        it.size
    }
    
    init {
        viewModelScope.launch {
            storeImagesInGallery = userPreferencesManager.getStoreImagesInGallery()
            crashlytics.setCustomKey(KEY_STORE_IMAGES_IN_GALLERY, storeImagesInGallery)
        }
    }
    
    private fun getFileName(): String {
        return SimpleDateFormat(FILENAME_FORMAT, Locale.getDefault()).format(System.currentTimeMillis()) + ".jpg"
    }
    
    private fun getFolderName(context: Context): String {
        return if (gameId != null) {
            context.resources.getString(R.string.game_folder_name, gameId.toString())
        } else {
            context.resources.getString(R.string.default_folder_name)
        }
    }

    fun getOutputOptions(context: Context): ImageCapture.OutputFileOptions.Builder {
        return if (storeImagesInGallery) {
            // Device photos directory
            val resolver = context.contentResolver
            
            val newImageDetails = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, getFileName())
            }
            ImageCapture.OutputFileOptions.Builder(resolver, imageCollection, newImageDetails)
        } else {
            // Local to app directory
            val mediaDir = context.filesDir?.let {
                    File(it, getFolderName(context)).apply { mkdirs() }
            }
            val storageDirectory = if (mediaDir != null && mediaDir.exists()) {
                mediaDir
            } else {
                context.filesDir
            }
            val file = File(storageDirectory, getFileName())
            internalAppFilePath = file.absolutePath
            ImageCapture.OutputFileOptions.Builder(file)
        }
    }
    
    fun addImageToGalleryIfRequired(context: Context, filePath: String) {
        viewModelScope.launch(Dispatchers.IO) {
            // Only expose to gallery if we are storing the image in the gallery
            if (storeImagesInGallery) {
                val file = File(filePath)
                if (file.exists()) {
                    MediaScannerConnection.scanFile(context, arrayOf(file.toString()), null, null)
                }
            }
        }
    }

    fun getRealPathFromURI(context: Context, contentUri: Uri): String? {
        val resolver = context.contentResolver
        val projection = arrayOf(MediaStore.Video.Media.DATA)
        val query = resolver.query(
            contentUri,
            projection,
            null,
            null,
            null
        )
        query?.use { cursor ->
            val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
            while (cursor.moveToNext()) {
                return cursor.getString(dataColumn)
            }
        }
        return null
    }
    
    fun saveImageToBuffer(imagePath: String) {
        val newBuffer = imageBuffer.value?.plus(imagePath) ?: listOf(imagePath)
        _imageBuffer.postValue(newBuffer)
        crashlytics.setCustomKey(KEY_CAMERA_SAVE_PHOTO_COUNT, newBuffer.size)
    }
}