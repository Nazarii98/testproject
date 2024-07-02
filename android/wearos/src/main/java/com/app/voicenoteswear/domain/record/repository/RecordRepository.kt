package com.app.voicenoteswear.domain.record.repository

import com.app.voicenoteswear.data.apimodels.response.EmptyResponse
import com.app.voicenoteswear.data.apimodels.response.RecordingAudioResponse
import com.app.voicenoteswear.data.apimodels.response.RecordingsResponse
import com.app.voicenoteswear.data.apimodels.response.StoreAudioResponse
import com.app.voicenoteswear.data.base.Resource
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Part

interface RecordRepository {
    suspend fun getAllRecordings(): Flow<Resource<RecordingsResponse>>
    suspend fun loadMoreItems(page: Int): Flow<Resource<RecordingsResponse>>
    suspend fun addTitle(recordingId: String): Flow<Resource<EmptyResponse>>
    suspend fun addTranscript(recordingId: String): Flow<Resource<EmptyResponse>>
    suspend fun storeAudio(audio: MultipartBody.Part, duration: RequestBody): Flow<Resource<StoreAudioResponse>>
    suspend fun getRecordingAudio(recordingId: String): Flow<Resource<RecordingAudioResponse>>
}