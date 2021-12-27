package dev.katiebarnett.decktagram.presentation

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.preference.ListPreference
import androidx.preference.SwitchPreferenceCompat
import com.takisoft.preferencex.PreferenceFragmentCompat
import dagger.hilt.android.AndroidEntryPoint
import dev.katiebarnett.decktagram.R
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsFragment : PreferenceFragmentCompat() {
    
    private val navigationId = R.id.SettingsFragment

    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreatePreferencesFix(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)

        // Get the image storage preference
        val storeImagesInGallerySwitch: SwitchPreferenceCompat? = findPreference("imageStorageGallery")
        storeImagesInGallerySwitch?.setOnPreferenceChangeListener { preference, newValue ->
            viewModel.setImageStorageGallery(newValue as Boolean)
            true
        }
        lifecycleScope.launch {
            storeImagesInGallerySwitch?.isChecked = viewModel.getImageStorageGallery(this.coroutineContext)
        }
        
        // Get the image quality preference
        val imageQualityListPreference: ListPreference? = findPreference("imageQuality")
        imageQualityListPreference?.setOnPreferenceChangeListener { preference, newValue ->
            viewModel.setImageQuality(newValue as String)
            true
        }
        lifecycleScope.launch {
            imageQualityListPreference?.value = viewModel.getImageQuality(this.coroutineContext).toString()
        }


        // TODO clean up unused internal images
    }
}