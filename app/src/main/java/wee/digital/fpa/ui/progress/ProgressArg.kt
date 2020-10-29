package wee.digital.fpa.ui.progress

import androidx.navigation.NavDirections
import wee.digital.fpa.MainDirections
import wee.digital.fpa.R

data class ProgressArg(
        val direction : NavDirections = MainDirections.actionGlobalProgressFragment(),
        val image: Int,
        val title: String,
        val message: String
) {
    companion object {

        val payment = ProgressArg(
                image = R.mipmap.img_progress,
                title = "CHÚNG TÔI ĐANG XỬ LÝ THANH TOÁN",
                message = "BẠN CHỜ MỘT CHÚT NHÉ"
        )
    }
}