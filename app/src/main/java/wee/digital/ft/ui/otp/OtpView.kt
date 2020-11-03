package wee.digital.ft.ui.otp

import android.view.MotionEvent
import android.webkit.JavascriptInterface
import gun0912.tedkeyboardobserver.TedRxKeyboardObserver
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.otp.*
import wee.digital.ft.R
import wee.digital.ft.util.SimpleLifecycleObserver
import wee.digital.ft.util.screenHeight
import wee.digital.library.extension.load
import wee.digital.library.extension.post

class OtpView(private val v: OtpFragment) {

    private var keyboardDisposable: Disposable? = null

    private var isMargin = false

    fun onViewInit() {
        settingWebView()
        v.viewLifecycleOwner.lifecycle.addObserver(object : SimpleLifecycleObserver() {
            override fun onResume() {
                initStatusKeyboard()
            }

            override fun onPause() {
                keyboardDisposable?.dispose()
            }
        })
    }

    private fun initStatusKeyboard() {
        keyboardDisposable = TedRxKeyboardObserver(v.requireActivity())
                .listen()
                .subscribe({ isShow -> checkMarginView(isShow) }, {})
    }

    private fun checkMarginView(statusKeyboard: Boolean) {
        if (statusKeyboard) {
            if (isMargin) return
            isMargin = true
            post(100) {
                v.webViewOtp_scroll.fling(0)
                v.webViewOtp_scroll.scrollTo(0, v.otpWebView.bottom)
            }
        } else {
            if (!isMargin) return
            isMargin = false
            v.webViewOtp_scroll.post {
                v.webViewOtp_scroll.fling(0)
                v.webViewOtp_scroll.scrollTo(0, v.otpWebView.bottom)
            }
        }
    }

    private fun settingWebView() {
        v.otpImageViewProgress.load(R.drawable.loading)
        v.otpWebView.layoutParams.height = screenHeight + 320
        v.otpWebView.setOnTouchListener { v, event -> event.action === MotionEvent.ACTION_MOVE }
        v.otpWebView.isScrollContainer = false
        v.otpWebView.isVerticalScrollBarEnabled = false
        v.otpWebView.isHorizontalScrollBarEnabled = false

        v.otpWebView.settings.javaScriptEnabled = true
        v.otpWebView.addJavascriptInterface(JavaScriptInterface(), "javascript_obj")
    }

    private inner class JavaScriptInterface {
        @JavascriptInterface
        fun onResultWebView() {
        }
    }

}