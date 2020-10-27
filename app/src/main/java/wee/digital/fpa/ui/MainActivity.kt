package wee.digital.fpa.ui


import com.intel.realsense.librealsense.RsContext
import com.intel.realsense.librealsense.UsbUtilities
import wee.digital.fpa.R
import wee.digital.fpa.app.App
import wee.digital.fpa.camera.RealSenseControl
import wee.digital.fpa.ui.base.BaseActivity

class MainActivity : BaseActivity() {

    val vm by lazy { viewModel(MainVM::class) }

    override fun layoutResource(): Int {
        return R.layout.main
    }

    override fun navigationHostId(): Int {
        return R.id.mainFragment
    }

    override fun onViewCreated() {
        RsContext.init(applicationContext)
        UsbUtilities.grantUsbPermissionIfNeeded(this)
        App.realSenseControl = RealSenseControl()
    }

    override fun onLiveDataObserve() {
        Main.rootDirection.observe {
            navigate(it) {
                setLaunchSingleTop(true)
                setInclusive(false)
            }
        }
    }


}