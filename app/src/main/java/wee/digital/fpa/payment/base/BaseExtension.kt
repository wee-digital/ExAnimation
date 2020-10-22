package wee.digital.fpa.payment.base

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import kotlin.reflect.KClass


const val DEFAULT_ARG_KEY: String = "default_arg_key"


fun <T : ViewModel> ViewModelStoreOwner.viewModel(cls: KClass<T>): T =
        ViewModelProvider(this).get(cls.java)

fun <T : ViewModel> ViewModelStoreOwner.newViewModel(cls: KClass<T>): T =
        ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[cls.java]

fun <T : ViewModel> Fragment.activityViewModel(cls: KClass<T>): T =
        ViewModelProvider(requireActivity()).get(cls.java)

fun <T : ViewModel> AppCompatActivity.activityViewModel(cls: KClass<T>): T =
        ViewModelProvider(this).get(cls.java)

fun Fragment.navigate(directions: NavDirections, block: (NavOptions.Builder.() -> Unit) = {}) {
    val option = NavOptions.Builder()
            .setDefaultAnim()
    option.block()
    findNavController().navigate(directions, option.build())
}

fun Fragment.navigateUp() {
    findNavController().navigateUp()
}

val Fragment.className : String get() = this::class.simpleName.toString()

fun <T> Fragment.navResult(key: String = DEFAULT_ARG_KEY): T? {
    return findNavController().currentBackStackEntry?.savedStateHandle?.get<T>(key)
}

fun <T> Fragment.navResultLiveData(key: String = DEFAULT_ARG_KEY): MutableLiveData<T>? {
    return findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<T>(key)
}

fun <T> Fragment.setNavResult(key: String?, result: T) {
    findNavController().previousBackStackEntry?.savedStateHandle?.set(key
            ?: DEFAULT_ARG_KEY, result)
}

fun NavOptions.Builder.setDefaultAnim(): NavOptions.Builder {
/*    setEnterAnim(R.anim.vertical_enter)
    setPopEnterAnim(R.anim.vertical_pop_enter)
    setExitAnim(R.anim.vertical_exit)
    setPopExitAnim(R.anim.vertical_pop_exit)*/
    return this
}