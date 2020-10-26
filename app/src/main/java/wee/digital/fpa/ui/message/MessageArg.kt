package wee.digital.fpa.ui.message

data class MessageArg(
        var icon: Int? = null,
        var title: String? = null,
        var button: String? = null,
        var message: String? = null,
        var onClose: () -> Unit = {}
)