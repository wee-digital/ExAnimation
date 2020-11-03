package wee.digital.ft.ui.progress

import kotlinx.android.synthetic.main.progress.*
import wee.digital.ft.R
import wee.digital.ft.ui.MainDialog
import wee.digital.ft.ui.onPaymentCancel
import wee.digital.library.extension.*
import wee.digital.library.util.Media

class ProgressFragment : MainDialog() {

    override fun layoutResource(): Int {
        return R.layout.progress
    }

    override fun onViewCreated() {
        progressImageViewPay.clear()
    }

    override fun onLiveDataObserve() {
        sharedVM.progress.observe {
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
        progressTextViewMessage.gradientHorizontal(R.color.colorPrimaryStart,R.color.colorPrimaryEnd)
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
            sharedVM.progress.postValue(null)
            onPaymentCancel()
        }

    }


}