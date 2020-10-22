package wee.digital.camera.widget

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.LifecycleOwner
import kotlinx.android.synthetic.main.widget_detector_view.view.*
import wee.digital.camera.R
import wee.digital.camera.detector.FaceDetector
import wee.digital.camera.job.DebugDetectJob

class DetectorView : ConstraintLayout, DebugDetectJob.UiListener {

    var faceRectListener: (Int, Int, Int, Int) -> Unit = { _, _, _, _ ->
    }

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
        LayoutInflater.from(context).inflate(R.layout.widget_detector_view, this)
    }

    fun observe(lifecycleOwner: LifecycleOwner) {
        DebugDetectJob(this).observe(lifecycleOwner)
    }

    /**
     * [FaceDetector.OptionListener] implement
     */
    override fun onFaceScore(score: Float): Boolean {
        textViewScore.text = score.toString()
        return true
    }

    override fun onFaceRect(left: Int, top: Int, width: Int, height: Int): Boolean {
        textViewLeft.text = left.toString()
        textViewTop.text = top.toString()
        textViewWidth.text = width.toString()
        textViewHeight.text = height.toString()
        faceRectListener(left, top, width, height)
        return true
    }

    override fun onFaceDegrees(x: Double, y: Double): Boolean {
        textViewDegrees.text = "x %.2f,  y %.2f".format(x, y)
        return true
    }

    override fun onMaskLabel(label: String, confidence: Float): Boolean {
        textViewMaskLabel.text = label
        return true
    }

    override fun onDepthLabel(label: String, confidence: Float): Boolean {
        textViewDepthLabel.text = label
        return true
    }


    /**
     * [FaceDetector.DataListener] implement
     */
    override fun onFaceColorImage(bitmap: Bitmap?) {
        imageViewCropColor.setImageBitmap(bitmap)
    }

    override fun onFaceDepthImage(bitmap: Bitmap?) {
        imageViewCropDepth.setImageBitmap(bitmap)
    }

    override fun onPortraitImage(bitmap: Bitmap) {
        imageViewPortrait?.setImageBitmap(bitmap)
    }


    /**
     * [FaceDetector.StatusListener] implement
     */
    override fun onFaceLeaved() {
        textViewFaceStatus.text = "Leaved"
    }

    override fun onFaceChanged() {
        textViewFaceStatus.text = "Changed"
    }

    override fun onFacePerformed() {
        textViewFaceStatus.text = "Performed"
    }


}