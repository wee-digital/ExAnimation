package wee.digital.fpa.ui.pin

import android.view.View
import kotlinx.android.synthetic.main.pin.*
import wee.digital.fpa.MainDirections
import wee.digital.fpa.R
import wee.digital.fpa.app.toast
import wee.digital.fpa.data.local.Timeout
import wee.digital.fpa.ui.Main
import wee.digital.library.extension.post

class PinFragment : Main.Dialog() {

    private val pinVM by lazy { viewModel(PinVM::class) }

    private val pinView by lazy { PinView(this) }

    override fun layoutResource(): Int {
        return R.layout.pin
    }

    override fun onViewCreated() {
        pinView.onViewInit()
        pinProgressLayout.onItemFilled = {
            remainingVM.startTimeout(Timeout.PIN_TIMEOUT)
           post(2000){
               pinVM.onPinFilled(it)
           }
        }
    }

    override fun onLiveDataObserve() {
        remainingVM.startTimeout(Timeout.PIN_TIMEOUT)
        pinVM.errorMessage.observe {
            pinProgressLayout.notifyInputRemoved()
            pinView.onBindErrorText(it)
        }
    }

    override fun onViewClick(v: View?) {
        when (v) {
            pinViewClose -> {
                dismiss()
                navigate(MainDirections.actionGlobalSplashFragment()) {
                    setLaunchSingleTop()
                }
            }
        }
    }


}