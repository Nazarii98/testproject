package com.app.voicenoteswear.presentation.flow.shared

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.app.voicenoteswear.data.apimodels.response.Recording
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
@HiltViewModel
class NoteSharedViewModel @Inject constructor() : ViewModel() {
    private val _note = mutableStateOf<Recording?>(null)
    val note: State<Recording?> = _note

    fun updateNoteData(newData: Recording) {
        _note.value = newData
    }
}