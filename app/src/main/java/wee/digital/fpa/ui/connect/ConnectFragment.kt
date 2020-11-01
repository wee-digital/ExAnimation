package wee.digital.fpa.ui.connect

import android.view.View
import kotlinx.android.synthetic.main.connect.*
import wee.digital.fpa.R
import wee.digital.fpa.ui.Main
import wee.digital.fpa.ui.MainFragment
import wee.digital.fpa.util.observerCameraStream
import wee.digital.library.extension.addFastClickListener
import kotlin.system.exitProcess

class ConnectFragment : MainFragment() {

    override fun layoutResource(): Int {
        return R.layout.connect
    }

    override fun onViewCreated() {
        observerCameraStream()
        addClickListener(connectViewScanQR)
        connectViewLogo.addFastClickListener(7) {
            exitProcess(0)
        }
    }

    override fun onLiveDataObserve() {

    }

    override fun onViewClick(v: View?) {
        when (v) {
            connectViewScanQR -> {
                navigate(Main.qr)
            }
        }
    }

}