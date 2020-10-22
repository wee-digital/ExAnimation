package wee.digital.log.dialog

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.dialog_log.view.*
import wee.digital.log.R

abstract class BaseDialog<T> {

    protected var self: AlertDialog? = null

    protected lateinit var view: View

    val context: Context? get() = view.context

    @Suppress("UNCHECKED_CAST")
    val adapter: Adapter<T>
        get() = view.dialogRecyclerView.adapter as Adapter<T>

    protected abstract fun View.onViewCreated()

    protected abstract fun onClearClick()

    protected abstract fun adapter(): Adapter<T>

    constructor(activity: FragmentActivity?) {
        activity ?: return
        onViewCreate(activity)
        onViewConfigs()
    }

    private fun onViewCreate(activity: FragmentActivity) {
        view = LayoutInflater.from(activity).inflate(R.layout.dialog_log, null)
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity, R.style.LogDialog)
        builder.setView(view)
        self = builder.create()
        onDismiss {}
        onShow {}
    }

    private fun onViewConfigs() {
        view.dialogRecyclerView.layoutManager = LinearLayoutManager(view.context).apply {
            reverseLayout = true
            stackFromEnd = true
        }
        view.dialogRecyclerView.adapter = adapter()
        view.viewClose.setOnClickListener {
            dismiss()
        }
        view.viewClear.setOnClickListener {
            onClearClick()
            dismiss()
        }
        view.onViewCreated()
        view.isFocusable = false
    }

    fun onShow(block: () -> Unit) {
        self?.setOnShowListener {
            onShow()
            block.apply { block() }
        }
    }

    protected open fun onShow() {

    }

    fun onDismiss(block: () -> Unit) {
        self?.setOnDismissListener {
            onDismiss()
            block.apply { block() }
        }
    }

    protected open fun onDismiss() {
    }

    open fun show() {
        try {
            self?.show()
        } catch (ignore: WindowManager.BadTokenException) {
        }
    }

    open fun dismiss() {
        self?.dismiss()
    }

    abstract class Adapter<T> : RecyclerView.Adapter<Adapter.VH>() {

        class VH(v: View) : RecyclerView.ViewHolder(v)

        private var mList: List<T>? = null

        var list: List<T>?
            get() = mList
            set(value) {
                mList = value
                notifyDataSetChanged()
            }

        override fun getItemCount(): Int {
            return list?.size ?: 0
        }

        abstract fun itemLayoutRes(): Int

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val v = LayoutInflater.from(parent.context).inflate(itemLayoutRes(), parent, false)
            return VH(v)
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            holder.itemView.onBindItem(list!![position])
        }

        abstract fun View.onBindItem(model: T)

    }

}