package wee.digital.fpa.ui.adv

import android.view.View
import kotlinx.android.synthetic.main.adv.*
import wee.digital.fpa.R
import wee.digital.fpa.ui.base.BaseDialog
import wee.digital.fpa.ui.base.activityVM
class AdvFragment : BaseDialog() {

    //Sample test
    private val videoList = listOf(
            MyMediaPlayer("https://assets.mixkit.co/videos/preview/mixkit-a-woman-walking-on-the-beach-on-a-sunny-day-1208-large.mp4"),
            MyMediaPlayer("https://assets.mixkit.co/videos/preview/mixkit-tree-with-yellow-flowers-1173-large.mp4"),
            MyMediaPlayer("https://assets.mixkit.co/videos/preview/mixkit-woman-swimming-in-a-pool-1258-large.mp4")
    )

    private lateinit var adapter: MediaAdapter

    private val vm by lazy { activityVM(AdvVM::class) }

    override fun layoutResource(): Int {
        return R.layout.adv
    }

    override fun onViewCreated() {
        adapter = MediaAdapter().also {
            it.set(videoList)
            it.bindToViewPager(advertiseViewPager)
        }
    }

    override fun onLiveDataObserve() {

    }

    override fun onViewClick(v: View?) {
        when (v) {

        }
    }

}