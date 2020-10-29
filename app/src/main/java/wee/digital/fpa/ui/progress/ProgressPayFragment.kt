package wee.digital.fpa.ui.progress

import kotlinx.android.synthetic.main.progress_pay.*
import wee.digital.fpa.R
import wee.digital.fpa.ui.Main
import wee.digital.fpa.ui.base.activityVM
import wee.digital.library.extension.load
import wee.digital.library.util.Media

class ProgressPayFragment : Main.Dialog() {

    private val progressVM by lazy { activityVM(ProgressVM::class) }

    override fun layoutResource(): Int {
        return R.layout.progress_pay
    }

    override fun onViewCreated() {

    }

    override fun onLiveDataObserve() {
        progressVM.arg.observe {
            if (it == null){
                dismiss()
            } else {
                onBindArg(it)
            }
        }
    }

    private fun onBindArg(it: ProgressArg?) {
        progressImageViewPay.load(it?.image!!)
        view?.postDelayed({
            Media.play(it?.sound)
        },it?.soundDelayed)

    }


}