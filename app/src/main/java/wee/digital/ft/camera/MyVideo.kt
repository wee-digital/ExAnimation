package wee.digital.ft.camera

import android.content.Context
import android.graphics.Bitmap
import android.os.Handler
import android.os.HandlerThread
import com.homesoft.encoder.FrameBuilder
import com.homesoft.encoder.MuxerConfig
import wee.digital.library.extension.post
import java.io.File

class MyVideo(context: Context) {

    private var ct: Context = context

    private var handlerVideo: Handler? = null

    private var handlerThreadVideo: HandlerThread? = null

    private var file: File? = null

    private var config: MuxerConfig? = null

    private var frameBuilder: FrameBuilder? = null

    private var onDoneVideo = false

    fun startVideo() {
        handlerThreadVideo = HandlerThread("MyVideo")
        handlerThreadVideo?.start()
        handlerVideo = Handler(handlerThreadVideo?.looper!!)

        file = File("${ct.externalCacheDir}/${System.currentTimeMillis()}.mp4")

        config = MuxerConfig(
                file = file!!,
                framesPerSecond = 30f,
                videoWidth = 640,
                videoHeight = 480
        )

        frameBuilder = FrameBuilder(ct, config!!, null)
        frameBuilder?.start()

        onDoneVideo = false
    }

    fun pushFrame(image: Bitmap) {
        handlerVideo?.post {
            if (onDoneVideo) return@post
            frameBuilder?.createFrame(image)
        }
    }

    fun onDoneVideo(listener: MyVideoCallBack) {
        onDoneVideo = true
        frameBuilder ?: return
        post(300) {
            frameBuilder?.releaseVideoCodec()
            frameBuilder?.releaseAudioExtractor()
            frameBuilder?.releaseMuxer()
            handlerThreadVideo?.quitSafely()
            frameBuilder = null
            handlerThreadVideo = null
            handlerVideo = null
            listener.onResult("$file")
        }
    }

    interface MyVideoCallBack {
        fun onResult(path: String)
    }

}