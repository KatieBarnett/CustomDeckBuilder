package dev.katiebarnett.decktagram.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import dev.katiebarnett.decktagram.BR
import dev.katiebarnett.decktagram.R
import dev.katiebarnett.decktagram.databinding.DeckFragmentBinding
import dev.katiebarnett.decktagram.models.Card
import dev.katiebarnett.decktagram.presentation.util.OnItemClickListener
import dev.katiebarnett.decktagram.util.navigateSafe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import me.tatarka.bindingcollectionadapter2.ItemBinding

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class DeckFragment : Fragment() {

    private val navigationId = R.id.DeckFragment
    
    private lateinit var binding: DeckFragmentBinding

    private val viewModel: DeckViewModel by viewModels()

    val args: DeckFragmentArgs by navArgs()

    private val cardListItemClickListener = (object: OnItemClickListener<Card> {
        override fun onItemClicked(item: Card) {
            // TODO show card
//            findNavController().navigate(GameFragmentDirections.actionGameFragmentToDeckFragment(deckId = item.id))
        }
    })

    private val listItemBinding = ItemBinding.of<Card>(BR.item, R.layout.card_item)
        .bindExtra(BR.itemClickListener, cardListItemClickListener)


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.deck_fragment, container, false)
        binding.viewModel = viewModel
        binding.listItemBinding = listItemBinding
        binding.lifecycleOwner = this

        viewModel.loadDeck(args.deckId)
        
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        binding.newCard.setOnClickListener {
            findNavController().navigateSafe(navigationId, DeckFragmentDirections.actionDeckFragmentToCameraFragment(args.deckId))
        }
        
        viewModel.deck.observe(viewLifecycleOwner, {
            it?.let {
                (activity as AppCompatActivity).supportActionBar?.title = String.format(getString(R.string.deck_fragment_title), it.name)
            }
        })

        viewModel.snackbar.observe(viewLifecycleOwner, {
            it?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
            }
        })
        
        binding.allCards.root.setOnClickListener {
            viewModel.displayState.value = if (viewModel.displayState.value != DeckViewModel.DeckDisplayState.ALL_CARDS) {
                DeckViewModel.DeckDisplayState.ALL_CARDS
            } else {
                DeckViewModel.DeckDisplayState.NONE
            }
        }

        binding.drawnCards.root.setOnClickListener {            
            viewModel.displayState.value = if (viewModel.displayState.value != DeckViewModel.DeckDisplayState.DRAWN_CARDS) {
                DeckViewModel.DeckDisplayState.DRAWN_CARDS
            } else {
                DeckViewModel.DeckDisplayState.NONE
            }
        }

        binding.remainingCards.root.setOnClickListener {            
            viewModel.displayState.value = if (viewModel.displayState.value != DeckViewModel.DeckDisplayState.REMAINING_CARDS) {
                DeckViewModel.DeckDisplayState.REMAINING_CARDS
            } else {
                DeckViewModel.DeckDisplayState.NONE
            }
        }
    }
}