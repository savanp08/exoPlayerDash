package com.example.exoplugin

import android.graphics.SurfaceTexture
import android.os.Bundle
import android.view.Surface
import android.view.TextureView
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import java.text.SimpleDateFormat
import com.example.exopluginlib.R as libR
import com.example.exopluginlib.ExoPlayer as LibraryExoPlayer

import java.util.*
import android.annotation.SuppressLint
import android.os.AsyncTask

import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

import java.util.Date
import java.util.Locale

import com.example.exopluginlib.ExoPlayerManager


class MainActivity : AppCompatActivity() {
    private lateinit var exoPlayerManager: ExoPlayerManager
    private lateinit var textureView: TextureView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity) // Ensure this layout contains a TextureView with id `texture_view`

        textureView = findViewById(R.id.texture_view)
        exoPlayerManager = ExoPlayerManager(this)
        exoPlayerManager.setTextureView(textureView)
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        exoPlayerManager.pause()
    }

    override fun onStop() {
        super.onStop()
        exoPlayerManager.stop()
    }
}



//package com.example.exopluginlib
//
//import android.graphics.SurfaceTexture
//import android.os.Bundle
//import android.view.Surface
//import android.view.TextureView
//import androidx.annotation.OptIn
//import androidx.appcompat.app.AppCompatActivity
//import androidx.media3.common.MediaItem
//import androidx.media3.common.MimeTypes
//import androidx.media3.common.Player
//import androidx.media3.common.util.UnstableApi
//import androidx.media3.exoplayer.ExoPlayer
//import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
//import java.text.SimpleDateFormat
//import java.util.*
//import android.annotation.SuppressLint
//import android.os.AsyncTask
//
//import androidx.core.view.WindowCompat
//import androidx.core.view.WindowInsetsCompat
//import androidx.core.view.WindowInsetsControllerCompat
//
//import java.io.OutputStreamWriter
//import java.net.HttpURLConnection
//import java.net.URL
//
//import java.util.Date
//import java.util.Locale
//
//class ExoPlayer : AppCompatActivity() {
//
//    private val TAG = "ExoPlayer"
//    private val playbackStateListener: Player.Listener = playbackStateListener()
//    private var player: ExoPlayer? = null
//
//    private var playWhenReady = true
//    private var currentItem = 0
//    private var playbackPosition = 0L
//    private lateinit var textureView: TextureView
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.main_activity)
//        textureView = findViewById(R.id.texture_view)
//        supportActionBar?.hide()
//
//        log("onCreate called")
//
//        textureView.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
//            override fun onSurfaceTextureAvailable(
//                surfaceTexture: SurfaceTexture,
//                width: Int,
//                height: Int
//            ) {
//                log("Surface texture available")
//                initializePlayer(surfaceTexture)
//            }
//
//            override fun onSurfaceTextureSizeChanged(
//                surfaceTexture: SurfaceTexture,
//                width: Int,
//                height: Int
//            ) {}
//
//            override fun onSurfaceTextureDestroyed(surfaceTexture: SurfaceTexture): Boolean {
//                log("Surface texture destroyed")
//                releasePlayer()
//                return true
//            }
//
//            override fun onSurfaceTextureUpdated(surfaceTexture: SurfaceTexture) {}
//        }
//    }
//
//    public override fun onStart() {
//        super.onStart()
//        log("onStart called")
//        initializePlayerIfReady()
//    }
//
//    public override fun onResume() {
//        super.onResume()
//        log("onResume called")
//        hideSystemUi()
//        if (player == null) {
//            initializePlayerIfReady()
//        }
//    }
//
//    public override fun onPause() {
//        super.onPause()
//        log("onPause called")
//        releasePlayer()
//    }
//
//    public override fun onStop() {
//        super.onStop()
//        log("onStop called")
//        releasePlayer()
//    }
//
//    private fun initializePlayerIfReady() {
//        textureView.surfaceTexture?.let {
//            log("Initializing player as surface texture is ready")
//            initializePlayer(it)
//        }
//    }
//
//    @OptIn(UnstableApi::class)
//    private fun initializePlayer(surfaceTexture: SurfaceTexture) {
//        log("Initializing player")
//        val trackSelector = DefaultTrackSelector(this).apply {
//            setParameters(buildUponParameters().setMaxVideoSizeSd())
//        }
//        player = ExoPlayer.Builder(this)
//            .setTrackSelector(trackSelector)
//            .build()
//            .also { exoPlayer ->
//                val surface = Surface(surfaceTexture)
//                exoPlayer.setVideoSurface(surface)
//
//                val mediaItem = MediaItem.Builder()
//                    .setUri(getString(R.string.media_url_dash))
//                    .setMimeType(MimeTypes.APPLICATION_MPD)
//                    .build()
//                exoPlayer.setMediaItem(mediaItem)
//                exoPlayer.playWhenReady = playWhenReady
//                exoPlayer.seekTo(currentItem, playbackPosition)
//                exoPlayer.addListener(playbackStateListener)
//                exoPlayer.prepare()
//                log("Player prepared and media item set")
//            }
//    }
//
//    private fun releasePlayer() {
//        log("Releasing player")
//        player?.let { exoPlayer ->
//            playbackPosition = exoPlayer.currentPosition
//            currentItem = exoPlayer.currentMediaItemIndex
//            playWhenReady = exoPlayer.playWhenReady
//            exoPlayer.removeListener(playbackStateListener)
//            exoPlayer.release()
//            log("Player released")
//        }
//        player = null
//    }
//
//    private fun hideSystemUi() {
//        log("Hiding system UI")
//        WindowCompat.setDecorFitsSystemWindows(window, false)
//        WindowInsetsControllerCompat(window, textureView).let { controller ->
//            controller.hide(WindowInsetsCompat.Type.systemBars())
//            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
//        }
//    }
//
//    private fun log(message: String) {
//        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
//        val logMessage = "$timestamp: $message"
//        // Send the log message to a server or log it locally
//        android.util.Log.d(TAG, message)
//    }
//
//    fun initializePlayerFromUnity(surfaceTextureId: Int) {
//        log("Unity called initializePlayer")
//        val surfaceTexture = SurfaceTexture(surfaceTextureId)
//        initializePlayer(surfaceTexture)
//    }
//
//    fun playFromUnity() {
//        player?.playWhenReady = true
//        log("Unity called play")
//    }
//
//    fun pauseFromUnity() {
//        player?.playWhenReady = false
//        log("Unity called pause")
//    }
//
//    fun stopFromUnity() {
//        player?.stop()
//        log("Unity called stop")
//    }
//
//    fun logMessageFromUnity(message: String) {
//        log(message)
//    }
//
//    private fun playbackStateListener() = object : Player.Listener {
//        override fun onPlaybackStateChanged(playbackState: Int) {
//            val stateString: String = when (playbackState) {
//                Player.STATE_IDLE -> "ExoPlayer.STATE_IDLE -"
//                Player.STATE_BUFFERING -> "ExoPlayer.STATE_BUFFERING -"
//                Player.STATE_READY -> "ExoPlayer.STATE_READY -"
//                Player.STATE_ENDED -> "ExoPlayer.STATE_ENDED -"
//                else -> "UNKNOWN_STATE -"
//            }
//            android.util.Log.d(TAG, "changed state to $stateString")
//            log("Playback state changed to $stateString")
//        }
//    }
//}

