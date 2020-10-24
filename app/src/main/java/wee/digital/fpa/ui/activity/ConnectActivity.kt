package wee.digital.fpa.ui.activity

import androidx.navigation.findNavController
import com.intel.realsense.librealsense.RsContext
import com.intel.realsense.librealsense.UsbUtilities
import kotlinx.android.synthetic.main.activity_connect.*
import wee.digital.fpa.R
import wee.digital.fpa.app.App
import wee.digital.fpa.camera.RealSenseControl
import wee.digital.fpa.repository.base.BaseData
import wee.digital.fpa.ui.base.BaseActivity
import wee.digital.fpa.util.setFastClickListener
import wee.digital.library.extension.hide
import wee.digital.library.extension.post
import kotlin.system.exitProcess

class ConnectActivity : BaseActivity() {

    override fun layoutResource(): Int = R.layout.activity_connect

    override fun onViewCreated() {
        initView()
        startCamera()
    }

    override fun onLiveDataObserve() {
    }

    private fun initView() {
        if(BaseData.loginStatus){
            startClear(HomeActivity::class.java)
        }
        actConnectAction.setOnClickListener {
            findNavController(R.id.actConnectHost).navigate(R.id.QRFragment, null, navAnim())
        }
        actConnectActionQuit.setFastClickListener(7) {
            App.realSenseControl?.stopStreamThread()
            exitProcess(0)
        }
        actConnectActionA.setOnClickListener {
            actConnectActionA.showLoading()
            post(2000) { actConnectActionA.hideLoading() }
        }
    }

    private fun startCamera() {
        RsContext.init(applicationContext)
        UsbUtilities.grantUsbPermissionIfNeeded(applicationContext)
        App.realSenseControl = RealSenseControl()
    }

}