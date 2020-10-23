package wee.digital.fpa.custom

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.title_view.view.*
import wee.digital.fpa.R
import wee.digital.library.extension.gone
import wee.digital.library.extension.hide
import wee.digital.library.extension.show

class TitleView : ConstraintLayout {

    constructor(context: Context) : super(context) {
        initView(context, null)
    }

    constructor(context: Context, attributeSet: AttributeSet?) : super(context, attributeSet) {
        initView(context, attributeSet)
    }

    private fun initView(context: Context, attributeSet: AttributeSet?) {
        LayoutInflater.from(context).inflate(R.layout.title_view, this)

        val types = context.theme.obtainStyledAttributes(attributeSet, R.styleable.TitleView, 0, 0)

        val actionBack = types.getBoolean(R.styleable.TitleView_action_back, false)
        if (actionBack) {
            titleViewActionBack.show()
        } else {
            titleViewActionBack.hide()
        }

        val actionCancel = types.getBoolean(R.styleable.TitleView_action_cancel, false)
        if (actionCancel) {
            titleViewActionCancel.show()
        } else {
            titleViewActionCancel.hide()
        }

        val text = types.getString(R.styleable.TitleView_android_text) ?: ""
        titleViewTitle.text = text
    }

    fun actionBackClick(block : () -> Unit){
        titleViewActionBack.setOnClickListener { block() }
    }

    fun actionCancelClick(block: () -> Unit){
        titleViewActionCancel.setOnClickListener { block() }
    }

}