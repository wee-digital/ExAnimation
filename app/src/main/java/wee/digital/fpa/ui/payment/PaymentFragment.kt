package wee.digital.fpa.ui.payment

import android.view.View
import kotlinx.android.synthetic.main.payment.*
import wee.digital.fpa.R
import wee.digital.fpa.data.local.Timeout
import wee.digital.fpa.ui.Main
import wee.digital.fpa.ui.MainDialog
import wee.digital.fpa.ui.base.activityVM
import wee.digital.fpa.ui.vm.SharedVM

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
        sharedVM.startTimeout(Timeout.PAYMENT_CONFIRM) {
            onPaymentDenied()
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
        activityVM(SharedVM::class).apply {
            progress.postValue(null)
            payment.postValue(null)
            stopTimeout()
        }
        dismiss()
    }


}