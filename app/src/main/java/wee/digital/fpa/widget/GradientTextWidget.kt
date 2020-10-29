package wee.digital.fpa.widget

import android.content.Context
import android.graphics.LinearGradient
import android.graphics.Shader
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import wee.digital.fpa.R

class GradientTextWidget : AppCompatTextView {

    constructor(context: Context, attr: AttributeSet? = null) : super(context, attr)

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        paint.shader = LinearGradient(0F, 0F, width.toFloat(), 0F,
                ContextCompat.getColor(context, R.color.gradient_blue_start2),
                ContextCompat.getColor(context, R.color.gradient_blue_end2),
                Shader.TileMode.CLAMP)
    }

}