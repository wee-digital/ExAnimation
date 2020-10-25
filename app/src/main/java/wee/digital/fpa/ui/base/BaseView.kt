package wee.digital.fpa.ui.base

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import wee.digital.fpa.R
import wee.digital.library.extension.ViewClickListener
import wee.digital.log.Logger
import kotlin.reflect.KClass

interface BaseView {

    val className: String get() = this::class.simpleName.toString()

    val log: Logger

    fun addClickListener(vararg views: View?) {
        val listener = object : ViewClickListener() {
            override fun onClicks(v: View?) {
                onViewClick(v)
            }
        }
        views.forEach {
            it?.setOnClickListener(listener)
        }
    }

    fun onViewClick(v: View?) {}

    val nav: NavController?

    fun navigate(directions: NavDirections, block: (NavOptions.Builder.() -> Unit) = {}) {
        val option = NavOptions.Builder()
                .setDefaultAnim()
        option.block()
        nav?.navigate(directions, option.build())
    }

    fun navigateUp() {
        nav?.navigateUp()
    }

    fun NavOptions.Builder.setDefaultAnim(): NavOptions.Builder {
        setEnterAnim(R.anim.vertical_enter)
        setPopEnterAnim(R.anim.vertical_pop_enter)
        setExitAnim(R.anim.vertical_exit)
        setPopExitAnim(R.anim.vertical_pop_exit)
        return this
    }

    fun <T : ViewModel> ViewModelStoreOwner.viewModel(cls: KClass<T>): T =
            ViewModelProvider(this).get(cls.java)

    fun <T : ViewModel> ViewModelStoreOwner.newVM(cls: KClass<T>): T =
            ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[cls.java]

    fun <T : ViewModel> Fragment.activityVM(cls: KClass<T>): T =
            ViewModelProvider(requireActivity()).get(cls.java)

    fun <T : ViewModel> AppCompatActivity.activityVM(cls: KClass<T>): T =
            ViewModelProvider(this).get(cls.java)

}