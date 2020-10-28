package wee.digital.fpa.ui.progress

import wee.digital.fpa.R
import wee.digital.fpa.ui.Main
import wee.digital.fpa.ui.base.activityVM

class ProgressNapasFragment : Main.Fragment() {

    private val progressVM by lazy { activityVM(ProgressVM::class) }

    override fun layoutResource(): Int {
        return R.layout.progress_napas
    }

    override fun onViewCreated() {
    }

    override fun onLiveDataObserve() {


    }

}