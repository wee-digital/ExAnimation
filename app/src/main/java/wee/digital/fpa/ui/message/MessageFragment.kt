package wee.digital.fpa.ui.message

import android.view.View
import kotlinx.android.synthetic.main.message.*
import wee.digital.fpa.R
import wee.digital.fpa.ui.Main
import wee.digital.fpa.ui.base.BaseDialog

class MessageFragment : BaseDialog() {

    override fun layoutResource(): Int {
        return R.layout.message
    }

    override fun onViewCreated() {
        addClickListener(dialogViewClose)
    }

    override fun onLiveDataObserve() {
        Main.messageArg.observe {
            onBindArg(it)
        }
    }

    override fun onViewClick(v: View?) {
        when (v) {
            dialogViewClose -> navigateUp()
        }
    }

    private fun onBindArg(arg: MessageArg) {
        dialogTextViewIcon.setImageResource(arg.icon)
        dialogTextViewTitle.text = arg.title
        dialogTextViewMessage.text = arg.message
    }

}