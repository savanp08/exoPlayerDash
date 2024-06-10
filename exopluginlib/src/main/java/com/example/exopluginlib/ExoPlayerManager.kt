package com.example.exopluginlib

import android.content.Context
import android.graphics.SurfaceTexture
import android.view.Surface
import android.view.TextureView
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import java.text.SimpleDateFormat
import java.util.*

class ExoPlayerManager(private val context: Context) {
    private val TAG = "ExoPlayerManager"
    var player: ExoPlayer? = null

    private var playWhenReady = false // Ensure it doesn't automatically play
    private var currentItem = 0
    private var playbackPosition = 0L
    private lateinit var textureView: TextureView

    fun setTextureView(textureView: TextureView) {
        this.textureView = textureView
        textureView.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
            override fun onSurfaceTextureAvailable(surfaceTexture: SurfaceTexture, width: Int, height: Int) {
                log("->>>>> Surface texture available")
                initializePlayer(surfaceTexture)
            }

            override fun onSurfaceTextureSizeChanged(surfaceTexture: SurfaceTexture, width: Int, height: Int) {}

            override fun onSurfaceTextureDestroyed(surfaceTexture: SurfaceTexture): Boolean {
                log("Surface texture destroyed")
                releasePlayer()
                return true
            }

            override fun onSurfaceTextureUpdated(surfaceTexture: SurfaceTexture) {}
        }
    }

    @OptIn(UnstableApi::class)
    fun initializePlayer(surfaceTexture: SurfaceTexture) {
        log("->>>>> Initializing player")
        val trackSelector = DefaultTrackSelector(context).apply {
            setParameters(buildUponParameters().setMaxVideoSizeSd())
        }
        player = ExoPlayer.Builder(context)
            .setTrackSelector(trackSelector)
            .build()
            .also { exoPlayer ->
                val surface = Surface(surfaceTexture)
                exoPlayer.setVideoSurface(surface)

                val mediaItem = MediaItem.Builder()
                    .setUri(context.getString(R.string.media_url_dash))
                    .setMimeType(MimeTypes.APPLICATION_MPD)
                    .build()
                exoPlayer.setMediaItem(mediaItem)
                exoPlayer.playWhenReady = playWhenReady
                exoPlayer.seekTo(currentItem, playbackPosition)
                exoPlayer.addListener(playbackStateListener())
                exoPlayer.prepare()
                log("->>> Player prepared and media item set")
                exoPlayer.playWhenReady = true
            }
    }

    fun releasePlayer() {
        log("->>> Releasing player")
        player?.let { exoPlayer ->
            playbackPosition = exoPlayer.currentPosition
            currentItem = exoPlayer.currentMediaItemIndex
            playWhenReady = exoPlayer.playWhenReady
            exoPlayer.removeListener(playbackStateListener())
            exoPlayer.release()
            log("->>> Player released")
        }
        player = null
    }

    @OptIn(UnstableApi::class)
    fun log(message: String) {
        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        val logMessage = "$timestamp: $message"
        Log.d(TAG, logMessage)
    }

    private fun playbackStateListener() = object : Player.Listener {
        @OptIn(UnstableApi::class)
        override fun onPlaybackStateChanged(playbackState: Int) {
            val stateString: String = when (playbackState) {
                Player.STATE_IDLE -> "ExoPlayer.STATE_IDLE      -"
                Player.STATE_BUFFERING -> "ExoPlayer.STATE_BUFFERING -"
                Player.STATE_READY -> "ExoPlayer.STATE_READY     -"
                Player.STATE_ENDED -> "ExoPlayer.STATE_ENDED     -"
                else -> "UNKNOWN_STATE             -"
            }
            log("Playback state changed to $stateString")
        }

        override fun onPlayerError(error: PlaybackException) {
            log("->>>>> Player error: ${error.message}")
        }
    }

    fun play() {
        player?.playWhenReady = true
        log("->>>>> Play called from App")
    }

    fun pause() {
        player?.playWhenReady = false
        log("->>> Pause called from App")
    }

    fun stop() {
        player?.stop()
        log("->>> Stop called from App")
    }

    companion object {
        private lateinit var instance: ExoPlayerManager

        @JvmStatic
        fun initialize(context: Context, textureView: TextureView) {
            instance = ExoPlayerManager(context)
            instance.setTextureView(textureView)
        }

        @JvmStatic
        fun initializePlayerFromUnity(surfaceTextureId: Int) {
            instance.log("Unity called initializePlayer")
            val surfaceTexture = SurfaceTexture(surfaceTextureId)
            instance.initializePlayer(surfaceTexture)
        }

        @JvmStatic
        fun playFromUnity() {
            instance.player?.playWhenReady = true
            instance.log("Unity called play")
        }

        @JvmStatic
        fun pauseFromUnity() {
            instance.player?.playWhenReady = false
            instance.log("Unity called pause")
        }

        @JvmStatic
        fun stopFromUnity() {
            instance.player?.stop()
            instance.log("Unity called stop")
        }

        @JvmStatic
        fun logMessageFromUnity(message: String) {
            instance.log(message)
        }
    }
}
