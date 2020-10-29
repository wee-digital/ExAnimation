package wee.digital.fpa.ui.message

data class MessageArg(
        var icon: Int? = null,
        var title: String? = null,
        var button: String? = "Đóng",
        var message: String? = null,
        var headerGuideline: Int = 0,
        var onClose: () -> Unit = {}
)