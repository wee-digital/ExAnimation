package wee.digital.fpa.ui.adv

import androidx.lifecycle.MutableLiveData
import com.google.android.exoplayer2.upstream.RawResourceDataSource
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import wee.digital.fpa.R
import wee.digital.fpa.ui.base.BaseViewModel
import wee.digital.fpa.ui.base.EventLiveData
import java.util.concurrent.TimeUnit

class AdvVM : BaseViewModel() {

    val imageList = MutableLiveData<List<MediaItem>?>()

    private var disposable: Disposable? = null

    val pageLiveData = EventLiveData<Boolean>()

    private val videoList = listOf(
            MediaItem(RawResourceDataSource.buildRawResourceUri(R.raw.video_tree).toString()),
            MediaItem(RawResourceDataSource.buildRawResourceUri(R.raw.video_water).toString())
    )

    fun fetchAdvList() {
        imageList.postValue(listOf(
                MediaItem(null, R.mipmap.img_adv1),
                MediaItem(null, R.mipmap.img_adv2),
                MediaItem(null, R.mipmap.img_adv3),
                MediaItem(null, R.mipmap.img_adv4)
        ))
    }

    fun countdownToNextSlide() {
        disposable?.dispose()
        disposable = Observable
                .interval(5000, 0, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    disposable?.dispose()
                    pageLiveData.postValue(true)
                }, {})

    }

    fun stopCountdownToNextSlide() {
        disposable?.dispose()
    }

}