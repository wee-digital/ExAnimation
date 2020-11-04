package wee.digital.ft.ui.message

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import kotlinx.android.synthetic.main.message.view.*
import wee.digital.ft.R
import wee.digital.library.extension.hide
import wee.digital.library.extension.setHyperText
import wee.digital.library.extension.show
import wee.digital.library.extension.string

class MessageView : ConstraintLayout {

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    fun onBindArg(arg: MessageArg) {
        onBindDialogSize(arg.headerGuideline)
        messageImageViewIcon.setImageResource(arg.icon)
        messageTextViewTitle.text = arg.title ?: string(R.string.app_name)
        messageTextViewMessage.setHyperText(arg.message)
        if (arg.buttonClose.isNullOrEmpty()) {
            messageViewClose.hide()
        } else {
            messageViewClose.show()
            messageViewClose.text = arg.buttonClose
        }
    }

    private fun onBindDialogSize(guidelineId: Int) {
        if (guidelineId == 0) return
        val viewId = messageDialogContent.id
        ConstraintSet().apply {
            clone(this@MessageView)
            constrainHeight(viewId, ConstraintSet.MATCH_CONSTRAINT)
            constrainDefaultHeight(viewId, ConstraintSet.MATCH_CONSTRAINT)
            connect(viewId, ConstraintSet.TOP, guidelineId, ConstraintSet.TOP)
            applyTo(this@MessageView)
        }
    }


}