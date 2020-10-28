package wee.digital.fpa.ui

import com.intel.realsense.librealsense.RsContext
import com.intel.realsense.librealsense.UsbUtilities
import kotlinx.android.synthetic.main.main.*
import wee.digital.fpa.BuildConfig
import wee.digital.fpa.app.App
import wee.digital.fpa.app.app
import wee.digital.fpa.camera.RealSenseControl
import wee.digital.fpa.repository.model.DeviceInfo
import wee.digital.fpa.util.SimpleLifecycleObserver

class MainView(val v: MainActivity) {

    fun onViewInit() {
        v.lifecycle.addObserver(object : SimpleLifecycleObserver() {
            override fun onCreated() {
                RsContext.init(app)
                UsbUtilities.grantUsbPermissionIfNeeded(app)
                App.realSenseControl = RealSenseControl()
            }
            override fun onPause() {
                App.realSenseControl?.listener = null
            }

            override fun onDestroy() {
                App.realSenseControl?.stopStreamThread()
            }
        })
    }

    fun onDeviceInfoChanged(it: DeviceInfo?){
        v.mainTextViewDeviceInfo.text = "${it?.fullName}${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_NAME})"
    }
}