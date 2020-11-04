package wee.digital.ft.ui.device

import android.text.Editable
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.device.*
import wee.digital.ft.R
import wee.digital.ft.shared.Config
import wee.digital.ft.util.SimpleTextWatcher
import wee.digital.library.extension.*

class DeviceView(private val v: DeviceFragment) : SimpleTextWatcher {


    private fun configTermText() {
        val sPolicy = string(R.string.device_policy).bold().color("#419DF2")
        val sTerm = string(R.string.device_term).format(sPolicy)
        v.deviceTextViewTerm.setHyperText(sTerm)
    }

    fun onBindStation(obj: JsonObject) {
        val s = obj.str("FullName")
        val text = string(R.string.device_hi).format(s.color("#419DF2").bold())
        v.deviceTextViewStation.setHyperText(text)
    }

    fun onNameError(s: String?) {
        v.deviceTextViewError.text = s
    }


    fun onViewInit() {
        v.deviceEditTextName.filterChars(Config.DEVICE_NAME_FILTER)
        v.deviceEditTextName.requestFocus()
        v.deviceViewProgress.load(R.mipmap.img_progress_small)
        configTermText()
        v.addClickListener(v.deviceViewBack, v.deviceViewClose, v.deviceViewRegister)
        v.deviceEditTextName.requestFocus()
        v.deviceTextViewError.hide()
    }

    fun showProgress() {
        v.deviceViewRegister.hide()
        v.deviceViewProgress.show()
    }

    fun hideProgress() {
        v.deviceViewRegister.show()
        v.deviceViewProgress.hide()
    }

    override fun afterTextChanged(s: Editable?) {
        v.deviceEditTextName.removeTextChangedListener(this)
        v.deviceEditTextName.trimIndentText
        v.deviceEditTextName.addTextChangedListener(this)
    }

}