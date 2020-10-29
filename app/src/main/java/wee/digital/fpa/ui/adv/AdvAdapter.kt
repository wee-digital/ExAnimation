package wee.digital.fpa.ui.adv

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import kotlinx.android.synthetic.main.fragment_slider.view.*
import wee.digital.fpa.R

class AdvAdapter : RecyclerView.Adapter<AdvAdapter.ViewHolder>() {

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v)

    /**
     * [RecyclerView.Adapter] override
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.fragment_slider, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return listItem.size * 10000000
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = get(position) ?: return
        holder.itemView.onBindViewHolder(model, position)
    }


    /**
     * Current of viewpager position adapt this adapter
     */
    var currentPosition: Int = -1

    private var listItem = mutableListOf<MyMediaPlayer>()

    fun get(position: Int): MyMediaPlayer? {
        if (listItem.isEmpty()) return null
        return listItem[position % listItem.size]
    }

    fun set(collection: Collection<MyMediaPlayer>) {
        listItem = collection.toMutableList()
        notifyDataSetChanged()
    }

    private fun View.onBindViewHolder(model: MyMediaPlayer, position: Int) {
        customMediaView.videoUri = model.uri
        model.myMediaPlayer = customMediaView
        customMediaView.setVideoListener(object : MyMediaPlayListener {
            override fun onVideoStopped() {
                viewPager?.also {
                    it.currentItem = it.currentItem + 1
                }
            }
        })
        if (position == 0) {
            currentPosition = 0
            customMediaView.onPlayVideo()
        }
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