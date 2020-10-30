package wee.digital.fpa.ui.adv

import kotlinx.android.synthetic.main.adv.*
import wee.digital.fpa.MainDirections
import wee.digital.fpa.R
import wee.digital.fpa.ui.Main
import wee.digital.fpa.ui.base.activityVM
import wee.digital.fpa.ui.progress.ProgressArg
import wee.digital.fpa.ui.progress.ProgressVM
import wee.digital.library.extension.post

class AdvFragment : Main.Fragment() {

    private val advAdapter = AdvAdapter()

    private val advVM by lazy { viewModel(AdvVM::class) }

    override fun layoutResource(): Int {
        return R.layout.adv
    }
    private val progressVM by lazy { activityVM(ProgressVM::class) }
    override fun onViewCreated() {
        viewTest.setOnClickListener {
            progressVM.arg.value = ProgressArg.payment.also {
                it.direction = MainDirections.actionGlobalProgressPayFragment()
            }

            post(5000) {
                progressVM.arg.value = null
            }
        }

    }

    override fun onLiveDataObserve() {
        advVM.fetchAdvList()
        advAdapter.onPageChanged = {
            val i = advViewPager.currentItem
            log.d(i)
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