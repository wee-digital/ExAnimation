package wee.digital.fpa.ui.confirm

import kotlinx.android.synthetic.main.confirm.*
import wee.digital.fpa.R
import wee.digital.library.extension.setHyperText
import wee.digital.library.extension.string

class ConfirmView(val v: ConfirmFragment) {

    fun onBindArg(arg: ConfirmArg) {
        v.confirmImageViewIcon.setImageResource(arg.icon ?: R.mipmap.img_checked_flat)
        v.confirmTextViewTitle.text = arg.title ?: string(R.string.app_name)
        v.confirmTextViewMessage.setHyperText(arg.message)
        v.confirmViewAccept.text = arg.buttonAccept ?: "Xác nhận"
        v.confirmViewDeny.text = arg.buttonDeny ?: "Đóng"
        v.confirmViewAccept.setOnClickListener {
            v.dismiss()
            arg.onAccept()
        }
        v.confirmViewDeny.setOnClickListener {
            v.dismiss()
            arg.onDeny()
        }
    }
}