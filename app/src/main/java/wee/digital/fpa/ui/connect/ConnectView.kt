package wee.digital.fpa.ui.connect

import kotlinx.android.synthetic.main.connect.*
import wee.digital.fpa.app.App
import wee.digital.fpa.util.SimpleLifecycleObserver
import wee.digital.fpa.util.observerCamera
import wee.digital.library.extension.addFastClickListener
import kotlin.system.exitProcess

class ConnectView(private val v: ConnectFragment) {

    fun onViewInit() {
        v.observerCamera()
        v.addClickListener(v.connectViewScanQR)
        v.connectViewLogo.addFastClickListener(7) {
            exitProcess(0)
        }
    }

}