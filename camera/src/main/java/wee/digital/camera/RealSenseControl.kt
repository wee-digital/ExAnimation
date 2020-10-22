package wee.digital.camera

import android.graphics.Bitmap
import android.os.Handler
import android.os.HandlerThread
import com.intel.realsense.librealsense.*

/**
 * Manufacture: Intel(R) RealSense(TM) Depth Camera SR305
 * Product : Intel(R) RealSense(TM) Depth Camera SR305
 * Vendor ID : 32902
 * Product ID: 2888
 */
class RealSenseControl {

    private var colorizer: Colorizer? = null
    private var pipeline: Pipeline? = null
    private var pipelineProfile: PipelineProfile? = null

    private var colorBitmap: Bitmap? = null
    private var depthBitmap: Bitmap? = null

    private var isDestroy = false
    private var isFrameOK = false
    var isPauseCamera = false
    private var isSleep = false
    private var isProcessingFrame = false
    var isStreaming = false

    private var mFrameCount = RealSense.FRAME_MAX_COUNT

    private var mHandlerThread: HandlerThread? = null
    private var mHandler: Handler? = null
    private val streamRunnable: Runnable = object : Runnable {
        override fun run() {
            var isNext = true
            try {
                FrameReleaser().use { fr ->
                    if (isPauseCamera || isProcessingFrame || !RealSense.imagesLiveData.hasObservers()) {
                        RealSense.imagesLiveData.postValue(null)
                        isSleep = true
                    }
                    isProcessingFrame = true
                    val frames: FrameSet = pipeline!!.waitForFrames(RealSense.TIME_WAIT).releaseWith(fr)
                    if (isFrameOK) {
                        mFrameCount--
                        when {
                            mFrameCount > 0 -> {
                                val colorFrame: Frame = frames.first(StreamType.COLOR).releaseWith(fr)
                                val processFrame = frames.applyFilter(colorizer).releaseWith(fr)
                                val depthFrame: Frame = processFrame.first(StreamType.DEPTH).releaseWith(fr)
                                frameProcessing(colorFrame, depthFrame)
                            }
                            mFrameCount < RealSense.FRAME_MAX_SLEEP -> {
                                mFrameCount = RealSense.FRAME_MAX_COUNT
                            }
                        }
                    }
                    isFrameOK = true
                    isSleep = false
                }
            } catch (e: Exception) {
                isNext = false
            } finally {
                isProcessingFrame = false
            }

            if (isNext) {
                mHandler?.postDelayed(this, 60)
            } else {
                isFrameOK = false
                hardwareReset()
            }
        }
    }

    init {
        mHandlerThread = HandlerThread("streaming").also {
            it.start()
            mHandler = Handler(it.looper)
        }
    }

    fun onCreate() {
        colorizer = Colorizer().apply {
            setValue(Option.COLOR_SCHEME, 0f)
        }
        if (isStreaming) return
        try {
            val config = Config().apply {
                enableStream(
                        StreamType.COLOR, 0,
                        RealSense.COLOR_WIDTH, RealSense.COLOR_HEIGHT,
                        StreamFormat.RGB8, RealSense.FRAME_RATE
                )
                enableStream(
                        StreamType.DEPTH, 0,
                        RealSense.DEPTH_WIDTH, RealSense.DEPTH_HEIGHT,
                        StreamFormat.Z16, RealSense.FRAME_RATE
                )
            }
            pipeline = Pipeline()
            pipelineProfile = pipeline?.start(config)?.apply {
                isStreaming = true
                mHandler?.post(streamRunnable)
            }
        } catch (t: Throwable) {
            isStreaming = false
        }
    }

    fun onPause() {
        try {
            isStreaming = false
            isDestroy = true
            mHandlerThread?.quitSafely()
            pipelineProfile?.close()
            pipeline?.stop()
        } catch (t: Throwable) {
        }
    }

    private fun frameProcessing(colorFrame: Frame, depthFrame: Frame) {
        try {
            ByteArray(RealSense.COLOR_SIZE).also {
                colorFrame.getData(it)
                colorBitmap = it.getColorBitmap()
            }
            ByteArray(RealSense.DEPTH_SIZE).also {
                depthFrame.getData(it)
                depthBitmap = it.getDepthBitmap()
            }
            if (colorBitmap != null && depthBitmap != null) {
                RealSense.imagesLiveData.postValue(Pair(colorBitmap!!, depthBitmap!!))
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        } finally {
            isProcessingFrame = false
        }
    }

    fun hasFace() {
        mFrameCount = RealSense.FRAME_MAX_COUNT
    }

    private fun hardwareReset() {
        try {
            pipelineProfile?.device?.hardwareReset()
        } catch (ignore: RuntimeException) {
        }
    }



}