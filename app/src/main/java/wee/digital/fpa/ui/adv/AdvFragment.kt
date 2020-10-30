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
        advVM.imageList.observe {
            advAdapter.set(it)
            advAdapter.bindToViewPager(advViewPager)

            // sai cho nay
            if (advAdapter.get(advAdapter.currentPosition + 1)?.isImage!!) {
                advVM.countdownToNextSlide(advAdapter.currentPosition)
            }
        }

        advVM.pageLiveData.observe {
            advViewPager.setCurrentItem(it, true)

            // sai cho nay
            if (advAdapter.get(it + 1)?.isImage == true) {
                advVM.countdownToNextSlide(it)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        advVM.stopCountdownToNextSlide()
    }

}