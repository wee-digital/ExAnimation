package wee.digital.fpa.ui.adv

import kotlinx.android.synthetic.main.adv.*
import wee.digital.fpa.R
import wee.digital.fpa.ui.Main

class AdvFragment : Main.Fragment() {

    private val advAdapter = AdvAdapter()

    private val advVM by lazy { viewModel(AdvVM::class) }

    override fun layoutResource(): Int {
        return R.layout.adv
    }

    override fun onViewCreated() {

    }

    override fun onLiveDataObserve() {
        advVM.fetchAdvList()
        advAdapter.onPageChanged = {
            val i = advViewPager.currentItem
            if (advAdapter.get(i)?.isImage == true) {
                advVM.countdownToNextSlide()
            }
        }
        advVM.imageList.observe {
            advAdapter.set(it)
            advAdapter.bindToViewPager(advViewPager)
        }
        advVM.pageLiveData.observe {
            val i = advViewPager.currentItem + 1
            advViewPager.setCurrentItem(i, true)
        }
    }

    override fun onPause() {
        super.onPause()
        advVM.stopCountdownToNextSlide()
    }

}