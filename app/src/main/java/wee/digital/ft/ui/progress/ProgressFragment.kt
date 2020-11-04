package wee.digital.ft.ui.progress

import kotlinx.android.synthetic.main.progress.*
import wee.digital.ft.R
import wee.digital.ft.ui.MainDialog
import wee.digital.library.extension.clear
import wee.digital.library.extension.post
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
                ProgressArg.paid -> {
                    progressView.onBindPaid(it)
                    post(2000) {
                        Media.play(R.raw.facepay_sound)
                    }
                    post(6000) {
                        dismissAllowingStateLoss()
                        sharedVM.onPaymentCancel()
                    }
                }
                else -> {
                    progressView.onBindProgress(it)
                }
            }
        }
    }


}