package dev.katiebarnett.decktagram.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.hilt.android.AndroidEntryPoint
import dev.katiebarnett.decktagram.BuildConfig
import dev.katiebarnett.decktagram.R
import dev.katiebarnett.decktagram.databinding.AboutFragmentBinding
import dev.katiebarnett.decktagram.util.AboutScreen
import dev.katiebarnett.decktagram.util.logScreenView
import javax.inject.Inject

@AndroidEntryPoint
class AboutFragment : Fragment() {
    
    private lateinit var binding: AboutFragmentBinding

    @Inject
    lateinit var analytics: FirebaseAnalytics

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.about_fragment, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.about_fragment_label)
        (activity as AppCompatActivity).supportActionBar?.subtitle = null
        
        binding.markdownView.isOpenUrlInBrowser = true
        binding.markdownView.loadMarkdownFromAssets("About.md")
        
        binding.version.text = getString(R.string.version_info, BuildConfig.APP_VERSION_NAME, BuildConfig.APP_VERSION_CODE.toString())
    }

    override fun onResume() {
        super.onResume()
        analytics.logScreenView(AboutScreen)
    }
}