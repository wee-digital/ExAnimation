package wee.digital.fpa.ui.confirm

import kotlinx.android.synthetic.main.confirm.*
import wee.digital.fpa.R
import wee.digital.fpa.ui.Main
import wee.digital.fpa.ui.base.BaseDialog
import wee.digital.fpa.ui.base.activityVM
import wee.digital.fpa.ui.device.DeviceVM
import wee.digital.library.extension.string

class ConfirmFragment : BaseDialog() {

    private val vm by lazy { activityVM(ConfirmVM::class) }

    override fun layoutResource(): Int {
        return R.layout.confirm
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

    private fun onBindArg(arg: ConfirmArg) {
        dialogTextViewIcon.setImageResource(arg.icon ?: R.mipmap.img_checked_flat)
        dialogTextViewTitle.text = arg.title ?: string(R.string.app_name)
        dialogTextViewMessage.text = arg.message
        dialogViewAccept.text = arg.buttonAccept ?: "Xác nhận"
        dialogViewDeny.text = arg.buttonDeny ?: "Đóng"
        dialogViewAccept.setOnClickListener {
            arg.onAccept()
            dismiss()
        }
        dialogViewDeny.setOnClickListener {
            arg.onDeny()
            dismiss()
        }
    }

}