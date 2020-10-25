package wee.digital.fpa.ui.message

import android.view.View
import kotlinx.android.synthetic.main.message.*
import wee.digital.fpa.R
import wee.digital.fpa.ui.base.BaseDialog
import wee.digital.fpa.ui.base.BaseFragment

class MessageFragment : BaseDialog() {

    private val vm: MessageVM by lazy { activityVM(MessageVM::class) }

    override fun layoutResource(): Int {
        return R.layout.message
    }

    override fun onViewCreated() {
        addClickListener(dialogViewClose)
    }

    override fun onLiveDataObserve() {
        vm.argLiveData.observe {
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