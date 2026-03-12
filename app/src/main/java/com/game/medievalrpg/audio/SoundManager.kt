package com.game.medievalrpg.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.SoundPool

class SoundManager(context: Context) {

    private var soundPool: SoundPool? = null
    private val soundMap = mutableMapOf<String, Int>()
    private var mediaPlayer: MediaPlayer? = null
    private var musicVolume: Float = 0.7f
    private var sfxVolume: Float = 1.0f
    var isMusicEnabled: Boolean = true
    var isSfxEnabled: Boolean = true

    init {
        val attrs = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        soundPool = SoundPool.Builder()
            .setMaxStreams(8)
            .setAudioAttributes(attrs)
            .build()
        // In a real project, load sounds from assets:
        // soundMap["attack"] = soundPool?.load(context, R.raw.attack, 1) ?: 0
        // For now we use silent no-ops so the class compiles and runs
    }

    fun playSound(key: String) {
        if (!isSfxEnabled) return
        val soundId = soundMap[key] ?: return
        soundPool?.play(soundId, sfxVolume, sfxVolume, 1, 0, 1f)
    }

    fun playMusic(context: Context, resId: Int) {
        if (!isMusicEnabled) return
        stopMusic()
        mediaPlayer = MediaPlayer.create(context, resId)?.apply {
            isLooping = true
            setVolume(musicVolume, musicVolume)
            start()
        }
    }

    fun stopMusic() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    fun pauseMusic() { mediaPlayer?.pause() }
    fun resumeMusic() { if (isMusicEnabled) mediaPlayer?.start() }

    fun setMusicVolume(vol: Float) {
        musicVolume = vol.coerceIn(0f, 1f)
        mediaPlayer?.setVolume(musicVolume, musicVolume)
    }

    fun setSfxVolume(vol: Float) { sfxVolume = vol.coerceIn(0f, 1f) }

    fun release() {
        stopMusic()
        soundPool?.release()
        soundPool = null
    }

    fun playAttack()   = playSound("attack")
    fun playHurt()     = playSound("hurt")
    fun playDeath()    = playSound("death")
    fun playLevelUp()  = playSound("level_up")
    fun playPickup()   = playSound("pickup")
    fun playSkill()    = playSound("skill")
    fun playQuestComplete() = playSound("quest_complete")
}
