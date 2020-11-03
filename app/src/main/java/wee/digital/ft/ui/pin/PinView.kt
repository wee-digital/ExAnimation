package wee.digital.ft.ui.pin

import kotlinx.android.synthetic.main.pin.*
import wee.digital.ft.R
import wee.digital.ft.ui.pin.view.PinKeyAdapter
import wee.digital.library.extension.color
import wee.digital.library.extension.gradientVertical

class PinView(private val v: PinFragment) {

    private val pinAdapter = PinKeyAdapter()

    private val keyListItem = listOf(
            "1", "2", "3", R.drawable.drw_pin_del,
            "4", "5", "6", null,
            "7", "8", "9", "0"
    )

    private fun configPinProgressView() {
        v.pinProgressLayout.build {
            progressColor = color(R.color.colorPrimary)
            itemCount = 6
        }
    }

    private fun configNumPadKeyView() {
        pinAdapter.apply {
            set(keyListItem)
            bind(v.pinRecyclerViewKey, 4)
            onItemClick { any, _ ->
                when (any) {
                    is String -> onNumKeyEvent(any)
                    is Int -> onIconKeyEvent(any)
                }
            }
        }
    }

    private fun onNumKeyEvent(s: String) {
        v.pinProgressLayout.addKey(s)
    }

    private fun onIconKeyEvent(i: Int) {
        when (i) {
            R.drawable.drw_pin_del -> {
                v.pinProgressLayout.delKey()
            }
        }
    }

    fun onViewInit() {
        v.addClickListener(v.pinViewClose)
        configNumPadKeyView()
        configPinProgressView()
    }

    fun onBindErrorText(s: String?) {
        if (s.isNullOrEmpty()) {
            v.pinTextViewTitle.gradientVertical(R.color.colorTextPrimary)
            v.pinTextViewTitle.text = "Vui lòng nhập PIN code thanh toán"
        } else {
            v.pinTextViewTitle.gradientVertical(R.color.colorAlertStart, R.color.colorAlertEnd)
            v.pinTextViewTitle.text = s
        }
    }
}


