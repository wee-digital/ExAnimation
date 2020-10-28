package wee.digital.fpa.ui.disconnect

import wee.digital.fpa.R
import wee.digital.fpa.ui.Main
import wee.digital.fpa.ui.base.activityVM
import wee.digital.fpa.ui.vm.SocketVM

class DisconnectFragment : Main.Dialog() {

    private val socketVM by lazy { activityVM(SocketVM::class) }

    override fun layoutResource(): Int {
        return R.layout.disconnnect
    }

    override fun onViewCreated() {
    }


    override fun onLiveDataObserve() {
        socketVM.webSocket.observe {
            if (it != null) {
                dismiss()
            }
        }
    }

}