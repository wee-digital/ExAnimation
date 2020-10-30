package wee.digital.fpa.ui.progress

import androidx.navigation.NavDirections
import wee.digital.fpa.MainDirections
import wee.digital.fpa.R

data class ProgressArg(
        var direction : NavDirections = MainDirections.actionGlobalProgressFragment(),
        var image: Int = R.mipmap.img_progress_small,
        var title: String,
        var message: String
) {
    companion object {

        val payment = ProgressArg(
                title = "CHÚNG TÔI ĐANG XỬ LÝ THANH TOÁN",
                message = "BẠN CHỜ MỘT CHÚT NHÉ"
        )
    }
}