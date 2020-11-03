package wee.digital.ft.ui.otp

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.UrlQuerySanitizer
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlinx.android.synthetic.main.otp.*
import wee.digital.ft.R
import wee.digital.ft.shared.Event
import wee.digital.ft.ui.Main
import wee.digital.ft.ui.MainDialog
import wee.digital.ft.ui.base.viewModel
import wee.digital.ft.ui.card.CardItem
import wee.digital.ft.ui.confirm.ConfirmArg
import wee.digital.ft.ui.message.MessageArg
import wee.digital.ft.ui.onPaymentCancel
import wee.digital.library.extension.gone
import wee.digital.library.extension.post

class OtpFragment : MainDialog() {

    private val otpVM by lazy { viewModel(OtpVM::class) }

    private val otpView by lazy { OtpView(this) }

    override fun layoutResource(): Int {
        return R.layout.otp
    }

    override fun onViewCreated() {
        otpView.onViewInit()
    }

    override fun onLiveDataObserve() {
        sharedVM.otpForm.observe {
            it ?: throw Event.otpFormError
            loadOtpWebView(it)
        }
        otpVM.retryMessageLiveData.observe {
            onRetryMessage(it)
        }
        otpVM.errorMessageLiveData.observe {
            onErrorMessage(it)
        }
        otpVM.cardList.observe {
            onCardListChanged(it)
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

    private fun onTransactionSuccess() {
        dismiss()
        navigate(Main.progress)
    }

    private fun onRetryMessage(it: ConfirmArg) {
        sharedVM.confirm.value = it.also {
            it.buttonDeny = "Hủy bỏ giao dịch"
            it.onDeny = { it.onPaymentCancel() }
            it.buttonAccept = "Thử lại"
            it.onAccept = { otpVM.fetchCardList(sharedVM.pin.value?.userId) }
        }
        dismiss()
        navigate(Main.confirm)
    }

    private fun onErrorMessage(it: MessageArg?) {
        it ?: return
        //messageVM.arg = it
    }

    private fun onCardListChanged(it: List<CardItem>?) {
        it ?: return
        sharedVM.progress.postValue(null)
        sharedVM.cardList.value = it
        dismiss()
        navigate(Main.card)
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
                    "${Napas.STATIC_URL}/payment-fail?reason" -> {
                        val reason = UrlQuerySanitizer(url).getValue("reason") ?: ""
                        otpVM.onTransactionFailed(reason)
                    }
                    "${Napas.STATIC_URL}/payment-success?facepayRef" -> {
                        onTransactionSuccess()
                    }
                }
            } catch (e: Exception) {
            }
        }
    }


}