package wee.digital.fpa.ui.adv


class MediaItem(
        val videoUri: String? = null,
        val imageRes: Int? = null
) {
    var myMediaPlayer: MyMediaController? = null

    val isImage get() = imageRes!=null
}