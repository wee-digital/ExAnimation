package wee.digital.fpa.ui.pin

import android.view.View
import kotlinx.android.synthetic.main.pin_key_item_icon.view.*
import kotlinx.android.synthetic.main.pin_key_item_text.view.*
import wee.digital.fpa.R
import wee.digital.fpa.ui.base.BaseRecyclerAdapter

class PinKeyAdapter : BaseRecyclerAdapter<Any?>() {

    override var listItem: MutableList<Any?> = mutableListOf(
            "1", "2", "3", R.drawable.ic_placeholder,
            "4", "5", "6", "0",
            "7", "8", "9",
    )

    override fun layoutResource(model: Any?, position: Int): Int {
        return when (model) {
            is Int -> R.layout.pin_key_item_icon
            else -> R.layout.pin_key_item_text
        }
    }

    override fun View.onBindModel(model: Any?, position: Int, layout: Int) {
        when (model) {
            is Int -> pinImageViewKey.setImageResource(model)
            else -> pinTextViewKey.text = model?.toString()
        }
    }

}