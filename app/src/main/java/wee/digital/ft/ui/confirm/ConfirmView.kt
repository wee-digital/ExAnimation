package wee.digital.ft.ui.confirm

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import kotlinx.android.synthetic.main.confirm.view.*
import wee.digital.ft.R
import wee.digital.library.extension.setHyperText
import wee.digital.library.extension.string

class ConfirmView : ConstraintLayout {

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    fun onBindArg(arg: ConfirmArg) {
        onBindDialogSize(arg.headerGuideline)
        confirmImageViewIcon.setImageResource(arg.icon)
        confirmTextViewTitle.text = arg.title ?: string(R.string.app_name)
        confirmTextViewMessage.setHyperText(arg.message)
        confirmViewAccept.text = arg.buttonAccept
        confirmViewDeny.text = arg.buttonDeny
    }

    private fun onBindDialogSize(guidelineId: Int) {
        if (guidelineId == 0) return
        val viewId = confirmDialogContent.id
        ConstraintSet().apply {
            clone(this@ConfirmView)
            constrainHeight(viewId, ConstraintSet.MATCH_CONSTRAINT)
            constrainDefaultHeight(viewId, ConstraintSet.MATCH_CONSTRAINT)
            connect(viewId, ConstraintSet.TOP, guidelineId, ConstraintSet.TOP)
            applyTo(this@ConfirmView)
        }
    }
}