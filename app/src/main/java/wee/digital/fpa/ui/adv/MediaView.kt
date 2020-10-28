package wee.digital.fpa.ui.adv

import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.util.Log
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import kotlinx.android.synthetic.main.view_video.view.*
import wee.digital.fpa.R

class MediaView : ConstraintLayout, Player.EventListener, MyMediaController {

    constructor(context: Context, attrs: AttributeSet? = null) : super(context, attrs)

    var videoUri: String? = null

    private var simpleExoplayer: SimpleExoPlayer? = null
    private var videoPlayListener: MyMediaPlayListener? = null
    private val dataSourceFactory: DataSource.Factory by lazy {
        DefaultDataSourceFactory(context, "exoplayer-simple")
    }

    init {
        inflate(context, R.layout.view_video, this)
    }

    private fun log(s: Any?) {
        Log.d(this::class.simpleName, s.toString())
    }

    fun setVideoListener(listener: MyMediaPlayListener) {
        videoPlayListener = listener
    }

    private fun buildMediaSource(videoUrl: String): MediaSource {
        return ProgressiveMediaSource.Factory(
                dataSourceFactory
        ).createMediaSource(
                MediaItem.fromUri(Uri.parse(videoUrl))
        )
    }

    override fun onPlayerError(error: ExoPlaybackException) {
        super.onPlayerError(error)
        log(error.message)
    }

    override fun onPlaybackStateChanged(state: Int) {
        when (state) {
            Player.STATE_IDLE -> {
                //log("Player.STATE_IDLE")
            }
            Player.STATE_BUFFERING -> {
                //log("STATE_BUFFERING")
            }
            Player.STATE_READY -> {
                //log("Player.STATE_READY")
                videoPlayListener?.onVideoStarted()
            }
            Player.STATE_ENDED -> {
                //log("Player.STATE_ENDED")
                videoPlayListener?.onVideoStopped()
            }
        }
    }

    /**
     * [] implement
     */
    override fun onPlayVideo(position: Int) {
        if (simpleExoplayer == null) {
            simpleExoplayer = SimpleExoPlayer.Builder(context).build().also {
                val mediaSource = buildMediaSource(videoUri!!)
                it.setMediaSource(mediaSource, true)
                exoPlayerView.player = it
                it.seekTo(1)
                it.playWhenReady = true
                it.addListener(this)
                it.prepare()
                log("onPlayVideo")
            }
        }
    }

    override fun onStopVideo(position: Int) {
        log("stop video at $position")
        if (simpleExoplayer != null) {
            simpleExoplayer?.also {
                it.removeListener(this)
                it.stop()
                it.release()
                simpleExoplayer = null
                log("onStopVideo")
            }
        }
    }

}

interface MyMediaController {
    fun onPlayVideo(position: Int = -1)
    fun onStopVideo(position: Int = -1)
}

interface MyMediaPlayListener {
    fun onVideoStarted() {}
    fun onVideoStopped() {}
}