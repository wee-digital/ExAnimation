package wee.digital.fpa.ui.confirm

import android.view.View
import kotlinx.android.synthetic.main.confirm.*
import wee.digital.fpa.R
import wee.digital.fpa.ui.Main
import wee.digital.fpa.ui.base.BaseDialog

class ConfirmFragment : BaseDialog() {


    override fun layoutResource(): Int {
        return R.layout.confirm
    }

    override fun onViewCreated() {
        addClickListener(dialogViewAccept, dialogViewDeny)
    }

    override fun onLiveDataObserve() {
        Main.confirmArg.observe {
            onBindArg(it)
        }
    }

    override fun onViewClick(v: View?) {
        navigateUp()
        when (v) {
            dialogViewAccept -> Main.confirmArg.value?.also {
                it.onAccept()
            }
        }
    }

    private fun onBindArg(arg: ConfirmArg) {
        dialogTextViewIcon.setImageResource(arg.icon)
        dialogTextViewTitle.text = arg.title
        dialogTextViewMessage.text = arg.message
    }

}