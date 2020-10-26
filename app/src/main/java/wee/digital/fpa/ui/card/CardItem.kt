package wee.digital.fpa.ui.card

import wee.digital.fpa.R

data class CardItem (
        val colors : ArrayList<String>,
        val name : String
) {

    companion object {
        val defaultList = listOf(
                CardItem(arrayListOf("#0496D3", "#0A588E"), "CTG"),
                CardItem(arrayListOf("#018D44", "#0DB24A"), "OCB"),
                CardItem(arrayListOf("#165FBF", "#0C4DA2"), "STB"),
                CardItem(arrayListOf("#01ABAE", "#0BBDC0"), "ABB"),
                CardItem(arrayListOf("#512879", "#7B4FA4"), "TPB")
        )
    }
}