package wee.digital.fpa.widget

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.Point
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.annotation.*
import wee.digital.fpa.R
import kotlin.math.roundToInt

class LayoutScanner : FrameLayout {

    var viewFinderView: ViewScanner? = null
        private set

    private var mAutoFocusButton: ImageView? = null
    private var mFlashButton: ImageView? = null
    private var mPreviewSize: Point? = null
    private var mButtonSize: Int = 0

    @ColorInt
    var autoFocusButtonColor: Int = 0
        set(@ColorInt color) {
            field = color
            mAutoFocusButton!!.setColorFilter(color)
        }

    @ColorInt
    var flashButtonColor: Int = 0
        set(@ColorInt color) {
            field = color
            mFlashButton!!.setColorFilter(color)
        }
    private var mFocusAreaSize: Int = 0


    var maskColor: Int
        @ColorInt
        get() = viewFinderView!!.maskColor
        set(@ColorInt color) {
            viewFinderView!!.maskColor = (color)
        }

    var frameColor: Int
        @ColorInt
        get() = viewFinderView!!.frameColor
        set(@ColorInt color) {
            viewFinderView!!.frameColor = (color)
        }

    var frameThickness: Int
        @Px
        get() = viewFinderView!!.frameThickness
        set(@Px thickness) {
            require(thickness >= 0) { "Frame thickness can't be negative" }
            viewFinderView!!.frameThickness = (thickness)
        }

    var frameCornersSize: Int
        @Px
        get() = viewFinderView!!.frameCornersSize
        set(@Px size) {
            require(size >= 0) { "Frame corners size can't be negative" }
            viewFinderView!!.frameCornersSize = (size)
        }

    var frameCornersRadius: Int
        @Px
        get() = viewFinderView!!.frameCornersRadius
        set(@Px radius) {
            require(radius >= 0) { "Frame corners radius can't be negative" }
            viewFinderView!!.frameCornersRadius = (radius)
        }

    var frameSize: Float
        @FloatRange(from = 0.1, to = 1.0)
        get() = viewFinderView!!.frameSize
        set(@FloatRange(from = 0.1, to = 1.0) size) {
            require(!(size < 0.1 || size > 1)) { "Max frame size value should be between 0.1 and 1, inclusive" }
            viewFinderView!!.frameSize = (size)
        }

    var isAutoFocusButtonVisible: Boolean
        get() = mAutoFocusButton!!.visibility == View.VISIBLE
        set(visible) {
            mAutoFocusButton!!.visibility = if (visible) View.VISIBLE else View.INVISIBLE
        }

    var isFlashButtonVisible: Boolean
        get() = mFlashButton!!.visibility == View.VISIBLE
        set(visible) {
            mFlashButton!!.visibility = if (visible) View.VISIBLE else View.INVISIBLE
        }

    constructor(context: Context) : super(context) {
        initialize(context, null, 0, 0)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initialize(context, attrs, 0, 0)
    }

    constructor(
            context: Context, attrs: AttributeSet?,
            @AttrRes defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        initialize(context, attrs, defStyleAttr, 0)
    }

