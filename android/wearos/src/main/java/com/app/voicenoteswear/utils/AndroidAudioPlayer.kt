package com.app.voicenoteswear.utils

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import androidx.core.net.toUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File

class AndroidAudioPlayer(
    private val context: Context
): AudioPlayer {

    var player: MediaPlayer? = null

    override fun playFile(file: File, onComplete: () -> Unit, onPrepared: () -> Unit) {
        GlobalScope.launch(Dispatchers.IO) {
            MediaPlayer.create(context, file.toUri()).apply {
                player = this
                player?.setOnCompletionListener {
                    onComplete.invoke()
                }
                player?.setOnPreparedListener {
                    onPrepared.invoke()
                }
                start()
            }
        }
    }

    override fun play(uri: Uri, onComplete: () -> Unit, onPrepared: () -> Unit) {
        GlobalScope.launch(Dispatchers.IO) {
        MediaPlayer.create(context, uri).apply {
            player = this
            player?.setOnCompletionListener {
                onComplete.invoke()
            }
            player?.setOnPreparedListener {
                onPrepared.invoke()
            }
            start()
        }
            }
    }

    override fun pause() {
        player?.pause()
    }

    override fun stop() {
        player?.stop()
        player?.release()
        player?.setOnCompletionListener(null)
        player = null
    }
}