package wee.digital.fpa.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import wee.digital.fpa.R
import wee.digital.fpa.shared.Config
import wee.digital.library.extension.hideSystemUI
import wee.digital.log.Logger

abstract class BaseDialog : DialogFragment(), BaseView {

    /**
     * [DialogFragment] override
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, style())
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.window?.attributes?.windowAnimations = R.style.App_DialogAnim
    }

    override fun onDestroyView() {
        super.onDestroyView()
        log.d("onViewDestroy")
        dialog?.window?.attributes?.windowAnimations = 0
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(layoutResource(), container, false)
        view.setOnTouchListener { _, _ -> true }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (Config.VIEW_ENABLE) {
            log.d("onViewCreated")
            onViewCreated()
        }
        if (Config.VM_ENABLE) {
            log.d("onLiveDataObserve")
            onLiveDataObserve()
        }
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

    override fun onResume() {
        super.onResume()
        log.d("onResume")
        hideSystemUI()
    }

    override fun onPause() {
        super.onPause()
        log.d("onPause")
    }

    /**
     * [BaseDialog] Required implements
     */
    abstract fun layoutResource(): Int

    abstract fun onViewCreated()

    abstract fun onLiveDataObserve()

    /**
     * [BaseView] implement
     */
    final override val nav get() = findNavController()

    final override val log by lazy { Logger(this::class) }

    /**
     * [BaseDialog] properties
     */
    protected open fun style(): Int {
        return R.style.App_Dialog_FullScreen_Transparent
    }

    fun <T> LiveData<T>.observe(block: (T) -> Unit) {
        observe(viewLifecycleOwner, Observer(block))
    }

}