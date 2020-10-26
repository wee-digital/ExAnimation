package wee.digital.fpa.ui.connect

import kotlinx.android.synthetic.main.connect.*
import wee.digital.fpa.app.App
import wee.digital.fpa.util.SimpleLifecycleObserver
import wee.digital.library.extension.addFastClickListener
import kotlin.system.exitProcess

class ConnectView(private val v: ConnectFragment) {

    private fun startCamera() {
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