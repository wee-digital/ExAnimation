package wee.digital.ft.ui.message

import android.view.View
import kotlinx.android.synthetic.main.message.*
import wee.digital.ft.R
import wee.digital.ft.ui.MainDialog

class MessageFragment : MainDialog() {

    override fun layoutResource(): Int {
        return R.layout.message
    }

    override fun onViewCreated() {
        addClickListener(messageViewClose)
    }

    override fun onLiveDataObserve() {
        sharedVM.message.observe {
            if (it != null) {
                messageView.onBindArg(it)
            } else {
                dismissAllowingStateLoss()
            }
        }
    }

    override fun onViewClick(v: View?) {
        when (v) {
            messageViewClose -> {
                dismiss()
                sharedVM.message.value?.onClose?.also { it(this) }
            }
        }
    }
}