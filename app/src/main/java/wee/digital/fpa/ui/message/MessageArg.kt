package wee.digital.fpa.ui.message

import wee.digital.fpa.R

data class MessageArg(
        var icon: Int? = null,
        var title: String? = null,
        var button: String? = "Đóng",
        var message: String? = null,
        var headerGuideline: Int = 0,
        var onClose: () -> Unit = {}
) {
    companion object {

        val paymentCancelMessage
            get() = MessageArg(
                    headerGuideline = R.id.guidelineConnect,
                    icon = R.mipmap.img_x_mark_flat,
                    title = "Giao dịch bị hủy bỏ",
                    button = null,
                    message = "Yêu cầu thanh toán của bạn đã bị hủy. Vui lòng liên hệ với nhân viên để biết thêm thông tin"
            )
    }
}