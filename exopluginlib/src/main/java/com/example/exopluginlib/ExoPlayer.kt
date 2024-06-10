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

class ExoPlayer(private val context: Context) {
    private val TAG = "ExoPlayerManager"
    private var player: ExoPlayer? = null

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
    private fun initializePlayer(surfaceTexture: SurfaceTexture) {
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
    private fun log(message: String) {
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
//    private val TAG = "ExoPlayer"
//    private val playbackStateListener: Player.Listener = playbackStateListener()
//    private var player: ExoPlayer? = null
//
//    private var playWhenReady = false // Ensure it doesn't automatically play
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
//                // Do not initialize the player here to avoid auto-playing the video
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
//    }
//
//    public override fun onResume() {
//        super.onResume()
//        log("onResume called")
//        hideSystemUi()
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
//    @OptIn(UnstableApi::class)
//    fun initializePlayer(surfaceTexture: SurfaceTexture) {
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
//    fun releasePlayer() {
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
//    @OptIn(UnstableApi::class)
//    private fun log(message: String) {
//        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
//        val logMessage = "$timestamp: $message"
//        SendLogTask().execute(logMessage)
//        Log.d(TAG, message)
//    }
//
//    // Public methods to be called from Unity or the app
//    fun initializePlayerFromUnity(surfaceTextureId: Int) {
//        log("Unity called initializePlayer")
//        val surfaceTexture = SurfaceTexture(surfaceTextureId)
//        initializePlayer(surfaceTexture)
//    }
//
//    fun initializePlayerFromApp() {
//        textureView.surfaceTexture?.let {
//            log("App called initializePlayer")
//            initializePlayer(it)
//        } ?: run {
//            log("Surface texture not available for initialization")
//        }
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
//    fun setTextureView(textureView: TextureView) {
//        this.textureView = textureView
//    }
//
//    fun play() {
//        player?.let {
//            it.playWhenReady = true
//            log("Play called from App")
//        }
//    }
//
//    fun pause() {
//        player?.let {
//            it.playWhenReady = false
//            log("Pause called from App")
//        }
//    }
//
//    fun stop() {
//        player?.let {
//            it.stop()
//            log("Stop called from App")
//        }
//    }
//
//    private class SendLogTask : AsyncTask<String, Void, Void>() {
//        @OptIn(UnstableApi::class)
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
//        @OptIn(UnstableApi::class)
//        override fun onPlaybackStateChanged(playbackState: Int) {
//            val stateString: String = when (playbackState) {
//                Player.STATE_IDLE -> "ExoPlayer.STATE_IDLE      -"
//                Player.STATE_BUFFERING -> "ExoPlayer.STATE_BUFFERING -"
//                Player.STATE_READY -> "ExoPlayer.STATE_READY     -"
//                Player.STATE_ENDED -> "ExoPlayer.STATE_ENDED     -"
//                else -> "UNKNOWN_STATE             -"
//            }
//            Log.d(TAG, "changed state to $stateString")
//            log("Playback state changed to $stateString")
//        }
//    }
//}
//
////package com.example.exopluginlib
////
////import android.annotation.SuppressLint
////import android.graphics.SurfaceTexture
////import android.os.AsyncTask
////import android.os.Bundle
////import android.view.Surface
////import android.view.TextureView
////import androidx.annotation.OptIn
////import androidx.appcompat.app.AppCompatActivity
////import androidx.core.view.WindowCompat
////import androidx.core.view.WindowInsetsCompat
////import androidx.core.view.WindowInsetsControllerCompat
////import androidx.media3.common.MediaItem
////import androidx.media3.common.MimeTypes
////import androidx.media3.common.Player
////import androidx.media3.common.util.Log
////import androidx.media3.common.util.UnstableApi
////import androidx.media3.common.util.Util
////import androidx.media3.exoplayer.ExoPlayer
////import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
////import java.io.OutputStreamWriter
////import java.net.HttpURLConnection
////import java.net.URL
////import java.text.SimpleDateFormat
////import java.util.Date
////import java.util.Locale
////
////class ExoPlayer : AppCompatActivity() {
////
////    private val TAG = "MainActivity"
////    private val playbackStateListener: Player.Listener = playbackStateListener()
////    private var player: ExoPlayer? = null
////
////    private var playWhenReady = false // Ensure it doesn't automatically play
////    private var currentItem = 0
////    private var playbackPosition = 0L
////    private lateinit var textureView: TextureView
////
////    public override fun onCreate(savedInstanceState: Bundle?) {
////        super.onCreate(savedInstanceState)
////        setContentView(R.layout.main_activity)
////        textureView = findViewById(R.id.texture_view)
////        supportActionBar?.hide()
////
////        log("onCreate called")
////
////        textureView.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
////            override fun onSurfaceTextureAvailable(
////                surfaceTexture: SurfaceTexture,
////                width: Int,
////                height: Int
////            ) {
////                log("Surface texture available")
////                // Do not initialize the player here to avoid auto-playing the video
////            }
////
////            override fun onSurfaceTextureSizeChanged(
////                surfaceTexture: SurfaceTexture,
////                width: Int,
////                height: Int
////            ) {
////            }
////
////            override fun onSurfaceTextureDestroyed(surfaceTexture: SurfaceTexture): Boolean {
////                log("Surface texture destroyed")
////                releasePlayer()
////                return true
////            }
////
////            override fun onSurfaceTextureUpdated(surfaceTexture: SurfaceTexture) {}
////        }
////    }
////
////    public override fun onStart() {
////        super.onStart()
////        log("onStart called")
////    }
////
////    public override fun onResume() {
////        super.onResume()
////        log("onResume called")
////        hideSystemUi()
////    }
////
////    public override fun onPause() {
////        super.onPause()
////        log("onPause called")
////        releasePlayer()
////    }
////
////    public override fun onStop() {
////        super.onStop()
////        log("onStop called")
////        releasePlayer()
////    }
////
////    @OptIn(UnstableApi::class)
////    private fun initializePlayer(surfaceTexture: SurfaceTexture) {
////        log("Initializing player")
////        val trackSelector = DefaultTrackSelector(this).apply {
////            setParameters(buildUponParameters().setMaxVideoSizeSd())
////        }
////        player = ExoPlayer.Builder(this)
////            .setTrackSelector(trackSelector)
////            .build()
////            .also { exoPlayer ->
////                val surface = Surface(surfaceTexture)
////                exoPlayer.setVideoSurface(surface)
////
////                val mediaItem = MediaItem.Builder()
////                    .setUri(getString(R.string.media_url_dash))
////                    .setMimeType(MimeTypes.APPLICATION_MPD)
////                    .build()
////                exoPlayer.setMediaItem(mediaItem)
////                exoPlayer.playWhenReady = playWhenReady
////                exoPlayer.seekTo(currentItem, playbackPosition)
////                exoPlayer.addListener(playbackStateListener)
////                exoPlayer.prepare()
////                log("Player prepared and media item set")
////            }
////    }
////
////    fun releasePlayer() {
////        log("Releasing player")
////        player?.let { exoPlayer ->
////            playbackPosition = exoPlayer.currentPosition
////            currentItem = exoPlayer.currentMediaItemIndex
////            playWhenReady = exoPlayer.playWhenReady
////            exoPlayer.removeListener(playbackStateListener)
////            exoPlayer.release()
////            log("Player released")
////        }
////        player = null
////    }
////
////    @SuppressLint("InlinedApi")
////    private fun hideSystemUi() {
////        log("Hiding system UI")
////        WindowCompat.setDecorFitsSystemWindows(window, false)
////        WindowInsetsControllerCompat(window, textureView).let { controller ->
////            controller.hide(WindowInsetsCompat.Type.systemBars())
////            controller.systemBarsBehavior =
////                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
////        }
////    }
////
////    @OptIn(UnstableApi::class)
////    private fun log(message: String) {
////        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
////        val logMessage = "$timestamp: $message"
////        SendLogTask().execute(logMessage)
////        Log.d(TAG, message)
////    }
////
////    // Public methods to be called from Unity or the app
////    fun initializePlayerFromUnity(surfaceTextureId: Int) {
////        log("Unity called initializePlayer")
////        val surfaceTexture = SurfaceTexture(surfaceTextureId)
////        initializePlayer(surfaceTexture)
////    }
////
////    fun initializePlayerFromApp() {
////        textureView.surfaceTexture?.let {
////            log("App called initializePlayer")
////            initializePlayer(it)
////        } ?: run {
////            log("Surface texture not available for initialization")
////        }
////    }
////
////    fun playFromUnity() {
////        player?.let {
////            it.playWhenReady = true
////            log("Unity called play")
////        }
////    }
////
////    fun pauseFromUnity() {
////        player?.let {
////            it.playWhenReady = false
////            log("Unity called pause")
////        }
////    }
////
////    fun stopFromUnity() {
////        player?.let {
////            it.stop()
////            log("Unity called stop")
////        }
////    }
////
////    fun logMessageFromUnity(message: String) {
////        log(message)
////    }
////
////    private class SendLogTask : AsyncTask<String, Void, Void>() {
////        @OptIn(UnstableApi::class)
////        override fun doInBackground(vararg params: String?): Void? {
////            val logMessage = params[0]
////            try {
////                val url = URL("https://a360-vr-video-streaming-7540c67a421c.herokuapp.com/logs")
////                val connection = url.openConnection() as HttpURLConnection
////                connection.requestMethod = "POST"
////                connection.doOutput = true
////                connection.setRequestProperty("Content-Type", "application/json; utf-8")
////                connection.setRequestProperty("Accept", "application/json")
////                connection.connectTimeout = 5000
////                connection.readTimeout = 5000
////
////                val jsonInputString = "{\"log\":\"$logMessage\"}"
////
////                connection.outputStream.use { os ->
////                    val input = jsonInputString.toByteArray()
////                    os.write(input, 0, input.size)
////                }
////
////                val responseCode = connection.responseCode
////                if (responseCode == HttpURLConnection.HTTP_OK) {
////                    Log.d("SendLogTask", "Log sent successfully")
////                } else {
////                    Log.d("SendLogTask", "Failed to send log: $responseCode")
////                }
////            } catch (e: Exception) {
////                Log.e("SendLogTask", "Exception in sending log", e)
////            }
////            return null
////        }
////    }
////
////    private fun playbackStateListener() = object : Player.Listener {
////        @OptIn(UnstableApi::class)
////        override fun onPlaybackStateChanged(playbackState: Int) {
////            val stateString: String = when (playbackState) {
////                ExoPlayer.STATE_IDLE -> "ExoPlayer.STATE_IDLE      -"
////                ExoPlayer.STATE_BUFFERING -> "ExoPlayer.STATE_BUFFERING -"
////                ExoPlayer.STATE_READY -> "ExoPlayer.STATE_READY     -"
////                ExoPlayer.STATE_ENDED -> "ExoPlayer.STATE_ENDED     -"
////                else -> "UNKNOWN_STATE             -"
////            }
////            Log.d("MainActivity", "changed state to $stateString")
////            log("Playback state changed to $stateString")
////        }
////    }
////}
