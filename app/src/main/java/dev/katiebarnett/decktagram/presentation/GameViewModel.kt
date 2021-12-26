package dev.katiebarnett.decktagram.presentation

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.katiebarnett.decktagram.data.repositories.GameRepository
import dev.katiebarnett.decktagram.models.Deck
import dev.katiebarnett.decktagram.models.Game
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class GameViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    private val state: SavedStateHandle
) : ViewModel() {

    private val _loading = MutableLiveData<Boolean>(false)
    val loading: LiveData<Boolean>
        get() = _loading

    private val _snackbar = MutableLiveData<String?>()
    val snackbar: LiveData<String?>
        get() = _snackbar

    private val _deckCreationResponse = MutableLiveData<Long>(-1)
    val deckCreationResponse: LiveData<Long>
        get() = _deckCreationResponse
    
    private val _gameDeleteResponse = MutableLiveData<Boolean>(false)
    val gameDeleteResponse: LiveData<Boolean>
        get() = _gameDeleteResponse
    
    private val gameId: Long
        get() = state.get<Long>("gameId") ?: -1
    
    private val _game: MutableStateFlow<Game?> = MutableStateFlow(null)
    
    val game = _game.asLiveData()
    
    val decks = _game.filterNotNull().flatMapLatest {
        gameRepository.getDecksForGame(it.id)
    }.asLiveData()

    init {
        launchDataLoad {
            gameRepository.getGame(gameId).collect { _game.value = it }
        }
    }
    
    fun createDeck(deckName: String, gameId: Long) {
        val deck = Deck(name = deckName, gameId = gameId)
        launchDataLoad {
            _deckCreationResponse.postValue(gameRepository.updateDeck(deck))
        }
    }
    
    fun deleteGame() {
        game.value?.let { gameToDelete ->
            val decksToDelete = decks.value
            launchDataLoad {
                gameRepository.deleteGame(gameToDelete)
                decksToDelete?.forEach {
                    gameRepository.deleteDeck(it)
                    // TODO delete associated cards
                    // TODO clean up unused internal images
                }
                _gameDeleteResponse.postValue(true)
            }
        }
    }

    fun clearDeckCreationResponse() {
        _deckCreationResponse.value = -1
    }

    private fun launchDataLoad(block: suspend () -> Unit): Job {
        return viewModelScope.launch(Dispatchers.IO) {
            try {
                _loading.postValue(true)
                block()
            } catch (error: Throwable) {
                _snackbar.postValue(error.message)
            } finally {
                _loading.postValue(false)
            }
        }
    }
}