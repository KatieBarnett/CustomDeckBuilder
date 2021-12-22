package dev.katiebarnett.decktagram.presentation

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.katiebarnett.decktagram.data.repositories.GameRepository
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
    
    fun loadDeck(id: Long) {
        launchDataLoad {
            deckId.emit(id)
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
}