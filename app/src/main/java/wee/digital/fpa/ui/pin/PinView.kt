package wee.digital.fpa.ui.pin

import kotlinx.android.synthetic.main.pin.*
import wee.digital.fpa.R
import wee.digital.library.extension.color

class PinView(private val v: PinFragment) {

    private val pinAdapter = PinKeyAdapter()

    private val keyListItem = listOf(
            "1", "2", "3", R.drawable.drw_pin_del,
            "4", "5", "6", "0",
            "7", "8", "9"
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
        v.addClickListener(v.dialogViewClose)
        configNumPadKeyView()
        configPinProgressView()
    }

    fun onBindErrorText(s: String?) {
        if (s.isNullOrEmpty()) {
            v.confirmTextViewTitle.color("232323")
            v.confirmTextViewTitle.text = "Vui lòng nhập PIN code thanh toán"
        } else {
            v.confirmTextViewTitle.color("F24141")
            v.confirmTextViewTitle.text = s
        }
    }
}


