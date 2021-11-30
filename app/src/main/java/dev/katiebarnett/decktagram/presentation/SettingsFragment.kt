package dev.katiebarnett.decktagram.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.datastore.core.DataStore
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import dagger.hilt.android.AndroidEntryPoint
import dev.katiebarnett.decktagram.R
import dev.katiebarnett.decktagram.data.storage.UserPreferencesManager
import dev.katiebarnett.decktagram.databinding.DeckFragmentBinding
import dev.katiebarnett.decktagram.models.UserPreferences
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

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
    }
}