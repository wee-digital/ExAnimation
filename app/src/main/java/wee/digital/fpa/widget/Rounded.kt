package wee.digital.fpa.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.Region
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import wee.digital.fpa.R

class Rounded @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var number = 1f
        set(value) {
            field = value
            Handler(Looper.getMainLooper()).post { invalidate() }
        }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val path = Path()
        path.reset()
        path.addCircle(
                (width / 2).toFloat(),
                (height / 2).toFloat(),
                number * (width / 2f) - 100f,
                Path.Direction.CW
        )
        canvas.clipPath(path, Region.Op.DIFFERENCE)
        canvas.drawColor(ContextCompat.getColor(context, R.color.colorWhite))
    }

}
