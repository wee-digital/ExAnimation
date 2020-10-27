package wee.digital.fpa.ui.adv

import android.view.View
import com.google.android.exoplayer2.upstream.RawResourceDataSource
import kotlinx.android.synthetic.main.adv.*
import wee.digital.fpa.R
import wee.digital.fpa.ui.base.BaseDialog
import wee.digital.fpa.ui.base.activityVM
class AdvFragment : BaseDialog() {

    private val videoList = listOf(
            MyMediaPlayer(RawResourceDataSource.buildRawResourceUri(R.raw.video_tree).toString()),
            MyMediaPlayer(RawResourceDataSource.buildRawResourceUri(R.raw.video_water).toString())
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