package dev.katiebarnett.decktagram.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import dev.katiebarnett.decktagram.R
import dev.katiebarnett.decktagram.databinding.ViewCardFragmentBinding

@AndroidEntryPoint
class ViewCardFragment : Fragment() {
    
    private val navigationId = R.id.ViewCardFragment
    
    private lateinit var binding: ViewCardFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.view_card_fragment, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }
}