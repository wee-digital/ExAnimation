package wee.digital.fpa.ui.message

import wee.digital.fpa.R

data class MessageArg(
        var headerGuideline: Int = R.id.guidelineHeader,
        var icon: Int = R.mipmap.img_x_mark_flat,
        var title: String? = null,
        var message: String? = null,
        var button: String? = "Đóng",
        var onClose: (MessageFragment) -> Unit = {}
) {
    companion object {

        val paymentCancelMessage
            get() = MessageArg(
                    title = "Giao dịch bị hủy bỏ",
                    button = null,
                    message = "Yêu cầu thanh toán của bạn đã bị hủy. Vui lòng liên hệ với nhân viên để biết thêm thông tin"
            )
    }
}