package wee.digital.fpa.ui.pin

import android.os.Bundle
import kotlinx.android.synthetic.main.pin.*
import wee.digital.fpa.R
import wee.digital.fpa.ui.base.BaseFragment

class PinFragment : BaseFragment() {

    override fun layoutResource(): Int {
        return R.layout.pin
    }

    override fun onCreated(state: Bundle?) {
        dialogViewClose.setOnClickListener {
            pinRecyclerViewProgress.build()
        }
        PinKeyAdapter().bind(pinRecyclerViewKey,4)

    }
}