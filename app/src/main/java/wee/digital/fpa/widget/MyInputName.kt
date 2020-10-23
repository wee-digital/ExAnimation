package wee.digital.fpa.widget

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.inputmethod.EditorInfo

class MyInputName : androidx.appcompat.widget.AppCompatEditText {

    constructor(context: Context) : super(context) {
        initView(context, null)
    }

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        initView(context, attributeSet)
    }

    private fun initView(context: Context, attributeSet: AttributeSet?) {
        this.imeOptions = EditorInfo.IME_ACTION_DONE

        this.maxLines = 1

        this.isSingleLine = true

        this.setEms(10)

        this.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {

                val text = this@MyInputName.text.toString()
                if (text == " ") {
                    this@MyInputName.removeTextChangedListener(this)
                    this@MyInputName.setText("")
                    this@MyInputName.addTextChangedListener(this)
                    return
                }

                if (text.length == 20 && text[19].toString() == " ") {
                    this@MyInputName.removeTextChangedListener(this)

                    val str = this@MyInputName.text.toString().removeRange(19, 20)

                    this@MyInputName.setText(str)
                    this@MyInputName.setSelection(this@MyInputName.text.toString().length)
                    this@MyInputName.addTextChangedListener(this)
                    return
                }

                if (text.length < 2 || text.length >= 19) return

                val spaceOne = text[text.length - 1].toString()
                val spaceTwo = text[text.length - 2].toString()
                if (spaceOne == " " && spaceTwo == " ") {
                    this@MyInputName.removeTextChangedListener(this)
                    this@MyInputName.setText(text.replace("  ", " "))
                    this@MyInputName.setSelection(this@MyInputName.text.toString().length)
                    this@MyInputName.addTextChangedListener(this)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        })
    }
}