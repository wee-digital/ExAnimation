package wee.digital.fpa.ui.pin

import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.pin.*
import wee.digital.fpa.R
import wee.digital.library.extension.color

class PinView(private val v: PinFragment) {

    private val pinAdapter = PinKeyAdapter()

    private val keyListItem by lazy {
        listOf(
                "1", "2", "3", R.drawable.drw_pin_del,
                "4", "5", "6", "0",
                "7", "8", "9"
        )
    }

    private fun Fragment.configPinProgressView() {
        pinProgressLayout.build {
            progressColor = color(R.color.colorPrimary)
            itemCount = 6
        }
    }

    private fun Fragment.configNumPadKeyView() {
        pinAdapter.apply {
            set(keyListItem)
            bind(pinRecyclerViewKey, 4)
            onItemClick { any, _ ->
                when (any) {
                    is String -> onNumKeyEvent(any)
                    is Int -> onIconKeyEvent(any)
                }
            }
        }
    }

    private fun Fragment.onNumKeyEvent(s: String) {
        pinProgressLayout.addKey(s)
    }

    private fun Fragment.onIconKeyEvent(i: Int) {
        when (i) {
            R.drawable.drw_pin_del -> {
                pinProgressLayout.delKey()
            }
        }
    }

    fun onViewInit() {
        v.addClickListener(v.dialogViewClose)
        v.configNumPadKeyView()
        v.configPinProgressView()
    }

    fun onBindErrorText(s: String?) {
        if (s.isNullOrEmpty()) {
            v.dialogTextViewTitle.color("232323")
            v.dialogTextViewTitle.text = "Vui lòng nhập PIN code thanh toán"
        } else {
            v.dialogTextViewTitle.color("F24141")
            v.dialogTextViewTitle.text = s
        }
    }
}


