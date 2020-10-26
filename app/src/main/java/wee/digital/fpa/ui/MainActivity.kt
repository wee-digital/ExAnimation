package wee.digital.fpa.ui

import com.intel.realsense.librealsense.RsContext
import com.intel.realsense.librealsense.UsbUtilities
import wee.digital.fpa.MainDirections
import wee.digital.fpa.R
import wee.digital.fpa.app.App
import wee.digital.fpa.camera.RealSenseControl
import wee.digital.fpa.ui.base.BaseActivity
import wee.digital.fpa.ui.base.activityVM
import wee.digital.fpa.ui.confirm.ConfirmVM
import wee.digital.fpa.ui.message.MessageVM

class MainActivity : BaseActivity() {

    val vm by lazy { viewModel(MainVM::class) }

    override fun layoutResource(): Int {
        return R.layout.main
    }

    override fun onViewCreated() {
        RsContext.init(applicationContext)
        UsbUtilities.grantUsbPermissionIfNeeded(this)
        App.realSenseControl = RealSenseControl()
    }

    override fun onLiveDataObserve() {
        Main.direction.observe {
            navigate(it) {
                setLaunchSingleTop(true)
                setInclusive(false)
            }
        }
    }


}