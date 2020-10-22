package wee.digital.fpa.ui.card

import wee.digital.fpa.R

class CardItem constructor(
    val image: Int,
    val name: String
) {

    companion object {
        val defaultList = listOf(
            CardItem(R.mipmap.img_placeholder, "Vietcombank"),
            CardItem(R.mipmap.img_placeholder, "DongA"),
            CardItem(R.mipmap.img_placeholder, "SeaBank"),
            CardItem(R.mipmap.img_placeholder, "OceanBank"),
            CardItem(R.mipmap.img_placeholder, "TPBank")
        )
    }
}