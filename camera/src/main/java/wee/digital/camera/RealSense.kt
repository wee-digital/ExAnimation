package wee.digital.camera

import android.app.Application
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbManager
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.intel.realsense.librealsense.RsContext

object RealSense {

    /**
     * Application
     */
    private var mApp: Application? = null

    var app: Application
        set(value) {
            mApp = value
            RsContext.init(value)
            filterConfigByDevice()
        }
        get() {
            if (null == mApp) throw NullPointerException("module not be set")
            return mApp!!
        }

    /**
     * Log
     */
    private const val TAG = "RealSense"

    fun d(s: Any?) {
        if (BuildConfig.DEBUG) Log.d(TAG, s.toString())
    }

    fun d(e: Throwable) {
        if (BuildConfig.DEBUG) Log.e(TAG, e.message?:"")
    }

    /**
     * Usb utils
     */
    private const val PERMISSION = ".USB_PERMISSION"

    @JvmStatic
    val usbManager: UsbManager
        get() = app.getSystemService(Context.USB_SERVICE) as UsbManager

    @JvmStatic
    val usbDevices: Collection<UsbDevice>
        get() = usbManager.deviceList.values

    private val intentFilter: IntentFilter by lazy {
        IntentFilter(PERMISSION).also {
            it.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED)
            it.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED)
        }
    }

    @JvmStatic
    val device: UsbDevice?
        get() {
            usbDevices.forEach {
                if (it.vendorId == VENDOR_ID) return it
            }
            return null
        }

    private var usbReceiver: BroadcastReceiver? = null

    private fun usbReceiver(permissionGranted: () -> Unit): BroadcastReceiver {
        return object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val usb = intent.getParcelableExtra<UsbDevice>(UsbManager.EXTRA_DEVICE)
                if (usb?.vendorId != VENDOR_ID) return
                if (intent.action === UsbManager.ACTION_USB_DEVICE_DETACHED) return
                if (intent.action === UsbManager.ACTION_USB_DEVICE_ATTACHED) {
                    if (usbManager.hasPermission(usb)) {
                        permissionGranted()
                    } else {
                        requestPermission(usb)
                    }
                }
                if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                    permissionGranted()
                }
            }
        }
    }

    @JvmStatic
    fun open(usb: UsbDevice?): UsbDeviceConnection {
        return usbManager.openDevice(usb)
    }

    @JvmStatic
    fun requestPermission(usb: UsbDevice?, permissionGranted: () -> Unit = {}) {
        if (hasPermission(usb)) {
            permissionGranted()
            return
        }

        if (usbReceiver == null) {
            usbReceiver = usbReceiver(permissionGranted).also {
                app.registerReceiver(it, intentFilter)
            }
        }

        usb ?: return
        val permissionIntent = PendingIntent.getBroadcast(app, 1234, Intent(PERMISSION), 0)
        usbManager.requestPermission(usb, permissionIntent)
    }

    @JvmStatic
    fun requestPermission(permissionGranted: () -> Unit = {}) {
        requestPermission(device, permissionGranted)
    }

    @JvmStatic
    fun hasPermission(usb: UsbDevice?): Boolean {
        usb ?: return false
        return usbManager.hasPermission(usb)
    }

    /**
     * [RealSenseControl]
     */
    private var control: RealSenseControl? = null

    val imagesLiveData: MutableLiveData<Pair<Bitmap, Bitmap>?> by lazy {
        MutableLiveData<Pair<Bitmap, Bitmap>?>()
    }

    var isStarting: Boolean = false

    fun start() {
        if (isStarting || control?.isStreaming == true) return
        isStarting = true
        Thread {
            control = RealSenseControl().also {
                Thread.sleep(2400)
                it.onCreate()
                isStarting = false
            }
        }.start()
    }

    fun stop() {
        control?.onPause()
        control = null
    }

    fun hasFace() {
        control?.hasFace()
    }

    /**
     * Config
     */
    const val VENDOR_ID = 32902
    const val D415 = 2771
    const val SR300 = 2725
    const val SR305 = 2888
    const val TIME_WAIT = 2000
    const val FRAME_MAX_COUNT = 200
    const val FRAME_MAX_SLEEP = -20
    const val COLOR_WIDTH = 1280
    const val COLOR_HEIGHT = 720
    const val COLOR_SIZE = COLOR_WIDTH * COLOR_HEIGHT * 3
    var DEPTH_WIDTH = 1280
    var DEPTH_HEIGHT = 720
    val DEPTH_SIZE: Int get() = DEPTH_WIDTH * DEPTH_HEIGHT * 3
    var FRAME_RATE = 15

    private fun filterConfigByDevice() {
        when (device?.productId) {
            D415 -> {
                DEPTH_WIDTH = 1280
                DEPTH_HEIGHT = 720
                FRAME_RATE = 15
            }
            SR300, SR305 -> {
                DEPTH_WIDTH = 640
                DEPTH_HEIGHT = 480
                FRAME_RATE = 10
            }
            else -> {
                DEPTH_WIDTH = 1280
                DEPTH_HEIGHT = 720
                FRAME_RATE = 15
            }
        }
    }


}