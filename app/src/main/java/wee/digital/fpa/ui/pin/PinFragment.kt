package wee.digital.fpa.ui.pin

import android.view.View
import kotlinx.android.synthetic.main.pin.*
import wee.digital.fpa.R
import wee.digital.fpa.ui.base.BaseDialog

class PinFragment : BaseDialog() {

    private val vm: PinVM by lazy { viewModel(PinVM::class) }

    private val v: PinView by lazy { PinView(this) }

    override fun layoutResource(): Int {
        return R.layout.pin
    }

    override fun onViewCreated() {
        v.onViewInit()
        pinProgressLayout.onItemFilled = {
            vm.onPinFilled(it)
        }
    }

    override fun onLiveDataObserve() {
        vm.errorMessage.observe {
            v.onBindErrorText(it)
        }
    }

    override fun onViewClick(v: View?) {
        when (v) {
            dialogViewClose -> {
                dismiss()
            }
        }
    }

}