//package com.example.exopluginlib
//
//import android.annotation.SuppressLint
//import android.graphics.SurfaceTexture
//import android.os.AsyncTask
//import android.os.Bundle
//import android.view.Surface
//import android.view.TextureView
//import androidx.annotation.OptIn
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.view.WindowCompat
//import androidx.core.view.WindowInsetsCompat
//import androidx.core.view.WindowInsetsControllerCompat
//import androidx.media3.common.MediaItem
//import androidx.media3.common.MimeTypes
//import androidx.media3.common.Player
//import androidx.media3.common.util.Log
//import androidx.media3.common.util.UnstableApi
//import androidx.media3.common.util.Util
//import androidx.media3.exoplayer.ExoPlayer
//import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
//import java.io.OutputStreamWriter
//import java.net.HttpURLConnection
//import java.net.URL
//import java.text.SimpleDateFormat
//import java.util.Date
//import java.util.Locale
//
//class ExoPlayer : AppCompatActivity() {
//
//    private val TAG = "MainActivity"
//    private val playbackStateListener: Player.Listener = playbackStateListener()
//    private var player: ExoPlayer? = null
//
//    private var playWhenReady = true
//    private var currentItem = 0
//    private var playbackPosition = 0L
//    private lateinit var textureView: TextureView
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.main_activity)
//        textureView = findViewById(R.id.texture_view)
//        getSupportActionBar()?.hide()
//
//        log("onCreate called")
//
//        textureView.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
//            override fun onSurfaceTextureAvailable(
//                surfaceTexture: SurfaceTexture,
//                width: Int,
//                height: Int
//            ) {
//                log("Surface texture available")
//                initializePlayer(surfaceTexture)
//            }
//
//            override fun onSurfaceTextureSizeChanged(
//                surfaceTexture: SurfaceTexture,
//                width: Int,
//                height: Int
//            ) {
//            }
//
//            override fun onSurfaceTextureDestroyed(surfaceTexture: SurfaceTexture): Boolean {
//                log("Surface texture destroyed")
//                releasePlayer()
//                return true
//            }
//
//            override fun onSurfaceTextureUpdated(surfaceTexture: SurfaceTexture) {}
//        }
//    }
//
//    public override fun onStart() {
//        super.onStart()
//        log("onStart called")
//
//            initializePlayerIfReady()
//
//    }
//
//    public override fun onResume() {
//        super.onResume()
//        log("onResume called")
//        hideSystemUi()
//        if (player == null) {
//            initializePlayerIfReady()
//        }
//    }
//
//    public override fun onPause() {
//        super.onPause()
//        log("onPause called")
//
//            releasePlayer()
//
//    }
//
//    public override fun onStop() {
//        super.onStop()
//        log("onStop called")
//
//            releasePlayer()
//
//    }
//
//    private fun initializePlayerIfReady() {
//        textureView.surfaceTexture?.let {
//            log("Initializing player as surface texture is ready")
//            initializePlayer(it)
//        }
//    }
//
//    @OptIn(UnstableApi::class)
//    private fun initializePlayer(surfaceTexture: SurfaceTexture) {
//        log("Initializing player")
//        val trackSelector = DefaultTrackSelector(this).apply {
//            setParameters(buildUponParameters().setMaxVideoSizeSd())
//        }
//        player = ExoPlayer.Builder(this)
//            .setTrackSelector(trackSelector)
//            .build()
//            .also { exoPlayer ->
//                val surface = Surface(surfaceTexture)
//                exoPlayer.setVideoSurface(surface)
//
//                val mediaItem = MediaItem.Builder()
//                    .setUri(getString(R.string.media_url_dash))
//                    .setMimeType(MimeTypes.APPLICATION_MPD)
//                    .build()
//                exoPlayer.setMediaItem(mediaItem)
//                exoPlayer.playWhenReady = playWhenReady
//                exoPlayer.seekTo(currentItem, playbackPosition)
//                exoPlayer.addListener(playbackStateListener)
//                exoPlayer.prepare()
//                log("Player prepared and media item set")
//            }
//    }
//
//    private fun releasePlayer() {
//        log("Releasing player")
//        player?.let { exoPlayer ->
//            playbackPosition = exoPlayer.currentPosition
//            currentItem = exoPlayer.currentMediaItemIndex
//            playWhenReady = exoPlayer.playWhenReady
//            exoPlayer.removeListener(playbackStateListener)
//            exoPlayer.release()
//            log("Player released")
//        }
//        player = null
//    }
//
//    @SuppressLint("InlinedApi")
//    private fun hideSystemUi() {
//        log("Hiding system UI")
//        WindowCompat.setDecorFitsSystemWindows(window, false)
//        WindowInsetsControllerCompat(window, textureView).let { controller ->
//            controller.hide(WindowInsetsCompat.Type.systemBars())
//            controller.systemBarsBehavior =
//                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
//        }
//    }
//
//    private fun log(message: String) {
//        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
//        val logMessage = "$timestamp: $message"
//        SendLogTask().execute(logMessage)
//        Log.d(TAG, message)
//    }
//
//    // Public methods to be called from Unity
//    fun initializePlayerFromUnity(surfaceTextureId: Int) {
//        log("Unity called initializePlayer")
//        val surfaceTexture = SurfaceTexture(surfaceTextureId)
//        initializePlayer(surfaceTexture)
//    }
//
//    fun playFromUnity() {
//        player?.let {
//            it.playWhenReady = true
//            log("Unity called play")
//        }
//    }
//
//    fun pauseFromUnity() {
//        player?.let {
//            it.playWhenReady = false
//            log("Unity called pause")
//        }
//    }
//
//    fun stopFromUnity() {
//        player?.let {
//            it.stop()
//            log("Unity called stop")
//        }
//    }
//
//    fun logMessageFromUnity(message: String) {
//        log(message)
//    }
//
//    private class SendLogTask : AsyncTask<String, Void, Void>() {
//        override fun doInBackground(vararg params: String?): Void? {
//            val logMessage = params[0]
//            try {
//                val url = URL("https://a360-vr-video-streaming-7540c67a421c.herokuapp.com/logs")
//                val connection = url.openConnection() as HttpURLConnection
//                connection.requestMethod = "POST"
//                connection.doOutput = true
//                connection.setRequestProperty("Content-Type", "application/json; utf-8")
//                connection.setRequestProperty("Accept", "application/json")
//                connection.connectTimeout = 5000
//                connection.readTimeout = 5000
//
//                val jsonInputString = "{\"log\":\"$logMessage\"}"
//
//                connection.outputStream.use { os ->
//                    val input = jsonInputString.toByteArray()
//                    os.write(input, 0, input.size)
//                }
//
//                val responseCode = connection.responseCode
//                if (responseCode == HttpURLConnection.HTTP_OK) {
//                    Log.d("SendLogTask", "Log sent successfully")
//                } else {
//                    Log.d("SendLogTask", "Failed to send log: $responseCode")
//                }
//            } catch (e: Exception) {
//                Log.e("SendLogTask", "Exception in sending log", e)
//            }
//            return null
//        }
//    }
//
//    private fun playbackStateListener() = object : Player.Listener {
//        override fun onPlaybackStateChanged(playbackState: Int) {
//            val stateString: String = when (playbackState) {
//                ExoPlayer.STATE_IDLE -> "ExoPlayer.STATE_IDLE      -"
//                ExoPlayer.STATE_BUFFERING -> "ExoPlayer.STATE_BUFFERING -"
//                ExoPlayer.STATE_READY -> "ExoPlayer.STATE_READY     -"
//                ExoPlayer.STATE_ENDED -> "ExoPlayer.STATE_ENDED     -"
//                else -> "UNKNOWN_STATE             -"
//            }
//            Log.d("MainActivity", "changed state to $stateString")
//            log("Playback state changed to $stateString")
//        }
//    }
//}