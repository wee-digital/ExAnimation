package wee.digital.fpa.widget

import android.content.Context
import android.graphics.BitmapFactory
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.animation.AnimationUtils
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.text.HtmlCompat
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import kotlinx.android.synthetic.main.background_face.view.*
import wee.digital.fpa.R
import wee.digital.library.extension.GlideApp
import wee.digital.library.extension.hide
import wee.digital.library.extension.load
import wee.digital.library.extension.show

class BackgroundFace : ConstraintLayout {

    private val viewTransition = ChangeBounds().apply {
        duration = 400
    }

    constructor(context: Context) : super(context) {
        initView(context, null)
    }

    constructor(context: Context, attributeSet: AttributeSet?) : super(context, attributeSet) {
        initView(context, attributeSet)
    }

    private fun initView(context: Context, attributeSet: AttributeSet?) {
        LayoutInflater.from(context).inflate(R.layout.background_face, this)

        bgFaceAnimGif.load(R.drawable.loading_faceface)

        val textFail = context.getString(
                R.string.bg_face_title,
                "<b><br><font color='#439EFB'>Place your face</font></b>"
        )
        bgFaceTitle.text = HtmlCompat.fromHtml(textFail, HtmlCompat.FROM_HTML_MODE_LEGACY)

    }

    fun showFrameResult(frame: ByteArray?){

        frame ?: return
        val bitmap = BitmapFactory.decodeByteArray(frame, 0, frame.size)
        bgFaceFrame.setImageBitmap(bitmap)

        bgFaceAnimGif.show()
        val animShow = AnimationUtils.loadAnimation(context, R.anim.fade_in)
        animShow.fillAfter = true
        bgFaceFrame.startAnimation(animShow)
    }

    fun hideFrameResult() {
        bgFaceAnimGif.hide()
        val animHide = AnimationUtils.loadAnimation(context, R.anim.fade_out)
        animHide.fillAfter = true
        bgFaceFrame.startAnimation(animHide)
    }

    fun animFrame() {
        bgFaceLine.numberLine = 0f
        bgFaceRounded.number = 0f

        val animHide = AnimationUtils.loadAnimation(context, R.anim.fade_out)
        animHide.fillAfter = true
        bgFaceContent.startAnimation(animHide)
        bgFaceTitle.startAnimation(animHide)

        val frameId = bgFaceFrame.id
        onViewAnimate {
            clear(frameId, ConstraintSet.BOTTOM)
            clear(frameId, ConstraintSet.TOP)
            clear(frameId, ConstraintSet.START)
            clear(frameId, ConstraintSet.END)

            connect(frameId, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
            connect(frameId, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
            connect(frameId, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)

            bgFaceFrame.animate().scaleX(0.5f).scaleY(0.5f).duration = 300
            bgFaceAnimGif.animate().scaleX(0.5f).scaleY(0.5f).duration = 300
        }

    }

    fun resetAnimFrame(){
        val animShow = AnimationUtils.loadAnimation(context, R.anim.fade_in)
        animShow.fillAfter = true
        bgFaceContent.startAnimation(animShow)
        bgFaceTitle.startAnimation(animShow)

        val frameId = bgFaceFrame.id
        onViewAnimate {
            clear(frameId, ConstraintSet.BOTTOM)
            clear(frameId, ConstraintSet.TOP)
            clear(frameId, ConstraintSet.START)
            clear(frameId, ConstraintSet.END)

            connect(frameId, ConstraintSet.TOP, bgFaceGuide25.id, ConstraintSet.TOP)
            connect(frameId, ConstraintSet.BOTTOM, bgFaceGuide75.id, ConstraintSet.BOTTOM)
            connect(frameId, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
            connect(frameId, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)

            bgFaceFrame.animate().scaleX(1f).scaleY(1f).duration = 300
            bgFaceAnimGif.animate().scaleX(1f).scaleY(1f).setDuration(300).withEndAction {
                bgFaceLine.numberLine = 1f
                bgFaceRounded.number = 1f
            }
        }
    }

    private fun onViewAnimate(block: ConstraintSet.() -> Unit) {
        TransitionManager.beginDelayedTransition(this, viewTransition)
        ConstraintSet().also {
            it.clone(bgFaceRoot)
            it.block()
            it.applyTo(bgFaceRoot)
        }
    }

}