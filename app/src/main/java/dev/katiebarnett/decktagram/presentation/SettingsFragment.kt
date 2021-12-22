package dev.katiebarnett.decktagram.presentation

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import dagger.hilt.android.AndroidEntryPoint
import dev.katiebarnett.decktagram.R
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsFragment : PreferenceFragmentCompat() {
    
    private val navigationId = R.id.SettingsFragment

    private val viewModel: SettingsViewModel by viewModels()
    
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)

        // Get the switch preference
        val storeImagesInGallerySwitch: SwitchPreferenceCompat? = findPreference("imageStorageGallery")
        storeImagesInGallerySwitch?.setOnPreferenceChangeListener { preference, newValue ->
            viewModel.setImageStorageGallery(newValue as Boolean)
            true
        }
        lifecycleScope.launch {
            storeImagesInGallerySwitch?.isChecked = viewModel.getImageStorageGallery(this.coroutineContext)
        }


        // TODO clean up unused internal images
    }
}