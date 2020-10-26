package wee.digital.fpa.ui.adv

class AdvItem(
        val res: String /* uri/url/.. */
) {

    val isImage: Boolean
        get() {
            return when (res.substringAfter(".")) {
                "jpg", "jpeg", "png", "bmp", "gif", "webp" -> true
                else -> false
            }
        }

    companion object {
        val defaultList = listOf(
                AdvItem("/a/b/c/image.jpg"),
                AdvItem("/a/b/c/video.mp4")
        )
    }

}