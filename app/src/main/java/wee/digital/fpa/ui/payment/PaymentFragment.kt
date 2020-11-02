package wee.digital.fpa.ui.payment

import android.view.View
import kotlinx.android.synthetic.main.payment.*
import wee.digital.fpa.R
import wee.digital.fpa.shared.Timeout
import wee.digital.fpa.ui.Main
import wee.digital.fpa.ui.MainDialog

class PaymentFragment : MainDialog() {

    private val paymentView by lazy { PaymentView(this) }

    override fun layoutResource(): Int {
        return R.layout.payment
    }

    override fun onViewCreated() {
        paymentView.onViewInit()
    }

    override fun onLiveDataObserve() {
        sharedVM.payment.observe {
            paymentView.onPaymentDataChanged(it)
        }
        sharedVM.startTimeout(Timeout.PAYMENT_CONFIRM)
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
        dismiss()
        navigate(Main.face) {
            setLaunchSingleTop()
        }
    }

    private fun onPaymentDenied() {
        sharedVM.apply {
            progress.postValue(null)
            payment.postValue(null)
            stopTimeout()
        }
        dismiss()
    }


}