package wee.digital.ft.ui.progress

import wee.digital.ft.R

data class ProgressArg(
        var image: Int,
        var title: String,
        var message: String
) {
    override fun equals(other: Any?): Boolean {
        return image == (other as? ProgressArg)?.image
    }

    companion object {

        val pay = ProgressArg(
                image = R.mipmap.img_progress_small,
                title = "CHÚNG TÔI ĐANG XỬ LÝ THANH TOÁN",
                message = "BẠN CHỜ MỘT CHÚT NHÉ"
        )

        val paid = ProgressArg(
                image = R.mipmap.img_face_paid,
                title = "CHÚNG TÔI ĐANG XỬ LÝ THANH TOÁN",
                message = "BẠN CHỜ MỘT CHÚT NHÉ"
        )
    }
}