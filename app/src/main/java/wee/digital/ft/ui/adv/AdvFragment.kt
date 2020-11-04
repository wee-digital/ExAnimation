package wee.digital.ft.ui.adv

import kotlinx.android.synthetic.main.adv.*
import wee.digital.ft.R
import wee.digital.ft.ui.MainFragment
import wee.digital.ft.ui.base.viewModel
import wee.digital.ft.util.stopCamera

class AdvFragment : MainFragment() {

    private val advVM by lazy { viewModel(AdvVM::class) }

    private val advAdapter = AdvAdapter()

    override fun layoutResource(): Int {
        return R.layout.adv
    }

    override fun onViewCreated() {
        stopCamera()
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