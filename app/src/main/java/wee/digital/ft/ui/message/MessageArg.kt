package wee.digital.ft.ui.message

import wee.digital.ft.R
import wee.digital.ft.repository.utils.ErrCode
import wee.digital.ft.shared.Shared
import wee.digital.ft.shared.Timeout

open class MessageArg {

    var headerGuideline: Int = R.id.guidelineHeader
    var icon: Int = R.mipmap.img_x_mark_flat
    var title: String? = null
    var timeout: Int = Timeout.ALERT_DIALOG
    var message: String? = null
    var buttonClose: String? = null
    var onClose: (MessageFragment) -> Unit = {}

    companion object {

        private val placeHolder = MessageArg().apply {
            title = ""
            message = ""
        }

        val timedOutError = MessageArg().apply {
            title = "Đã hết thời gian"
            message = "Bạn vui lòng thực hiện lại giao dịch"
        }

        val faceNotExistedError = MessageArg().apply {
            title = "Bạn chưa đăng ký tài khoản Facepay"
            message = "Bạn vui lòng thử lại hoặc tải ứng dụng ${Shared.facePayText}<br/>để đăng ký tài khoản"
        }

        val wrongPinManyTimes = MessageArg().apply {
            title = "Sai mã PIN"
            message = "Bạn đã sai mã PIN quá nhiều lần,<br/>vui lòng kiểm tra lại mã PIN của bạn"
        }

        val systemError = MessageArg().apply {
            title = "Có lỗi xảy ra"
            message = "Bạn vui lòng thử lại hoặc liên hệ<br/>nhân viên để được hỗ trợ"
        }

        val paymentError = MessageArg().apply {
            title = "Thanh toán không thành công"
            message = "Bạn vui lòng kiểm tra thông tin thẻ thanh toán<br/>hoặc liên hệ nhân viên để được hỗ trợ"
        }

        val connectionError = MessageArg().apply {
            title = "Có lỗi xảy ra"
            message = "Kết nối mạng không ổn định, bạn vui lòng thử lại"
        }

        val accountSuspendedError = MessageArg().apply {
            title = "Tài khoản của bạn đã bị khóa"
            message = "Tài khoản ${Shared.facePayText} của bạn tạm thời không thể thanh toán, bạn vui lòng kiểm tra lại"
        }

        val facepayDisabledError = MessageArg().apply {
            title = "Bạn đang tắt tính năng thanh toán"
            message = "Bạn vui lòng mở lại tính năng thanh toán<br/>trong tài khoản ${Shared.facePayText}"
        }

        val noBankAccountError = MessageArg().apply {
            title = "Bạn chưa liên kết ngân hàng"
            message = "Bạn vui lòng liên kết tài khoản ngân hàng<br/>để thanh toán ${Shared.facePayText}"
        }

        val paymentLimitError = MessageArg().apply {
            title = "Vượt quá hạn mức"
            message = "Bạn vui lòng kiểm tra lại cài đặt<br/>hạn mức giao dịch của tài khoản"
        }

        val insufficient = MessageArg().apply {
            title = "Số dư không đủ để thực hiện thanh toán"
            message = "Tài khoản liên kết hiện tại không đủ số dư để thực hiện giao dịch"
        }

        fun fromCode(code: Int): MessageArg {
            return when (code) {
                ErrCode.NOT_LINKED_BANK_ACC, ErrCode.BANK_ACC_NOT_FOUND -> noBankAccountError
                ErrCode.TRANS_EXCEED_LIMIT -> paymentLimitError
                ErrCode.ACC_FACEPAY_LOCKED, ErrCode.ACC_LOCKED -> facepayDisabledError

                ErrCode.BANK_UNKNOWN_ERROR -> MessageArg().apply {
                    title = "Thanh toán không thành công"
                    message = "Tài khoản ngân hàng liên kết hiện tại không thể thực hiện thanh toán. Bạn Vui lòng thử lại"
                }
                ErrCode.USER_BANK_ACC_NOT_FOUND -> MessageArg().apply {
                    title = "Thanh toán không thành công"
                    message = "Không tìm thấy tài khoản ngân hàng đã liên kết"
                }


                ErrCode.INSUFFICIENT_ACC_BALANCE -> insufficient

                ErrCode.FACE_NOT_FOUND -> MessageArg().apply {
                    title = "Tài khoản không tồn tại"
                    message = "Bạn vui lòng đăng ký tài khoản ${Shared.facePayText} trước khi thực hiện thanh toán"
                }

                ErrCode.USER_IS_OWNER -> MessageArg().apply {
                    title = "Thanh toán không thành công"
                    message = "Chủ shop không thể thanh toán, bạn vui lòng thử lại"
                }


                ErrCode.BILL_PAID -> MessageArg().apply {
                    title = "Thanh toán không thành công"
                    message = "Giao dịch của bạn đã được thanh toán trước đó"
                }

                else -> MessageArg().apply {
                    title = "Thanh toán không thành công"
                    message = "Phát hiện lỗi, Vui lòng thử lại"
                }
            }
        }

    }
}