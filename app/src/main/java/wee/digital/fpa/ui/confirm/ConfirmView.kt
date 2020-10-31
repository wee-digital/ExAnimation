package wee.digital.fpa.ui.confirm

import androidx.constraintlayout.widget.ConstraintSet
import kotlinx.android.synthetic.main.confirm.*
import wee.digital.fpa.R
import wee.digital.library.extension.setHyperText
import wee.digital.library.extension.string

class ConfirmView(val v: ConfirmFragment) {

    fun onBindArg(arg: ConfirmArg?) {
        arg ?: return
        onBindDialogSize(arg.headerGuideline)
        v.confirmImageViewIcon.setImageResource(arg.icon)
        v.confirmTextViewTitle.text = arg.title ?: string(R.string.app_name)
        v.confirmTextViewMessage.setHyperText(arg.message)
        v.confirmViewAccept.text = arg.buttonAccept ?: "Xác nhận"
        v.confirmViewDeny.text = arg.buttonDeny ?: "Đóng"
        v.confirmViewAccept.setOnClickListener {
            v.dismiss()
            arg.onAccept(v)
        }
        v.confirmViewDeny.setOnClickListener {
            v.dismiss()
            arg.onDeny(v)
        }
    }

    private fun onBindDialogSize(guidelineId: Int) {
        if (guidelineId == 0) return
        val viewId = v.confirmDialogContent.id
        ConstraintSet().apply {
            clone(v.viewContent)
            constrainHeight(viewId, ConstraintSet.MATCH_CONSTRAINT)
            constrainDefaultHeight(viewId, ConstraintSet.MATCH_CONSTRAINT)
            connect(viewId, ConstraintSet.TOP, guidelineId, ConstraintSet.TOP)
            applyTo(v.viewContent)
        }
    }
}