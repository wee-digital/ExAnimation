package sample

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import wee.digital.fpa.app.toast
import wee.digital.fpa.ui.base.BaseFragment
import wee.digital.fpa.ui.base.navResultLiveData
import wee.digital.fpa.ui.base.setNavResult

private class PassingDataBetween2Fragment : BaseFragment() {

    override fun layoutResource(): Int = 0

    override fun onViewCreated() {
        view?.setOnClickListener {
            // Passing argument before navigate
            activityVM(Fragment2VM::class).argLiveData.value = Fragment2Arg(
                    key = className,
                    data = "Foo"
            )
            // navigate Fragment2
        }
    }

    override fun onLiveDataObserve() {
        // Observer data send from navigated if need
        navResultLiveData<String>(className)
                ?.observe {
                    println(it)
                }
    }
}

data class Fragment2Arg(val key: String, val data: String)

class Fragment2VM : ViewModel() {
    val argLiveData = MutableLiveData<Fragment2Arg>()
}

class Fragment2 : BaseFragment() {

    private val vm: Fragment2VM by lazy { activityVM(Fragment2VM::class) }

    override fun layoutResource(): Int = 0

    override fun onViewCreated() {
        view?.setOnClickListener {
            // Send data to previous fragment
            vm.argLiveData.value?.also {
                setNavResult(it.key, "hello i'm Sample2Fragment")
            }
            navigateUp()
        }
    }

    override fun onLiveDataObserve() {
        // Observer data from previous fragment
        vm.argLiveData.observe {
            toast(it.data)
        }
    }
}

