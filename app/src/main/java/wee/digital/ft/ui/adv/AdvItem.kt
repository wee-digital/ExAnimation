package wee.digital.ft.ui.adv


class AdvItem(
        val videoUri: String? = null,
        val imageRes: Int? = null
) {
    var myMediaPlayer: MyMediaController? = null

    val isImage get() = imageRes != null
}