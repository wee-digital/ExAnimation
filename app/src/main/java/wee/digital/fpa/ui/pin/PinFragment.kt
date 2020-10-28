package wee.digital.fpa.ui.pin

import android.view.View
import kotlinx.android.synthetic.main.pin.*
import wee.digital.fpa.MainDirections
import wee.digital.fpa.R
import wee.digital.fpa.ui.Main

class PinFragment : Main.Dialog() {

    private val pinVM by lazy { viewModel(PinVM::class) }

    private val pinView by lazy { PinView(this) }

    override fun layoutResource(): Int {
        return R.layout.pin
    }

    override fun onViewCreated() {
        pinView.onViewInit()
        pinProgressLayout.onItemFilled = {
            pinVM.onPinFilled(it)
        }
    }

    override fun onLiveDataObserve() {
        pinVM.errorMessage.observe {
            pinView.onBindErrorText(it)
        }
    }

    override fun onViewClick(v: View?) {
        when (v) {
            dialogViewClose -> {
                dismiss()
                navigate(MainDirections.actionGlobalSplashFragment()) {
                    setLaunchSingleTop()
                }
            }
        }
    }

}