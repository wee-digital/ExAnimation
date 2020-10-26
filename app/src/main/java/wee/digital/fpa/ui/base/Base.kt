package wee.digital.fpa.ui.base

import androidx.annotation.MainThread
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.lifecycle.Observer
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import wee.digital.fpa.R
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.collections.set
import kotlin.reflect.KClass


const val DEFAULT_ARG_KEY: String = "default_arg_key"

fun <T> Fragment.navResult(key: String = DEFAULT_ARG_KEY): T? {
    return findNavController().currentBackStackEntry?.savedStateHandle?.get<T>(key)
}

fun <T> Fragment.navResultLiveData(key: String = DEFAULT_ARG_KEY): MutableLiveData<T>? {
    return findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<T>(key)
}

fun <T> Fragment.setNavResult(key: String?, result: T) {
    findNavController().previousBackStackEntry?.savedStateHandle?.set(key
            ?: DEFAULT_ARG_KEY, result)
}

fun NavOptions.Builder.setVerticalAnim(): NavOptions.Builder {
    setEnterAnim(R.anim.vertical_enter)
    setPopEnterAnim(R.anim.vertical_pop_enter)
    setExitAnim(R.anim.vertical_exit)
    setPopExitAnim(R.anim.vertical_pop_exit)
    return this
}

fun NavOptions.Builder.setHorizontalAnim(): NavOptions.Builder {
    setEnterAnim(R.anim.horizontal_enter)
    setPopEnterAnim(R.anim.horizontal_pop_enter)
    setExitAnim(R.anim.horizontal_exit)
    setPopExitAnim(R.anim.horizontal_pop_exit)
    return this
}

@Suppress("UNCHECKED_CAST")
fun <R, T : LiveData<R>> T.event(): T {
    val result = EventLiveData<R>()
    result.addSource(this) {
        result.value = it as R
    }
    return result as T
}

@Suppress("UNCHECKED_CAST")
fun <R, T : LiveData<R>> T.single(): T {
    val result = SingleLiveData<R>()
    result.addSource(this) {
        result.value = it as R
    }
    return result as T
}

inline fun <T> LiveData<T?>.observe(owner: LifecycleOwner, crossinline block: (t: T?) -> Unit) {
    this.observe(owner, Observer {
        block(it)
    })
}

/**
 * Live data only trigger when data change for multi observer
 */
open class EventLiveData<T> : MediatorLiveData<T>() {

    private val observers = ConcurrentHashMap<LifecycleOwner, MutableSet<ObserverWrapper<T>>>()

    @MainThread
    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        val wrapper = ObserverWrapper(observer)
        val set = observers[owner]
        set?.apply {
            @Suppress("UNCHECKED_CAST")
            add(wrapper as ObserverWrapper<T>)
        } ?: run {
            val newSet = Collections.newSetFromMap(ConcurrentHashMap<ObserverWrapper<T>, Boolean>())
            @Suppress("UNCHECKED_CAST")
            newSet.add(wrapper as ObserverWrapper<T>?)
            observers[owner] = newSet
        }
        super.observe(owner, wrapper)
    }

    override fun removeObservers(owner: LifecycleOwner) {
        observers.remove(owner)
        super.removeObservers(owner)
    }

    override fun removeObserver(observer: Observer<in T>) {
        observers.forEach {
            @Suppress("TYPE_INFERENCE_ONLY_INPUT_TYPES_WARNING")
            if (it.value.remove(observer)) {
                if (it.value.isEmpty()) {
                    observers.remove(it.key)
                }
                return@forEach
            }
        }
        super.removeObserver(observer)
    }

    @MainThread
    override fun setValue(t: T?) {
        observers.forEach { it.value.forEach { wrapper -> wrapper.newValue() } }
        super.setValue(t)
    }

    protected open fun onDataChanged(t: T?) {
    }


    /**
     * Used for cases where T is Void, to make calls cleaner.
     */
    @MainThread
    fun call() {
        value = null
    }

    private inner class ObserverWrapper<R>(private val observer: Observer<R>) : Observer<R> {

        private val pending = AtomicBoolean(false)

        override fun onChanged(t: R?) {
            if (pending.compareAndSet(true, false)) {
                @Suppress("UNCHECKED_CAST")
                (t as? T)?.also { onDataChanged(it) }
                observer.onChanged(t)
            }
        }

        fun newValue() {
            pending.set(true)
        }
    }

}

/**
 * Live data only trigger when data change for single observer
 */
open class SingleLiveData<T> : MediatorLiveData<T>() {

    private val observers = ConcurrentHashMap<LifecycleOwner, MutableSet<ObserverWrapper<T>>>()

    @MainThread
    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        val wrapper = ObserverWrapper(observer)
        val set = observers[owner]
        set?.apply {
            @Suppress("UNCHECKED_CAST")
            add(wrapper as ObserverWrapper<T>)
        } ?: run {
            val newSet = Collections.newSetFromMap(ConcurrentHashMap<ObserverWrapper<T>, Boolean>())
            @Suppress("UNCHECKED_CAST")
            newSet.add(wrapper as ObserverWrapper<T>?)
            observers[owner] = newSet
        }
        super.observe(owner, wrapper)
    }

    override fun removeObservers(owner: LifecycleOwner) {
        observers.remove(owner)
        super.removeObservers(owner)
    }

    override fun removeObserver(observer: Observer<in T>) {
        observers.forEach {
            @Suppress("TYPE_INFERENCE_ONLY_INPUT_TYPES_WARNING")
            if (it.value.remove(observer)) {
                if (it.value.isEmpty()) {
                    observers.remove(it.key)
                }
                return@forEach
            }
        }
        super.removeObserver(observer)
    }

    @MainThread
    override fun setValue(t: T?) {
        observers.forEach { it.value.forEach { wrapper -> wrapper.newValue() } }
        super.setValue(t)
    }

    protected open fun onDataChanged(t: T?) {
    }


    /**
     * Used for cases where T is Void, to make calls cleaner.
     */
    @MainThread
    fun call() {
        value = null
    }

    private inner class ObserverWrapper<R>(private val observer: Observer<R>) : Observer<R> {

        private val pending = AtomicBoolean(false)

        override fun onChanged(t: R?) {
            if (pending.compareAndSet(true, false)) {
                @Suppress("UNCHECKED_CAST")
                (t as? T)?.also { onDataChanged(it) }
                observer.onChanged(t)
            }
        }

        fun newValue() {
            pending.set(true)
        }
    }

}
