package dev.katiebarnett.decktagram.presentation

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.AndroidEntryPoint
import dev.katiebarnett.decktagram.BR
import dev.katiebarnett.decktagram.R
import dev.katiebarnett.decktagram.databinding.GameFragmentBinding
import dev.katiebarnett.decktagram.models.Deck
import dev.katiebarnett.decktagram.presentation.dialogs.NewDeckDialog
import dev.katiebarnett.decktagram.presentation.util.OnItemClickListener
import dev.katiebarnett.decktagram.util.CrashlyticsConstants
import kotlinx.coroutines.ExperimentalCoroutinesApi
import me.tatarka.bindingcollectionadapter2.ItemBinding
import javax.inject.Inject

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class GameFragment : Fragment(), NewDeckDialog.DialogListener {
    
    private val navigationId = R.id.GameFragment
    
    private lateinit var binding: GameFragmentBinding

    private val viewModel: GameViewModel by viewModels()

    private val args: GameFragmentArgs by navArgs()

    @Inject
    lateinit var crashlytics: FirebaseCrashlytics

    private val deckListItemClickListener = (object: OnItemClickListener<Deck> {
        override fun onItemClicked(item: Deck) {
            findNavController().navigate(GameFragmentDirections.actionGameFragmentToDeckFragment(deckId = item.id))
        }
    })

    private val listItemBinding = ItemBinding.of<Deck>(BR.item, R.layout.deck_item)
        .bindExtra(BR.itemClickListener, deckListItemClickListener)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.game_fragment, container, false)
        binding.viewModel = viewModel
        binding.listItemBinding = listItemBinding
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        binding.addDeckButton.setOnClickListener {
            addDeck() 
        }

        viewModel.game.observe(viewLifecycleOwner, {
            it?.let {
                (activity as AppCompatActivity).supportActionBar?.title = String.format(getString(R.string.game_fragment_title), it.name)
            }
        })

        viewModel.snackbar.observe(viewLifecycleOwner, {
            it?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
            }
        })

        viewModel.deckCreationResponse.observe(viewLifecycleOwner, {
            if (it != -1L) {
                viewModel.clearDeckCreationResponse()
                findNavController().navigate(
                    GameFragmentDirections.actionGameFragmentToDeckFragment(
                        deckId = it
                    )
                )
            }
        })

        viewModel.gameDeleteResponse.observe(viewLifecycleOwner, {
            if (it) {
                findNavController().navigateUp()
            }
        })

        viewModel.decks.observe(viewLifecycleOwner, {
            crashlytics.setCustomKey(CrashlyticsConstants.KEY_GAME_DECK_COUNT, it.size)
        })
    }
    
    fun addDeck() {
        val dialog = NewDeckDialog()
        dialog.setListener(this)
        dialog.show(childFragmentManager, NewDeckDialog.TAG)
    }

    override fun onDialogPositiveClick(deckName: String) {
        viewModel.createDeck(deckName, args.gameId)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_game, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_add_deck -> {
                addDeck()
            }
            R.id.action_reset_game -> {
                // TODO
            }
            R.id.action_delete_game -> {
                viewModel.deleteGame()
            }
        }
        return true
    }
}