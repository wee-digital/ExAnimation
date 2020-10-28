package wee.digital.log.dialog

import android.graphics.Color
import android.os.Build
import android.text.Html
import android.view.View
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.dialog_api_item.view.*
import wee.digital.log.LogBook
import wee.digital.log.R
import wee.digital.log.data.LogItem

class LogDialog : BaseLogDialog<LogItem> {

    private val dataUpdateObserver: Observer<Long> by lazy {
        Observer<Long> {
            adapter.notifyDataSetChanged()
        }
    }

    constructor(activity: FragmentActivity?) : super(activity)

    override fun View.onViewCreated() {
        adapter.list = LogBook.instance
    }

    override fun onClearClick() {
        LogBook.instance.clear()
    }

    override fun adapter(): Adapter<LogItem> {
        return object : Adapter<LogItem>() {

            override fun itemLayoutRes(): Int {
                return R.layout.dialog_api_item
            }

            override fun View.onBindItem(model: LogItem) {
                textViewTitle.text = "${model.method} - ${model.url}"
                onBindRequest(model)
                model.throwable?.also {
                    textViewResponseTitle.setTextColor(Color.parseColor("#D81B60"))
                    textViewResponseTitle.setHyperText("<font color=#FF5252>${(it.message ?: "unknown error")}</font>")
                    return
                }
                onBindResponse(model)
            }

            private fun View.onBindRequest(model: LogItem) {
                textViewRequestTitle.text = "Request - ${model.ago}"
                textViewRequest.text = "..."
                val onClick = View.OnClickListener {
                    textViewRequest.text = if (textViewRequest.text.toString() == "...") model.requestBody else "..."
                }
                textViewRequestTitle.setOnClickListener(onClick)
                textViewRequest.setOnClickListener(onClick)
            }

            private fun View.onBindResponse(model: LogItem) {
                textViewResponseTitle.setTextColor(Color.parseColor("#00897B"))
                textViewResponseTitle.text = when (model.responseBody) {
                    null -> "Response"
                    else -> "Response - ${model.code}  - ${model.interval}"
                }
                val onClick = View.OnClickListener {
                    textViewResponse.text = if (textViewResponse.text.toString() == "...") model.responseBody else "..."
                }
                textViewResponse.text = "..."
                textViewResponseTitle.setOnClickListener(onClick)
                textViewResponse.setOnClickListener(onClick)
            }

            private fun TextView.setHyperText(s: String?) {
                text = when {
                    s.isNullOrEmpty() -> null
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> Html.fromHtml(s, 1)
                    else -> @Suppress("DEPRECATION")
                    Html.fromHtml(s)
                }
            }


        }
    }

    override fun onShow() {
        LogBook.updateLiveData.observeForever(dataUpdateObserver)
    }

    override fun onDismiss() {
        LogBook.updateLiveData.removeObserver(dataUpdateObserver)
    }


}