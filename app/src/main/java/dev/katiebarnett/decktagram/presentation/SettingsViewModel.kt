package dev.katiebarnett.decktagram.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.katiebarnett.decktagram.data.storage.UserPreferencesManager
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
    
}