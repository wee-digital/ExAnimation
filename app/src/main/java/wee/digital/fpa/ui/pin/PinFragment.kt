package wee.digital.fpa.ui.pin

import kotlinx.android.synthetic.main.pin.*
import wee.digital.fpa.R
import wee.digital.fpa.ui.base.BaseFragment

class PinFragment : BaseFragment() {

    override fun layoutResource(): Int {
        return R.layout.pin
    }

    override fun onViewCreated() {
        dialogViewClose.setOnClickListener {
            pinRecyclerViewProgress.build()
        }
        PinKeyAdapter().bind(pinRecyclerViewKey, 4)

    }

    override fun onLiveDataObserve() {
    }

}