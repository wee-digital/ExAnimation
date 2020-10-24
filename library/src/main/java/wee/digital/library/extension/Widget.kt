package wee.digital.library.extension

import android.app.Activity
import android.content.Context
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.content.res.ColorStateList
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.PorterDuff
import android.os.Build
import android.text.Html
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView


/**
 * @param actionId: see [android.view.inputmethod.EditorInfo]
 */
fun EditText.addEditorActionListener(actionId: Int, block: (String?) -> Unit) {
    imeOptions = actionId

    setImeActionLabel(null, actionId)
    setOnEditorActionListener(object : TextView.OnEditorActionListener {
        override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
            if (actionId == actionId) {
                (context as? Activity)?.currentFocus?.windowToken.run {
                    val imm =
                            context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow((context as Activity).currentFocus?.windowToken, 0)
                }
                isSelected = false
                block(text.toString())
                clearFocus()
                return true
            }
            return false
        }
    })
}

fun NestedScrollView.scrollToCenter(view: View) {
    post {
        val top = view.top
        val bot = view.bottom
        val height = this.height
        this.scrollTo(0, (top + bot - height) / 2)
    }
}

fun TextView.color(@ColorRes colorRes: Int) {
    setTextColor(ContextCompat.getColor(context, colorRes))
}

fun TextView.setHyperText(s: String?) {
    text = when {
        s.isNullOrEmpty() -> null
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> Html.fromHtml(s, 1)
        else -> @Suppress("DEPRECATION")
        Html.fromHtml(s)
    }
}

fun ViewGroup.inflate(@LayoutRes layoutRes: Int): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, false)
}

fun View.updateState(state: Int) {
    if (visibility != state) {
        if (isOnMainThread) {
            visibility = state
        } else this.post {
            visibility = state
        }
    }
}

fun View.show() {
    updateState(View.VISIBLE)
}

fun View.hide() {
    updateState(View.INVISIBLE)
}

fun View.gone() {
    updateState(View.GONE)
}

fun View.isShow(show: Boolean?) {
    if (show == true) this.show()
    else this.gone()
}

fun View.isGone(gone: Boolean?) {
    if (gone == true) this.gone()
    else this.show()
}

fun View.activity(): Activity? {
    return context as? Activity
}

fun show(vararg views: View?) {
    for (v in views) v?.show()
}

fun hide(vararg views: View?) {
    for (v in views) v?.hide()
}

fun gone(vararg views: View?) {
    for (v in views) v?.gone()
}

fun View.backgroundTint(color: Int) {
    post {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            background?.colorFilter = BlendModeColorFilter(color, BlendMode.SRC_OVER)
        } else {
            @Suppress("DEPRECATION")
            background.setTint(color)
        }
    }
}

fun RadioGroup.addOnCheckedChangeListener(block: (RadioButton) -> Unit) {
    setOnCheckedChangeListener { _, checkedId ->
        val button = (context as Activity).findViewById<RadioButton>(checkedId)
        block(button)
    }
}

fun Context.view(@LayoutRes layoutRes: Int): View {
    val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
    return inflater.inflate(layoutRes, null)
}

