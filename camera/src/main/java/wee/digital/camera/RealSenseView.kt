package wee.digital.camera

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.Rect
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView


class RealSenseView : SurfaceView {

    private var mHolder: SurfaceHolder? = null

    private var mHandlerThread = HandlerThread("RealSenseView")

    private var mHandler = Handler(Looper.getMainLooper())

    private val mPaint = Paint(Paint.FILTER_BITMAP_FLAG)

    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initView()
    }

    private fun initView() {
        mHandlerThread.start()
        mHandler = Handler(mHandler.looper)
        mPaint.isAntiAlias = true
        mPaint.isFilterBitmap = true
        mPaint.isDither = true
        mHolder = holder
        mHolder?.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
                holder.setFixedSize(width, height)
                mHolder = holder
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                mHolder = holder
            }

            override fun surfaceCreated(holder: SurfaceHolder) {
                mHolder = holder
            }

        })
    }

    fun setBitmap(bitmap: Bitmap?) {
        bitmap ?: return
        mHolder ?: return
        mHandler.post {
            try {
                val canvas = mHolder?.lockCanvas()
                val src = Rect(0, 0, bitmap.width, bitmap.height)
                val dest = Rect(0, 0, width, height)
                canvas?.drawBitmap(bitmap, src, dest, mPaint)
                mHolder?.unlockCanvasAndPost(canvas)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onDetachedFromWindow() {
        mHandlerThread.quitSafely()
        try {
            mHandlerThread.join()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        super.onDetachedFromWindow()
    }
}