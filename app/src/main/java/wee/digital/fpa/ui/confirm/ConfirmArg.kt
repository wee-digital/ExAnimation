package wee.digital.fpa.ui.confirm

class ConfirmArg(
        var icon: Int,
        var message: String?,
        var title: String?,
        var onAccept: () -> Unit = {}
)