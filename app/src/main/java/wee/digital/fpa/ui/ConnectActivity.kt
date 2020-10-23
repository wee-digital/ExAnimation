package wee.digital.fpa.ui

import androidx.navigation.findNavController
import com.intel.realsense.librealsense.RsContext
import com.intel.realsense.librealsense.UsbUtilities
import kotlinx.android.synthetic.main.activity_connect.*
import wee.digital.fpa.R
import wee.digital.fpa.app.App
import wee.digital.fpa.camera.RealSenseControl
import wee.digital.fpa.ui.base.BaseActivity
import wee.digital.library.extension.hideSystemUI

class ConnectActivity : BaseActivity() {

    override fun layoutResource(): Int = R.layout.activity_connect

    override fun onViewCreated() {
        viewClick()
        startCamera()
    }

    override fun onLiveDataObserve() {
    }

    private fun viewClick() {
        actConnectAction.setOnClickListener {
            findNavController(R.id.actConnectHost).navigate(R.id.QRFragment, null, navAnim())
        }
    }

    private fun startCamera() {
        RsContext.init(applicationContext)
        UsbUtilities.grantUsbPermissionIfNeeded(applicationContext)
        App.realSenseControl = RealSenseControl()
        App.realSenseControl?.startStreamThread()
    }

    override fun onResume() {
        super.onResume()
        hideSystemUI()
    }

    override fun onDestroy() {
        super.onDestroy()
        App.realSenseControl?.stopStreamThread()
    }

}