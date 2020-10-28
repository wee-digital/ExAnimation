package wee.digital.fpa.ui.confirm

import wee.digital.fpa.R
import wee.digital.fpa.ui.Main
import wee.digital.fpa.ui.base.activityVM

class ConfirmFragment : Main.Dialog() {

    private val confirmVM by lazy { activityVM(ConfirmVM::class) }

    private val confirmView by lazy { ConfirmView(this) }

    override fun layoutResource(): Int {
        return R.layout.confirm
    }

    override fun onViewCreated() {
    }

    override fun onLiveDataObserve() {
        confirmVM.arg.observe {
            it ?: return@observe
            confirmView.onBindArg(it)
        }
    }


}