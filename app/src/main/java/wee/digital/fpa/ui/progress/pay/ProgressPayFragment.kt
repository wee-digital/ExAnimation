package wee.digital.fpa.ui.progress.pay

import wee.digital.fpa.R
import wee.digital.fpa.ui.base.BaseFragment

class ProgressPayFragment : BaseFragment() {

    private val vm: ProgressPayVM by lazy { activityVM(ProgressPayVM::class) }

    override fun layoutResource(): Int {
        return R.layout.progress_pay
    }

    override fun onViewCreated() {
    }

    override fun onLiveDataObserve() {

        /*if (drawable is Animatable) {
            val gif: Animatable = drawable as Animatable
            if (gif.isRunning) {
                gif.stop()
            } else {
                gif.start()
            }
        }*/
    }

}