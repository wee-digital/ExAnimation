package wee.digital.fpa.ui

import android.os.Bundle
import wee.digital.fpa.R
import wee.digital.fpa.ui.base.BaseActivity

class MainActivity : BaseActivity() {

    override fun layoutResource(): Int {
        return R.layout.main
    }

    override fun navigationHostId(): Int? {
        return R.id.mainFragment
    }

    override fun onCreated(state: Bundle?) {
    }


}