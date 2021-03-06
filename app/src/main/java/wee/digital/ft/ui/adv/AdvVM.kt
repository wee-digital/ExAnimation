package wee.digital.ft.ui.adv

import androidx.lifecycle.MutableLiveData
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import wee.digital.ft.R
import wee.digital.ft.ui.base.BaseViewModel
import wee.digital.ft.ui.base.EventLiveData
import java.util.concurrent.TimeUnit

class AdvVM : BaseViewModel() {

    val imagesLiveData = MutableLiveData<List<AdvItem>?>()

    val pageLiveData = EventLiveData<Boolean>()

    private var disposable: Disposable? = null

    fun fetchAdvList() {
        imagesLiveData.postValue(listOf(
                AdvItem(null, R.mipmap.img_adv1),
                AdvItem(null, R.mipmap.img_adv2),
                AdvItem(null, R.mipmap.img_adv3),
                AdvItem(null, R.mipmap.img_adv4)
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