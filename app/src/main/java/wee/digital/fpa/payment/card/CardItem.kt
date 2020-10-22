package wee.digital.fpa.payment.card

class CardItem constructor(
    val image: Int,
    val name: String
) {

    companion object {
        val defaultList = listOf(
            CardItem(1, "Vietcombank"),
            CardItem(1, "DongA"),
            CardItem(1, "SeaBank"),
            CardItem(1, "OceanBank"),
            CardItem(1, "TPBank")
        )
    }
}