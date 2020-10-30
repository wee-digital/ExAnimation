package wee.digital.fpa.ui

import com.intel.realsense.librealsense.RsContext
import com.intel.realsense.librealsense.UsbUtilities
import kotlinx.android.synthetic.main.main.*
import wee.digital.fpa.BuildConfig
import wee.digital.fpa.MainDirections
import wee.digital.fpa.R
import wee.digital.fpa.app.App
import wee.digital.fpa.app.app
import wee.digital.fpa.camera.RealSenseControl
import wee.digital.fpa.repository.model.DeviceInfo
import wee.digital.fpa.util.SimpleLifecycleObserver
import wee.digital.library.extension.addFastClickListener

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

    fun onBindDeviceInfo(it: DeviceInfo?) {
        it ?: return
        v.mainTextViewDeviceInfo.text = "${it.fullName} - ${it.posName}"
    }

    fun showDisconnectDialog(){
        v.navigate(MainDirections.actionGlobalDisconnectFragment()) {
            setEnterAnim(R.anim.vertical_enter)
            setPopExitAnim(R.anim.vertical_pop_exit)
        }
    }
}