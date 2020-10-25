package wee.digital.fpa.ui.progress.napas

import wee.digital.fpa.R
import wee.digital.fpa.ui.base.BaseFragment

class ProgressNapasFragment : BaseFragment() {

    private val vm by lazy { activityVM(ProgressNapasVM::class) }

    override fun layoutResource(): Int {
        return R.layout.progress_napas
    }

    override fun onViewCreated() {
    }

    override fun onLiveDataObserve() {


    }

}