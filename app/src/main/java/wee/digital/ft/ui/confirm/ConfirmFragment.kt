package wee.digital.ft.ui.confirm

import android.view.View
import kotlinx.android.synthetic.main.confirm.*
import wee.digital.ft.R
import wee.digital.ft.ui.MainDialog

class ConfirmFragment : MainDialog() {

    override fun layoutResource(): Int {
        return R.layout.confirm
    }

    override fun onViewCreated() {
        addClickListener(confirmViewAccept, confirmViewDeny)
    }

    override fun onLiveDataObserve() {
        sharedVM.confirm.observe {
            it?.also {
                confirmView.onBindArg(it)
                return@observe
            }
            dismissAllowingStateLoss()
            sharedVM.onPaymentCancel()
        }
    }

    override fun onViewClick(v: View?) {
        when (v) {
            confirmViewAccept -> {
                dismiss()
                sharedVM.confirm.value?.onAccept?.also { it(this) }
            }
            confirmViewDeny -> {
                dismiss()
                sharedVM.confirm.value?.onDeny?.also { it(this) }
            }
        }
    }

}