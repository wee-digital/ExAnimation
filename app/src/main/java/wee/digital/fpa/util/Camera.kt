package wee.digital.fpa.util

import androidx.lifecycle.LifecycleOwner
import com.intel.realsense.librealsense.RsContext
import com.intel.realsense.librealsense.UsbUtilities
import wee.digital.fpa.app.App
import wee.digital.fpa.app.app

fun LifecycleOwner.observerCamera() {
    lifecycle.addObserver(object : SimpleLifecycleObserver() {
        override fun onCreated() {
            RsContext.init(app)
            UsbUtilities.grantUsbPermissionIfNeeded(app)
            App.realSenseControl?.startStreamThread()
        }

        override fun onPause() {
            App.realSenseControl?.listener = null
        }

        override fun onDestroy() {
            App.realSenseControl?.stopStreamThread()
        }
    })
}