package wee.digital.fpa.ui.connect

import android.view.View
import kotlinx.android.synthetic.main.connect.*
import wee.digital.fpa.R
import wee.digital.fpa.ui.Main
import kotlin.reflect.KClass

class ConnectFragment : Main.Fragment<ConnectVM>() {

    private val connectView by lazy { ConnectView(this) }

    override fun layoutResource(): Int {
        return R.layout.connect
    }

    override fun localViewModel(): KClass<ConnectVM> {
        return ConnectVM::class
    }

    override fun onViewCreated() {
        connectView.onViewInit()
    }

    override fun onLiveDataObserve() {

    }

    override fun onLiveEventChanged(event: Int) {
    }

    override fun onViewClick(v: View?) {
        when (v) {
            connectViewScanQR -> {
                navigate(Main.qr)
            }
        }
    }


}