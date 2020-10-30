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

    val second = MutableLiveData<Int>()

    val inTheEnd = MutableLiveData<Boolean>()

    fun startTimeout(intervalInSecond: Int) {
        val waitingCounter = AtomicInteger(intervalInSecond + 1)
        disposable?.dispose()
        disposable = Observable
                .interval(1, 1, TimeUnit.SECONDS)
                .map { waitingCounter.decrementAndGet() }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    second.value = it
                    when {
                        it == 0 -> {
                            inTheEnd.postValue(true)
                            inTheEnd.value = false
                        }
                        it < 0 -> {
                            disposable?.dispose()
                        }
                    }
                }, {})

    }

    fun stopTimeout() {
        disposable?.dispose()
        second.postValue(-1)
        inTheEnd.postValue(false)
    }

}