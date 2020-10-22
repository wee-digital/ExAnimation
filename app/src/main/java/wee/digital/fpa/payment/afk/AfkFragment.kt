package wee.digital.fpa.payment.afk

import android.os.Bundle
import wee.digital.fpa.R
import wee.digital.fpa.payment.base.BaseFragment

class AfkFragment : BaseFragment() {
    override fun layoutResource(): Int {
        return R.layout.payment_confirm
    }

    override fun onCreated(state: Bundle?) {
    }
}