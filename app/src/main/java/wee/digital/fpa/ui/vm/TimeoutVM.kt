package wee.digital.fpa.ui.vm

import androidx.lifecycle.MutableLiveData
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import wee.digital.fpa.ui.base.BaseViewModel
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

class TimeoutVM : BaseViewModel() {

    private var disposable: Disposable? = null

    val interval = MutableLiveData<Int>()

    fun startTimeout(intervalInSecond: Int) {
        val waitingCounter = AtomicInteger(intervalInSecond)
        interval.value = intervalInSecond
        disposable?.dispose()
        disposable = Observable
                .interval(1, 1, TimeUnit.SECONDS)
                .map {
                    waitingCounter.decrementAndGet()
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    interval.value = it
                    if (it <= 0) {
                        disposable?.dispose()
                    }
                }, {})

    }

    fun stopTimeout() {
        disposable?.dispose()
        interval.value = -1
    }

}