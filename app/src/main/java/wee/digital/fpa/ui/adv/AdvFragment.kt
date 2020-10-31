package wee.digital.fpa.ui.adv

import kotlinx.android.synthetic.main.adv.*
import wee.digital.fpa.R
import wee.digital.fpa.ui.Main
import kotlin.reflect.KClass

class AdvFragment : Main.Fragment<AdvVM>() {

    private val advAdapter = AdvAdapter()

    override fun layoutResource(): Int {
        return R.layout.adv
    }

    override fun localViewModel(): KClass<AdvVM> {
        return AdvVM::class
    }

    override fun onViewCreated() {
        advAdapter.onPageChanged = {
            val i = advViewPager.currentItem
            log.d(i)
            if (advAdapter.get(i)?.isImage == true) {
                localVM.countdownToNextSlide()
            }
        }
    }

    override fun onLiveEventChanged(event: Int) {
    }

    override fun onLiveDataObserve() {
        localVM.imageList.observe {
            advAdapter.set(it)
            advAdapter.bindToViewPager(advViewPager)
            view?.postDelayed({
                advViewPager?.setCurrentItem(advViewPager.currentItem + 1, true)
            }, 5000L)
        }
        localVM.pageLiveData.observe {
            val i = advViewPager.currentItem + 1
            advViewPager.setCurrentItem(i, true)
        }
    }

    override fun onPause() {
        super.onPause()
        localVM.stopCountdownToNextSlide()
    }


}