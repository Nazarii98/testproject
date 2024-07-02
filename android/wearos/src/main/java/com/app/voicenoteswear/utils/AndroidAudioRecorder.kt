
package com.app.voicenoteswear.utils

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import java.io.File
import java.io.FileOutputStream

class AndroidAudioRecorder(
    private val context: Context
): AudioRecorder {

    var recorder: MediaRecorder? = null
    var isRecording = false
    var audioFile: File? = null
    private var listener: AudioRecorderListener? = null

    fun setAudioRecorderListener(listener: AudioRecorderListener?) {
        this.listener = listener
    }
    private fun createRecorder(): MediaRecorder {
        return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else MediaRecorder()
    }

    override fun start(outputFile: File) {
        createRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(FileOutputStream(outputFile).fd)

            prepare()
            start()

            recorder = this
            isRecording = true
            startAmplitudeUpdates()
        }
    }

    override fun resume() {
        recorder?.resume()
        isRecording = true
    }

    override fun pause() {
        isRecording = false
        recorder?.pause()
    }

    override fun stop() {
        isRecording = false
        recorder?.stop()
        recorder?.reset()
        recorder = null
    }

    fun getAmplitude(): Int {
        return if (isRecording) {
            recorder?.maxAmplitude ?: 0
        } else 0
    }

    private fun startAmplitudeUpdates() {
        val handler = Handler(Looper.getMainLooper())
        handler.post(object : Runnable {
            override fun run() {
                if (isRecording) {
                    val maxAmplitude = recorder?.maxAmplitude ?: 0
                    listener?.onAmplitudeChanged(maxAmplitude)
                    // Update UI with maxAmplitude
                    Log.d("VoiceAmplitude1", "Max Amplitude: $maxAmplitude")
                    handler.postDelayed(this, 50) // Update every 100ms
                }
            }
        })
    }
}
