package wee.digital.ft.ui.payment

import android.view.View
import kotlinx.android.synthetic.main.payment.*
import wee.digital.ft.R
import wee.digital.ft.shared.Timeout
import wee.digital.ft.ui.Main
import wee.digital.ft.ui.MainDialog
import wee.digital.ft.ui.message.MessageArg

class PaymentFragment : MainDialog() {

    override fun layoutResource(): Int {
        return R.layout.payment
    }

    override fun onViewCreated() {
        addClickListener(paymentViewAccept, paymentViewDeny)
    }

    override fun onLiveDataObserve() {
        sharedVM.startTimeout(Timeout.PAYMENT_CONFIRM, MessageArg.timedOutError)
        sharedVM.isSplashing = false
        sharedVM.payment.observe {
            paymentView.onPaymentDataChanged(it)
        }
        sharedVM.deviceInfo.observe {
            paymentView.onDeviceInfoChanged(it)
        }
    }

    override fun onViewClick(v: View?) {
        when (v) {
            paymentViewAccept -> onPaymentAccept()
            paymentViewDeny -> onPaymentDenied()
        }
    }

    private fun onPaymentAccept() {
        sharedVM.stopTimeout()
        dismissAllowingStateLoss()
        navigate(Main.face) {
            setLaunchSingleTop()
        }
    }

    private fun onPaymentDenied() {
        dismissAllowingStateLoss()
        sharedVM.onPaymentCancel()
    }

}