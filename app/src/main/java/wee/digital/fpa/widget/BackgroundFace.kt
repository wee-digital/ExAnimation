package wee.digital.fpa.widget

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.animation.AnimationUtils
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.text.HtmlCompat
import kotlinx.android.synthetic.main.background_face.view.*
import wee.digital.fpa.R
import wee.digital.library.extension.hide
import wee.digital.library.extension.load
import wee.digital.library.extension.show

class BackgroundFace : ConstraintLayout {

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

    fun showImageResult(frame : Bitmap?){
        bgFaceFrame.setImageBitmap(frame)
        bgFaceAnimGif.show()
        val animShow = AnimationUtils.loadAnimation(context, R.anim.fade_in)
        animShow.fillAfter = true
        bgFaceRootFrame.startAnimation(animShow)
    }

    fun hideImageResult(){
        bgFaceAnimGif.hide()
        val animHide = AnimationUtils.loadAnimation(context, R.anim.fade_out)
        animHide.fillAfter = true
        bgFaceRootFrame.startAnimation(animHide)
    }

}