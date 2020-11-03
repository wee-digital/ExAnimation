package wee.digital.ft.ui.confirm

import wee.digital.ft.R

class ConfirmArg(
        var headerGuideline: Int = R.id.guidelineHeader,
        var icon: Int = R.mipmap.img_x_mark_flat,
        var title: String? = null,
        var message: String? = null,
        var timeOut: Int = 5,
        var buttonAccept: String? = null,
        var buttonDeny: String? = "Đóng",
        var onAccept: (ConfirmFragment) -> Unit = {},
        var onDeny: (ConfirmFragment) -> Unit = {}
)