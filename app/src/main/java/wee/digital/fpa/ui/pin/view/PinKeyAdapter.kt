package wee.digital.fpa.ui.pin.view

import android.view.View
import kotlinx.android.synthetic.main.pin_key_item_icon.view.*
import kotlinx.android.synthetic.main.pin_key_item_text.view.*
import wee.digital.fpa.R
import wee.digital.library.adapter.BaseRecyclerAdapter

class PinKeyAdapter : BaseRecyclerAdapter<Any?>() {

    override fun layoutResource(model: Any?, position: Int): Int {
        return when (model) {
            is Int -> R.layout.pin_key_item_icon
            is String -> R.layout.pin_key_item_text
            else -> 0
        }
    }

    override fun View.onBindModel(model: Any?, position: Int, layout: Int) {
        when (model) {
            is Int -> pinImageViewKey.setImageResource(model)
            is String -> pinTextViewKey.text = model.toString()
        }
    }

}