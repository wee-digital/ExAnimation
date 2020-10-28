package wee.digital.log.dialog

import android.view.View
import androidx.fragment.app.FragmentActivity
import kotlinx.android.synthetic.main.dialog_api_item.view.*
import kotlinx.android.synthetic.main.dialog_enroll_item.view.*
import wee.digital.log.R
import wee.digital.log.data.EnrollItem
import wee.digital.log.data.LogDb

class EnrollDialog : BaseLogDialog<EnrollItem> {

    constructor(activity: FragmentActivity?) : super(activity)

    override fun View.onViewCreated() {
        adapter.list = LogDb.instance.enrollDao.all()
    }

    override fun onClearClick() {
        LogDb.instance.enrollDao.clear()
    }

    override fun adapter(): Adapter<EnrollItem> {
        return object : Adapter<EnrollItem>() {

            override fun itemLayoutRes(): Int {
                return R.layout.dialog_enroll_item
            }

            override fun View.onBindItem(model: EnrollItem) {
                textViewName.text = model.name
                textViewSize.text = model.requestContentLength.toString()
                textViewDetect.text = (model.requestedTime - model.detectedTime).toString()
                textViewRequest.text = (model.responseTime - model.requestedTime).toString()
            }

        }
    }

}