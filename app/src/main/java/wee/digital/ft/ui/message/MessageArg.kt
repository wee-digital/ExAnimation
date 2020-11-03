package wee.digital.ft.ui.message

import wee.digital.ft.R
import wee.digital.ft.repository.utils.ErrCode

data class MessageArg(
        var headerGuideline: Int = R.id.guidelineHeader,
        var icon: Int = R.mipmap.img_x_mark_flat,
        var title: String? = null,
        var message: String? = null,
        var button: String? = null,
        var timeOut: Int = 5,
        var onClose: (MessageFragment) -> Unit = {}
) {

    companion object {

        val paymentCancel
            get() = MessageArg(
                    title = "Giao dịch bị hủy bỏ",
                    message = "Yêu cầu thanh toán của bạn đã bị hủy. Vui lòng liên hệ với nhân viên để biết thêm thông tin"
            )

        fun fromCode(code: Int): MessageArg {
            return when (code) {
                ErrCode.BANK_UNKNOWN_ERROR -> MessageArg(
                        title = "Thanh toán không thành công",
                        message = "Tài khoản ngân hàng liên kết hiện tại không thể thực hiện thanh toán. Bạn Vui lòng thử lại"
                )

                ErrCode.USER_BANK_ACC_NOT_FOUND -> MessageArg(
                        title = "Thanh toán không thành công",
                        message = "Không tìm thấy tài khoản ngân hàng đã liên kết"
                )

                ErrCode.ACC_FACEPAY_LOCKED, ErrCode.ACC_LOCKED -> MessageArg(
                        title = "Thanh toán không thành công",
                        message = "Tài khoản Facepay của bạn hiện đang tạm khóa chức năng thanh toán."
                )

                ErrCode.NOT_LINKED_BANK_ACC, ErrCode.BANK_ACC_NOT_FOUND -> MessageArg(
                        title = "Tài khoản chưa liên kết ngân hàng",
                        message = "Giao dịch không thể thực hiện do tài khoản của bạn chưa liên kết ngân hàng. Bạn vui lòng liên kết tài khoản trước khi thanh toán"
                )

                ErrCode.INSUFFICIENT_ACC_BALANCE -> MessageArg(
                        title = "Số dư không đủ để thực hiện thanh toán",
                        message = "Tài khoản liên kết hiện tại không đủ số dư để thực hiện giao dịch"
                )

                ErrCode.FACE_NOT_FOUND -> MessageArg(
                        title = "Tài khoản không tồn tại",
                        message = "Bạn vui lòng đăng ký tài khoản Facepay trước khi thực hiện thanh toán"
                )

                ErrCode.USER_IS_OWNER -> MessageArg(
                        title = "Thanh toán không thành công",
                        message = "Chủ shop không thể thanh toán, bạn vui lòng thử lại"
                )


                ErrCode.TRANS_EXCEED_LIMIT -> MessageArg(
                        title = "Quá hạn mức giao dịch",
                        message = "Giao dịch không thể thực hiện do số tiền yêu cầu thanh toán vượt quá hạn mức giao dịch của tài khoản."
                )
                ErrCode.BILL_PAID -> MessageArg(
                        title = "Thanh toán không thành công",
                        message = "Giao dịch của bạn đã được thanh toán trước đó"
                )

                else -> MessageArg(
                        title = "Thanh toán không thành công",
                        message = "Phát hiện lỗi, Vui lòng thử lại"
                )
            }
        }
    }
}