package wee.digital.fpa.ui.adv

import kotlinx.android.synthetic.main.adv.*
import wee.digital.fpa.R
import wee.digital.fpa.ui.Main
import wee.digital.fpa.ui.base.activityVM

class AdvFragment : Main.Fragment() {

    private val advAdapter = AdvAdapter()

    private val advVM by lazy { activityVM(AdvVM::class) }

    override fun layoutResource(): Int {
        return R.layout.adv
    }

    override fun onViewCreated() {
    }

    override fun onLiveDataObserve() {
        advVM.fetchAdvList()
        advVM.imageList.observe {

        }
    }

}