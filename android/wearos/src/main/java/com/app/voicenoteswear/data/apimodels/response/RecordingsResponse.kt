package com.app.voicenoteswear.data.apimodels.response

import com.google.gson.annotations.SerializedName

data class RecordingsResponse(
    val data: List<Recording>? = null,
    val links: Links? = null
)

data class Recording(
    val id: String,
    @SerializedName("recording_id")
    val recordingId: String,
    @SerializedName("title")
    val title: String?,
    @SerializedName("transcript")
    val transcript: String?,
    @SerializedName("duration")
    val duration: Long,
    @SerializedName("created_at")
    val createdAt: String,
) {
    var status: RecordingStatus = RecordingStatus.NORMAL
}

enum class RecordingStatus{
    NORMAL, SYNCED, UPLOADING, TRANSCRIPTING
}

data class Links(
    val next: String? = null
)
