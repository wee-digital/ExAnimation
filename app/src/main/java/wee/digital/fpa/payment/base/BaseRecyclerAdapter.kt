package wee.digital.fpa.payment.base

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView


abstract class BaseRecyclerAdapter<T> : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v)

    /**
     * [BaseRecyclerAdapter] abstract function for initialize recycler view type.
     */
    @LayoutRes
    protected abstract fun layoutResource(model: T, position: Int): Int

    protected abstract fun View.onBindModel(model: T, position: Int, @LayoutRes layout: Int)


    /**
     * [RecyclerView.Adapter] override.
     */
    override fun getItemCount(): Int {
        return if (blankLayoutResource != 0 || footerLayoutResource != 0)
            size + 1
        else size
    }

    override fun getItemViewType(position: Int): Int {

        if (dataIsEmpty && blankLayoutResource != 0) return blankLayoutResource

        if (dataNotEmpty && footerLayoutResource != 0 && position == size) return footerLayoutResource

        val model = get(position) ?: return 0

        return layoutResource(model, position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = if (viewType != 0) {
            LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        } else {
            goneView(parent.context)
        }
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {

        val type = getItemViewType(position)

        if (type == 0) return

        if (type == blankLayoutResource) {
            blankVisible(viewHolder.itemView)
            return
        }

        if (type == footerLayoutResource) {
            if (footerIndexed == position) return
            footerIndexed = position
            footerVisible(viewHolder.itemView, position)
            return
        }

        val model = get(position) ?: return

        viewHolder.itemView.onBindModel(model, position, type)

        viewHolder.itemView.setOnClickListener {
            itemClick(model, position)
        }
    }

    private fun goneView(context: Context): View {
        val view = View(context)
        view.visibility = View.GONE
        return view
    }

    /**
     * Layout resource for empty list.
     */
    private var blankLayoutResource = blankLayoutResource()

    @LayoutRes
    open fun blankLayoutResource(): Int {
        return 0
    }


    /**
     * Layout resource for footer item.
     */
    @Volatile
    private var footerIndexed: Int = -1

    private var footerLayoutResource = footerLayoutResource()

    @LayoutRes
    open fun footerLayoutResource(): Int {
        return 0
    }

    open fun showFooter(@LayoutRes res: Int) {
        footerLayoutResource = res
        notifyItemChanged(size)
    }

    open fun hideFooter() {
        footerLayoutResource = 0
        notifyItemChanged(size)
    }


    /**
     * User interfaces.
     */
    open var itemClick: (T, Int) -> Unit = { _, _ -> }

    open fun onItemClick(block: (T, Int) -> Unit) {
        itemClick = block
    }

    // footerLayoutResource() != 0
    private var footerVisible: (View, Int) -> Unit = { _, _ -> }

    open fun onBindFooter(block: (View, Int) -> Unit) {
        footerVisible = block
    }

    // blankLayoutResource() != 0
    private var blankVisible: (View) -> Unit = {}

    open fun onBindBlank(block: ((View) -> Unit)) {
        blankVisible = block
    }


    /**
     * Data
     */
    open var listItem: MutableList<T> = mutableListOf()

    open val size: Int get() = listItem.size

    open val dataIsEmpty: Boolean get() = listItem.isEmpty()

    open val dataNotEmpty: Boolean get() = listItem.isNotEmpty()

    open val lastIndex: Int get() = listItem.lastIndex

    open fun indexInBound(position: Int): Boolean {
        return position > -1 && position < size
    }

    open fun get(position: Int): T? {
        if (indexInBound(position)) return listItem[position]
        return null
    }

    open fun set(collection: Collection<T>?) {
        listItem = collection?.toMutableList() ?: mutableListOf()
        notifyDataSetChanged()
    }

    open fun set(mutableList: MutableList<T>?) {
        listItem = mutableList ?: mutableListOf()
        notifyDataSetChanged()
    }

    open fun set(array: Array<T>?) {
        listItem = array?.toMutableList() ?: mutableListOf()
        notifyDataSetChanged()
    }

    open fun set(model: T?) {
        listItem = if (null == model) mutableListOf()
        else mutableListOf(model)
        notifyDataSetChanged()
    }

    open fun setElseEmpty(collection: Collection<T>?) {
        if (collection.isNullOrEmpty()) return
        listItem = collection.toMutableList()
        notifyDataSetChanged()
    }

    open fun setElseEmpty(mutableList: MutableList<T>?) {
        if (mutableList.isNullOrEmpty()) return
        listItem = mutableList
        notifyDataSetChanged()
    }

    open fun setElseEmpty(array: Array<T>?) {
        if (null == array || array.isEmpty()) return
        listItem = array.toMutableList()
        notifyDataSetChanged()
    }

    open fun setElseEmpty(model: T?) {
        model ?: return
        listItem = mutableListOf(model)
        notifyDataSetChanged()
    }

    open fun add(collection: Collection<T>?) {
        if (collection.isNullOrEmpty()) return
        listItem.addAll(collection)
        notifyDataSetChanged()
    }

    open fun add(array: Array<T>?) {
        if (null == array || array.isEmpty()) return
        listItem.addAll(array)
        notifyDataSetChanged()
    }

    open fun add(model: T?) {
        model ?: return
        listItem.add(model)
        notifyDataSetChanged()
    }

    open fun addFirst(model: T?) {
        model ?: return
        listItem.add(0, model)
        notifyDataSetChanged()
    }

    open fun edit(index: Int, model: T?) {
        model ?: return
        if (indexInBound(index)) {
            listItem[index] = model
            notifyItemChanged(index)
        }
    }

    open fun remove(index: Int) {
        listItem.removeAt(index)
        notifyItemRemoved(index)
    }

    open fun remove(model: T?) {
        model ?: return
        val index = listItem.indexOf(model)
        if (indexInBound(index)) {
            listItem.remove(model)
            notifyItemRemoved(index)
        }
    }

    open fun clear() {
        listItem.clear()
        notifyDataSetChanged()
    }

    open fun unBind() {
        listItem = mutableListOf()
        notifyDataSetChanged()
    }

    open fun notifyRangeChanged() {
        val s = size
        notifyItemRangeChanged(s, size + 1)
    }

    open fun bind(
            recyclerView: RecyclerView,
            spanCount: Int,
            includeEdge: Boolean = true,
            block: (androidx.recyclerview.widget.GridLayoutManager.() -> Unit)? = null
    ) {

        val layoutManager =
                androidx.recyclerview.widget.GridLayoutManager(recyclerView.context, spanCount)
        layoutManager.spanSizeLookup =
                object : androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        return if (dataIsEmpty || position == size) layoutManager.spanCount
                        else 1
                    }
                }
        block?.let { layoutManager.block() }
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = this
    }

}