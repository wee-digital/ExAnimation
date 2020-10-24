package wee.digital.fpa.ui.base

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import wee.digital.fpa.R
import wee.digital.library.extension.hideSystemUI
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
    final override val nav: NavController? get() = findNavController(navigationHostId())

    final override val log: Logger by lazy { Logger(this::class) }

    /**
     * [BaseActivity] properties
     */
    open fun navigationHostId(): Int {
        return 0
    }

    fun <T> LiveData<T>.observe(block: (T) -> Unit) {
        observe(this@BaseActivity, Observer(block))
    }

    fun navAnim(): NavOptions {
        return NavOptions.Builder()
                .setEnterAnim(R.anim.slide_in_up)
                .setPopExitAnim(R.anim.slide_in_down)
                .build()
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            hideSystemUI()
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.windowToken, 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

}