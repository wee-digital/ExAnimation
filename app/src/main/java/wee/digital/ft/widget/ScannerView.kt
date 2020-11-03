package wee.digital.ft.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import androidx.annotation.Px

class ScannerView(context: Context) : View(context) {

    private val mMaskPaint: Paint

    private val mFramePaint: Paint

    private val mPath: Path

    var frameRect: Rect? = null
        private set

    @get:Px
    var frameCornersSize = 0
        set(@Px size) {
            field = size
            if (isLaidOut) {
                invalidate()
            }
        }

    @get:Px
    var frameCornersRadius = 0
        set(@Px radius) {
            field = radius
            if (isLaidOut) {
                invalidate()
            }
        }
    private var mFrameRatioWidth = 1f
    private var mFrameRatioHeight = 1f

    @get:FloatRange(from = 0.1, to = 1.0)
    var frameSize = 0.75f
        set(@FloatRange(from = 0.1, to = 1.0) size) {
            field = size
            invalidateFrameRect()
            if (isLaidOut) {
                invalidate()
            }
        }

    var frameAspectRatioWidth: Float
        @FloatRange(from = 0.0, fromInclusive = false)
        get() = mFrameRatioWidth
        set(@FloatRange(from = 0.0, fromInclusive = false) ratioWidth) {
            mFrameRatioWidth = ratioWidth
            invalidateFrameRect()
            if (isLaidOut) {
                invalidate()
            }
        }

    var frameAspectRatioHeight: Float
        @FloatRange(from = 0.0, fromInclusive = false)
        get() = mFrameRatioHeight
        set(@FloatRange(from = 0.0, fromInclusive = false) ratioHeight) {
            mFrameRatioHeight = ratioHeight
            invalidateFrameRect()
            if (isLaidOut) {
                invalidate()
            }
        }

    var maskColor: Int
        @ColorInt
        get() = mMaskPaint.color
        set(@ColorInt color) {
            mMaskPaint.color = color
            if (isLaidOut) {
                invalidate()
            }
        }

    var frameColor: Int
        @ColorInt
        get() = mFramePaint.color
        set(@ColorInt color) {
            mFramePaint.color = color
            if (isLaidOut) {
                invalidate()
            }
        }

    var frameThickness: Int
        @Px
        get() = mFramePaint.strokeWidth.toInt()
        set(@Px thickness) {
            mFramePaint.strokeWidth = thickness.toFloat()
            if (isLaidOut) {
                invalidate()
            }
        }

    init {
        mMaskPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mMaskPaint.style = Paint.Style.FILL
        mFramePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mFramePaint.style = Paint.Style.STROKE
        val path = Path()
        path.fillType = Path.FillType.EVEN_ODD
        mPath = path
    }

