package wee.digital.fpa.ui

import wee.digital.fpa.R
import wee.digital.fpa.ui.base.BaseActivity

class MainActivity : BaseActivity() {

    override fun layoutResource(): Int {
        return R.layout.main
    }

    override fun navigationHostId(): Int {
        return R.id.mainFragment
    }


    override fun onViewCreated() {
    }

    override fun onLiveDataObserve() {
    }


}