package com.app.voicenoteswear.presentation.flow.main.note_detail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.voicenoteswear.data.apimodels.response.RecordingAudioResponse
import com.app.voicenoteswear.data.base.Resource
import com.app.voicenoteswear.data.database.FilePath
import com.app.voicenoteswear.data.database.FilePathDao
import com.app.voicenoteswear.data.datastorage.UserDataStorage
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
import java.io.File
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class NoteDetailViewModel @Inject constructor(
    private val recordRepository: RecordRepository,
    private val userDataStorage: UserDataStorage,
    private val filePathDao: FilePathDao
) : ViewModel() {
    private val _state = MutableSharedFlow<State>()
    val state: SharedFlow<State> = _state

    private val _elapsedTime = MutableStateFlow(0L)
    private val _timerState = MutableStateFlow(TimerState.RESET)
    val timerState = _timerState.asStateFlow()

    var audioFile: File? = null

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

    private val _elapsedTime2 = MutableStateFlow(0L)
    private val _timerState2 = MutableStateFlow(TimerState.RESET)
    val timerState2 = _timerState2.asStateFlow()

    val stopWatchText2 = _elapsedTime2
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
        class FetchAudioSuccess(val audio: RecordingAudioResponse) : State
        class StoreAudioSuccess(val recordingId: String) : State
        object AddTitleSuccess : State
        object AddTransciptSuccess : State
        class Error(val message: String) : State
    }

    init {
        Log.d("NoteDetailViewModel", "Init")

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

        _timerState2
            .flatMapLatest { timerState ->
                getTimerFlow(
                    isRunning = timerState == TimerState.RUNNING
                )
            }
            .onEach { timeDiff ->
                _elapsedTime2.update { it + timeDiff }
            }
            .launchIn(viewModelScope)
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

    fun toggleIsRunning2() {
        when(timerState2.value) {
            TimerState.RUNNING -> _timerState2.update { TimerState.PAUSED }
            TimerState.PAUSED,
            TimerState.RESET -> _timerState2.update { TimerState.RUNNING }
        }
    }

    fun resetTimer2() {
        _timerState2.update { TimerState.RESET }
        _elapsedTime2.update { 0L }
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

    fun getRecordingAudio(recordingId: String) {
        viewModelScope.launch {
            _state.emit(State.Loading)
            val result = recordRepository.getRecordingAudio(recordingId)
            result.collectLatest {
                when(it) {
                    is Resource.Error -> {
                        _state.emit(State.Error(it.exception.message.orEmpty()))
                    }
                    Resource.Loading -> {
                        _state.emit(State.Loading)
                    }
                    is Resource.Success -> {
                        val data = it.data
                        _state.emit(State.FetchAudioSuccess(data))
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
}