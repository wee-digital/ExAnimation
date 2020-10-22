package wee.digital.fpa.ui.sample

import android.os.Bundle
import wee.digital.fpa.ui.base.BaseFragment
import wee.digital.fpa.ui.base.activityViewModel
import wee.digital.fpa.ui.base.className
import wee.digital.fpa.ui.base.navResultLiveData

class Sample1Fragment : BaseFragment() {

    override fun layoutResource(): Int {
        return 0
    }

    override fun onCreated(state: Bundle?) {
        view?.setOnClickListener {
            activityViewModel(Sample2VM::class).argLiveData.value = Sample2Arg(
                    key = className,
                    field1 = "Foo",
                    field2 = "Bar"
            )
            // navigate Sample2Fragment
        }
    }

    override fun onLiveDataObserve() {
        navResultLiveData<String>(className)
                ?.observe {
                    println(it)
                }

    }
}