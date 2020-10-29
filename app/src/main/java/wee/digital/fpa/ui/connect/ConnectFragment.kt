package wee.digital.fpa.ui.connect

import android.view.View
import kotlinx.android.synthetic.main.connect.*
import wee.digital.fpa.MainDirections
import wee.digital.fpa.R
import wee.digital.fpa.ui.Main
import wee.digital.fpa.ui.base.activityVM
import wee.digital.fpa.ui.device.DeviceVM

class ConnectFragment : Main.Fragment() {

    private val connectVM by lazy { activityVM(ConnectVM::class) }

    private val connectView by lazy { ConnectView(this) }

    override fun layoutResource(): Int {
        return R.layout.connect
    }

    override fun onViewCreated() {
        connectView.onViewInit()
    }

    override fun onLiveDataObserve() {

    }

    override fun onViewClick(v: View?) {
        when (v) {
            connectViewScanQR -> {
                navigate(MainDirections.actionGlobalQrFragment())
            }
        }
    }


}