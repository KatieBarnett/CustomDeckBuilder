package dev.katiebarnett.decktagram.models

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

data class UserPreferences(
    val storeImagesInGallery: Boolean = false,
    val imageQuality: ImageQuality = ImageQuality()
)

internal object PreferencesKeys {
    val STORE_IMAGES_IN_GALLERY = booleanPreferencesKey("store_images_in_gallery")
    val IMAGE_QUALITY = stringPreferencesKey("image_quality")
}