package wee.digital.fpa.ui.otp

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.UrlQuerySanitizer
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlinx.android.synthetic.main.otp.*
import wee.digital.fpa.R
import wee.digital.fpa.ui.Event
import wee.digital.fpa.ui.Main
import wee.digital.fpa.ui.message.MessageArg
import wee.digital.library.extension.gone
import wee.digital.library.extension.post

class OtpFragment : Main.Dialog() {

    companion object {
        private const val NAPAS_STATIC_URL = "https://napas-qc.facepay.vn/v1/static"
    }

    private val otpView by lazy { OtpView(this) }

    override fun layoutResource(): Int {
        return R.layout.otp
    }

    override fun onViewCreated() {
        otpView.onViewInit()

    }

    override fun onLiveDataObserve() {
        otpVM.otpForm.observe {
            loadOtpWebView(it)
        }
    }

    /**
     * load webView Otp
     */
    @SuppressLint("SetJavaScriptEnabled")
    private fun loadOtpWebView(otpFormUrl: String) {
        otpWebView.loadDataWithBaseURL(
                "https://dps-staging.napas.com.vn/api/restjs/resources/js/napas.hostedform.min.js",
                """$otpFormUrl""",
                "text/html",
                "UTF-8",
                null
        )
        otpWebView.webViewClient = OtpWebViewClient()
    }

    private fun onTransactionFailed(data: String) {
        when (data) {
            "INSUFFICIENT_FUNDS" -> {
                messageVM.arg.value = MessageArg(
                        title = "Giao dịch thất bại",
                        message = "Không đủ số dư thanh toán",
                        button = null,
                )
            }
            "TRANSACTION_BELOW_LIMIT", "TRANSACTION_OUT_OF_LIMIT_BANK" -> {
                messageVM.arg.value = MessageArg(
                        title = "Giao dịch thất bại",
                        message = "Quá hạn mức giao dịch",
                        button = null,
                )
            }
            else -> { //  data == "CANCEL"
                messageVM.arg.value = MessageArg.paymentCancelMessage
            }
        }
        navigate(Main.message)
    }

    private fun onTransactionSuccess() {
        dismiss()
        navigate(Main.progress)
    }

    private inner class OtpWebViewClient : WebViewClient() {

        override fun onPageCommitVisible(view: WebView?, url: String?) {
            super.onPageCommitVisible(view, url)
            post(3000) {
                if (otpImageViewProgress.isShown) {
                    otpImageViewProgress.gone()
                }
            }
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            try {
                val listUrl = url?.split("=") ?: return
                if (listUrl.size < 2) return
                when (listUrl[0]) {
                    "$NAPAS_STATIC_URL/payment-fail?reason" -> {
                        val reason = UrlQuerySanitizer(url).getValue("reason") ?: ""
                        onTransactionFailed(reason)
                    }
                    "$NAPAS_STATIC_URL/payment-success?facepayRef" -> {
                        onTransactionSuccess()
                    }
                }
            } catch (e: Exception) {
            }
        }
    }


}