package wee.digital.ft.ui.otp

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.UrlQuerySanitizer
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlinx.android.synthetic.main.otp.*
import wee.digital.ft.R
import wee.digital.ft.shared.Timeout
import wee.digital.ft.ui.Main
import wee.digital.ft.ui.MainDialog
import wee.digital.ft.ui.base.viewModel
import wee.digital.ft.ui.card.CardItem
import wee.digital.ft.ui.confirm.ConfirmArg
import wee.digital.ft.ui.message.MessageArg
import wee.digital.ft.ui.progress.ProgressArg
import wee.digital.library.extension.gone

class OtpFragment : MainDialog() {

    private inner class JavaScriptInterface {
        @JavascriptInterface
        fun onResultWebView() {
        }
    }

    private val otpVM by lazy { viewModel(OtpVM::class) }

    override fun layoutResource(): Int {
        return R.layout.otp
    }

    override fun onViewCreated() {
        addClickListener(otpViewClose)
        otpView.onViewInit(this)
        otpView.settingWebView(otpWebView)

    }

    override fun onLiveDataObserve() {
        sharedVM.otpForm.observe {
            if (it.isNullOrEmpty()) {
                dismissAllowingStateLoss()
            } else {
                loadOtpWebView(it)
            }
        }
        otpVM.errorMessageLiveData.observe {
            onErrorMessage(it)
        }
        otpVM.cardList.observe {
            onCardListChanged(it)
        }
    }

    override fun onViewClick(v: View?) {
        when (v) {
            otpViewClose -> {
                clearWebView()
                dismissAllowingStateLoss()
                sharedVM.onPaymentCancel()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        clearWebView()
    }

    /**
     * load webView Otp
     */
    @SuppressLint("SetJavaScriptEnabled")
    fun loadOtpWebView(otpFormUrl: String) {
        otpWebView?.post {
            otpWebView.settings.javaScriptEnabled = true
            otpWebView.addJavascriptInterface(JavaScriptInterface(), "javascript_obj")
            otpWebView.webViewClient = OtpWebViewClient()
            otpWebView.loadDataWithBaseURL(
                    "https://dps-staging.napas.com.vn/api/restjs/resources/js/napas.hostedform.min.js",
                    """$otpFormUrl""",
                    "text/html",
                    "UTF-8",
                    null
            )
        }
    }

    private fun clearWebView() {
        otpWebView.apply {
            clearCache(false)
            clearFormData()
            clearHistory()
            clearAnimation()
            destroy()
        }
    }

    private fun onTransactionSuccess() {
        dismissAllowingStateLoss()
        val arg = ProgressArg.paid.also {
        }
        sharedVM.progress.postValue(arg)
    }

    private fun onRetryMessage(it: MessageArg) {
        dismissAllowingStateLoss()
        sharedVM.startTimeout(Timeout.OTP, MessageArg.timedOutError)
        sharedVM.confirm.value = ConfirmArg().apply {
            title = it.title
            message = it.message
            buttonDeny = "Hủy bỏ giao dịch"
            onDeny = { it.sharedVM.onPaymentCancel() }
            buttonAccept = "Thử lại"
            onAccept = {
                sharedVM.stopTimeout()
                otpVM.fetchCardList(sharedVM.pin.value?.userId)
            }
        }
    }

    private fun onErrorMessage(it: MessageArg) {
        dismissAllowingStateLoss()
        sharedVM.startTimeout(it)
    }

    private fun onCardListChanged(it: List<CardItem>?) {
        it ?: return
        sharedVM.progress.postValue(null)
        sharedVM.cardList.value = it
        dismissAllowingStateLoss()
        navigate(Main.card)
    }

    private inner class OtpWebViewClient : WebViewClient() {

        override fun onPageCommitVisible(view: WebView?, url: String?) {
            super.onPageCommitVisible(view, url)
            this@OtpFragment.view?.postDelayed({
                if (otpImageViewProgress?.isShown == true) {
                    otpImageViewProgress?.gone()
                    sharedVM.startTimeout(Timeout.OTP, MessageArg.timedOutError)
                }
            }, 2000)
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            onWebPageFinish(url)
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            onWebPageFinish(url)
        }
    }

    private fun onWebPageFinish(url: String?) {
        try {
            val listUrl = url?.split("=") ?: return
            for (i in 0..listUrl.lastIndex) {
                log.d("Napas url list [$i]- ${listUrl[i]}")
            }
            if (listUrl.size < 2) {
                return
            }
            val url = listUrl.first()
            sharedVM.stopTimeout()
            when (url) {
                "${Napas.STATIC_URL}/payment-fail?reason" -> {
                    val reason = UrlQuerySanitizer(url).getValue("reason") ?: ""
                    log.d("Napas reason - $reason")
                    otpVM.onTransactionFailed(reason)
                }
                "${Napas.STATIC_URL}/payment-success?facepayRef" -> {
                    onTransactionSuccess()
                }
                else -> {
                    otpVM.onTransactionFailed()
                }
            }
        } catch (e: Exception) {
            log.e(e.message)
        }
    }


}