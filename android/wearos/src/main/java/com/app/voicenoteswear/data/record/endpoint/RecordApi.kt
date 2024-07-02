package com.app.voicenoteswear.data.record.endpoint

import com.app.voicenoteswear.data.apimodels.response.EmptyResponse
import com.app.voicenoteswear.data.apimodels.response.RecordingAudioResponse
import com.app.voicenoteswear.data.apimodels.response.RecordingsResponse
import com.app.voicenoteswear.data.apimodels.response.StoreAudioResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface RecordApi {
    @Multipart
    @POST("api/recordings")
    suspend fun storeAudio(@Part audio: MultipartBody.Part, @Part("duration") duration: RequestBody): StoreAudioResponse

    @PATCH("api/recordings/{recording_id}/transcript")
    suspend fun addTranscript(@Path("recording_id") recordingId: String): EmptyResponse

    @PATCH("api/recordings/{recording_id}/title")
    suspend fun addTitle(@Path("recording_id") recordingId: String): EmptyResponse

    @GET("api/recordings")
    suspend fun getAllRecordings(): RecordingsResponse

    @GET("api/recordings")
    suspend fun loadMoreItems(@Query("page") page: Int): RecordingsResponse
    @GET("/api/recordings/{recording_id}/signed-url")
    suspend fun getRecordingAudio(@Path("recording_id") recordingId: String): RecordingAudioResponse
}