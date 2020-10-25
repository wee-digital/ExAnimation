package wee.digital.fpa.repository.base

class BaseHelper {
    companion object {
        val ins: BaseHelper by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { BaseHelper() }
    }
}