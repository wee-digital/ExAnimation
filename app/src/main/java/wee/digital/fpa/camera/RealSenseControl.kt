package wee.digital.fpa.camera

import android.graphics.Bitmap
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import com.intel.realsense.librealsense.*
import wee.digital.fpa.repository.model.DataCollect

class RealSenseControl : DeviceListener {

    companion object {
        const val TAG = "RealSenseControl"
        val ins: RealSenseControl by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { RealSenseControl() }
        const val COLOR_WIDTH = 640
        const val COLOR_HEIGHT = 480
        const val DEPTH_WIDTH = 640
        const val DEPTH_HEIGHT = 480
        const val TIME_WAIT = 1000
        const val FRAME_RATE = 30
        const val FRAME_MAX_COUNT = 900 // Run 30s
        const val FRAME_MAX_SLEEP = -60 // Sleep 2s
        var DEPTH_UNIT = 0f
        var DEVICE_CONFIG = DeviceConfig()
    }

    var listener: Listener? = null
    var startListener: StartListener? = null
    private var mRSContext: RsContext? = null
    private var mPipeline: Pipeline? = null
    private var mPipelineProfile: PipelineProfile? = null
    private var isFrameOK = false
    private var mHandlerThread: HandlerThread? = null
    private var mHandler: Handler? = null
    private var mIsStreaming = false
    private var mDevice: Device? = null
    private var mFrameCount = FRAME_MAX_COUNT
    var isPauseCamera = false
    private var isSleep = false
    private var isCheck = false
    private var isFrameProcessing = false
    var colorConfig = ColorConfig()
    var deviceConfig = DeviceConfig()
    private var mColorSensor: ColorSensor? = null
    private val mAlign = Align(StreamType.COLOR)
    private val mColorizerOrg = Colorizer().apply {
        /*0 - Jet
        1 - Classic
        2 - WhiteToBlack
        3 - BlackToWhite
        4 - Bio
        5 - Cold
        6 - Warm
        7 - Quantized
        8 - Pattern*/
        setValue(Option.COLOR_SCHEME, 0f)
    }
    val config = Config().apply {
        enableStream(
            StreamType.COLOR,
            0,
            COLOR_WIDTH,
            COLOR_HEIGHT,
            StreamFormat.RGB8,
            FRAME_RATE
        )
        enableStream(
            StreamType.DEPTH,
            0,
            DEPTH_WIDTH,
            DEPTH_HEIGHT,
            StreamFormat.Z16,
            FRAME_RATE
        )
    }
    private var isStopCamera = false
    var isInternet = true
    var isGetDepthFrame = false

    private val mStreaming: Runnable = object : Runnable {
        @Throws
        @Synchronized
        override fun run() {
            var errorMessage = ""
            val isNext = try {
                try {
                    if (/*listener != null && !isPauseCamera && */!isFrameProcessing && isInternet) {
                        isFrameProcessing = true
                        FrameReleaser().use { fr ->
                            val frames: FrameSet =
                                mPipeline!!.waitForFrames(TIME_WAIT).releaseWith(fr)
                            isSleep = if (isFrameOK) {
                                mFrameCount--
                                when {
                                    mFrameCount > 0 -> {
                                        Log.i(TAG, "Getting Frame ....")
                                        val colorFrame: Frame =
                                            frames.first(StreamType.COLOR).releaseWith(fr)
                                        //if (mFrameCount % 2 != 0) {
                                        val alignProcess =
                                            mAlign.process(frames).releaseWith(fr)
                                        val depthData = alignProcess
                                            .first(StreamType.DEPTH).releaseWith(fr)
                                        val depthFrame: Frame =
                                            alignProcess.applyFilter(mColorizerOrg)
                                                .releaseWith(fr)
                                                .first(StreamType.DEPTH).releaseWith(fr)
                                        frameProcessing(colorFrame, depthFrame, depthData)
                                        /*} else {
                                            frameProcessing(colorFrame, null, null)
                                        }*/
                                        false
                                    }
                                    mFrameCount < FRAME_MAX_SLEEP -> {
                                        mFrameCount = FRAME_MAX_COUNT
                                        isFrameProcessing = false
                                        true
                                    }
                                    else -> {
                                        listener?.onCameraData(null, null, null)
                                        isFrameProcessing = false
                                        true
                                    }
                                }
                            } else {
                                isFrameOK = true
                                listener?.onCameraStarted()
                                isFrameProcessing = false
                                true
                            }
                            isCheck = false
                        }
                    } else {
                        if (isCheck) {
                            FrameReleaser().use { fr ->
                                mPipeline!!.waitForFrames(TIME_WAIT).releaseWith(fr)
                            }
                            isCheck = false
                            startListener?.onStart()
                        }
                        isSleep = true
                    }

                    true
                } catch (e: Exception) {
                    errorMessage += " ${e.message}"
                    false
                }

            } catch (e: Exception) {
                Log.e(TAG, "${e.message}")
                errorMessage += " ${e.message}"
                false
            }

            if (isNext && !isStopCamera) {
                mHandler?.removeCallbacks(this)
                if (!isSleep) {
                    mHandler?.post(this)
                } else {
                    mHandler?.postDelayed(this, 80)
                }
            } else {
                mPipeline?.stop()
                mPipeline?.close()
                mPipeline = null
                mHandler?.removeCallbacks(this)
                isFrameOK = false
                Log.e(TAG, errorMessage)
                if (errorMessage.isNotEmpty()) {
                    hardwareReset()
                } else {
                    mRSContext?.close()
                    mRSContext = null
                }
                listener?.onCameraError(errorMessage)
                startListener?.onError(errorMessage)
            }
        }
    }
    private var mProcessThread = HandlerThread("Processing....")
    private var mHandlerProcess = Handler()

