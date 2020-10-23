package wee.digital.fpa.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.line_view.view.*
import wee.digital.fpa.R

class LineView : ConstraintLayout {

    var title: String?
        get() = lineViewNumber.text.toString()
        set(value) {
            lineViewNumber.text = value
        }

    var text: String?
        get() = lineViewContent.text.toString()
        set(value) {
            lineViewContent.text = value
        }

    constructor(context: Context) : super(context) {
        initView(context, null)
    }

    constructor(context: Context, attributeSet: AttributeSet?) : super(context, attributeSet) {
        initView(context, attributeSet)
    }

    private fun initView(context: Context, attributeSet: AttributeSet?) {
        LayoutInflater.from(context).inflate(R.layout.line_view, this)

        val types =
                context.theme.obtainStyledAttributes(attributeSet, R.styleable.LineView, 0, 0)

        val title = types.getString(R.styleable.LineView_android_title) ?: ""
        lineViewNumber.text = title

        val text = types.getString(R.styleable.LineView_android_text) ?: ""
        lineViewContent.text = text

    }

}