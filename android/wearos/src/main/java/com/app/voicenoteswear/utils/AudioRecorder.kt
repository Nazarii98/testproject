package com.app.voicenoteswear.utils

import java.io.File

interface AudioRecorder {
    fun start(outputFile: File)
    fun resume()
    fun pause()
    fun stop()
}