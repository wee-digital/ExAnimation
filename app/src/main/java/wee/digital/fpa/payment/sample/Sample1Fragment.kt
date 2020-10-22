package wee.digital.fpa.payment.sample

import android.os.Bundle
import wee.digital.fpa.payment.base.BaseFragment
import wee.digital.fpa.payment.base.activityViewModel
import wee.digital.fpa.payment.base.className
import wee.digital.fpa.payment.base.navResultLiveData

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