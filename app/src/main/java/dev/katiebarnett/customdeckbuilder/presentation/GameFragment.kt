package dev.katiebarnett.customdeckbuilder.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import dev.katiebarnett.customdeckbuilder.R
import dev.katiebarnett.customdeckbuilder.databinding.GameFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GameFragment : Fragment() {
    
    private lateinit var binding: GameFragmentBinding

    private val viewModel: GameViewModel by viewModels()

    val args: GameFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.game_fragment, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        
        viewModel.loadGame(args.gameId)
        
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        viewModel.snackbar.observe(viewLifecycleOwner, {
            it?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
            }
            // TODO handle fatal error where we have to close
        })
        
        viewModel.game.observe(viewLifecycleOwner, {
            (activity as AppCompatActivity?)?.supportActionBar?.title = it.name
        })

        binding.newDeck.setOnClickListener {
            findNavController().navigate(GameFragmentDirections.actionGameFragmentToDeckFragment())
        }
    }
}