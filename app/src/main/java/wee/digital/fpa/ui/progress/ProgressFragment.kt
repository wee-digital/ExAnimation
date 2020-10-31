package wee.digital.fpa.ui.progress

import kotlinx.android.synthetic.main.progress.*
import wee.digital.fpa.R
import wee.digital.fpa.ui.Main
import wee.digital.library.extension.load

class ProgressFragment : Main.Dialog() {

    override fun layoutResource(): Int {
        return R.layout.progress
    }

    override fun onViewCreated() {

    }

    override fun onLiveDataObserve() {
        progressVM.arg.observe {
            if (it == null) {
                dismiss()
            } else {
                onBindArg(it)
            }
        }
    }

    private fun onBindArg(it: ProgressArg) {
        progressImageView.load(it.image)
        progressTextViewTitle.text = it.title
        progressTextViewMessage.text = it.message
    }


}