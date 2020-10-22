package wee.digital.fpa.payment.sample

import android.os.Bundle
import wee.digital.fpa.payment.base.BaseFragment
import wee.digital.fpa.payment.base.activityViewModel
import wee.digital.fpa.payment.base.navigateUp
import wee.digital.fpa.payment.base.setNavResult

class Sample2Fragment : BaseFragment() {

    /**
     * [BaseFragment] override
     */
    private val vm: Sample2VM by lazy {
        activityViewModel(Sample2VM::class)
    }

    override fun layoutResource(): Int {
        return 0
    }

    override fun onCreated(state: Bundle?) {
        view?.setOnClickListener {
            sendDataResult()
        }
    }

    override fun onLiveDataObserve() {
        /**
         * Observer data of previous fragment
         */
        vm.argLiveData.observe {
            println(it.field1)
            println(it.field2)
        }
    }

    /**
     * [Sample2Fragment] properties
     */
    /**
     * Send data to previous fragment
     */
    private fun sendDataResult() {
        vm.argLiveData?.value?.also {
            setNavResult(it.key, "hello i'm Sample2Fragment")
        }
        navigateUp()
    }
}