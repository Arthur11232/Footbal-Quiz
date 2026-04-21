package com.arthuralexandryan.footballquiz.utils

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.ToneGenerator
import com.arthuralexandryan.footballquiz.R

class GameSoundManager(context: Context) {

    private val appContext = context.applicationContext
    private val toneGenerator: ToneGenerator? = try {
        ToneGenerator(AudioManager.STREAM_MUSIC, 80)
    } catch (_: RuntimeException) {
        null
    }

    private val rightAnswerPlayer: MediaPlayer? = createPlayer(R.raw.right_answer)
    private val wrongAnswerPlayer: MediaPlayer? = createPlayer(R.raw.wrong_answer)

    fun playRightAnswer() {
        if (!play(rightAnswerPlayer)) {
            playRightFallback()
        }
    }

    fun playRightAnswer(onComplete: (() -> Unit)?) {
        if (!play(rightAnswerPlayer, onComplete)) {
            playRightFallback()
            onComplete?.invoke()
        }
    }

    fun playWrongAnswer() {
        if (!play(wrongAnswerPlayer)) {
            playWrongFallback()
        }
    }

    fun release() {
        rightAnswerPlayer?.release()
        wrongAnswerPlayer?.release()
        toneGenerator?.release()
    }

    private fun createPlayer(resId: Int): MediaPlayer? {
        return try {
            MediaPlayer.create(appContext, resId)?.apply {
                isLooping = false
            }
        } catch (_: Exception) {
            null
        }
    }

    private fun play(player: MediaPlayer?): Boolean {
        if (player == null) return false
        return try {
            player.setOnCompletionListener(null)
            if (player.isPlaying) {
                player.pause()
            }
            player.seekTo(0)
            player.start()
            true
        } catch (_: Exception) {
            false
        }
    }

    private fun play(player: MediaPlayer?, onComplete: (() -> Unit)?): Boolean {
        if (player == null) return false
        return try {
            player.setOnCompletionListener { onComplete?.invoke() }
            if (player.isPlaying) {
                player.pause()
            }
            player.seekTo(0)
            player.start()
            true
        } catch (_: Exception) {
            false
        }
    }

    private fun playRightFallback() {
        val tone = toneGenerator ?: return
        tone.startTone(ToneGenerator.TONE_PROP_BEEP2, 120)
        tone.startTone(ToneGenerator.TONE_PROP_ACK, 180)
    }

    private fun playWrongFallback() {
        val tone = toneGenerator ?: return
        tone.startTone(ToneGenerator.TONE_PROP_NACK, 180)
        tone.startTone(ToneGenerator.TONE_CDMA_SOFT_ERROR_LITE, 180)
    }
}
