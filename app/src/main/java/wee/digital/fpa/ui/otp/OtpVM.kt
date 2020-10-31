package wee.digital.fpa.ui.otp

import androidx.lifecycle.MutableLiveData
import wee.digital.fpa.R
import wee.digital.fpa.ui.base.BaseViewModel
import wee.digital.fpa.ui.confirm.ConfirmArg

class OtpVM : BaseViewModel() {

    val otpForm = MutableLiveData<String>()

    fun temp(){
        val arg = ConfirmArg(
                headerGuideline = R.id.guidelineFace,
                title = "Giao dịch bị hủy bỏ",
                message = "Lỗi thanh toán. Bạn vui lòng chọn thẻ khác".format(),
                buttonAccept = "Đồng ý",
                onAccept = {

                },
                buttonDeny = "Huỷ bỏ",
                onDeny = {

                },
        )
    }
}
