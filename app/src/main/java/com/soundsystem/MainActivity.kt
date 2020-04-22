package com.soundsystem

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.audio.AudioProcessor
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory

class MainActivity : AppCompatActivity() {

    private var player: ExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initPlayer()
    }

    private val fftAudioProcessor = FFTAudioProcessor()

    private fun initPlayer() {
        // We need to create a renderers factory to inject our own audio processor at the end of the list
        val renderersFactory = object : DefaultRenderersFactory(this) {
            override fun buildAudioProcessors(): Array<AudioProcessor> {
                val processors = super.buildAudioProcessors()
                return processors + fftAudioProcessor
            }
        }
        player = ExoPlayerFactory.newSimpleInstance(this, renderersFactory, DefaultTrackSelector())

        val visualizer = findViewById<ExoVisualizer>(R.id.visualizer)
        visualizer.processor = fftAudioProcessor

        val uri = Uri.parse("https://connorsermons.s3-eu-west-1.amazonaws.com/2020/19-04-20am.mp3")

        val mediaSource = ProgressiveMediaSource.Factory(
            DefaultDataSourceFactory(this, "ExoVisualizer")
        ).createMediaSource(uri)
        player?.playWhenReady = true
        player?.prepare(mediaSource, true, true)
    }

    override fun onResume() {
        super.onResume()
        player?.playWhenReady = true
    }

    override fun onPause() {
        super.onPause()
        player?.playWhenReady = false
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.stop()
        player?.release()
    }
}
