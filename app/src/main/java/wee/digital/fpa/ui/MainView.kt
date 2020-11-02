package wee.digital.fpa.ui

import com.intel.realsense.librealsense.RsContext
import com.intel.realsense.librealsense.UsbUtilities
import kotlinx.android.synthetic.main.main_widgets.*
import wee.digital.fpa.BuildConfig
import wee.digital.fpa.app.App
import wee.digital.fpa.app.app
import wee.digital.fpa.camera.RealSenseControl
import wee.digital.fpa.repository.model.DeviceInfo
import wee.digital.fpa.util.SimpleLifecycleObserver
import wee.digital.library.extension.*

class MainView(val v: MainActivity) {

    fun onViewInit() {
        v.mainTextViewVersion.text = "${BuildConfig.VERSION_NAME}-${BuildConfig.VERSION_CODE}"
        v.lifecycle.addObserver(object : SimpleLifecycleObserver() {
            override fun onCreated() {
                RsContext.init(app)
                UsbUtilities.grantUsbPermissionIfNeeded(app)
                App.realSenseControl = RealSenseControl()
            }

            override fun onDestroy() {
                App.realSenseControl?.stopStreamThread()
            }
        })
        v.mainViewExit.addFastClickListener(7) {
            v.finish()
        }
    }


    fun showDisconnectDialog(isDisconnected: Boolean) {
        v.mainViewDisconnect.isShow(isDisconnected)
    }

    fun onTimeoutColorChanged(colorRes: Int) {
        v.mainTextViewTimeout.color(colorRes)
    }

    fun onTimeoutSecondChanged(second: Int) {
        if (second < 0) {
            v.mainTextViewTimeout.text = null
            return
        }
        val sHour = "%02d:%02d".format(second / 60, second % 60).bold()
        val sRemaining = "Thời gian còn lại: %s".format(sHour)
        v.mainTextViewTimeout.setHyperText(sRemaining)
    }

    fun onDeviceInfoChanged(it: DeviceInfo?) {
        it ?: return
        v.mainTextViewDeviceInfo.text = "${it.fullName} - ${it.posName}"
    }


}