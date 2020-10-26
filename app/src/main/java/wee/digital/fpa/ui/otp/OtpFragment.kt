package wee.digital.fpa.ui.otp

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.UrlQuerySanitizer
import android.view.MotionEvent
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_otp.*
import wee.digital.fpa.MainDirections
import wee.digital.fpa.R
import wee.digital.fpa.app.toast
import wee.digital.fpa.ui.base.BaseFragment
import wee.digital.fpa.ui.base.messageArg
import wee.digital.fpa.ui.message.MessageArg
import wee.digital.fpa.util.Utils
import wee.digital.library.extension.gone
import wee.digital.library.extension.load
import wee.digital.library.extension.post

class OtpFragment : BaseFragment() {

    private var keyboardDisposable: Disposable? = null

    private var isMargin = false

    override fun layoutResource(): Int = R.layout.fragment_otp

    override fun onViewCreated() {
        settingWebView()
        //loadWebViewOtp()   call to load WebView
    }

    override fun onLiveDataObserve() {}

    private fun initStatusKeyboard() {
       /* keyboardDisposable = TedRxKeyboardObserver(activity())
                .listen()
                .subscribe({ isShow -> checkMarginView(isShow) }, {})*/
    }

    /**
     * setting webView & init handler keyboard status
     */
    private fun settingWebView() {
        frgOtpGif.load(R.drawable.loading)
        webViewOtp_web.layoutParams.height = Utils.getScreenHeight() + 320
        webViewOtp_web.setOnTouchListener { v, event -> event.action === MotionEvent.ACTION_MOVE }
        webViewOtp_web.isScrollContainer = false
        webViewOtp_web.isVerticalScrollBarEnabled = false
        webViewOtp_web.isHorizontalScrollBarEnabled = false
    }

    private fun checkMarginView(statusKeyboard: Boolean) {
        if (statusKeyboard) {
            if (isMargin) return
            isMargin = true
            post(100) {
                webViewOtp_scroll.fling(0)
                webViewOtp_scroll.scrollTo(0, webViewOtp_web.bottom)
            }
        } else {
            if (!isMargin) return
            isMargin = false
            webViewOtp_scroll.post {
                webViewOtp_scroll.fling(0)
                webViewOtp_scroll.scrollTo(0, webViewOtp_web.bottom)
            }
        }
    }

    /**
     * load webView Otp
     */
    @SuppressLint("SetJavaScriptEnabled")
    private fun loadWebViewOtp(html: String) {
        webViewOtp_web.loadDataWithBaseURL(
                "https://dps-staging.napas.com.vn/api/restjs/resources/js/napas.hostedform.min.js",
                """$html""",
                "text/html",
                "UTF-8",
                null
        )

        webViewOtp_web.settings.javaScriptEnabled = true
        webViewOtp_web.addJavascriptInterface(JavaScriptInterface(), "javascript_obj")

        webViewOtp_web.webViewClient = object : WebViewClient() {

            override fun onPageCommitVisible(view: WebView?, url: String?) {
                super.onPageCommitVisible(view, url)
                post(3000) { if (frgOtpGif.isShown) frgOtpGif.gone() }
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                try {
                    val listUrl = url?.split("=") ?: return
                    if (listUrl.size < 2) return
                    when (listUrl[0]) {
                        "https://napas-qc.facepay.vn/v1/static/payment-fail?reason" -> {
                            val reason = UrlQuerySanitizer(url).getValue("reason") ?: ""
                            handlerTransactionFail(reason)
                        }
                        "https://napas-qc.facepay.vn/v1/static/payment-success?facepayRef" -> toast("payment transaction success")
                    }
                } catch (e: Exception) {
                }
            }
        }
    }

    private fun handlerTransactionFail(data: String) {
        when (data) {
            "INSUFFICIENT_FUNDS" -> "khong du so du thanh toan"
            "TRANSACTION_BELOW_LIMIT", "TRANSACTION_OUT_OF_LIMIT_BANK" -> toast("qua han muc giao dich")
            "CANCEL" -> {

                messageArg = MessageArg(
                        icon = R.mipmap.img_x_mark_flat,
                        title = "Sample title",
                        message = "huy bo giao dich"
                )
                navigate(MainDirections.actionGlobalMessageFragment())
            }
            else -> toast("null")
        }
    }

    private inner class JavaScriptInterface {
        @JavascriptInterface
        fun onResultWebView() {
        }
    }

    override fun onResume() {
        super.onResume()
        initStatusKeyboard()
    }

    override fun onPause() {
        super.onPause()
        keyboardDisposable?.dispose()

    }

}