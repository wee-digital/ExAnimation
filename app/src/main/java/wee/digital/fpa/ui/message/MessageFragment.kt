package wee.digital.fpa.ui.message

import kotlinx.android.synthetic.main.message.*
import wee.digital.fpa.R
import wee.digital.fpa.ui.base.BaseDialog
import wee.digital.fpa.ui.base.activityVM
import wee.digital.library.extension.string

class MessageFragment : BaseDialog() {

    private val vm by lazy { activityVM(MessageVM::class) }

    override fun layoutResource(): Int {
        return R.layout.message
    }

    override fun onViewCreated() {
    }

    override fun onLiveDataObserve() {
        vm.arg.observe {
            it?.also {
                onBindArg(it)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        vm.arg.value = null
    }


    private fun onBindArg(arg: MessageArg) {
        dialogTextViewIcon.setImageResource(arg.icon ?: R.mipmap.img_checked_flat)
        dialogTextViewTitle.text = arg.title ?: string(R.string.app_name)
        dialogTextViewMessage.text = arg.message
        dialogViewClose.text = arg.button ?: "Đóng"
        dialogViewClose.setOnClickListener {
            arg.onClose()
            dismiss()
        }
    }

}