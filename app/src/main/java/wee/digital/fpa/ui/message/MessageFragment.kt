package wee.digital.fpa.ui.message

import android.view.View
import wee.digital.fpa.R
import wee.digital.fpa.ui.base.BaseDialog
import wee.digital.fpa.ui.base.activityVM

class MessageFragment : BaseDialog() {

    private val vm by lazy { activityVM(MessageVM::class) }

    private val v by lazy { MessageView(this) }

    override fun layoutResource(): Int {
        return R.layout.message
    }

    override fun onViewCreated() {
    }

    override fun onLiveDataObserve() {
        vm.arg.observe {
            v.onBindArg(it)
        }
    }

    override fun onViewClick(v: View?) {
        when (v) {
        }
    }
}