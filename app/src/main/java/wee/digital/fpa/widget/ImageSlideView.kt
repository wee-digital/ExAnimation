package wee.digital.fpa.widget

import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.daimajia.slider.library.Animations.BaseAnimationInterface
import com.daimajia.slider.library.SliderLayout
import com.daimajia.slider.library.SliderTypes.BaseSliderView
import com.daimajia.slider.library.SliderTypes.TextSliderView
import com.daimajia.slider.library.Transformers.BaseTransformer
import com.nineoldandroids.view.ViewHelper
import kotlinx.android.synthetic.main.widget_image_slider.view.*
import wee.digital.fpa.R

class ImageSlideView : ConstraintLayout {

    private var mListItem: List<Int>? = null


    constructor(context: Context, attr: AttributeSet? = null) : super(context, attr)

    init {
        LayoutInflater.from(context).inflate(R.layout.widget_image_slider, this)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        slideLayout.removeAllSliders()
        slideIndicator.destroySelf()
    }

    var listItem: List<Int>?
        get() = mListItem
        set(value) {
            mListItem = value ?: return
            slideLayout.removeAllSliders()
            initSlide(value)
        }

    private fun initSlide(list: List<Int>) {
        val fileMaps = HashMap<String, Int>()
        for (i in 0..list.lastIndex) {
            fileMaps["Demo $i"] = list[i]
        }
        fileMaps.keys.forEach { key ->
            val textSliderView = TextSliderView(context as Activity).apply {
                image(fileMaps[key]!!)
                        .setScaleType(BaseSliderView.ScaleType.Fit)
                        .bundle(Bundle())
                        .bundle.putString("extra", key)
            }
            slideLayout.apply {
                addSlider(textSliderView)
                setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom)
                setCustomAnimation(DescriptionAnimation())
                setDuration(5000)
                setPagerTransformer(true, FadeOutTransformation())
            }
        }
    }

    class DescriptionAnimation : BaseAnimationInterface {

        override fun onPrepareCurrentItemLeaveScreen(current: View) {
            val descriptionLayout = current.findViewById<View>(com.daimajia.slider.library.R.id.description_layout)
            if (descriptionLayout != null) {
                current.findViewById<View>(com.daimajia.slider.library.R.id.description_layout).visibility = View.INVISIBLE
            }
        }

        /**
         * When next item is coming to show, let's hide the description layout.
         * @param next
         */
        override fun onPrepareNextItemShowInScreen(next: View) {
            val descriptionLayout = next.findViewById<View>(com.daimajia.slider.library.R.id.description_layout)
            if (descriptionLayout != null) {
                next.findViewById<View>(com.daimajia.slider.library.R.id.description_layout).visibility = View.INVISIBLE
            }
        }


        override fun onCurrentItemDisappear(view: View) {

        }

        /**
         * When next item show in ViewPagerEx, let's make an animation to show the
         * description layout.
         * @param view
         */
        override fun onNextItemAppear(view: View) {
            val descriptionLayout = view.findViewById<View>(com.daimajia.slider.library.R.id.description_layout)
            if (descriptionLayout != null) {
                val layoutY = ViewHelper.getY(descriptionLayout)
                ObjectAnimator.ofFloat(
                        descriptionLayout, "y", layoutY + descriptionLayout.height,
                        layoutY)
                        .setDuration(500)
                        .start()
            }
        }
    }

    inner class FadeOutTransformation : BaseTransformer() {
        override fun onTransform(page: View?, position: Float) {
            page!!.translationX = -position * page.width

            page.alpha = 1 - Math.abs(position)

        }

        public override fun isPagingEnabled(): Boolean {
            return true
        }
    }


}