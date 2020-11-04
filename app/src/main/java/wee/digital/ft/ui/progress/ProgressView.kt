package wee.digital.ft.ui.progress

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import kotlinx.android.synthetic.main.progress.view.*
import wee.digital.ft.R
import wee.digital.library.extension.gradientHorizontal
import wee.digital.library.extension.hide
import wee.digital.library.extension.load
import wee.digital.library.extension.loadGif

class ProgressView : ConstraintLayout {

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    fun onBindProgress(it: ProgressArg) {
        onBindDialogSize(it.headerGuideline)
        progressImageView.load(it.image)
        progressTextViewTitle.text = it.title
        progressTextViewMessage.gradientHorizontal(R.color.colorPrimaryStart, R.color.colorPrimaryEnd)
        progressTextViewMessage.text = it.message
    }

    fun onBindPaid(it: ProgressArg) {
        onBindDialogSize(it.headerGuideline)
        hide(progressImageView, progressTextViewTitle, progressTextViewMessage)
        progressImageViewPay.loadGif(R.mipmap.img_face_paid)
    }

    private fun onBindDialogSize(guidelineId: Int) {

        if (guidelineId == 0) return
        val viewId = progressViewContent.id
        ConstraintSet().apply {
            clone(this@ProgressView)
            constrainHeight(viewId, ConstraintSet.MATCH_CONSTRAINT)
            constrainDefaultHeight(viewId, ConstraintSet.MATCH_CONSTRAINT)
            connect(viewId, ConstraintSet.TOP, guidelineId, ConstraintSet.TOP)
            applyTo(this@ProgressView)
        }
    }

}