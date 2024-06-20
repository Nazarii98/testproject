package com.app.voicenoteswear.data.apimodels.response

import com.google.gson.annotations.SerializedName

data class RecordingAudioResponse(
    val url: String,
    @SerializedName("expiry_time")
    val expiryTime: String
)
