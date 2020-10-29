package wee.digital.fpa.ui.card

data class CardItem(
        val id: String,
        val bankCode: String,
        val name: String,
        val shortName: String,
        val colors: List<String>
)