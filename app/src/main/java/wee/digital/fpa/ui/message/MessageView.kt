package wee.digital.fpa.ui.message

import androidx.constraintlayout.widget.ConstraintSet
import kotlinx.android.synthetic.main.message.*
import wee.digital.fpa.R
import wee.digital.library.extension.hide
import wee.digital.library.extension.setHyperText
import wee.digital.library.extension.show
import wee.digital.library.extension.string

class MessageView(val v: MessageFragment) {

    fun onBindArg(arg: MessageArg?) {
        arg ?: return
        onBindDialogSize(arg.headerGuideline)
        v.messageImageViewIcon.setImageResource(arg.icon ?: R.mipmap.img_checked_flat)
        v.messageTextViewTitle.text = arg.title ?: string(R.string.app_name)
        v.messageTextViewMessage.setHyperText(arg.message)

        if (arg.button.isNullOrEmpty()) {
            v.messageViewClose.hide()
        } else {
            v.messageViewClose.show()
            v.messageViewClose.text = arg.button
        }

        v.messageViewClose.setOnClickListener {
            v.dismiss()
            arg.onClose()
        }
    }

    private fun onBindDialogSize(guidelineId: Int) {
        if (guidelineId == 0) return
        val viewId = v.messageDialogContent.id
        ConstraintSet().apply {
            clone(v.viewContent)
            constrainHeight(viewId, ConstraintSet.MATCH_CONSTRAINT)
            constrainDefaultHeight(viewId, ConstraintSet.MATCH_CONSTRAINT)
            connect(viewId, ConstraintSet.TOP, guidelineId, ConstraintSet.TOP)
            applyTo(v.viewContent)
        }
    }
}