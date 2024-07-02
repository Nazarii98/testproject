package com.app.voicenoteswear.presentation.flow.main.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.voicenoteswear.data.apimodels.request.RefreshTokenRequest
import com.app.voicenoteswear.data.apimodels.response.Recording
import com.app.voicenoteswear.data.base.Resource
import com.app.voicenoteswear.data.database.FilePath
import com.app.voicenoteswear.data.database.FilePathDao
import com.app.voicenoteswear.data.datastorage.UserDataStorage
import com.app.voicenoteswear.domain.auth.repository.AuthRepository
import com.app.voicenoteswear.domain.record.repository.RecordRepository
import com.app.voicenoteswear.utils.TimerState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException
import java.io.File
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val recordRepository: RecordRepository,
    private val userDataStorage: UserDataStorage,
    private val filePathDao: FilePathDao
) : ViewModel() {

    private val _state = MutableSharedFlow<State>()
    val state: SharedFlow<State> = _state

    var audioFile: File? = null

    private var page = 1

    private val _elapsedTime = MutableStateFlow(0L)
    private val _timerState = MutableStateFlow(TimerState.RESET)
    val timerState = _timerState.asStateFlow()

    private val formatter = DateTimeFormatter.ofPattern("mm:ss")
    val stopWatchText = _elapsedTime
        .map { millis ->
            LocalTime.ofNanoOfDay(millis * 1_000_000).format(formatter)
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            "00:00"
        )

    sealed interface State {
        object Loading : State
        class FetchNotesSuccess(val notes: List<Recording>) : State
        class StoreAudioSuccess(val recordingId: String) : State
        object LogoutSuccess : State
        object AddTitleSuccess : State
        object AddTransciptSuccess : State
        class LoadMoreItemsSuccess(val notes: List<Recording>) : State
        class RetrieveFilePathsSuccess(val files: List<FilePath>) : State
        class Error(val message: String) : State
    }

    init {
        _timerState
            .flatMapLatest { timerState ->
                getTimerFlow(
                    isRunning = timerState == TimerState.RUNNING
                )
            }
            .onEach { timeDiff ->
                _elapsedTime.update { it + timeDiff }
            }
            .launchIn(viewModelScope)
        getAllRecordings()
    }

    fun toggleIsRunning() {
        when(timerState.value) {
            TimerState.RUNNING -> _timerState.update { TimerState.PAUSED }
            TimerState.PAUSED,
            TimerState.RESET -> _timerState.update { TimerState.RUNNING }
        }
    }

    fun resetTimer() {
        _timerState.update { TimerState.RESET }
        _elapsedTime.update { 0L }
    }

    private fun getTimerFlow(isRunning: Boolean): Flow<Long> {
        return flow {
            var startMillis = System.currentTimeMillis()
            while(isRunning) {
                val currentMillis = System.currentTimeMillis()
                val timeDiff = if(currentMillis > startMillis) {
                    currentMillis - startMillis
                } else 0L
                emit(timeDiff)
                startMillis = System.currentTimeMillis()
                delay(10L)
            }
        }
    }

    fun getAllRecordings() {
        viewModelScope.launch {
            _state.emit(State.Loading)
            val result = recordRepository.getAllRecordings()
            result.collectLatest {
                when(it) {
                    is Resource.Error -> {
                        Log.d("HomeViewModel", "getAllRecordings error")
                        if (it.exception is HttpException && it.exception.code() == 401) {
                            Log.d("HomeViewModel", "refresh")
                            refreshToken()
                        } else {
                            _state.emit(State.Error(it.exception.message.orEmpty()))
                            getFilesFromDb()
                        }
                    }
                    Resource.Loading -> {
                        _state.emit(State.Loading)
                    }
                    is Resource.Success -> {
                        Log.d("HomeViewModel", "getAllRecordings success")
                        val list = it.data.data ?: emptyList()
                        page = 1
                        _state.emit(State.FetchNotesSuccess(list))
                    }
                }
            }
        }
    }

    private fun refreshToken() {
        viewModelScope.launch {
            val result = authRepository.refresh(RefreshTokenRequest(userDataStorage.accessToken))
            result.collectLatest {
                when(it) {
                    is Resource.Error -> {

                    }
                    Resource.Loading -> {
                        _state.emit(State.Loading)
                    }
                    is Resource.Success -> {
                        it.data.authorisation?.token?.let {
                            userDataStorage.accessToken = it
                            getAllRecordings()
                        }
                    }
                }
            }
        }
    }

    fun addTitle(recordingId: String) {
        viewModelScope.launch {
            _state.emit(State.Loading)
            val result = recordRepository.addTitle(recordingId)
            result.collectLatest {
                when(it) {
                    is Resource.Error -> {
                        _state.emit(State.Error(it.exception.message.orEmpty()))
                    }
                    Resource.Loading -> {
                        _state.emit(State.Loading)
                    }
                    is Resource.Success -> {
                        Log.d("HomeViewModel", "addTitle $recordingId success")
                        _state.emit(State.AddTitleSuccess)
                    }
                }
            }
        }
    }

    fun addTranscript(recordingId: String) {
        viewModelScope.launch {
            _state.emit(State.Loading)
            val result = recordRepository.addTranscript(recordingId)
            result.collectLatest {
                when(it) {
                    is Resource.Error -> {
                        _state.emit(State.Error(it.exception.message.orEmpty()))
                    }
                    Resource.Loading -> {
                        _state.emit(State.Loading)
                    }
                    is Resource.Success -> {
                        Log.d("HomeViewModel", "addTranscript $recordingId success")
                        _state.emit(State.AddTransciptSuccess)
                    }
                }
            }
        }
    }

    fun storeAudio(file: File, duration: Long) {
        viewModelScope.launch {
            _state.emit(State.Loading)
            // Create a RequestBody instance from file
            val requestFile = RequestBody.create("audio/*".toMediaTypeOrNull(), file)
            val body = MultipartBody.Part.createFormData("audio", file.name, requestFile)

            // Create RequestBody instance from duration
            val duration = RequestBody.create(MultipartBody.FORM, duration.toString())
            val result = recordRepository.storeAudio(body, duration)
            result.collectLatest {
                when(it) {
                    is Resource.Error -> {
                        _state.emit(State.Error(it.exception.message.orEmpty()))
                    }
                    Resource.Loading -> {
                        _state.emit(State.Loading)
                    }
                    is Resource.Success -> {
                        _state.emit(State.StoreAudioSuccess(it.data.recording?.recordingId.orEmpty()))
                    }
                }
            }
        }
    }

    fun insertFile(file: File?) {
        viewModelScope.launch {
            file?.let {
                filePathDao.insertFilePath(FilePath(path = it.absolutePath))
            }
        }
    }

    fun getFilesFromDb() {
        Log.d("HomeViewModel", "getFilesFromDb start")
        viewModelScope.launch {
            val result = filePathDao.getAllFilePaths()
            result.collectLatest {
                Log.d("HomeViewModel", "getFilesFromDb success")
                _state.emit(State.RetrieveFilePathsSuccess(it))
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            val result = authRepository.logout()
            result.collectLatest {
                when(it) {
                    is Resource.Error -> {

                    }
                    Resource.Loading -> {
                        _state.emit(State.Loading)
                    }
                    is Resource.Success -> {
                        userDataStorage.accessToken = ""
                        _state.emit(State.LogoutSuccess)
                    }
                }
            }
        }
    }

    fun syncFilesFromDbIfNeeded() {
        viewModelScope.launch {
            val result = filePathDao.getAllFilePaths()
            Log.d("HomeViewModel", "syncFilesFromDbIfNeeded")

            result.collectLatest {
                Log.d("HomeViewModel", "getFilesFromDb success list = ${it.size}")
                if (it.isNotEmpty()) {
                    it.forEach{
                        val file = File(it.path)
                        Log.d("HomeViewModel", "file = $file path = ${file.absolutePath}")
                        storeAudio((file), 4000)
//                        if (file.exists()) file.delete()
                    }
                        filePathDao.deleteAllFilePaths()
                }
            }
        }
    }

    fun loadMoreItems() {
        page++
        viewModelScope.launch {
            _state.emit(State.Loading)
            val result = recordRepository.loadMoreItems(page)
            result.collectLatest {
                when(it) {
                    is Resource.Error -> {
                        Log.d("HomeViewModel", "loadMoreItems error")
                        _state.emit(State.Error(it.exception.message.orEmpty()))
                    }
                    Resource.Loading -> {
                        _state.emit(State.Loading)
                    }
                    is Resource.Success -> {
                        Log.d("HomeViewModel", "loadMoreItems success")
                        val list = it.data.data ?: emptyList()
                        _state.emit(State.LoadMoreItemsSuccess(list))
                    }
                }
            }
        }
    }
}