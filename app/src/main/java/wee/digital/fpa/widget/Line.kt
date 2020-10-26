package wee.digital.fpa.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import wee.digital.fpa.R

class Line @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var numberLine = 1f
        set(value) {
            field = value
            Handler(Looper.getMainLooper()).post { invalidate() }
        }

    var colorLine = R.color.gray_1
        set(value) {
            field = value
            Handler(Looper.getMainLooper()).post { invalidate() }
        }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val radiusLine = numberLine * (width / 2f) - 100f
        val paintLine = createPaint(context, colorLine, 6f)
        val ovalLine =
                createOval(paintLine, (width / 2).toFloat(), (height / 2).toFloat(), radiusLine)
        canvas.drawArc(ovalLine, 0f, 360f, false, paintLine)
    }

    private fun createOval(
            paint: Paint,
            centerX: Float,
            centerY: Float,
            radiusTopLeft: Float
    ): RectF {
        val oval = RectF()
        paint.style = Paint.Style.STROKE
        oval.set(centerX - radiusTopLeft, centerY - radiusTopLeft, centerX + radiusTopLeft, centerY + radiusTopLeft)
        return oval
    }

    private fun createPaint(
            context: Context,
            @ColorRes color: Int = R.color.colorWhite,
            stroke: Float = 10f
    ): Paint {
        val paint = Paint()
        paint.color = ContextCompat.getColor(context, color)
        paint.strokeWidth = stroke
        paint.style = Paint.Style.STROKE
        paint.strokeCap = Paint.Cap.ROUND
        return paint
    }

}