    private fun initialize(
            context: Context, attrs: AttributeSet?,
            @AttrRes defStyleAttr: Int, @StyleRes defStyleRes: Int
    ) {

        viewFinderView = ViewScanner(context)
        val density = context.resources.displayMetrics.density
        mButtonSize = (density * BUTTON_SIZE_DP).roundToInt()
        mFocusAreaSize = (density * FOCUS_AREA_SIZE_DP).roundToInt()
        mAutoFocusButton = ImageView(context)
        mAutoFocusButton!!.layoutParams = LayoutParams(mButtonSize, mButtonSize)
        mAutoFocusButton!!.scaleType = ImageView.ScaleType.CENTER
        mFlashButton = ImageView(context)
        mFlashButton!!.layoutParams = LayoutParams(mButtonSize, mButtonSize)
        mFlashButton!!.scaleType = ImageView.ScaleType.CENTER
        if (attrs == null) {
            viewFinderView!!.setFrameAspectRatio(
                    DEFAULT_FRAME_ASPECT_RATIO_WIDTH,
                    DEFAULT_FRAME_ASPECT_RATIO_HEIGHT
            )
            viewFinderView!!.maskColor = (DEFAULT_MASK_COLOR)
            viewFinderView!!.frameColor = (DEFAULT_FRAME_COLOR)
            viewFinderView!!.frameThickness = (Math.round(DEFAULT_FRAME_THICKNESS_DP * density))
            viewFinderView!!.frameCornersSize = (Math.round(DEFAULT_FRAME_CORNER_SIZE_DP * density))
            viewFinderView!!
                    .frameCornersRadius = (Math.round(DEFAULT_FRAME_CORNERS_RADIUS_DP * density))
            viewFinderView!!.frameSize = (DEFAULT_FRAME_SIZE)
            mAutoFocusButton!!.setColorFilter(DEFAULT_AUTO_FOCUS_BUTTON_COLOR)
            mFlashButton!!.setColorFilter(DEFAULT_FLASH_BUTTON_COLOR)
            mAutoFocusButton!!.visibility =
                    DEFAULT_AUTO_FOCUS_BUTTON_VISIBILITY
            mFlashButton!!.visibility =
                    DEFAULT_FLASH_BUTTON_VISIBILITY
        } else {
            var a: TypedArray? = null
            try {
                a = context.theme.obtainStyledAttributes(
                        attrs,
                        R.styleable.CodeScannerView,
                        defStyleAttr,
                        defStyleRes
                )
                maskColor = a.getColor(R.styleable.CodeScannerView_maskColor,
                        DEFAULT_MASK_COLOR
                )
                frameColor = a.getColor(R.styleable.CodeScannerView_frameColor,
                        DEFAULT_FRAME_COLOR
                )
                frameThickness = a.getDimensionPixelOffset(
                        R.styleable.CodeScannerView_frameThickness,
                        Math.round(DEFAULT_FRAME_THICKNESS_DP * density)
                )
                frameCornersSize = a.getDimensionPixelOffset(
                        R.styleable.CodeScannerView_frameCornersSize,
                        Math.round(DEFAULT_FRAME_CORNER_SIZE_DP * density)
                )
                frameCornersRadius = a.getDimensionPixelOffset(
                        R.styleable.CodeScannerView_frameCornersRadius,
                        Math.round(DEFAULT_FRAME_CORNERS_RADIUS_DP * density)
                )
                setFrameAspectRatio(
                        a.getFloat(
                                R.styleable.CodeScannerView_frameAspectRatioWidth,
                                DEFAULT_FRAME_ASPECT_RATIO_WIDTH
                        ),
                        a.getFloat(
                                R.styleable.CodeScannerView_frameAspectRatioHeight,
                                DEFAULT_FRAME_ASPECT_RATIO_HEIGHT
                        )
                )
                frameSize = a.getFloat(R.styleable.CodeScannerView_frameSize,
                        DEFAULT_FRAME_SIZE
                )
            } finally {
                a?.recycle()
            }
        }
        addView(viewFinderView)
        addView(mAutoFocusButton)
        addView(mFlashButton)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        performLayout(right - left, bottom - top)
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        performLayout(width, height)
    }

    private fun setFrameAspectRatio(
            @FloatRange(from = 0.0, fromInclusive = false) ratioWidth: Float,
            @FloatRange(from = 0.0, fromInclusive = false) ratioHeight: Float
    ) {
        require(!(ratioWidth <= 0 || ratioHeight <= 0)) { "Frame aspect ratio values should be greater than zero" }
        viewFinderView!!.setFrameAspectRatio(ratioWidth, ratioHeight)
    }

    private fun performLayout(width: Int, height: Int) {
        val previewSize = mPreviewSize
        if (previewSize != null) {
            var frameLeft = 0
            var frameTop = 0
            var frameRight = width
            var frameBottom = height
            val previewWidth = previewSize.x
            if (previewWidth > width) {
                val d = (previewWidth - width) / 2
                frameLeft -= d
                frameRight += d
            }
            val previewHeight = previewSize.y
            if (previewHeight > height) {
                val d = (previewHeight - height) / 2
                frameTop -= d
                frameBottom += d
            }
        }
        viewFinderView!!.layout(0, 0, width, height)
        val buttonSize = mButtonSize
        mAutoFocusButton!!.layout(0, 0, buttonSize, buttonSize)
        mFlashButton!!.layout(width - buttonSize, 0, width, buttonSize)
    }

    companion object {
        private const val DEFAULT_AUTO_FOCUS_BUTTON_VISIBILITY = View.VISIBLE
        private const val DEFAULT_FLASH_BUTTON_VISIBILITY = View.VISIBLE
        private const val DEFAULT_MASK_COLOR = 0x77000000
        private const val DEFAULT_FRAME_COLOR = Color.WHITE
        private const val DEFAULT_AUTO_FOCUS_BUTTON_COLOR = Color.WHITE
        private const val DEFAULT_FLASH_BUTTON_COLOR = Color.WHITE
        private const val DEFAULT_FRAME_THICKNESS_DP = 2f
        private const val DEFAULT_FRAME_ASPECT_RATIO_WIDTH = 1f
        private const val DEFAULT_FRAME_ASPECT_RATIO_HEIGHT = 1f
        private const val DEFAULT_FRAME_CORNER_SIZE_DP = 50f
        private const val DEFAULT_FRAME_CORNERS_RADIUS_DP = 0f
        private const val DEFAULT_FRAME_SIZE = 0.75f
        private const val BUTTON_SIZE_DP = 56f
        private const val FOCUS_AREA_SIZE_DP = 20f
    }
}