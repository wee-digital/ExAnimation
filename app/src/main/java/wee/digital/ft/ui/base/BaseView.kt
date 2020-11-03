package wee.digital.ft.ui.base

import android.view.View
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import wee.digital.ft.R
import wee.digital.library.extension.ViewClickListener
import wee.digital.log.Logger

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

    val nav: NavController

    fun navigate(directions: NavDirections, block: (NavOptions.Builder.() -> Unit) = {}) {
        val options = NavOptions.Builder()
                .setDefaultAnim()
        options.block()
        nav.navigate(directions, options.build())
    }

    fun navigateUp() {
        nav.navigateUp()
    }

    fun NavOptions.Builder.setDefaultAnim(): NavOptions.Builder {
        setEnterAnim(R.anim.vertical_reserved_enter)
        setPopEnterAnim(R.anim.vertical_reserved_pop_enter)
        return this
    }

    fun NavOptions.Builder.setNoneAnim(): NavOptions.Builder {
        setEnterAnim(0)
        setPopEnterAnim(0)
        return this
    }

    fun NavOptions.Builder.setLaunchSingleTop(): NavOptions.Builder {
        setLaunchSingleTop(true)
        setPopUpTo(nav.graph.id, false)
        return this
    }
}

