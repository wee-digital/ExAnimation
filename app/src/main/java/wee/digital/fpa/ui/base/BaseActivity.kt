package wee.digital.fpa.ui.base

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import wee.digital.fpa.R


abstract class BaseActivity : AppCompatActivity() {


    lateinit var nav: NavController

    @LayoutRes
    protected abstract fun layoutResource(): Int

    protected abstract fun onCreated(state: Bundle?)

    protected open fun navigationHostId(): Int? {
        return null
    }

    protected open fun onLiveDataObserve() {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutResource())
        navigationHostId()?.also {
            nav = findNavController(it)
        }
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
        observe(this@BaseActivity, Observer { block(it) })
    }

    open fun onViewClick(view: View?) {}

    protected fun addClickListener(vararg views: View?) {
        for (v in views) v?.setOnClickListener(onViewClick)
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
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

    fun navAnim(): NavOptions {
        return NavOptions.Builder()
                .setEnterAnim(R.anim.slide_in_up)
                .setPopExitAnim(R.anim.slide_in_down)
                .build()
    }

}