    override fun onDraw(canvas: Canvas) {
        val frame = frameRect ?: return
        val width = width
        val height = height
        val top = frame.top.toFloat()
        val left = frame.left.toFloat()
        val right = frame.right.toFloat()
        val bottom = frame.bottom.toFloat()
        val frameCornersSize = this.frameCornersSize.toFloat()
        val frameCornersRadius = this.frameCornersRadius.toFloat()
        val path = mPath
        if (frameCornersRadius > 0) {
            val normalizedRadius = Math.min(frameCornersRadius, Math.max(frameCornersSize - 1, 0f))
            path.reset()
            path.moveTo(left, top + normalizedRadius)
            path.quadTo(left, top, left + normalizedRadius, top)
            path.lineTo(right - normalizedRadius, top)
            path.quadTo(right, top, right, top + normalizedRadius)
            path.lineTo(right, bottom - normalizedRadius)
            path.quadTo(right, bottom, right - normalizedRadius, bottom)
            path.lineTo(left + normalizedRadius, bottom)
            path.quadTo(left, bottom, left, bottom - normalizedRadius)
            path.lineTo(left, top + normalizedRadius)
            path.moveTo(0f, 0f)
            path.lineTo(width.toFloat(), 0f)
            path.lineTo(width.toFloat(), height.toFloat())
            path.lineTo(0f, height.toFloat())
            path.lineTo(0f, 0f)
            canvas.drawPath(path, mMaskPaint)
            path.reset()
            path.moveTo(left, top + frameCornersSize)
            path.lineTo(left, top + normalizedRadius)
            path.quadTo(left, top, left + normalizedRadius, top)
            path.lineTo(left + frameCornersSize, top)
            path.moveTo(right - frameCornersSize, top)
            path.lineTo(right - normalizedRadius, top)
            path.quadTo(right, top, right, top + normalizedRadius)
            path.lineTo(right, top + frameCornersSize)
            path.moveTo(right, bottom - frameCornersSize)
            path.lineTo(right, bottom - normalizedRadius)
            path.quadTo(right, bottom, right - normalizedRadius, bottom)
            path.lineTo(right - frameCornersSize, bottom)
            path.moveTo(left + frameCornersSize, bottom)
            path.lineTo(left + normalizedRadius, bottom)
            path.quadTo(left, bottom, left, bottom - normalizedRadius)
            path.lineTo(left, bottom - frameCornersSize)
            canvas.drawPath(path, mFramePaint)
        } else {
            path.reset()
            path.moveTo(left, top)
            path.lineTo(right, top)
            path.lineTo(right, bottom)
            path.lineTo(left, bottom)
            path.lineTo(left, top)
            path.moveTo(0f, 0f)
            path.lineTo(width.toFloat(), 0f)
            path.lineTo(width.toFloat(), height.toFloat())
            path.lineTo(0f, height.toFloat())
            path.lineTo(0f, 0f)
            canvas.drawPath(path, mMaskPaint)
            path.reset()
            path.moveTo(left, top + frameCornersSize)
            path.lineTo(left, top)
            path.lineTo(left + frameCornersSize, top)
            path.moveTo(right - frameCornersSize, top)
            path.lineTo(right, top)
            path.lineTo(right, top + frameCornersSize)
            path.moveTo(right, bottom - frameCornersSize)
            path.lineTo(right, bottom)
            path.lineTo(right - frameCornersSize, bottom)
            path.moveTo(left + frameCornersSize, bottom)
            path.lineTo(left, bottom)
            path.lineTo(left, bottom - frameCornersSize)
            canvas.drawPath(path, mFramePaint)
        }
    }

    override fun onLayout(
            changed: Boolean, left: Int, top: Int, right: Int,
            bottom: Int
    ) {
        invalidateFrameRect(right - left, bottom - top)
    }

    fun setFrameAspectRatio(
            @FloatRange(from = 0.0, fromInclusive = false) ratioWidth: Float,
            @FloatRange(from = 0.0, fromInclusive = false) ratioHeight: Float
    ) {
        mFrameRatioWidth = ratioWidth
        mFrameRatioHeight = ratioHeight
        invalidateFrameRect()
        if (isLaidOut) {
            invalidate()
        }
    }

    private fun invalidateFrameRect(width: Int = getWidth(), height: Int = getHeight()) {
        if (width > 0 && height > 0) {
            val viewAR = width.toFloat() / height.toFloat()
            val frameAR = mFrameRatioWidth / mFrameRatioHeight
            val frameWidth: Int
            val frameHeight: Int
            if (viewAR <= frameAR) {
                frameWidth = Math.round(width * frameSize)
                frameHeight = Math.round(frameWidth / frameAR)
            } else {
                frameHeight = Math.round(height * frameSize)
                frameWidth = Math.round(frameHeight * frameAR)
            }
            val frameLeft = (width - frameWidth) / 2
            val frameTop = ((height - frameHeight) / 2.5).toInt()
            frameRect = Rect(frameLeft, frameTop, frameLeft + frameWidth, frameTop + frameHeight)
        }
    }
}