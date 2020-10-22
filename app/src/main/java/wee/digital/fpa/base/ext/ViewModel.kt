package wee.digital.fpa.base.ext

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import kotlin.reflect.KClass

class ShareVM : ViewModel()

fun <T : ViewModel> ViewModelStoreOwner.viewModel(cls: KClass<T>): T =
        ViewModelProvider(this).get(cls.java)

fun <T : ViewModel> ViewModelStoreOwner.newViewModel(cls: KClass<T>): T =
        ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[cls.java]

fun <T : ViewModel> Fragment.activityViewModel(cls: KClass<T>): T =
        ViewModelProvider(requireActivity()).get(cls.java)

fun <T : ViewModel> AppCompatActivity.activityViewModel(cls: KClass<T>): T =
        ViewModelProvider(this).get(cls.java)