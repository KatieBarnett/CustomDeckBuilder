package dev.katiebarnett.decktagram.presentation

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.AndroidEntryPoint
import dev.katiebarnett.decktagram.BR
import dev.katiebarnett.decktagram.R
import dev.katiebarnett.decktagram.databinding.DeckFragmentBinding
import dev.katiebarnett.decktagram.models.Card
import dev.katiebarnett.decktagram.presentation.util.OnItemClickListener
import dev.katiebarnett.decktagram.util.*
import dev.katiebarnett.decktagram.util.CrashlyticsConstants.KEY_DECK_CARD_COUNT
import kotlinx.coroutines.ExperimentalCoroutinesApi
import me.tatarka.bindingcollectionadapter2.ItemBinding
import javax.inject.Inject

@androidx.camera.core.ExperimentalUseCaseGroup
@androidx.camera.lifecycle.ExperimentalUseCaseGroupLifecycle
@ExperimentalCoroutinesApi
@AndroidEntryPoint
class DeckFragment : Fragment() {

    private val navigationId = R.id.DeckFragment
    
    private lateinit var binding: DeckFragmentBinding

    private val viewModel: DeckViewModel by viewModels()

    @Inject
    lateinit var crashlytics: FirebaseCrashlytics

    @Inject
    lateinit var analytics: FirebaseAnalytics

    private val cardListItemClickListener = (object: OnItemClickListener<Card> {
        override fun onItemClicked(item: Card) {
            // TODO show card
//            findNavController().navigate(GameFragmentDirections.actionGameFragmentToDeckFragment(deckId = item.id))
        }
    })

    private val listItemBinding = ItemBinding.of<Card>(BR.item, R.layout.card_item)
        .bindExtra(BR.itemClickListener, cardListItemClickListener)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.deck_fragment, container, false)
        binding.viewModel = viewModel
        binding.listItemBinding = listItemBinding
        binding.lifecycleOwner = this
        
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        viewModel.deck.observe(viewLifecycleOwner, {
            it?.let { deck ->
                (activity as AppCompatActivity).supportActionBar?.title = String.format(getString(R.string.deck_fragment_title), deck.name)
            }
        })
        
        binding.addCardsButton.setOnClickListener {
            addCards()
        }

        viewModel.snackbar.observe(viewLifecycleOwner, {
            it?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
            }
        })
        
        binding.drawCardButton.setOnClickListener {
            analytics.logAction(DrawCard)
            viewModel.drawCard()
        }

        binding.undoButton.setOnClickListener {
            analytics.logAction(UndoDrawCard)
            viewModel.undoDrawCard()
        }
        
        binding.allCards.root.setOnClickListener {
            viewModel.displayState.value = if (viewModel.displayState.value != DeckViewModel.DeckDisplayState.ALL_CARDS) {
                DeckViewModel.DeckDisplayState.ALL_CARDS
            } else {
                DeckViewModel.DeckDisplayState.NONE
            }
            analytics.logAction(OpenAllCards(viewModel.cards.value?.size ?: 0, viewModel.drawnCards.value?.size ?: 0, viewModel.remainingCards.value?.size ?: 0))
        }

        binding.drawnCards.root.setOnClickListener {            
            viewModel.displayState.value = if (viewModel.displayState.value != DeckViewModel.DeckDisplayState.DRAWN_CARDS) {
                DeckViewModel.DeckDisplayState.DRAWN_CARDS
            } else {
                DeckViewModel.DeckDisplayState.NONE
            }
            analytics.logAction(OpenDrawnCards(viewModel.cards.value?.size ?: 0, viewModel.drawnCards.value?.size ?: 0, viewModel.remainingCards.value?.size ?: 0))
        }

        binding.remainingCards.root.setOnClickListener {            
            viewModel.displayState.value = if (viewModel.displayState.value != DeckViewModel.DeckDisplayState.REMAINING_CARDS) {
                DeckViewModel.DeckDisplayState.REMAINING_CARDS
            } else {
                DeckViewModel.DeckDisplayState.NONE
            }
            analytics.logAction(OpenRemainingCards(viewModel.cards.value?.size ?: 0, viewModel.drawnCards.value?.size ?: 0, viewModel.remainingCards.value?.size ?: 0))
        }

        viewModel.deckDeleteResponse.observe(viewLifecycleOwner, {
            if (it) {
                findNavController().navigateUp()
            }
        })
        
        viewModel.cards.observe(viewLifecycleOwner, {
            crashlytics.setCustomKey(KEY_DECK_CARD_COUNT, it.size)

            viewModel.doResetIfRequired()
        })
    }
    
    fun addCards() {
        viewModel.deck.value?.gameId?.let { gameId ->
            val dialog = CameraDialogFragment.newInstance(gameId)
            dialog.setListener(object : CameraDialogFragment.DialogListener {
                override fun onDone(imagePaths: List<String>) {
                    viewModel.saveCards(imagePaths)
                }
            })
            dialog.show(childFragmentManager, CameraDialogFragment.TAG)
            analytics.logAction(AddCardsToDeck(viewModel.cards.value?.size ?: 0))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_deck, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_add_cards -> {
                addCards()
            }
            R.id.action_reset_deck -> {
                analytics.logAction(ResetDeck(viewModel.cards.value?.size ?: 0))
                viewModel.resetDeck()
            }
            R.id.action_delete_deck -> {
                analytics.logAction(DeleteDeck(viewModel.cards.value?.size ?: 0))
                viewModel.deleteDeck()
            }
        }
        return true
    }

    override fun onResume() {
        super.onResume()
        analytics.logScreenView(DeckScreen)
    }
}