package wee.digital.fpa.ui.confirm

import wee.digital.fpa.R
import wee.digital.fpa.ui.Main
import kotlin.reflect.KClass

class ConfirmFragment : Main.Dialog<ConfirmVM>() {

    private val confirmView by lazy { ConfirmView(this) }

    override fun layoutResource(): Int {
        return R.layout.confirm
    }

    override fun localViewModel(): KClass<ConfirmVM> {
        return ConfirmVM::class
    }

    override fun onViewCreated() {
    }

    override fun onLiveDataObserve() {
        localVM.arg.observe {
            it ?: return@observe
            confirmView.onBindArg(it)
        }
    }
    override fun onLiveEventChanged(event: Int) {
    }



}