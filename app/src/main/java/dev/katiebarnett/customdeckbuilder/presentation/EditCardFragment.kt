package dev.katiebarnett.customdeckbuilder.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import dev.katiebarnett.customdeckbuilder.R
import dev.katiebarnett.customdeckbuilder.databinding.EditCardFragmentBinding

@AndroidEntryPoint
class EditCardFragment : Fragment() {
    
    private lateinit var binding: EditCardFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.edit_card_fragment, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }
}