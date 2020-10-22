package wee.digital.fpa.payment.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import wee.digital.fpa.R


abstract class BaseDialog : DialogFragment() {

    @LayoutRes
    protected abstract fun layoutResource(): Int

    protected abstract fun onCreated(state: Bundle?)

    protected open fun onLiveDataObserve() {}

    protected open fun style(): Int {
        return R.style.App_Dialog
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, style())
    }

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

    override fun onStart() {
        super.onStart()
        when (style()) {
            R.style.App_Dialog_FullScreen,
            R.style.App_Dialog_FullScreen_Transparent -> dialog?.window?.apply {
                setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            }
        }
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