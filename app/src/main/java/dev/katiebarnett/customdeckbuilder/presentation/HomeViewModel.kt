package dev.katiebarnett.customdeckbuilder.presentation

import androidx.lifecycle.*
import dev.katiebarnett.customdeckbuilder.data.repositories.GameRepository
import dev.katiebarnett.customdeckbuilder.models.ErrorResource
import dev.katiebarnett.customdeckbuilder.models.Game
import dev.katiebarnett.customdeckbuilder.models.Resource
import dev.katiebarnett.customdeckbuilder.models.SuccessResource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val gameRepository: GameRepository
) : ViewModel() {
    
    private val _loading = MutableLiveData<Boolean>(false)
    val loading: LiveData<Boolean>
        get() = _loading

    private val _snackbar = MutableLiveData<String?>()
    val snackbar: LiveData<String?>
        get() = _snackbar
    
    private val _gameCreationResponse = MutableLiveData<Long>(-1)
    val gameCreationResponse: LiveData<Long>
        get() = _gameCreationResponse

    val games: LiveData<List<Game>> = gameRepository.getAllGames().asLiveData()
    
    fun createGame(gameName: String) {
        val game = Game(name = gameName)
        launchDataLoad {
            _gameCreationResponse.postValue(gameRepository.updateGame(game))
        }
    }
    
    fun clearGameCreationResponse() {
        _gameCreationResponse.value = -1
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