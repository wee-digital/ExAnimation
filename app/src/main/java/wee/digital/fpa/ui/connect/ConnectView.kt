package wee.digital.fpa.ui.connect

import com.intel.realsense.librealsense.RsContext
import com.intel.realsense.librealsense.UsbUtilities
import kotlinx.android.synthetic.main.connect.*
import wee.digital.fpa.app.App
import wee.digital.fpa.camera.RealSenseControl
import wee.digital.fpa.util.SimpleLifecycleObserver
import wee.digital.library.extension.addFastClickListener
import kotlin.system.exitProcess

class ConnectView(private val v: ConnectFragment) {

    private fun startCamera() {
        val context = v.requireContext().applicationContext
        RsContext.init(context)
        UsbUtilities.grantUsbPermissionIfNeeded(context)
        App.realSenseControl = RealSenseControl()
        App.realSenseControl?.startStreamThread()
    }

    private fun onLifecycleObserve() {
        v.viewLifecycleOwner.lifecycle.addObserver(object : SimpleLifecycleObserver() {
            override fun onDestroy() {
                App.realSenseControl?.stopStreamThread()
            }
        })
    }

    fun onViewInit() {
        onLifecycleObserve()
        startCamera()
        v.addClickListener(v.connectViewScanQR)
        v.connectViewLogo.addFastClickListener(7) {
            exitProcess(0)
        }
    }

}