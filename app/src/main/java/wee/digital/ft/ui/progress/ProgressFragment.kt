package wee.digital.ft.ui.progress

import androidx.constraintlayout.widget.ConstraintSet
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
                ProgressArg.paid -> onBindPaid(it)
                else -> onBindProgress(it)
            }
        }
    }

    private fun onBindProgress(it: ProgressArg) {
        onBindDialogSize(it.headerGuideline)
        progressImageView.load(it.image)
        progressTextViewTitle.text = it.title
        progressTextViewMessage.gradientHorizontal(R.color.colorPrimaryStart, R.color.colorPrimaryEnd)
        progressTextViewMessage.text = it.message
    }

    private fun onBindPaid(it: ProgressArg) {
        onBindDialogSize(it.headerGuideline)
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

    private fun onBindDialogSize(guidelineId: Int) {

        if (guidelineId == 0) return
        val viewId = progressViewContent.id
        ConstraintSet().apply {
            clone(viewContent)
            constrainHeight(viewId, ConstraintSet.MATCH_CONSTRAINT)
            constrainDefaultHeight(viewId, ConstraintSet.MATCH_CONSTRAINT)
            connect(viewId, ConstraintSet.TOP, guidelineId, ConstraintSet.TOP)
            applyTo(viewContent)
        }
    }
}