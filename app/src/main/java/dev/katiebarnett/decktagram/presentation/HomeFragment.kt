package dev.katiebarnett.decktagram.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.AndroidEntryPoint
import dev.katiebarnett.decktagram.BR
import dev.katiebarnett.decktagram.R
import dev.katiebarnett.decktagram.databinding.HomeFragmentBinding
import dev.katiebarnett.decktagram.models.Game
import dev.katiebarnett.decktagram.presentation.dialogs.NewGameDialog
import dev.katiebarnett.decktagram.presentation.util.OnItemClickListener
import dev.katiebarnett.decktagram.util.CrashlyticsConstants
import dev.katiebarnett.decktagram.util.navigateSafe
import me.tatarka.bindingcollectionadapter2.ItemBinding
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment(), NewGameDialog.DialogListener {
    
    private val navigationId = R.id.HomeFragment
    
    private lateinit var binding: HomeFragmentBinding
    
    private val viewModel: HomeViewModel by viewModels()

    @Inject 
    lateinit var crashlytics: FirebaseCrashlytics
    
    private val gameListItemClickListener = (object: OnItemClickListener<Game> {
        override fun onItemClicked(item: Game) {
            findNavController().navigateSafe(navigationId, HomeFragmentDirections.actionHomeFragmentToGameFragment(gameId = item.id))
        }
    })
    
    private val listItemBinding = ItemBinding.of<Game>(BR.item, R.layout.game_item)
        .bindExtra(BR.itemClickListener, gameListItemClickListener)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.home_fragment, container, false)
        binding.viewModel = viewModel
        binding.listItemBinding = listItemBinding
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        binding.newGame.setOnClickListener {
            val dialog = NewGameDialog()
            dialog.setListener(this)
            dialog.show(childFragmentManager, NewGameDialog.TAG)
        }
        
        viewModel.snackbar.observe(viewLifecycleOwner, {
            it?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
            }
        })
        
        viewModel.gameCreationResponse.observe(viewLifecycleOwner, {
            if (it != -1L) {
                viewModel.clearGameCreationResponse()
                findNavController().navigateSafe(navigationId,
                    HomeFragmentDirections.actionHomeFragmentToGameFragment(
                        gameId = it
                    )
                )
            }
        })

        viewModel.games.observe(viewLifecycleOwner, {
            crashlytics.setCustomKey(CrashlyticsConstants.KEY_GAME_COUNT, it.size)
        })
    }

    override fun onDialogPositiveClick(gameName: String) {
        viewModel.createGame(gameName)
    }
}