package wee.digital.fpa.payment.pin

import android.os.Bundle
import kotlinx.android.synthetic.main.pin.*
import wee.digital.fpa.R
import wee.digital.fpa.payment.base.BaseFragment

class PinFragment : BaseFragment() {

    override fun layoutResource(): Int {
        return R.layout.pin
    }

    override fun onCreated(state: Bundle?) {
        pinRecyclerViewProgress.stepCount = 6
        pinRecyclerViewProgress.build()
    }
}