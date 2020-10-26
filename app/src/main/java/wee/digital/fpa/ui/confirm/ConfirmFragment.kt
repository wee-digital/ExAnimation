package wee.digital.fpa.ui.confirm

import wee.digital.fpa.R
import wee.digital.fpa.ui.base.BaseDialog
import wee.digital.fpa.ui.base.activityVM

class ConfirmFragment : BaseDialog() {

    private val vm by lazy { activityVM(ConfirmVM::class) }

    private val v by lazy { ConfirmView(this) }

    override fun layoutResource(): Int {
        return R.layout.confirm
    }

    override fun onViewCreated() {
    }

    override fun onLiveDataObserve() {
        vm.arg.observe {
            it ?: return@observe
            v.onBindArg(it)
        }
    }


}