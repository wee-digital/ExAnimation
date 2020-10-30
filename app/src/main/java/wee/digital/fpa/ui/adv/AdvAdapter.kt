package wee.digital.fpa.ui.adv

import android.view.View
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import kotlinx.android.synthetic.main.adv_image_item.view.*
import kotlinx.android.synthetic.main.adv_video_item.view.*
import wee.digital.fpa.R
import wee.digital.library.adapter.BaseRecyclerAdapter
import wee.digital.library.extension.load

class AdvAdapter : BaseRecyclerAdapter<MediaItem>() {

    /**
     * Current of viewpager position adapt this adapter
     */
    var currentPosition: Int = -1

    var onPageChanged: () -> Unit = {}

    override fun layoutResource(model: MediaItem, position: Int): Int {
        return if (model.imageRes != null) {
            R.layout.adv_image_item
        } else {
            R.layout.adv_video_item
        }
    }

    override fun View.onBindModel(model: MediaItem, position: Int, layout: Int) {
        currentPosition = position

        when {
            model.imageRes != null -> {
                advImageView.load(model.imageRes)
            }
            model.videoUri != null -> {
                customMediaView.videoUri = model.videoUri
                model.myMediaPlayer = customMediaView
                customMediaView.setVideoListener(object : MyMediaPlayListener {
                    override fun onVideoStopped() {
                        viewPager?.also {
                            it.currentItem = it.currentItem + 1
                        }
                    }
                })
                if (position == 0) {
                    customMediaView.onPlayVideo()
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return listItem.size * 10000000
    }

    override fun get(position: Int): MediaItem? {
        if (listItem.isEmpty()) return null
        val realPosition = position % listItem.size
        if (realPosition !in 0..lastIndex) return null
        return listItem[realPosition]
    }

    var viewPager: ViewPager2? = null

    fun bindToViewPager(viewPager: ViewPager2) {
        this.viewPager = viewPager
        viewPager.adapter = this
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrollStateChanged(state: Int) {
                when (state) {
                    ViewPager.SCROLL_STATE_DRAGGING -> {
                    }
                    ViewPager.SCROLL_STATE_IDLE -> {
                        onPageChanged()
                        if (currentPosition != -1) {
                            get(currentPosition)?.myMediaPlayer?.onStopVideo(currentPosition)
                        }
                        currentPosition = viewPager.currentItem
                        get(currentPosition)?.myMediaPlayer?.onPlayVideo(currentPosition)
                    }
                }
            }
        })
    }


}