package dev.katiebarnett.decktagram.presentation

import android.content.ContentValues
import android.content.Context
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.camera.core.ImageCapture
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.katiebarnett.decktagram.R
import dev.katiebarnett.decktagram.data.repositories.GameRepository
import dev.katiebarnett.decktagram.data.storage.UserPreferencesManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val userPreferencesManager: UserPreferencesManager,
    private val gameRepository: GameRepository
) : ViewModel() {
    
    companion object {
        // TODO change this to ISO format
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    }

    private val _loading = MutableLiveData<Boolean>(false)
    val loading: LiveData<Boolean>
        get() = _loading

    private val _snackbar = MutableLiveData<String?>()
    val snackbar: LiveData<String?>
        get() = _snackbar

    private val _photoSaveResponse = MutableLiveData<Long>(-1)
    val photoSaveResponse: LiveData<Long>
        get() = _photoSaveResponse

    private val imageCollection =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }
    
    var storeImagesInGallery: Boolean = false
    
    init {
        viewModelScope.launch {
            storeImagesInGallery = userPreferencesManager.getStoreImagesInGallery()
        }
    }
    
    fun getFileName(): String {
        return SimpleDateFormat(FILENAME_FORMAT, Locale.getDefault()).format(System.currentTimeMillis()) + ".jpg"
    }

    fun getOutputOptions(context: Context, deckId: Long): ImageCapture.OutputFileOptions.Builder {
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
                File(it, context.resources.getString(R.string.deck_folder_name, deckId.toString())).apply { mkdirs() }
            }
            val storageDirectory = if (mediaDir != null && mediaDir.exists()) {
                mediaDir
            } else {
                context.filesDir
            }
            val file = File(storageDirectory, getFileName())
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
    
    fun saveImageToDatabase(deckId: Long, path: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _photoSaveResponse.postValue(gameRepository.updateCard(deckId, "cardName", path))
        }
    }
}