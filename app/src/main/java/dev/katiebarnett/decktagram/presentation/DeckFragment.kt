package dev.katiebarnett.decktagram.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import dev.katiebarnett.decktagram.R
import dev.katiebarnett.decktagram.databinding.DeckFragmentBinding
import dev.katiebarnett.decktagram.util.navigateSafe

@AndroidEntryPoint
class DeckFragment : Fragment() {

    private val navigationId = R.id.DeckFragment
    
    private lateinit var binding: DeckFragmentBinding

    val args: DeckFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.deck_fragment, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.newCard.setOnClickListener {
            findNavController().navigateSafe(navigationId, DeckFragmentDirections.actionDeckFragmentToCameraFragment(args.deckId))
        }
    }
}