
package com.app.voicenoteswear.utils

import android.net.Uri
import java.io.File

interface AudioPlayer {
    fun playFile(file: File, onComplete: () -> Unit, onPrepared: () -> Unit)
    fun stop()
    fun play(uri: Uri, onComplete: () -> Unit, onPrepared: () -> Unit)
    fun pause()
}
