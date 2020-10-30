package wee.digital.fpa.ui.confirm

import wee.digital.fpa.R

class ConfirmArg(
        var headerGuideline: Int = R.id.guidelineConnect,
        var icon: Int = R.mipmap.img_x_mark_flat,
        var title: String? = null,
        var message: String? = null,
        var buttonAccept: String? = null,
        var buttonDeny: String? = "Đóng",
        var onAccept: () -> Unit = {},
        var onDeny: () -> Unit = {}
)