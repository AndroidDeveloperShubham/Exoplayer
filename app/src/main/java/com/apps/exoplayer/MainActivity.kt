package com.apps.exoplayer

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerControlView
import com.google.android.exoplayer2.ui.PlayerView
import java.lang.Math.round

class MainActivity : AppCompatActivity() {
    private var isShowingTrackSelectionDialog = false
    private var trackSelector: DefaultTrackSelector? = null
    var speed = arrayOf("0.25x", "0.5x", "Normal", "1.5x", "2x")
    var sBrightness = 255

    //demo url
    var url1 = "https://5b44cf20b0388.streamlock.net:8443/vod/smil:bbb.smil/playlist.m3u8"
    var playerView: PlayerView? = null
    var simpleExoPlayer: SimpleExoPlayer? = null
    var seekBar: SeekBar? = null
    var seekbarvol: SeekBar? = null
    var tv_per: TextView? = null
    var tv_pervol: TextView? = null
    var audioManager: AudioManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        trackSelector = DefaultTrackSelector(this)
        simpleExoPlayer = SimpleExoPlayer.Builder(this).setTrackSelector(trackSelector!!).build()
        playerView = findViewById(R.id.exoPlayerView)
        playerView!!.setPlayer(simpleExoPlayer)
        val mediaItem = MediaItem.fromUri(url1)
        simpleExoPlayer!!.addMediaItem(mediaItem)
        simpleExoPlayer!!.prepare()
        simpleExoPlayer!!.play()

        val farwordBtn = playerView!!.findViewById<ImageView>(R.id.fwd)
        val rewBtn = playerView!!.findViewById<ImageView>(R.id.rew)
        val setting = playerView!!.findViewById<ImageView>(R.id.exo_track_selection_view)
        val speedBtn = playerView!!.findViewById<ImageView>(R.id.exo_playback_speed)
        val speedTxt = playerView!!.findViewById<TextView>(R.id.speed)
        tv_per = findViewById(R.id.tv_per)
        seekBar = findViewById(R.id.seek_bar)
        seekbarvol = findViewById(R.id.seek_vol)
        tv_pervol = findViewById(R.id.tv_pervol)
        val cBrightness =Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        val k = cBrightness / 2.55
        sBrightness =round(k).toInt()
        tv_per!!.setText("" + sBrightness)
        seekBar!!.setProgress(sBrightness)
        seekBar!!.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            @RequiresApi(api = Build.VERSION_CODES.M)
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                val context = this@MainActivity.applicationContext
                if (Settings.System.canWrite(context)) {
                    val k = progress * 2.55
                    sBrightness =round(k).toInt()
                    changeScreenBrightness(context, sBrightness)
                    tv_per!!.setText(""+ progress)
                    return
                }
                context.startActivity(Intent("android.settings.action.MANAGE_WRITE_SETTINGS"))
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
        audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        seekbarvol!!.setMax(audioManager!!.getStreamMaxVolume(AudioManager.STREAM_MUSIC))
        seekbarvol!!.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, newVolume: Int, b: Boolean) {
                tv_pervol!!.setText("" + newVolume)
                audioManager!!.setStreamVolume(AudioManager.STREAM_MUSIC, newVolume, 0)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
        speedBtn.setOnClickListener { v: View? ->
            val builder = AlertDialog.Builder(this@MainActivity)
            builder.setTitle("Set Speed")
            builder.setItems(speed) { dialog, which -> // the user clicked on colors[which]
                if (which == 0) {
                    speedTxt.visibility = View.VISIBLE
                    speedTxt.text = "0.25X"
                    val param = PlaybackParameters(0.5f)
                    simpleExoPlayer!!.playbackParameters = param
                }
                if (which == 1) {
                    speedTxt.visibility = View.VISIBLE
                    speedTxt.text = "0.5X"
                    val param = PlaybackParameters(0.5f)
                    simpleExoPlayer!!.playbackParameters = param
                }
                if (which == 2) {
                    speedTxt.visibility = View.GONE
                    val param = PlaybackParameters(1f)
                    simpleExoPlayer!!.playbackParameters = param
                }
                if (which == 3) {
                    speedTxt.visibility = View.VISIBLE
                    speedTxt.text = "1.5X"
                    val param = PlaybackParameters(1.5f)
                    simpleExoPlayer!!.playbackParameters = param
                }
                if (which == 4) {
                    speedTxt.visibility = View.VISIBLE
                    speedTxt.text = "2X"
                    val param = PlaybackParameters(2f)
                    simpleExoPlayer!!.playbackParameters = param
                }
            }
            builder.show()
        }
        farwordBtn.setOnClickListener { v: View? ->
            simpleExoPlayer!!.seekTo(
                simpleExoPlayer!!.currentPosition + 10000
            )
        }
        rewBtn.setOnClickListener { v: View? ->
            val num = simpleExoPlayer!!.currentPosition - 10000
            if (num < 0) {
                simpleExoPlayer!!.seekTo(0)
            } else {
                simpleExoPlayer!!.seekTo(simpleExoPlayer!!.currentPosition - 10000)
            }
        }
        val fullscreenButton = playerView!!.findViewById<ImageView>(R.id.fullscreen)
        fullscreenButton.setOnClickListener { view: View? ->
            val orientation = this@MainActivity.resources.configuration.orientation
            requestedOrientation = if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                // code for portrait mode
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            } else {
                // code for landscape mode
                Toast.makeText(this@MainActivity, "Land", Toast.LENGTH_SHORT).show()
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
        }
        findViewById<View>(R.id.exo_play).setOnClickListener { v: View? -> simpleExoPlayer!!.play() }
        findViewById<View>(R.id.exo_pause).setOnClickListener { v: View? -> simpleExoPlayer!!.pause() }
        simpleExoPlayer!!.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                if (state == ExoPlayer.STATE_ENDED) {
                }
            }
        })
        playerView!!.setControllerVisibilityListener(PlayerControlView.VisibilityListener { })
        setting.setOnClickListener {
            if (!isShowingTrackSelectionDialog
                && TrackSelectionDialog.willHaveContent(trackSelector)
            ) {
                isShowingTrackSelectionDialog = true
                val trackSelectionDialog = TrackSelectionDialog.createForTrackSelector(
                    trackSelector
                )  /* onDismissListener= */
                { dismissedDialog: DialogInterface? -> isShowingTrackSelectionDialog = false }
                trackSelectionDialog.show(supportFragmentManager,  /* tag= */null)
            }
        }
    }

    ///////////////////////////////.....................Methods...........................//////////////////////////////
    protected fun releasePlayer() {
        if (simpleExoPlayer != null) {
            simpleExoPlayer!!.release()
            simpleExoPlayer = null
            trackSelector = null
        }
    }

    public override fun onStop() {
        super.onStop()
        releasePlayer()
    }

    private fun changeScreenBrightness(context: Context, screenBrightnessValue: Int) {
        // Change the screen brightness change mode to manual.
        Settings.System.putInt(
            context.contentResolver,
            Settings.System.SCREEN_BRIGHTNESS_MODE,
            Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL
        )
        // Apply the screen brightness value to the system, this will change
        // the value in Settings ---> Display ---> Brightness level.
        // It will also change the screen brightness for the device.
        Settings.System.putInt(
            context.contentResolver,
            Settings.System.SCREEN_BRIGHTNESS, screenBrightnessValue
        )
    }
}