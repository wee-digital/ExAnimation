package wee.digital.library.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.viewpager.widget.ViewPager

abstract class BasePagerAdapter<T> : androidx.viewpager.widget.PagerAdapter() {

    var data: List<T> = listOf()

    val size: Int get() = data.size

    open fun set(item: T?) {
        item ?: return
        data = listOf(item)
        notifyDataSetChanged()
    }

    open fun set(collection: List<T>?) {
        collection ?: return
        if (collection.isEmpty()) return
        data = collection.toList()
        notifyDataSetChanged()
    }

    open fun get(position: Int): T? {
        if (data.isEmpty()) return null
        return data[position % size]
    }

    fun getActualPosition(position: Int): Int {
        return position % size
    }

    open fun bind(viewPager: ViewPager?) {
        if (data.isEmpty()) return
        viewPager?.adapter = this
    }

    @LayoutRes
    abstract fun layoutRes(): Int

    abstract fun View.onBind(model: T)

    override fun getCount(): Int {
        return size
    }

    override fun getItemPosition(obj: Any): Int {
        @Suppress("UNCHECKED_CAST")
        val position = data.indexOf(obj as T)
        return if (position >= 0) position else POSITION_NONE
    }

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return obj == view
    }

    override fun instantiateItem(viewGroup: ViewGroup, position: Int): Any {
        val view = LayoutInflater.from(viewGroup.context).inflate(layoutRes(), viewGroup, false)
        val model = get(position)
        if (null != model) {
            view.onBind(model)
        }
        viewGroup.addView(view, 0)
        return view
    }

    override fun destroyItem(viewGroup: ViewGroup, position: Int, obj: Any) {
        viewGroup.removeView(obj as View)
    }

}