package dev.katiebarnett.customdeckbuilder.presentation

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.katiebarnett.customdeckbuilder.data.repositories.GameRepository
import dev.katiebarnett.customdeckbuilder.models.Deck
import dev.katiebarnett.customdeckbuilder.models.Game
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
    private val gameRepository: GameRepository
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

    private val game: MutableStateFlow<Game?> = MutableStateFlow(null)
    
    val decks = game.filterNotNull().flatMapLatest {
        gameRepository.getDecksForGame(it.id)
    }.asLiveData()

    fun loadGame(gameId: Long) {
        launchDataLoad { 
            gameRepository.getGame(gameId).collect { game.value = it.firstOrNull() }
        }
    }
    
    fun createDeck(deckName: String, gameId: Long) {
        val deck = Deck(name = deckName, gameId = gameId)
        launchDataLoad {
            _deckCreationResponse.postValue(gameRepository.updateDeck(deck))
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