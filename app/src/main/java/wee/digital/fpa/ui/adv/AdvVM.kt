package wee.digital.fpa.ui.adv

import androidx.lifecycle.MutableLiveData
import com.google.android.exoplayer2.upstream.RawResourceDataSource
import wee.digital.fpa.R
import wee.digital.fpa.ui.base.BaseViewModel

class AdvVM : BaseViewModel() {

    val imageList = MutableLiveData<List<Int>>()

    private val videoList = listOf(
            MediaItem(RawResourceDataSource.buildRawResourceUri(R.raw.video_tree).toString()),
            MediaItem(RawResourceDataSource.buildRawResourceUri(R.raw.video_water).toString())
    )

    fun fetchAdvList() {
        imageList.postValue(listOf(
                R.mipmap.img_adv1, R.mipmap.img_adv2, R.mipmap.img_adv3, R.mipmap.img_adv4
        ))
    }

}