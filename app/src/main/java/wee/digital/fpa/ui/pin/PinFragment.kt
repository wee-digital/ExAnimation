package wee.digital.fpa.ui.pin

import android.view.View
import kotlinx.android.synthetic.main.pin.*
import wee.digital.fpa.R
import wee.digital.fpa.ui.base.BaseFragment
import wee.digital.library.extension.color

class PinFragment : BaseFragment() {

    override fun layoutResource(): Int {
        return R.layout.pin
    }

    override fun onViewCreated() {
        addClickListener(dialogViewClose)
        PinKeyAdapter().bind(pinRecyclerViewKey, 4)
        pinRecyclerViewProgress.apply {
            accentColor = color(R.color.colorPrimary)
            itemCount = 6
            build()
        }


    }

    override fun onLiveDataObserve() {
    }

    override fun onViewClick(v: View?) {
        when (v) {
            dialogViewClose -> {
                pinRecyclerViewProgress.incrementProgress(1)
            }
        }
    }

}