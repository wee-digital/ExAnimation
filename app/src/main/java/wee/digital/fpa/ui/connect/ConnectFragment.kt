package wee.digital.fpa.ui.connect

import android.view.View
import kotlinx.android.synthetic.main.connect.*
import wee.digital.fpa.MainDirections
import wee.digital.fpa.R
import wee.digital.fpa.ui.base.BaseFragment
import wee.digital.fpa.ui.base.activityVM
import wee.digital.fpa.ui.device.DeviceVM

class ConnectFragment : BaseFragment() {

    private val vm by lazy { viewModel(ConnectVM::class) }

    private val v by lazy { ConnectView(this) }

    override fun layoutResource(): Int {
        return R.layout.connect
    }

    override fun onViewCreated() {
        v.onViewInit()
    }

    override fun onLiveDataObserve() {
        activityVM(DeviceVM::class).arg.observe {
            it ?: return@observe
            navigate(MainDirections.actionGlobalDeviceFragment())
        }
    }

    override fun onViewClick(v: View?) {
        when (v) {
            connectViewScanQR -> {
                navigate(MainDirections.actionGlobalQrFragment())
            }
        }
    }


}