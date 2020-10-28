package wee.digital.fpa.ui.arg

class ConfirmArg(
        var icon: Int? = null,
        var title: String? = null,
        var buttonAccept: String? = null,
        var buttonDeny: String? = null,
        var message: String? = null,
        var headerGuideline: Int = 0,
        var onAccept: () -> Unit = {},
        var onDeny: () -> Unit = {}
)