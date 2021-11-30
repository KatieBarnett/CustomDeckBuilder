package dev.katiebarnett.decktagram.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import dev.katiebarnett.decktagram.BR
import dev.katiebarnett.decktagram.R
import dev.katiebarnett.decktagram.databinding.GameFragmentBinding
import dev.katiebarnett.decktagram.models.Deck
import dev.katiebarnett.decktagram.presentation.dialogs.NewDeckDialog
import dev.katiebarnett.decktagram.presentation.util.OnItemClickListener
import kotlinx.coroutines.ExperimentalCoroutinesApi
import me.tatarka.bindingcollectionadapter2.ItemBinding

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class GameFragment : Fragment(), NewDeckDialog.DialogListener {
    
    private val navigationId = R.id.GameFragment
    
    private lateinit var binding: GameFragmentBinding

    private val viewModel: GameViewModel by viewModels()

    val args: GameFragmentArgs by navArgs()

    private val deckListItemClickListener = (object: OnItemClickListener<Deck> {
        override fun onItemClicked(item: Deck) {
            findNavController().navigate(GameFragmentDirections.actionGameFragmentToDeckFragment(deckId = item.id))
        }
    })

    private val listItemBinding = ItemBinding.of<Deck>(BR.item, R.layout.deck_item)
        .bindExtra(BR.itemClickListener, deckListItemClickListener)


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.game_fragment, container, false)
        binding.viewModel = viewModel
        binding.listItemBinding = listItemBinding
        binding.lifecycleOwner = this
        
        viewModel.loadGame(args.gameId)
        
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        binding.newDeck.setOnClickListener {
            val dialog = NewDeckDialog()
            dialog.setListener(this)
            dialog.show(childFragmentManager, NewDeckDialog.TAG)
        }

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
    }

    override fun onDialogPositiveClick(deckName: String) {
        viewModel.createDeck(deckName, args.gameId)
    }
}