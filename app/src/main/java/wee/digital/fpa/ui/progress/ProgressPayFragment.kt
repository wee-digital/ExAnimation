package wee.digital.fpa.ui.progress

import kotlinx.android.synthetic.main.progress_pay.*
import wee.digital.fpa.MainDirections
import wee.digital.fpa.R
import wee.digital.fpa.ui.Main
import wee.digital.fpa.ui.base.activityVM
import wee.digital.fpa.ui.payment.PaymentVM
import wee.digital.library.extension.*
import wee.digital.library.util.Media

class ProgressPayFragment : Main.Dialog() {

    private val paymentVM by lazy { activityVM(PaymentVM::class) }

    override fun layoutResource(): Int {
        return R.layout.progress_pay
    }

    override fun onViewCreated() {
        progressImageViewPay.clear()
    }

    override fun onLiveDataObserve() {
        progressVM.arg.observe {
            if (it == null) {
                onEndProgress()
            } else {
                onBindArg(it)
            }
        }
    }

    private fun onBindArg(it: ProgressArg) {
        progressImageView.load(it.image)
        progressTextViewTitle.text = it.title
        progressTextViewMessage.text = it.message
    }

    private fun onEndProgress() {
        hide(progressImageView, progressTextViewTitle, progressTextViewMessage)
        progressImageViewPay.loadGif(R.mipmap.img_face_paid)
        post(2000) {
            Media.play(R.raw.facepay_sound)
        }
        post(6000) {
            dismiss()
            paymentVM.arg.postValue(null)
            navigate(Main.adv) {
                setNoneAnim()
                setLaunchSingleTop()
            }
        }

    }


}