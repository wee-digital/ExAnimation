package wee.digital.fpa.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

abstract class BaseFragment : Fragment() {

    @LayoutRes
    protected abstract fun layoutResource(): Int

    protected abstract fun onCreated(state: Bundle?)

    protected open fun onLiveDataObserve() {}

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(layoutResource(), container, false)
        view.setOnTouchListener { _, _ -> true }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onCreated(savedInstanceState)
        onLiveDataObserve()
    }

    /**
     * Utils
     */
    private val onViewClick: View.OnClickListener by lazy {
        object : View.OnClickListener {
            override fun onClick(v: View?) {
                onViewClick(v)
            }
        }
    }

    fun <T> LiveData<T>.observe(block: (T) -> Unit) {
        observe(viewLifecycleOwner, Observer { block(it) })
    }

    open fun onViewClick(view: View?) {}

    protected fun addClickListener(vararg views: View?) {
        for (v in views) v?.setOnClickListener(onViewClick)
    }

}