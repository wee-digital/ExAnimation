package wee.digital.library.extension

import android.app.Activity
import android.content.Context
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.content.res.Resources
import android.graphics.*
import android.os.Build
import android.text.Html
import android.text.InputFilter
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.annotation.ColorInt
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
                isSelected = false
                block(text.toString())
                clearFocus()
                return true
            }
            return false
        }
    })
}

fun EditText.filterChars(chars : CharArray){
    val arrayList = arrayListOf<InputFilter>()
    this.filters?.apply { arrayList.addAll(this) }
    arrayList.add(InputFilter { source, start, end, _, _, _ ->
        when {
            end > start -> for (index in start until end) {
                if (!String(chars).contains(source[index].toString())) {
                    return@InputFilter ""
                }
            }
        }
        return@InputFilter null
    })
    this.filters = arrayList.toArray(arrayOfNulls<InputFilter>(arrayList.size))
    this.inputType = EditorInfo.TYPE_TEXT_FLAG_NO_SUGGESTIONS
}


/**
 * Ex: "Kotlin   Language   Extension"
 * @return: "KotlinLanguageExtension"
 */
val EditText?.trimIndentText: String?
    get() {
        this ?: return null
        var s = text?.toString()
        if (s.isNullOrEmpty()) return null
        s = s.replace("\\s+", " ").trim()
        setText(s)
        setSelection(s.length)
        return s
    }

/**
 * Ex: "Kotlin   Language   Extension"
 * @return: "Kotlin Language Extension"
 */
val EditText?.trimText: String?
    get() {
        this ?: return null
        var s = text?.toString()
        if (s.isNullOrEmpty()) return null
        s = s.replace("\\s+", " ").trim()
        setText(s)
        setSelection(s.length)
        return s
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
    try {
        setTextColor(ContextCompat.getColor(context, colorRes))
    } catch (ignore: Resources.NotFoundException) {

    }
}

fun TextView.color(colorStr: String) {
    val s = if (colorStr.firstOrNull() != '#') "#$colorStr" else colorStr
    setTextColor(Color.parseColor(s))
}

fun TextView.setHyperText(s: String?) {
    post {
        text = when {
            s.isNullOrEmpty() -> null
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> Html.fromHtml(s, 1)
            else -> @Suppress("DEPRECATION")
            Html.fromHtml(s)
        }
    }
}

fun TextView.gradientHorizontal(@ColorRes colorStart: Int, @ColorRes colorEnd: Int= colorStart) {
    paint.shader = LinearGradient(0f, 0f, this.width.toFloat(), 0f,
            ContextCompat.getColor(context, colorStart),
            ContextCompat.getColor(context, colorEnd),
            Shader.TileMode.CLAMP)
}

fun TextView.gradientVertical(@ColorRes colorStart: Int, @ColorRes colorEnd: Int = colorStart) {
    paint.shader = LinearGradient(0f, 0f, 0f, this.height.toFloat(),
            ContextCompat.getColor(context, colorStart),
            ContextCompat.getColor(context, colorEnd),
            Shader.TileMode.CLAMP)
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

fun View.backgroundTint(@ColorInt color: Int) {
    post {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            background?.colorFilter = BlendModeColorFilter(color, BlendMode.SRC_ATOP)
        } else {
            background?.colorFilter = null
            @Suppress("DEPRECATION")
            background?.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
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

