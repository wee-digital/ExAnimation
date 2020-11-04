package wee.digital.ft.ui.payment

import android.view.View
import kotlinx.android.synthetic.main.payment.*
import wee.digital.ft.R
import wee.digital.ft.shared.Timeout
import wee.digital.ft.ui.Main
import wee.digital.ft.ui.MainDialog

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
            it ?: return@observe
            sharedVM.startTimeout(Timeout.PAYMENT_CONFIRM)
            sharedVM.isSplashing = false
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
        dismiss()
        navigate(Main.face) {
            setLaunchSingleTop()
        }
    }

    private fun onPaymentDenied() {
        sharedVM.apply {
            progress.postValue(null)
            stopTimeout()
        }
        dismiss()
    }


}