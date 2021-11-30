package dev.katiebarnett.decktagram.data.storage

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.katiebarnett.decktagram.models.PreferencesKeys
import dev.katiebarnett.decktagram.models.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        private const val USER_PREFERENCES_NAME = "user_preferences"
    }

    private val Context.userPreferencesDataStore by preferencesDataStore(
        name = USER_PREFERENCES_NAME
    )
    val userPreferencesFlow: Flow<UserPreferences> = context.userPreferencesDataStore.data
        .catch { exception ->
            // userPreferencesDataStore.data throws an IOException when an error is encountered when reading data
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            val showCompleted = preferences[PreferencesKeys.STORE_IMAGES_IN_GALLERY] ?: false
            UserPreferences(showCompleted)
        }

    suspend fun updateStoreImagesInGallery(storeImagesInGallery: Boolean) {
        context.userPreferencesDataStore.edit { preferences ->
            preferences[PreferencesKeys.STORE_IMAGES_IN_GALLERY] = storeImagesInGallery
        }
    }

    suspend fun getStoreImagesInGallery(): Boolean {
        return userPreferencesFlow.first().storeImagesInGallery
    }
}