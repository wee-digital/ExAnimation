package wee.digital.library.extension

import android.os.Build
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.annotation.IdRes
import androidx.fragment.app.*
import wee.digital.library.R


fun Fragment?.hideKeyboard() {
    this?.requireActivity()?.hideKeyboard()
}

fun DialogFragment?.hideSystemUI() {
    this?.dialog?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    this?.hideKeyboard()
    hideStatusBar()
    hideNavigationBar()
}

fun DialogFragment?.hideStatusBar() {
    this ?: return
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

        dialog?.window?.insetsController?.hide(WindowInsets.Type.statusBars())
    } else {
        @Suppress("DEPRECATION")
        dialog?.window?.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }
}

fun DialogFragment?.hideNavigationBar(hasFocus: Boolean = true) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && hasFocus) this?.dialog?.window?.apply {
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
        val decorView = this?.dialog?.window?.decorView ?: return
        decorView.systemUiVisibility = flags
        decorView.setOnSystemUiVisibilityChangeListener { visibility ->
            if (visibility and View.SYSTEM_UI_FLAG_FULLSCREEN == 0) {
                decorView.systemUiVisibility = flags
            }
        }
    }
}

fun FragmentActivity?.addFragment(
        fragment: Fragment, @IdRes container: Int,
        backStack: Boolean = true
) {
    this ?: return
    val tag = fragment::class.java.simpleName
    supportFragmentManager.scheduleTransaction {
        add(container, fragment, tag)
        if (backStack) addToBackStack(tag)
    }
}

fun FragmentActivity?.replaceFragment(
        fragment: Fragment, @IdRes container: Int,
        backStack: Boolean = true
) {
    this ?: return
    val tag = fragment::class.java.simpleName
    supportFragmentManager.scheduleTransaction {
        replace(container, fragment, tag)
        if (backStack) addToBackStack(tag)
    }
}

fun FragmentActivity?.removeFragment(cls: Class<*>) {
    removeFragment(cls.simpleName)
}

fun FragmentActivity?.removeFragment(tag: String?) {
    this ?: return
    tag ?: return
    val fragment = supportFragmentManager.findFragmentByTag(tag) ?: return
    supportFragmentManager.scheduleTransaction {
        remove(fragment)
    }
}

fun FragmentActivity?.isExist(cls: Class<*>): Boolean {
    this ?: return false
    val tag = cls.simpleName
    val fragment = supportFragmentManager.findFragmentByTag(tag)
    return null != fragment
}

fun FragmentActivity?.isNotExist(cls: Class<*>): Boolean {
    this ?: return false
    val tag = cls.simpleName
    val fragment = supportFragmentManager.findFragmentByTag(tag)
    return null == fragment
}


fun FragmentManager.scheduleTransaction(
        block: FragmentTransaction.() -> Unit
) {

    val transaction = beginTransaction()
    transaction.setCustomAnimations(
            R.anim.horizontal_enter,
            R.anim.horizontal_exit,
            R.anim.horizontal_pop_enter,
            R.anim.horizontal_pop_exit
    )
    transaction.block()
    transaction.commitAllowingStateLoss()

}

