package wee.digital.ft.ui.otp

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.webkit.JavascriptInterface
import android.webkit.WebView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import gun0912.tedkeyboardobserver.TedRxKeyboardObserver
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.otp.view.*
import wee.digital.ft.R
import wee.digital.ft.util.SimpleLifecycleObserver
import wee.digital.ft.util.screenHeight
import wee.digital.library.extension.hideKeyboard
import wee.digital.library.extension.load
import wee.digital.library.extension.post

class OtpView : ConstraintLayout {

    constructor(context: Context, attrs: AttributeSet? = null) : super(context, attrs)

    private var keyboardDisposable: Disposable? = null

    private var isMargin = false

    fun onViewInit(fragment: Fragment) {
        otpImageViewProgress.load(R.drawable.loading)
        fragment.viewLifecycleOwner.lifecycle.addObserver(object : SimpleLifecycleObserver() {
            override fun onResume() {
                keyboardDisposable = TedRxKeyboardObserver(fragment.requireActivity())
                        .listen()
                        .subscribe({ isShow -> checkMarginView(isShow) }, {})
            }

            override fun onPause() {
                keyboardDisposable?.dispose()
            }
        })
        webViewOtp_scroll.setOnScrollChangeListener { _, _, _, _, _ ->
            fragment.hideKeyboard()
        }
    }

    private fun checkMarginView(statusKeyboard: Boolean) {
        if (statusKeyboard) {
            if (isMargin) return
            isMargin = true
            post(100) {
                webViewOtp_scroll.fling(0)
                webViewOtp_scroll.scrollTo(0, otpWebView.bottom)
            }
        } else {
            if (!isMargin) return
            isMargin = false
            webViewOtp_scroll.post {
                webViewOtp_scroll.fling(0)
                webViewOtp_scroll.scrollTo(0, otpWebView.bottom)
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    fun settingWebView(webView: WebView?) {
        webView?:return
        webView.layoutParams.height = screenHeight + 320
        webView.setOnTouchListener { v, event -> event.action === MotionEvent.ACTION_MOVE }
        webView.isScrollContainer = false
        webView.isVerticalScrollBarEnabled = false
        webView.isHorizontalScrollBarEnabled = false
    }

    private inner class JavaScriptInterface {
        @JavascriptInterface
        fun onResultWebView() {
        }
    }

}