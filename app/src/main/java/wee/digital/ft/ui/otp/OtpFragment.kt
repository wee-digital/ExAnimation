package wee.digital.ft.ui.otp

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.UrlQuerySanitizer
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlinx.android.synthetic.main.otp.*
import wee.digital.ft.R
import wee.digital.ft.shared.Config
import wee.digital.ft.shared.Event
import wee.digital.ft.shared.Timeout
import wee.digital.ft.ui.Main
import wee.digital.ft.ui.MainDialog
import wee.digital.ft.ui.base.viewModel
import wee.digital.ft.ui.card.CardItem
import wee.digital.ft.ui.confirm.ConfirmArg
import wee.digital.ft.ui.message.MessageArg
import wee.digital.ft.ui.progress.ProgressArg
import wee.digital.library.extension.gone
import wee.digital.library.extension.post

class OtpFragment : MainDialog() {

    private val otpVM by lazy { viewModel(OtpVM::class) }

    override fun layoutResource(): Int {
        return R.layout.otp
    }

    override fun onViewCreated() {
        addClickListener(otpViewClose)
        otpView.onViewInit(this)
        if (Config.TESTING) post(2000) {
            onTransactionSuccess()
            //otpVM.onTransactionFailed(Napas.INSUFFICIENT_FUNDS)
        }
    }

    override fun onLiveDataObserve() {

        sharedVM.startTimeout(Timeout.OTP)
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

    override fun onViewClick(v: View?) {
        when (v) {
            otpViewClose -> {
                dismissAllowingStateLoss()
                sharedVM.onPaymentCancel()
            }
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
        dismissAllowingStateLoss()
        val arg = ProgressArg.paid.also {
        }
        sharedVM.progress.postValue(arg)
    }

    private fun onRetryMessage(it: MessageArg) {
        dismissAllowingStateLoss()
        sharedVM.startTimeout(Timeout.OTP)
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
                }
            }, 2000)
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            try {
                val listUrl = url?.split("=") ?: return
                if (listUrl.size < 2) return
                when (listUrl[0]) {
                    "${Napas.STATIC_URL}/payment-fail?reason" -> {
                        val reason = UrlQuerySanitizer(url).getValue("reason") ?: ""
                        sharedVM.stopTimeout()
                        otpVM.onTransactionFailed(reason)
                    }
                    "${Napas.STATIC_URL}/payment-success?facepayRef" -> {
                        sharedVM.stopTimeout()
                        onTransactionSuccess()
                    }
                }
            } catch (e: Exception) {
            }
        }
    }


}