package wee.digital.fpa.ui.payment

import android.view.View
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.payment.*
import wee.digital.fpa.R
import wee.digital.fpa.app.toast
import wee.digital.fpa.ui.base.BaseDialog
import wee.digital.fpa.ui.plash.SplashVM

class PaymentFragment : BaseDialog() {

    private val splashVM by lazy { activityVM(SplashVM::class) }

    private val vm by lazy { activityVM(PaymentVM::class) }

    private val v by lazy { PaymentView(this) }

    override fun layoutResource(): Int {
        return R.layout.payment
    }

    override fun onViewCreated() {
        v.onViewInit()
    }

    override fun onLiveDataObserve() {
        splashVM.paymentInfo.observe {
            onPaymentData(it)
        }
    }

    override fun onViewClick(v: View?) {
        when (v) {
            paymentViewAccept -> {
                toast("onViewClick.paymentViewAccept")
            }
            paymentViewDeny -> {
                splashVM.paymentInfo.value = null
            }
        }
    }

    private fun onPaymentData(obj: JsonObject?) {
        when (obj) {
            null -> {
                dismiss()
            }
            else -> {
                v.bindPaymentInfo(obj)
            }
        }
    }

}