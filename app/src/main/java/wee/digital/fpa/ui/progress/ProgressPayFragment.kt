package wee.digital.fpa.ui.progress

import wee.digital.fpa.R
import wee.digital.fpa.ui.Main
import wee.digital.fpa.ui.base.activityVM
class ProgressPayFragment : Main.Fragment() {

    private val progressVM by lazy { activityVM(ProgressVM::class) }

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