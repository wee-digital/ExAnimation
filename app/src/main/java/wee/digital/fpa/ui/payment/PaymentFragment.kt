package wee.digital.fpa.ui.payment

import android.view.View
import kotlinx.android.synthetic.main.payment.*
import wee.digital.fpa.R
import wee.digital.fpa.data.local.Timeout
import wee.digital.fpa.ui.Main
import wee.digital.fpa.ui.MainDialog
import wee.digital.fpa.ui.onPaymentCancel

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
        sharedVM.timeoutEnd.observe {
            it ?: return@observe
            onPaymentCancel()
        }
        sharedVM.deviceInfo.observe {
            paymentView.onDeviceInfoChanged(it)
        }
    }

    override fun onViewClick(v: View?) {
        when (v) {
            paymentViewAccept -> onPaymentAccept()
            paymentViewDeny -> onPaymentCancel()
        }
    }

    private fun onPaymentAccept() {
        sharedVM.stopTimeout()
        dismiss()
        navigate(Main.face) {
            setLaunchSingleTop()
        }
    }


}