    init {
        mHandlerThread = HandlerThread("Streaming...")
        mHandlerThread?.start()
        mHandler = Handler(mHandlerThread!!.looper)
        mHandlerThread?.uncaughtExceptionHandler =
            Thread.UncaughtExceptionHandler { t, e -> Log.e(TAG, "${t.name} - ${e.message}") }

        mProcessThread.start()
        mHandlerProcess = Handler(mProcessThread.looper)
    }

    private fun frameProcessing(colorFrame: Frame, depthColorFrame: Frame?, depthValueFrame: Frame?) {
        val colorData = FrameUtil.getFrameData(colorFrame)
        var depthColor: ByteArray? = null
        var collData: DataCollect? = null
        if (depthColorFrame != null) {
            depthColor = FrameUtil.getFrameData(depthColorFrame)
        }
        if (depthValueFrame != null) {
            if(DEPTH_UNIT==0f){
                val depth = depthValueFrame.`as`<DepthFrame>(Extension.DEPTH_FRAME)
                DEPTH_UNIT = depth.units
            }
            val depthValue = FrameUtil.getFrameData(depthValueFrame)
            collData = DataCollect(
                unit = DEPTH_UNIT,
                depthData = depthValue,
                colorData = colorData,
                device = DEVICE_CONFIG,
                isRepaired = false
            )
        }
        val rgbBitmap = FrameUtil.getBitmapFromFrame(colorData, COLOR_WIDTH, COLOR_HEIGHT)
        listener?.onCameraData(rgbBitmap, depthColor, collData)
        isFrameProcessing = false
    }

    fun startStreamThread() {
        Log.e(TAG, "try streaming starting...")
        if (mIsStreaming) return
        try {
            mIsStreaming = true
            isStopCamera = false
            isCheck = true
            if (mRSContext != null) {
                mRSContext?.removeDevicesChangedCallback()
                mRSContext?.close()
                mRSContext = null
            }
            mRSContext = RsContext()
            mRSContext?.setDevicesChangedCallback(this)
            mPipeline = Pipeline(mRSContext)
            configAndStart()
        } catch (e: Exception) {
            startListener?.onError("${e.message}")
            Log.e(TAG, "${e.message}")
        }
    }

    fun stopStreamThread() {
        if (!mIsStreaming) return
        Log.e(TAG, "try stop streaming")
        try {
            mIsStreaming = false
            isStopCamera = true
            isFrameProcessing = false
            mDevice?.close()
            mPipelineProfile?.close()
            Log.e(TAG, "streaming stopped successfully")
        } catch (e: java.lang.Exception) {
            Log.e(TAG, "failed to stop streaming : ${e.message}")
            startListener?.onError("${e.message}")
            mPipeline = null
        }
    }

    fun hasFace() {
        // Reset sleep time when has face
        mFrameCount = FRAME_MAX_COUNT
    }

    private fun configAndStart() {
        mHandler?.post {
            val now = System.currentTimeMillis()
            mPipelineProfile = mPipeline!!.start(config)
            val time = System.currentTimeMillis() - now
            Log.e("startCamera", "$time")
            mDevice = mPipelineProfile?.device
            mHandler?.post(mStreaming)

            val firmware = mDevice?.getInfo(CameraInfo.FIRMWARE_VERSION)
            val name = mDevice?.getInfo(CameraInfo.NAME)
            val productID = mDevice?.getInfo(CameraInfo.PRODUCT_ID)
            val serialNum = mDevice?.getInfo(CameraInfo.SERIAL_NUMBER)
            deviceConfig.apply {
                this.firmware = firmware ?: ""
                this.id = productID ?: ""
                this.serial = serialNum ?: ""
                this.name = name ?: ""
            }
            DEVICE_CONFIG = deviceConfig
            printInfo(mDevice!!)
            Log.e(TAG, "streaming started successfully")
        }
    }

