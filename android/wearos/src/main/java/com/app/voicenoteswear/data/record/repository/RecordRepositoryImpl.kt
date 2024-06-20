package com.app.voicenoteswear.data.record.repository

import com.app.voicenoteswear.data.apimodels.response.EmptyResponse
import com.app.voicenoteswear.data.apimodels.response.RecordingAudioResponse
import com.app.voicenoteswear.data.apimodels.response.RecordingsResponse
import com.app.voicenoteswear.data.apimodels.response.StoreAudioResponse
import com.app.voicenoteswear.data.base.BaseRepository
import com.app.voicenoteswear.data.base.Resource
import com.app.voicenoteswear.data.record.endpoint.RecordApi
import com.app.voicenoteswear.domain.record.repository.RecordRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class RecordRepositoryImpl @Inject constructor(
    private val api: RecordApi
) : BaseRepository(), RecordRepository {
    override suspend fun getAllRecordings(): Flow<Resource<RecordingsResponse>> {
        return flow<Resource<RecordingsResponse>> {
            val result = api.getAllRecordings()
            emit(Resource.Success(result))
        }.catch {
            emit(Resource.Error((it as Exception)))
        }
    }

    override suspend fun loadMoreItems(page: Int): Flow<Resource<RecordingsResponse>> {
        return flow<Resource<RecordingsResponse>> {
            val result = api.loadMoreItems(page)
            emit(Resource.Success(result))
        }.catch {
            emit(Resource.Error((it as Exception)))
        }
    }

    override suspend fun addTitle(recordingId: String): Flow<Resource<EmptyResponse>> {
        return flow<Resource<EmptyResponse>> {
            val result = api.addTitle(recordingId)
            emit(Resource.Success(result))
        }.catch {
            emit(Resource.Error((it as Exception)))
        }
    }

    override suspend fun addTranscript(recordingId: String): Flow<Resource<EmptyResponse>> {
        return flow<Resource<EmptyResponse>> {
            val result = api.addTranscript(recordingId)
            emit(Resource.Success(result))
        }.catch {
            emit(Resource.Error((it as Exception)))
        }
    }

    override suspend fun storeAudio(
        audio: MultipartBody.Part,
        duration: RequestBody
    ): Flow<Resource<StoreAudioResponse>> {
        return flow<Resource<StoreAudioResponse>> {
            val result = api.storeAudio(audio, duration)
            emit(Resource.Success(result))
        }.catch {
            emit(Resource.Error((it as Exception)))
        }
    }

    override suspend fun getRecordingAudio(recordingId: String): Flow<Resource<RecordingAudioResponse>> {
        return flow<Resource<RecordingAudioResponse>> {
            val result = api.getRecordingAudio(recordingId)
            emit(Resource.Success(result))
        }.catch {
            emit(Resource.Error((it as Exception)))
        }
    }

}