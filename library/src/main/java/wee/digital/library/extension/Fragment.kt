package wee.digital.library.extension

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import wee.digital.library.R


fun Fragment.hideKeyboard() {
    activity?.hideKeyboard()
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

