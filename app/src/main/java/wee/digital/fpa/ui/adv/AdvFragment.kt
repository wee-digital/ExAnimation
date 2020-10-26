package wee.digital.fpa.ui.adv

import android.view.View
import wee.digital.fpa.R
import wee.digital.fpa.ui.base.BaseDialog
import wee.digital.fpa.ui.base.activityVM

class AdvFragment : BaseDialog() {

    private val vm by lazy { activityVM(AdvVM::class) }

    override fun layoutResource(): Int {
        return R.layout.adv
    }

    override fun onViewCreated() {

    }

    override fun onLiveDataObserve() {

    }

    override fun onViewClick(v: View?) {
        when (v) {

        }
    }

}