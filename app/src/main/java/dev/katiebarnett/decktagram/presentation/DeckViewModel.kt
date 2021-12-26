package dev.katiebarnett.decktagram.presentation

import androidx.lifecycle.*
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.katiebarnett.decktagram.data.repositories.GameRepository
import dev.katiebarnett.decktagram.models.Card
import dev.katiebarnett.decktagram.models.Deck
import dev.katiebarnett.decktagram.util.CrashlyticsConstants.KEY_DECK_CARD_COUNT
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
class DeckViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    private val state: SavedStateHandle
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

    private val deckId: Long
        get() = state.get<Long>("deckId") ?: -1

    private val _deck: MutableStateFlow<Deck?> = MutableStateFlow(null)

    val deck = _deck.asLiveData()
    
    val cards = _deck.filterNotNull().flatMapLatest {
        gameRepository.getCardsForDeck(it.id)
    }.asLiveData()
    
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
    
    init {
        launchDataLoad {
            gameRepository.getDeck(deckId).collect { 
                _deck.value = it
            }
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

    fun saveCards(paths: List<String>) {
        launchDataLoad {
            deck.value?.id?.let {
                paths.forEach { path ->
                    gameRepository.updateCard(deckId = it, cardPath = path)
                }
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