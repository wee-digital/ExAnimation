package wee.digital.fpa.ui.progress

import kotlinx.android.synthetic.main.progress.*
import wee.digital.fpa.R
import wee.digital.fpa.ui.Main
import wee.digital.fpa.ui.base.activityVM
import wee.digital.fpa.ui.payment.PaymentVM
import wee.digital.library.extension.load
import wee.digital.library.util.Media

class ProgressPayFragment : Main.Dialog() {

    private val paymentVM by lazy { activityVM(PaymentVM::class) }

    override fun layoutResource(): Int {
        return R.layout.progress_pay
    }

    override fun onViewCreated() {
        timeoutVM.startTimeout(5)
        progressImageView.load(R.mipmap.img_face_paid)
        view?.postDelayed({
            Media.play(R.raw.facepay_sound)
        }, 2000)
    }

    override fun onLiveDataObserve() {
        timeoutVM.inTheEnd.observe {
            it ?: return@observe
            paymentVM.arg.postValue(null)
            dismiss()
        }
        progressVM.arg.observe {
            if (it == null) {
                dismiss()
            }
        }
    }


}