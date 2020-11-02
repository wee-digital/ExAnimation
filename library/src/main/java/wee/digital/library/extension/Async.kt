package wee.digital.library.extension


import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

val uiHandler: Handler get() = Handler(Looper.getMainLooper())

val isOnMainThread: Boolean get() = Looper.myLooper() == Looper.getMainLooper()

fun mainThread(block: () -> Unit) {
    if (Looper.myLooper() == Looper.getMainLooper()) block()
    else Handler(Looper.getMainLooper()).post { block() }
}

fun mainThread(delay: Long, block: () -> Unit) {
    Handler(Looper.getMainLooper()).postDelayed({ block() }, delay)
}

fun post(block: () -> Unit) {
    uiHandler.post { block() }
}

fun post(delay: Long, block: () -> Unit) {
    uiHandler.postDelayed({ block() }, delay)
}

