package dev.katiebarnett.decktagram.presentation

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.katiebarnett.decktagram.data.repositories.GameRepository
import dev.katiebarnett.decktagram.models.Card
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class DeckViewModel @Inject constructor(
    private val gameRepository: GameRepository
) : ViewModel() {

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean>
        get() = _loading

    private val _snackbar = MutableLiveData<String?>()
    val snackbar: LiveData<String?>
        get() = _snackbar

    private val _deckDeleteResponse = MutableLiveData<Boolean>(false)
    val deckDeleteResponse: LiveData<Boolean>
        get() = _deckDeleteResponse

    private val deckId: MutableStateFlow<Long?> = MutableStateFlow(null)

    private val deckMap = deckId.filterNotNull().flatMapLatest {
        gameRepository.getDeck(it)
    }.asLiveData()

    val deck = Transformations.map(deckMap) {
        it.keys.firstOrNull()
    }
    
    val cards = Transformations.map(deckMap) {
        it.values.firstOrNull()?.sortedByDescending { it.lastModified }
    }
    
    val drawnCards = MutableLiveData(listOf<Card>())
    
    val remainingCards = MutableLiveData(listOf<Card>())

    val displayState = MutableLiveData(DeckDisplayState.NONE)
    
    val displayAllCards = Transformations.map(displayState) {
        it == DeckDisplayState.ALL_CARDS
    }

    val displayDrawnCards = Transformations.map(displayState) {
        it == DeckDisplayState.DRAWN_CARDS
    }

    val displayRemainingCards = Transformations.map(displayState) {
        it == DeckDisplayState.REMAINING_CARDS
    }
    
    fun loadDeck(id: Long) {
        launchDataLoad {
            deckId.emit(id)
        }
    }
    
    fun resetDeck() {
        // TODO
    }

    fun deleteDeck() {
        deck.value?.let { deckToDelete ->
            val cardsToDelete = cards.value
            launchDataLoad {
                gameRepository.deleteDeck(deckToDelete)
                cardsToDelete?.forEach { 
                    gameRepository.deleteCard(it)
                    // TODO clean up unused internal images
                }
                _deckDeleteResponse.postValue(true)
            }
        }
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
    
    enum class DeckDisplayState {
        NONE, ALL_CARDS, DRAWN_CARDS, REMAINING_CARDS
    }
}