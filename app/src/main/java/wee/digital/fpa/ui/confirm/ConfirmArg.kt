package wee.digital.fpa.ui.confirm

class ConfirmArg(
        var icon: Int? = null,
        var title: String? = null,
        var buttonAccept: String? = null,
        var buttonDeny: String? = null,
        var message: String? = null,
        var onAccept: () -> Unit = {},
        var onDeny: () -> Unit = {}
)