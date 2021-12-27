package dev.katiebarnett.decktagram.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.katiebarnett.decktagram.data.storage.UserPreferencesManager
import dev.katiebarnett.decktagram.models.ImageQuality
import dev.katiebarnett.decktagram.models.ImageQuality.Companion.DEVICE_DEFAULT
import dev.katiebarnett.decktagram.util.CrashlyticsConstants
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferencesManager: UserPreferencesManager,
    private val crashlytics: FirebaseCrashlytics
) : ViewModel() {
    
    fun setImageStorageGallery(choice: Boolean) {
        viewModelScope.launch { 
            userPreferencesManager.updateStoreImagesInGallery(choice)
            crashlytics.setCustomKey(CrashlyticsConstants.KEY_STORE_IMAGES_IN_GALLERY, choice)
        }
    }

    suspend fun getImageStorageGallery(context: CoroutineContext): Boolean {
        return withContext(context) {
            userPreferencesManager.getStoreImagesInGallery()
        }
    }

    fun setImageQuality(choice: String) {
        viewModelScope.launch {
            if (choice.isBlank()) {
                userPreferencesManager.updateImageQuality(ImageQuality(DEVICE_DEFAULT, DEVICE_DEFAULT))
            } else {
                val split = choice.split(",")
                userPreferencesManager.updateImageQuality(ImageQuality(split[0].toIntOrNull(), split[1].toIntOrNull()))
            }
            crashlytics.setCustomKey(CrashlyticsConstants.KEY_STORE_IMAGES_IN_GALLERY, choice)
        }
    }

    suspend fun getImageQuality(context: CoroutineContext): ImageQuality {
        return withContext(context) {
            userPreferencesManager.getImageQuality()
        }
    }
    
}