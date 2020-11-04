package wee.digital.ft.ui.pin

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.pin.view.*
import wee.digital.ft.R
import wee.digital.ft.ui.pin.view.PinKeyAdapter
import wee.digital.library.extension.color
import wee.digital.library.extension.gradientVertical

class PinView : ConstraintLayout {

    constructor(context: Context, attrs: AttributeSet? = null) : super(context, attrs)

    private val pinAdapter = PinKeyAdapter()

    private val keyListItem = listOf(
            "1", "2", "3", R.drawable.drw_pin_del,
            "4", "5", "6", null,
            "7", "8", "9", "0"
    )

    private fun configPinProgressView() {
        pinProgressLayout.build {
            progressColor = color(R.color.colorPrimary)
            itemCount = 6
        }
    }

    private fun configNumPadKeyView() {
        pinAdapter.apply {
            set(keyListItem)
            bind(pinRecyclerViewKey, 4)
            onItemClick { any, _ ->
                pinProgressLayout.pushKey(any)
            }
        }
    }

    fun onViewInit() {
        configNumPadKeyView()
        configPinProgressView()
    }

    fun onBindErrorText(s: String?) {
        if (s.isNullOrEmpty()) {
            pinTextViewTitle.gradientVertical(R.color.colorTextPrimary)
            pinTextViewTitle.text = "Vui lòng nhập PIN code thanh toán"
        } else {
            pinTextViewTitle.gradientVertical(R.color.colorAlertStart, R.color.colorAlertEnd)
            pinTextViewTitle.text = s
        }
    }
}