    private fun hardwareReset() {
        Log.e(TAG, "hardwareReset")
        mDevice?.hardwareReset()
    }

    fun applyColorConfig(option: Option, value: Float) {
        mColorSensor ?: return
        try {
            mColorSensor?.setValue(option, value)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getCurConfig(option: Option): Float {
        return mColorSensor!!.getValue(option)
    }

    private fun printInfo(device: Device) {
        device.querySensors()?.forEach { sensor ->
            Log.e(TAG, "Sensor $sensor")
            if (sensor != null) {
                try {
                    if (sensor.`is`(Extension.COLOR_SENSOR)) {
                        mColorSensor = sensor.`as`(Extension.COLOR_SENSOR)
                        val brightness = mColorSensor!!.getValue(Option.BRIGHTNESS)
                        Log.e(TAG, "brightness $brightness - Setting.....")
                        val exposure = mColorSensor!!.getValue(Option.EXPOSURE)
                        Log.e(TAG, "exposure $exposure - Setting.....")
                        val contrast = mColorSensor!!.getValue(Option.CONTRAST)
                        Log.e(TAG, "contrast $contrast - Setting.....")
                        val saturation = mColorSensor!!.getValue(Option.SATURATION)
                        Log.e(TAG, "saturation $saturation - Setting.....")
                        val backLightCompensation =
                            mColorSensor!!.getValue(Option.BACKLIGHT_COMPENSATION)
                        Log.e(TAG, "backLight_compensation $backLightCompensation - Setting.....")
                        val gain = mColorSensor!!.getValue(Option.GAIN)
                        Log.e(TAG, "gain $gain - Setting.....")
                        val gamma = mColorSensor!!.getValue(Option.GAMMA)
                        Log.e(TAG, "gama $gamma - Setting.....")
                        val whiteBalance = mColorSensor!!.getValue(Option.WHITE_BALANCE)
                        Log.e(TAG, "white_balance $whiteBalance - Setting.....")
                        val sharpness = mColorSensor!!.getValue(Option.SHARPNESS)
                        Log.e(TAG, "sharpness $sharpness - Setting.....")
                        val hue = mColorSensor!!.getValue(Option.HUE)
                        Log.e(TAG, "hue $hue - Setting.....")
                        val enableAutoExposure =
                            mColorSensor!!.getValue(Option.ENABLE_AUTO_EXPOSURE)
                        Log.e(TAG, "enable_auto_exposure $enableAutoExposure - Setting.....")
                        val enableAutoWhiteBalance =
                            mColorSensor!!.getValue(Option.ENABLE_AUTO_WHITE_BALANCE)
                        Log.e(TAG, "auto_white_balance $enableAutoWhiteBalance - Setting.....")

                        colorConfig.apply {
                            this.brightness = brightness
                            this.exposure = exposure
                            this.contrast = contrast
                            this.saturation = saturation
                            this.backlight = backLightCompensation
                            this.gain = gain
                            this.gamma = gamma
                            this.sharpness = sharpness
                            this.hue = hue
                            this.autoExposure = enableAutoExposure
                            this.autoWhiteBalance = enableAutoWhiteBalance
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error: ${e.message}")
                }
            }
        }
    }

    interface Listener {
        fun onCameraStarted()
        fun onCameraData(colorBitmap: Bitmap?, depthBitmap: ByteArray?, dataCollect: DataCollect?)
        fun onCameraError(mess: String)
    }

    interface StartListener {
        fun onStart()
        fun onError(mess: String)
    }

    data class ColorConfig(
        var backlight: Float = 0f,
        var brightness: Float = 0f,
        var contrast: Float = 0f,
        var exposure: Float = 0f,
        var gain: Float = 0f,
        var gamma: Float = 0f,
        var hue: Float = 0f,
        var saturation: Float = 0f,
        var sharpness: Float = 0f,
        var autoExposure: Float = 0f,
        var autoWhiteBalance: Float = 0f
    )

    data class DeviceConfig(
        var name: String = "",
        var serial: String = "",
        var id: String = "",
        var firmware: String = ""
    )

    override fun onDeviceAttach() {
        Log.e(TAG, "onDeviceAttach")
        startStreamThread()
    }

    override fun onDeviceDetach() {
        Log.e(TAG, "onDeviceDetach")
        stopStreamThread()
    }

}