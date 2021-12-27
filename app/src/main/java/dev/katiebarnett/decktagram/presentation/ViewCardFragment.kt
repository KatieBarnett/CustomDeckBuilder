package dev.katiebarnett.decktagram.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.hilt.android.AndroidEntryPoint
import dev.katiebarnett.decktagram.R
import dev.katiebarnett.decktagram.databinding.ViewCardFragmentBinding
import dev.katiebarnett.decktagram.util.ViewCardScreen
import dev.katiebarnett.decktagram.util.logScreenView
import javax.inject.Inject

@AndroidEntryPoint
class ViewCardFragment : Fragment() {
    
    private val navigationId = R.id.ViewCardFragment
    
    private lateinit var binding: ViewCardFragmentBinding

    @Inject
    lateinit var analytics: FirebaseAnalytics

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.view_card_fragment, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        analytics.logScreenView(ViewCardScreen)
    }
}