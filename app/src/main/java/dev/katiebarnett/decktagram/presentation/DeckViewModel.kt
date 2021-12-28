package dev.katiebarnett.decktagram.presentation

import android.util.Log
import androidx.lifecycle.*
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.katiebarnett.decktagram.data.repositories.GameRepository
import dev.katiebarnett.decktagram.data.repositories.StateRepository
import dev.katiebarnett.decktagram.models.Deck
import dev.katiebarnett.decktagram.models.DeckState
import dev.katiebarnett.decktagram.models.PersistedDeckState
import dev.katiebarnett.decktagram.util.map
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
    private val stateRepository: StateRepository,
    private val state: SavedStateHandle,
    private val crashlytics: FirebaseCrashlytics
) : ViewModel() {
    
    companion object {
        private const val TAG = "DeckViewModel"   
    }
    
    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean>
        get() = _loading

    private val _snackbar = MutableLiveData<String?>()
    val snackbar: LiveData<String?>
        get() = _snackbar

    private val _deckDeleteResponse = MutableLiveData(false)
    val deckDeleteResponse: LiveData<Boolean>
        get() = _deckDeleteResponse

    private val deckId: Long
        get() = state.get<Long>("deckId") ?: -1

    private val _deck: MutableStateFlow<Deck?> = MutableStateFlow(null)
    private var persistedDeckState: PersistedDeckState? = null

    val deck = _deck.asLiveData()
    
    val cards = _deck.filterNotNull().flatMapLatest {
        gameRepository.getCardsForDeck(it.id)
    }.asLiveData()
    
    val deckState = MutableLiveData<DeckState?>(null)
    
    val onDeckStateChanged = Transformations.map(deckState) {
        saveDeckState()
    }

    val onDeckAndCardsLoaded = Transformations.map(cards) {
        loadDeckState()
    }
    
    private val stateSyncLoading = MutableLiveData(false)
    
    val lastDrawnCard = Transformations.map(deckState) {
        it?.drawnCards?.lastOrNull()
    }

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

    val drawCardEnabled = MediatorLiveData<Boolean>().apply {
        addSource(deckState) {
            value = deckState.value?.remainingCards.isNullOrEmpty() == false && stateSyncLoading.value == false
        }
        addSource(stateSyncLoading) {
            value = deckState.value?.remainingCards.isNullOrEmpty() == false && stateSyncLoading.value == false
        }
    }

    val undoDrawCardEnabled = MediatorLiveData<Boolean>().apply {
        addSource(deckState) {
            value = deckState.value?.drawnCards.isNullOrEmpty() == false && stateSyncLoading.value == false
        }
        addSource(stateSyncLoading) {
            value = deckState.value?.drawnCards.isNullOrEmpty() == false && stateSyncLoading.value == false
        }
    }

    val showEmpty = Transformations.map(cards) {
        it.isEmpty()
    }

    val showContent = Transformations.map(cards) {
        it.isNotEmpty()
    }
    
    init {
        launchDataLoad {
            gameRepository.getDeck(deckId).collect { 
                _deck.value = it
                persistedDeckState = stateRepository.getDeckState(it.id)
                _loading.postValue(false)
            }
        }
    }
    
    fun drawCard() {
        deckState.value?.let { currentState -> 
            val drawnCard = currentState.remainingCards.firstOrNull()
            drawnCard?.let { card ->
                deckState.postValue(
                    DeckState(
                        drawnCards = currentState.drawnCards.plus(card),
                        remainingCards = currentState.remainingCards.minus(card)
                    )
                )
            }
        }
    }

    fun undoDrawCard() {
        deckState.value?.let { currentState ->
            val drawnCard = currentState.drawnCards.lastOrNull()
            drawnCard?.let { card ->
                val newRemainingCards = currentState.remainingCards.toMutableList()
                newRemainingCards.add(0, card)
                deckState.postValue(
                    DeckState(
                        drawnCards = currentState.drawnCards.minus(card),
                        remainingCards = newRemainingCards
                    )
                )
            }
        }
    }
    
    fun resetDeck() {
        cards.value?.let { cards ->
            deckState.postValue(
                DeckState(
                    drawnCards = listOf(),
                    remainingCards = cards.shuffled()
                )
            )
        }
    }

    private fun loadDeckState() {
        deck.value?.let { _ ->
            cards.value?.let { cards ->
                if (cards.isNotEmpty()) {
                    viewModelScope.launch(Dispatchers.IO) {
                        stateSyncLoading.postValue(true)
                        try {
                            val persistedState = persistedDeckState?.map(cards)
                            if (persistedState != null) {
                                deckState.postValue(persistedState)
                            } else {
                                resetDeck()
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "State updating failed", e)
                            crashlytics.recordException(e)
                            resetDeck()
                        }
                        stateSyncLoading.postValue(false)
                    }
                }
            }
        }
    }

    private fun saveDeckState() {
        deckState.value?.let { state ->
            deck.value?.let { deck ->
                viewModelScope.launch(Dispatchers.IO) {
                    stateRepository.updateDeckState(deck, state)
                }
            }
        }
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
                _loading.postValue(false)
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