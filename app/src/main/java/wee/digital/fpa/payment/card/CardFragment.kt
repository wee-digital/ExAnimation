package wee.digital.fpa.payment.card

import android.os.Bundle
import wee.digital.fpa.R
import wee.digital.fpa.payment.base.BaseFragment

class CardFragment : BaseFragment() {
    override fun layoutResource(): Int {
        return R.layout.card
    }

    override fun onCreated(state: Bundle?) {
    }
}