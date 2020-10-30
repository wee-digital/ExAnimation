package wee.digital.fpa.ui.base

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import wee.digital.library.extension.hideSystemUI
import wee.digital.log.Logger

abstract class BaseFragment : Fragment(), BaseView {

    /**
     * [Fragment] override
     */
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


    override fun onResume() {
        super.onResume()
        requireActivity().hideSystemUI()
    }

    /**
     * [BaseFragment] required implements
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
     * [BaseFragment] properties
     */
    fun <T> LiveData<T>.observe(block: (T) -> Unit) {
        observe(viewLifecycleOwner, Observer(block))
    }

    open fun activity(): BaseActivity {
        if (activity !is BaseActivity) throw ClassCastException("BaseFragment must be owned in BaseActivity")
        return activity as BaseActivity
    }

    fun startClear(cls: Class<*>) {
        activity().run {
            val intent = Intent(this, cls)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            this.startActivity(intent)
            this.finish()
        }

    }

}