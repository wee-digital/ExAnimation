package wee.digital.fpa.ui.adv

import kotlinx.android.synthetic.main.adv.*
import wee.digital.fpa.R
import wee.digital.fpa.ui.Main
import wee.digital.fpa.ui.base.activityVM

class AdvFragment : Main.Fragment() {

    private val advAdapter = AdvAdapter()

    private val advVM by lazy { activityVM(AdvVM::class) }

    override fun layoutResource(): Int {
        return R.layout.adv
    }

    override fun onViewCreated() {

    }

    override fun onLiveDataObserve() {
        advVM.fetchAdvList()
        advVM.imageList.observe {
            advAdapter.set(it)
            advAdapter.bindToViewPager(advViewPager)

            if (advAdapter.get(advAdapter.currentPosition +1)?.isImage!!){
                advVM.countdownToNextSlide(advAdapter.currentPosition)
            }
        }

        advVM.pageLiveData.observe {
            advViewPager.setCurrentItem(it,true)
            view?.postDelayed({
                if (advAdapter.get(it)?.isImage!!){
                    advVM.countdownToNextSlide(it)
                }
            },5000L)


        }
    }

    override fun onPause() {
        super.onPause()
        advVM.stopCountdownToNextSlide()
    }

}