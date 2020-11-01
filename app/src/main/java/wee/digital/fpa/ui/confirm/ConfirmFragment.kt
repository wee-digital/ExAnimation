package wee.digital.fpa.ui.confirm

import androidx.constraintlayout.widget.ConstraintSet
import kotlinx.android.synthetic.main.confirm.*
import wee.digital.fpa.R
import wee.digital.fpa.ui.MainDialog
import wee.digital.library.extension.setHyperText
import wee.digital.library.extension.string

class ConfirmFragment : MainDialog() {

    override fun layoutResource(): Int {
        return R.layout.confirm
    }

    override fun onViewCreated() {
    }

    override fun onLiveDataObserve() {
        sharedVM.confirm.observe {
            onBindArg(it)
        }
    }

    private fun onBindArg(arg: ConfirmArg?) {
        arg ?: return
        onBindDialogSize(arg.headerGuideline)
        confirmImageViewIcon.setImageResource(arg.icon)
        confirmTextViewTitle.text = arg.title ?: string(R.string.app_name)
        confirmTextViewMessage.setHyperText(arg.message)
        confirmViewAccept.text = arg.buttonAccept ?: "Xác nhận"
        confirmViewDeny.text = arg.buttonDeny ?: "Đóng"
        confirmViewAccept.setOnClickListener {
            dismiss()
            arg.onAccept(this)
        }
        confirmViewDeny.setOnClickListener {
            dismiss()
            arg.onDeny(this)
        }
    }

    private fun onBindDialogSize(guidelineId: Int) {
        if (guidelineId == 0) return
        val viewId = confirmDialogContent.id
        ConstraintSet().apply {
            clone(viewContent)
            constrainHeight(viewId, ConstraintSet.MATCH_CONSTRAINT)
            constrainDefaultHeight(viewId, ConstraintSet.MATCH_CONSTRAINT)
            connect(viewId, ConstraintSet.TOP, guidelineId, ConstraintSet.TOP)
            applyTo(viewContent)
        }
    }
}