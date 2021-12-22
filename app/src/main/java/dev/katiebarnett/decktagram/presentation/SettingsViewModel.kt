package dev.katiebarnett.decktagram.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.katiebarnett.decktagram.data.storage.UserPreferencesManager
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferencesManager: UserPreferencesManager
) : ViewModel() {
    
    fun setImageStorageGallery(choice: Boolean) {
        viewModelScope.launch { 
            userPreferencesManager.updateStoreImagesInGallery(choice)
        }
    }

    suspend fun getImageStorageGallery(context: CoroutineContext): Boolean {
        return withContext(context) {
            userPreferencesManager.getStoreImagesInGallery()
        }
    }
    
}