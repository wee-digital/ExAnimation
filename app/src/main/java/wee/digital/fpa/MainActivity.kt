package wee.digital.fpa

import android.os.Bundle
import wee.digital.fpa.payment.base.BaseActivity

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