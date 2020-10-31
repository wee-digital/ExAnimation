package wee.digital.fpa.ui.progress

import kotlinx.android.synthetic.main.progress.*
import wee.digital.fpa.R
import wee.digital.fpa.ui.Main
import wee.digital.library.extension.*
import wee.digital.library.util.Media

class ProgressFragment : Main.Dialog() {

    override fun layoutResource(): Int {
        return R.layout.progress
    }

    override fun onViewCreated() {
        progressImageViewPay.clear()
    }

    override fun onLiveDataObserve() {
        progressVM.arg.observe {
            when (it) {
                null -> dismiss()
                ProgressArg.paid -> onBindPaid()
                else -> onBindProgress(it)
            }
        }
    }

    private fun onBindProgress(it: ProgressArg) {
        progressImageView.load(it.image)
        progressTextViewTitle.text = it.title
        progressTextViewMessage.text = it.message
    }

    private fun onBindPaid() {
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