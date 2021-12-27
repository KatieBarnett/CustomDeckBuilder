package dev.katiebarnett.decktagram.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import dev.katiebarnett.decktagram.BuildConfig
import dev.katiebarnett.decktagram.R
import dev.katiebarnett.decktagram.databinding.AboutFragmentBinding

@AndroidEntryPoint
class AboutFragment : Fragment() {
    
    private lateinit var binding: AboutFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.about_fragment, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.markdownView.isOpenUrlInBrowser = true
        binding.markdownView.loadMarkdownFromAssets("About.md")
        
        binding.version.text = getString(R.string.version_info, BuildConfig.APP_VERSION_NAME, BuildConfig.APP_VERSION_CODE)
    }
    
}