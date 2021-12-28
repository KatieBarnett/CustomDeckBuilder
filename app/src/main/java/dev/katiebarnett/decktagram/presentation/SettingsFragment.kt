package dev.katiebarnett.decktagram.presentation

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.preference.ListPreference
import androidx.preference.SwitchPreferenceCompat
import com.google.firebase.analytics.FirebaseAnalytics
import com.takisoft.preferencex.PreferenceFragmentCompat
import dagger.hilt.android.AndroidEntryPoint
import dev.katiebarnett.decktagram.R
import dev.katiebarnett.decktagram.util.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : PreferenceFragmentCompat() {
    
    private val navigationId = R.id.SettingsFragment

    private val viewModel: SettingsViewModel by viewModels()

    @Inject
    lateinit var analytics: FirebaseAnalytics

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.settings_fragment_label)
        (activity as AppCompatActivity).supportActionBar?.subtitle = null
    }

    override fun onCreatePreferencesFix(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)

        // Get the image storage preference
        val storeImagesInGallerySwitch: SwitchPreferenceCompat? = findPreference("imageStorageGallery")
        storeImagesInGallerySwitch?.setOnPreferenceChangeListener { preference, newValue ->
            viewModel.setImageStorageGallery(newValue as Boolean)
            analytics.logAction(SetImageStoreInGallery(newValue))
            true
        }
        lifecycleScope.launch {
            storeImagesInGallerySwitch?.isChecked = viewModel.getImageStorageGallery(this.coroutineContext)
        }
        
        // Get the image quality preference
        val imageQualityListPreference: ListPreference? = findPreference("imageQuality")
        imageQualityListPreference?.setOnPreferenceChangeListener { preference, newValue ->
            viewModel.setImageQuality(newValue as String)
            analytics.logAction(SetImageQuality(newValue))
            true
        }
        lifecycleScope.launch {
            imageQualityListPreference?.value = viewModel.getImageQuality(this.coroutineContext).toString()
        }


        // TODO clean up unused internal images
    }

    override fun onResume() {
        super.onResume()
        analytics.logScreenView(SettingsScreen)
    }
}