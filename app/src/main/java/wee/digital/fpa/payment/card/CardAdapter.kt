package wee.digital.fpa.payment.card

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.card_item.view.*
import wee.digital.fpa.R

/**
 * -------------------------------------------------------------------------------------------------
 * @Project: ExAnimation
 * @Created: Huy 2020/10/22
 * @Organize: Wee Digital
 * @Description: ...
 * All Right Reserved
 * -------------------------------------------------------------------------------------------------
 */
class CardAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    /**
     * [RecyclerView.Adapter] override.
     */
    override fun getItemCount(): Int {
        return currentList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v =
                LayoutInflater.from(parent.context).inflate(R.layout.card_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        viewHolder.itemView.onBindViewHolder(currentList[position])
    }

    /**
     * [CardAdapter] properties
     */
    private var currentList: List<CardItem> = listOf()

    var onItemClick: (CardItem) -> Unit = { }

    private fun View.onBindViewHolder(model: CardItem) {
        paymentImageViewCard.setImageResource(model.image)
    }

    fun set(collection: Collection<CardItem>?) {
        currentList = collection?.toList() ?: listOf()
        notifyDataSetChanged()
    }

    fun bind(recyclerView: RecyclerView, spanCount: Int = 1, block: GridLayoutManager.() -> Unit = {}) {
        val lm = GridLayoutManager(recyclerView.context, spanCount)
        lm.block()
        recyclerView.layoutManager = lm
        recyclerView.adapter = this
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v)

}