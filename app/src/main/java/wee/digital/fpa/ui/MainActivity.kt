package wee.digital.fpa.ui

import wee.digital.fpa.R
import wee.digital.fpa.repository.base.BaseData
import wee.digital.fpa.ui.activity.HomeActivity
import wee.digital.fpa.ui.base.BaseActivity

class MainActivity : BaseActivity() {

    override fun layoutResource(): Int {
        return R.layout.main
    }

    override fun onViewCreated() {
    }

    override fun onLiveDataObserve() {
    }


}