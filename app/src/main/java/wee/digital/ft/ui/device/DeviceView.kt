package wee.digital.ft.ui.device

import android.content.Context
import android.text.Editable
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.device.view.*
import wee.digital.ft.R
import wee.digital.ft.shared.Config
import wee.digital.ft.util.SimpleTextWatcher
import wee.digital.library.extension.*

class DeviceView : ConstraintLayout, SimpleTextWatcher {

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)


    private fun configTermText() {
        val sPolicy = string(R.string.device_policy).bold().color("#419DF2")
        val sTerm = string(R.string.device_term).format(sPolicy)
        deviceTextViewTerm.setHyperText(sTerm)
    }

    fun onBindStation(obj: JsonObject) {
        val s = obj.str("FullName")
        val text = string(R.string.device_hi).format(s.color("#419DF2").bold())
        deviceTextViewStation.setHyperText(text)
    }

    fun onNameError(s: String?) {
        deviceTextViewError.text = s
    }


    fun onViewInit() {
        deviceEditTextName.filterChars(Config.DEVICE_NAME_FILTER)
        deviceEditTextName.requestFocus()
        deviceViewProgress.load(R.mipmap.img_progress_small)
        configTermText()

        deviceEditTextName.requestFocus()
        deviceTextViewError.hide()
    }

    fun showProgress() {
        deviceViewRegister.hide()
        deviceViewProgress.show()
    }

    fun hideProgress() {
        deviceViewRegister.show()
        deviceViewProgress.hide()
    }

    override fun afterTextChanged(s: Editable?) {
        deviceEditTextName.removeTextChangedListener(this)
        deviceEditTextName.trimIndentText
        deviceEditTextName.addTextChangedListener(this)
    }

}