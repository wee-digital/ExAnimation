package wee.digital.fpa.util

import io.reactivex.Observer
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable


interface SimpleObserver<T> : Observer<T> {

    override fun onSubscribe(d: Disposable) {
    }

    override fun onComplete() {
    }

    override fun onError(e: Throwable) {
        onComplete(null, e)
    }

    override fun onNext(t: T) {
        onComplete(t, null)
    }

    fun onComplete(t: T?, e: Throwable?)
}

interface SimpleSingleObserver<T> : SingleObserver<T> {

    override fun onSubscribe(d: Disposable) {
    }

    override fun onSuccess(t: T) {
        onComplete(t, null)
    }

    override fun onError(e: Throwable) {
        onComplete(null, e)
    }

    fun onComplete(t: T?, e: Throwable?)
}
