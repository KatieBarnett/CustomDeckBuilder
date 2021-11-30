package dev.katiebarnett.decktagram.presentation

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.katiebarnett.decktagram.data.repositories.GameRepository
import dev.katiebarnett.decktagram.data.storage.UserPreferencesManager
import dev.katiebarnett.decktagram.models.Game
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
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