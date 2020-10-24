package wee.digital.fpa.ui.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import wee.digital.fpa.R
import wee.digital.library.extension.ViewClickListener
import wee.digital.log.Logger

abstract class BaseDialog : DialogFragment(), BaseView {

    /**
     * [DialogFragment] override
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, style())
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackCallback)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(layoutResource(), container, false)
        view.setOnTouchListener { _, _ -> true }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onViewCreated()
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
     * [BaseDialog] Required implements
     */
    abstract fun layoutResource(): Int

    abstract fun onViewCreated()

    abstract fun onLiveDataObserve()

    /**
     * [BaseView] implement
     */
    override val baseActivity: BaseActivity? get() = activity as? BaseActivity

    /**
     * [BaseDialog] properties
     */
    protected val log: Logger by lazy { Logger(this::class) }

    protected open fun style(): Int {
        return R.style.App_Dialog
    }

    private val onViewClick: ViewClickListener by lazy {
        object : ViewClickListener() {
            override fun onClicks(v: View?) {
                onViewClick(v)
            }
        }
    }

    fun addClickListener(vararg views: View?) {
        views.forEach {
            it?.setOnClickListener(onViewClick)
        }
    }

    protected open fun onViewClick(v: View?) {}

    private val onBackCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (onBackPressed()) {
                popBackStack()
            }
        }
    }

    protected open fun onBackPressed(): Boolean {
        return true
    }

    val nav: NavController get() = findNavController()

    fun navigate(directions: NavDirections, block: (NavOptions.Builder.() -> Unit) = {}) {
        val option = NavOptions.Builder()
                .setDefaultAnim()
        option.block()
        nav.navigate(directions, option.build())
    }

    fun navigateUp() {
        nav.navigateUp()
    }

    fun NavOptions.Builder.setDefaultAnim(): NavOptions.Builder {
        setEnterAnim(R.anim.vertical_enter)
        setPopEnterAnim(R.anim.vertical_pop_enter)
        setExitAnim(R.anim.vertical_exit)
        setPopExitAnim(R.anim.vertical_pop_exit)
        return this
    }

    fun <T> LiveData<T>.observe(block: (T) -> Unit) {
        observe(viewLifecycleOwner, Observer(block))
    }


}