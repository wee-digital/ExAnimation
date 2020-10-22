package wee.digital.fpa.ui.card

import wee.digital.fpa.R

class CardItem constructor(
    val image: Int,
    val name: String
) {

    companion object {
        val defaultList = listOf(
            CardItem(R.mipmap.img_card, "Vietcombank"),
            CardItem(R.mipmap.img_card, "DongA"),
            CardItem(R.mipmap.img_card, "SeaBank"),
            CardItem(R.mipmap.img_card, "OceanBank"),
            CardItem(R.mipmap.img_card, "TPBank")
        )
    }
}