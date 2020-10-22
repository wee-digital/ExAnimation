package wee.digital.fpa.payment.pin

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import wee.digital.fpa.R

class IndicatorView : FrameLayout {

    constructor(context: Context, attrs: AttributeSet? = null) : super(context, attrs) {
        LayoutInflater.from(context).inflate(R.layout.pin_indicator, this)
    }
}