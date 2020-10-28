package wee.digital.log.dialog

import android.view.View
import androidx.fragment.app.FragmentActivity
import kotlinx.android.synthetic.main.dialog_crash_item.view.*
import wee.digital.log.R
import wee.digital.log.data.CrashItem
import wee.digital.log.data.LogDb
import java.lang.reflect.InvocationTargetException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class CrashDialog : BaseLogDialog<CrashItem> {

    private val DATE_TIME_FORMAT = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")

    constructor(activity: FragmentActivity?) : super(activity)

    override fun View.onViewCreated() {
        adapter.list = LogDb.instance.crashDao.all()
    }

    override fun onClearClick() {
        LogDb.instance.crashDao.clear()
        adapter.list = null
    }

    override fun adapter(): Adapter<CrashItem> {
        return object : Adapter<CrashItem>() {

            override fun itemLayoutRes(): Int {
                return R.layout.dialog_crash_item
            }

            override fun View.onBindItem(model: CrashItem) {
                textViewTitle.text = "${model.title} - ${model.time.toText()}"
                textViewTrace.text = model.trace
                textViewCause.text = model.cause
            }

            private fun Long.toText(): String {
                return try {
                    DATE_TIME_FORMAT.format(Date(this))
                } catch (e: ParseException) {
                    "..."
                } catch (e: InvocationTargetException) {
                    "..."
                }
            }


        }
    }

}