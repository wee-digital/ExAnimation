package wee.digital.fpa.ui.message

import androidx.constraintlayout.widget.ConstraintSet
import kotlinx.android.synthetic.main.message.*
import wee.digital.fpa.R
import wee.digital.fpa.ui.MainDialog
import wee.digital.fpa.ui.onPaymentCancel
import wee.digital.library.extension.hide
import wee.digital.library.extension.setHyperText
import wee.digital.library.extension.show
import wee.digital.library.extension.string

class MessageFragment : MainDialog() {

    override fun layoutResource(): Int {
        return R.layout.message
    }

    override fun onViewCreated() {
    }

    override fun onLiveDataObserve() {
        sharedVM.message.observe {
            onBindArg(it)
        }
    }

    private fun onBindArg(arg: MessageArg?) {
        arg ?: return
        onBindDialogSize(arg.headerGuideline)
        messageImageViewIcon.setImageResource(arg.icon)
        messageTextViewTitle.text = arg.title ?: string(R.string.app_name)
        messageTextViewMessage.setHyperText(arg.message)

        if (arg.button.isNullOrEmpty()) {
            messageViewClose.hide()
        } else {
            messageViewClose.show()
            messageViewClose.text = arg.button
        }

        messageViewClose.setOnClickListener {
            dismiss()
            arg.onClose(this)
        }
    }

    private fun onBindDialogSize(guidelineId: Int) {
        if (guidelineId == 0) return
        val viewId = messageDialogContent.id
        ConstraintSet().apply {
            clone(viewContent)
            constrainHeight(viewId, ConstraintSet.MATCH_CONSTRAINT)
            constrainDefaultHeight(viewId, ConstraintSet.MATCH_CONSTRAINT)
            connect(viewId, ConstraintSet.TOP, guidelineId, ConstraintSet.TOP)
            applyTo(viewContent)
        }
    }

}