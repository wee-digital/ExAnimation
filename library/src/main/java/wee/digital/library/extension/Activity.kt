package wee.digital.library.extension


import android.app.Activity
import android.os.Build
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager

fun Activity?.hideKeyboard() {
    this ?: return
    val view = this.currentFocus
    if (view != null) {
        val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm?.hideSoftInputFromWindow(view.windowToken, 0)
    }
}

fun Activity?.hideSystemUI() {
    this?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    hideKeyboard()
    hideStatusBar()
    hideNavigationBar()
}

fun Activity?.hideStatusBar() {
    this ?: return
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        window.insetsController?.hide(WindowInsets.Type.statusBars())
    } else {
        @Suppress("DEPRECATION")
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }
}

fun Activity?.hideNavigationBar(hasFocus: Boolean = true) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && hasFocus) this?.window?.apply {
        setDecorFitsSystemWindows(false)
        return
    }
    @Suppress("DEPRECATION")
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && hasFocus) {
        val flags = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        val decorView = this?.window?.decorView ?: return
        decorView.systemUiVisibility = flags
        decorView.setOnSystemUiVisibilityChangeListener { visibility ->
            if (visibility and View.SYSTEM_UI_FLAG_FULLSCREEN == 0) {
                decorView.systemUiVisibility = flags
            }
        }
    }
}




