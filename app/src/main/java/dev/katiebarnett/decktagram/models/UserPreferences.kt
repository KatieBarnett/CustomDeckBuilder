package dev.katiebarnett.decktagram.models

import androidx.datastore.preferences.core.booleanPreferencesKey

data class UserPreferences(
    val storeImagesInGallery: Boolean = false
)

internal object PreferencesKeys {
    val STORE_IMAGES_IN_GALLERY = booleanPreferencesKey("store_images_in_gallery")
}