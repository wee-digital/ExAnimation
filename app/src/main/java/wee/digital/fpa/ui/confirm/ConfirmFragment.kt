package wee.digital.fpa.ui.confirm

import android.view.View
import kotlinx.android.synthetic.main.confirm.*
import wee.digital.fpa.R
import wee.digital.fpa.ui.base.BaseDialog
import wee.digital.fpa.ui.base.BaseFragment

class ConfirmFragment : BaseDialog() {

    private val vm: ConfirmVM by lazy { activityVM(ConfirmVM::class) }

    override fun layoutResource(): Int {
        return R.layout.confirm
    }

    override fun onViewCreated() {
        addClickListener(dialogViewAccept, dialogViewDeny)
    }

    override fun onLiveDataObserve() {
        vm.argLiveData.observe {
            onBindArg(it)
        }
    }

    override fun onViewClick(v: View?) {
        navigateUp()
        when (v) {
            dialogViewAccept -> vm.argLiveData.value?.also {
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