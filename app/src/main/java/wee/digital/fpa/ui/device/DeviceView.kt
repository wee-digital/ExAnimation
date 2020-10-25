package wee.digital.fpa.ui.device

import android.text.Editable
import android.view.inputmethod.EditorInfo
import kotlinx.android.synthetic.main.device.*
import wee.digital.fpa.R
import wee.digital.fpa.util.SimpleTextWatcher
import wee.digital.library.extension.bold
import wee.digital.library.extension.color
import wee.digital.library.extension.setHyperText
import wee.digital.library.extension.string

class DeviceView(private val v: DeviceFragment) {

    private fun configDeviceNameText() {
        v.deviceEditTextName.apply {
            imeOptions = EditorInfo.IME_ACTION_DONE
            maxLines = 1
            isSingleLine = true
            setEms(10)
            addTextChangedListener(object : SimpleTextWatcher {

                override fun afterTextChanged(editable: Editable?) {
                    val s = text.toString()
                    if (s == " ") {
                        removeTextChangedListener(this)
                        setText("")
                        addTextChangedListener(this)
                        return
                    }
                    if (s.length == 20 && s[19].toString() == " ") {
                        removeTextChangedListener(this)
                        val str = text.toString().removeRange(19, 20)
                        setText(str)
                        setSelection(text.toString().length)
                        addTextChangedListener(this)
                        return
                    }
                    if (s.length < 2 || s.length >= 19) return
                    val spaceOne = s[s.length - 1].toString()
                    val spaceTwo = s[s.length - 2].toString()
                    if (spaceOne == " " && spaceTwo == " ") {
                        removeTextChangedListener(this)
                        setText(s.replace("  ", " "))
                        setSelection(text.toString().length)
                        addTextChangedListener(this)
                    }
                }
            })
        }
    }

    private fun configTermText() {
        val sPolicy = string(R.string.device_policy).bold().color("#1279DA")
        val sTerm = string(R.string.device_term).format(sPolicy)
        v.deviceTextViewTerm.setHyperText(sTerm)
    }

    fun onBindStation(name: String) {
        v.deviceTextViewStation.setHyperText(name)
    }

    fun onBindError(s: String?) {
        v.deviceTextViewError.text = s
    }

    fun onViewInit() {
        configDeviceNameText()
        configTermText()
        v.addClickListener(v.deviceViewBack, v.deviceViewClose, v.deviceViewRegister)

    }

}