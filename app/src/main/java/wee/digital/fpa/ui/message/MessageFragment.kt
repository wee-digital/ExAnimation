package wee.digital.fpa.ui.message

import android.view.View
import wee.digital.fpa.R
import wee.digital.fpa.ui.Main

class MessageFragment : Main.Dialog() {

    private val messageView by lazy { MessageView(this) }

    override fun layoutResource(): Int {
        return R.layout.message
    }

    override fun onViewCreated() {
    }

    override fun onLiveDataObserve() {
        messageVM.arg.observe {
            messageView.onBindArg(it)
        }
    }

    override fun onViewClick(v: View?) {
        when (v) {
        }
    }
}