package wee.digital.fpa.ui.connect

import kotlinx.android.synthetic.main.connect.*
import wee.digital.fpa.util.observerCameraStream
import wee.digital.library.extension.addFastClickListener
import kotlin.system.exitProcess

class ConnectView(private val v: ConnectFragment) {

    fun onViewInit() {
        v.observerCameraStream()
        v.addClickListener(v.connectViewScanQR)
        v.connectViewLogo.addFastClickListener(7) {
            exitProcess(0)
        }
    }

}