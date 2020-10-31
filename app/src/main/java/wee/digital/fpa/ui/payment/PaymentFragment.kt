package wee.digital.fpa.ui.payment

import android.view.View
import kotlinx.android.synthetic.main.payment.*
import wee.digital.fpa.R
import wee.digital.fpa.data.local.Timeout
import wee.digital.fpa.ui.Main
import wee.digital.fpa.ui.mainVM
import wee.digital.fpa.ui.timeoutVM
import kotlin.reflect.KClass

class PaymentFragment : Main.Dialog<PaymentVM>() {

    private val paymentView by lazy { PaymentView(this) }

    override fun layoutResource(): Int {
        return R.layout.payment
    }

    override fun localViewModel(): KClass<PaymentVM> {
        return PaymentVM::class
    }

    override fun onViewCreated() {
        paymentView.onViewInit()
    }

    override fun onLiveDataObserve() {
        localVM.arg.observe {
            paymentView.onPaymentDataChanged(it)
        }
        timeoutVM.startTimeout(Timeout.PAYMENT_CONFIRM)
        timeoutVM.inTheEnd.observe {
            it ?: return@observe
            onPaymentDeny()
        }
        mainVM.deviceInfo.observe {
            paymentView.onDeviceInfoChanged(it)
        }
    }

    override fun onLiveEventChanged(event: Int) {
    }

    override fun onViewClick(v: View?) {
        when (v) {
            paymentViewAccept -> onPaymentAccept()
            paymentViewDeny -> onPaymentDeny()
        }
    }

    private fun onPaymentAccept() {
        timeoutVM.stopTimeout()
        dismiss()
        navigate(Main.face) {
            setLaunchSingleTop()
        }
    }

    private fun onPaymentDeny() {
        dismiss()
        timeoutVM.stopTimeout()
        localVM.arg.postValue(null)
    }


}