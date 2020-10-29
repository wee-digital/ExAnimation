package wee.digital.fpa.ui.progress

import wee.digital.fpa.R
import wee.digital.fpa.ui.Main
import wee.digital.fpa.ui.base.activityVM

class ProgressPayFragment : Main.Fragment() {

    private val progressVM by lazy { activityVM(ProgressVM::class) }

    override fun layoutResource(): Int {
        return R.layout.progress_pay
    }

    override fun onViewCreated() {

    }

    override fun onLiveDataObserve() {
        progressVM.arg.observe {
            onBindArg(it)
        }
    }

    private fun onBindArg(it: ProgressArg?) {

    }


}