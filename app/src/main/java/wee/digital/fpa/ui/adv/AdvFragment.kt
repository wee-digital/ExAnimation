package wee.digital.fpa.ui.adv

import kotlinx.android.synthetic.main.adv.*
import wee.digital.fpa.R
import wee.digital.fpa.ui.MainFragment
import wee.digital.fpa.ui.base.viewModel

class AdvFragment : MainFragment() {

    private val advVM by lazy { viewModel(AdvVM::class) }

    private val advAdapter = AdvAdapter()

    override fun layoutResource(): Int {
        return R.layout.adv
    }

    override fun onViewCreated() {
        advAdapter.onPageChanged = {
            val i = advViewPager.currentItem
            log.d(i)
            if (advAdapter.get(i)?.isImage == true) {
                advVM.countdownToNextSlide()
            }
        }
    }

    override fun onLiveDataObserve() {
        advVM.fetchAdvList()
        advVM.imagesLiveData.observe {
            advAdapter.set(it)
            advAdapter.bindToViewPager(advViewPager)
            view?.postDelayed({
                advViewPager?.setCurrentItem(advViewPager.currentItem + 1, true)
            }, 5000L)
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