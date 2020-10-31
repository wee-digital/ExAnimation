package wee.digital.fpa.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import wee.digital.fpa.R
import wee.digital.fpa.data.local.Config
import wee.digital.library.extension.hideSystemUI
import wee.digital.log.Logger

abstract class BaseBottomDialog : BottomSheetDialogFragment(), BaseView {

    /**
     * [BottomSheetDialogFragment] override
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, style())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(layoutResource(), container, false)
        view.setOnTouchListener { _, _ -> true }
        configDialog()
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
     * [BaseBottomDialog] Required implements
     */
    abstract fun layoutResource(): Int

    abstract fun onViewCreated()

    abstract fun onLiveDataObserve()

    /**
     * [BaseView] implement
     */
    final override val log: Logger by lazy { Logger(this::class) }

    /**
     * [BaseBottomDialog] properties
     */
    protected open fun style(): Int {
        return R.style.App_Dialog
    }

    private fun configDialog() {
        val bottomDialog = dialog as BottomSheetDialog
        val bottomSheet = bottomDialog.findViewById<View>(R.id.design_bottom_sheet)
        val coordinatorLayout = bottomSheet?.parent as? CoordinatorLayout ?: return
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.peekHeight = bottomSheet.height
        coordinatorLayout.parent.requestLayout()
    }

    fun <T> LiveData<T>.observe(block: (T) -> Unit) {
        observe(viewLifecycleOwner, Observer(block))
    }
}


