package wee.digital.fpa.ui.arg

data class MessageArg(
        var icon: Int? = null,
        var title: String? = null,
        var button: String? = null,
        var message: String? = null,
        var headerGuideline: Int = 0,
        var onClose: () -> Unit = {}
)