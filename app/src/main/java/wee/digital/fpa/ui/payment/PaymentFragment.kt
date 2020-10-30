package wee.digital.fpa.ui.payment

import android.view.View
import kotlinx.android.synthetic.main.payment.*
import wee.digital.fpa.MainDirections
import wee.digital.fpa.R
import wee.digital.fpa.data.local.Timeout
import wee.digital.fpa.ui.Main
import wee.digital.fpa.ui.base.activityVM

class PaymentFragment : Main.Dialog() {

    private val paymentVM by lazy { activityVM(PaymentVM::class) }

    private val paymentView by lazy {
        PaymentView(this)
    }

    override fun layoutResource(): Int {
        return R.layout.payment
    }

    override fun onViewCreated() {
        paymentView.onViewInit()
    }

    override fun onLiveDataObserve() {
        timeoutVM.startTimeout(Timeout.PAYMENT_CONFIRM)
        timeoutVM.inTheEnd.observe {
            it ?: return@observe
            onPaymentDeny()
        }
        paymentVM.arg.observe {
            paymentView.onPaymentDataChanged(it)
        }
        mainVM.deviceInfo.observe {
            paymentView.onDeviceInfoChanged(it)
        }
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
        navigate(MainDirections.actionGlobalFaceFragment()) {
            setLaunchSingleTop()
        }
    }

    private fun onPaymentDeny() {
        dismiss()
        timeoutVM.stopTimeout()
        paymentVM.arg.postValue(null)
    }

}