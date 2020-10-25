package wee.digital.fpa.ui.payment.progress

import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable

import kotlinx.android.synthetic.main.payment_progress_fragment.*
import wee.digital.fpa.R
import wee.digital.fpa.ui.base.BaseFragment

class PaymentProgressFragment : BaseFragment() {

    private var drawable: Drawable? = null
    private var progressVM: PaymentProgressViewModel? = null
    override fun layoutResource(): Int {
        return R.layout.progress_pay
    }

    override fun onViewCreated() {
        drawable = paymentProgressViewLogo.drawable
    }

    override fun onLiveDataObserve() {
        progressVM = PaymentProgressViewModel()
        progressVM?.paymentProgressData?.observe {
            // Run gif image

            if (drawable is Animatable) {
                val gif: Animatable = drawable as Animatable
                if (gif.isRunning) {
                    gif.stop()
                } else {
                    gif.start()
                }
            }
        }
    }

}