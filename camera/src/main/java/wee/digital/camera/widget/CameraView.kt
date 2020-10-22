package wee.digital.camera.widget

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.widget_camera_view.view.*
import wee.digital.camera.R
import wee.digital.camera.RealSense
import wee.digital.camera.RealSenseControl

class CameraView : ConstraintLayout {

    constructor(context: Context) : super(context) {
        onViewInit(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        onViewInit(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        onViewInit(context)
    }

    private fun onViewInit(context: Context) {
        LayoutInflater.from(context).inflate(R.layout.widget_camera_view, this)
    }

    fun observe(lifecycleOwner: LifecycleOwner) {
        RealSense.imagesLiveData.observe(lifecycleOwner, Observer<Pair<Bitmap, Bitmap>?> {
            it?.apply {
                imageViewColor.setBitmap(first)
                imageViewDepth.setImageBitmap(second)
            }
        })
    }

    fun targetFace(left: Int, top: Int = 0, width: Int = 0, height: Int = 0) {
        if (left < 0) {
            imageViewCensored.visibility = View.INVISIBLE
            return
        }
        imageViewCensored.visibility = View.VISIBLE
        val set = ConstraintSet()
        set.clone(viewPreview)
        set.connect(
                imageViewCensored.id,
                ConstraintSet.START,
                ConstraintSet.PARENT_ID,
                ConstraintSet.START,
                viewPreview.width * left / RealSense.COLOR_WIDTH
        )
        set.connect(
                imageViewCensored.id,
                ConstraintSet.END,
                ConstraintSet.PARENT_ID,
                ConstraintSet.END,
                viewPreview.width - (viewPreview.width * (left + width) / RealSense.COLOR_WIDTH)
        )

        set.connect(
                imageViewCensored.id,
                ConstraintSet.TOP,
                ConstraintSet.PARENT_ID,
                ConstraintSet.TOP,
                viewPreview.height * top / RealSense.COLOR_HEIGHT
        )
        set.connect(
                imageViewCensored.id,
                ConstraintSet.BOTTOM,
                ConstraintSet.PARENT_ID,
                ConstraintSet.BOTTOM,
                viewPreview.height - (viewPreview.height * (top + height) / RealSense.COLOR_HEIGHT)
        )
        set.applyTo(viewPreview)
    }

}