package wee.digital.fpa.ui.payment

import android.view.View
import kotlinx.android.synthetic.main.payment.*
import wee.digital.fpa.MainDirections
import wee.digital.fpa.R
import wee.digital.fpa.data.local.Timeout
import wee.digital.fpa.ui.Main

class PaymentFragment : Main.Dialog() {

    private val v by lazy { PaymentView(this) }

    override fun layoutResource(): Int {
        return R.layout.payment
    }

    override fun onViewCreated() {
        v.onViewInit()
    }

    override fun onLiveDataObserve() {
        remainingVM.startTimeout(Timeout.PAYMENT_TIMEOUT)
        mainVM.paymentArg.observe {
            v.onPaymentDataChanged(it)
        }
        mainVM.deviceInfo.observe {
            v.onDeviceInfoChanged(it)
        }
    }

    override fun onViewClick(v: View?) {
        when (v) {
            paymentViewAccept -> onPaymentAccept()
            paymentViewDeny -> onPaymentDeny()
        }
    }

    private fun onPaymentAccept() {
        dismiss()
        navigate(MainDirections.actionGlobalFaceFragment()) {
            setLaunchSingleTop()
        }
    }

    private fun onPaymentDeny() {
        mainVM.paymentArg.value = null
    }

}