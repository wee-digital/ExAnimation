package wee.digital.fpa.ui.base

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.findNavController
import wee.digital.library.extension.ViewClickListener
import wee.digital.log.Logger

abstract class BaseActivity : AppCompatActivity(), BaseView {

    /**
     * [AppCompatActivity] override
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutResource())
        onViewCreated()
        onLiveDataObserve()
    }

    /**
     * [BaseActivity] abstract implements
     */
    abstract fun layoutResource(): Int

    abstract fun onViewCreated()

    abstract fun onLiveDataObserve()

    /**
     * [BaseView] implement
     */
    final override val baseActivity: BaseActivity? get() = this

    final override fun showProgress() {

    }

    final override fun hideProgress() {

    }

    final override fun alert(message: String?) {
        message ?: return

    }

    final override fun alert(message: String?, block: () -> Unit) {
        message ?: return

    }

    /**
     * [BaseActivity] properties
     */
    protected val log: Logger by lazy { Logger(this::class) }

    open fun navigationHostId(): Int {
        return 0
    }

    val nav: NavController get() = findNavController(navigationHostId())

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

    fun <T> LiveData<T>.observe(block: (T) -> Unit) {
        observe(this@BaseActivity, Observer(block))
    }

    fun navAnim(): NavOptions {
        return NavOptions.Builder()
                .setEnterAnim(R.anim.slide_in_up)
                .setPopExitAnim(R.anim.slide_in_down)
                .build()
    }

}