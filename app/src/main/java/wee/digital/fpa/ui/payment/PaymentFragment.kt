package wee.digital.fpa.ui.payment

import android.view.View
import kotlinx.android.synthetic.main.payment.*
import wee.digital.fpa.R
import wee.digital.fpa.app.toast
import wee.digital.fpa.ui.base.BaseDialog
import wee.digital.fpa.ui.plash.SplashVM

class PaymentFragment : BaseDialog() {

    private val splashVM: SplashVM by lazy { activityVM(SplashVM::class) }

    private val vm: PaymentVM by lazy { activityVM(PaymentVM::class) }

    private val v: PaymentView by lazy { PaymentView(this) }

    override fun style(): Int {
        return R.style.App_Dialog_FullScreen_Transparent
    }

    override fun layoutResource(): Int {
        return R.layout.payment
    }

    override fun onViewCreated() {
        v.onViewInit()
    }

    override fun onDestroy() {
        super.onDestroy()
        splashVM.paymentInfo.value = null
    }

    override fun onLiveDataObserve() {
        splashVM.paymentInfo.observe {
            it ?: return@observe
            v.bindPaymentInfo(it)
        }
    }

    override fun onViewClick(v: View?) {
        when (v) {
            paymentViewAccept -> {
                toast("onViewClick.paymentViewAccept")
            }
            paymentViewDeny -> {
                dismiss()
            }
        }
    }


}