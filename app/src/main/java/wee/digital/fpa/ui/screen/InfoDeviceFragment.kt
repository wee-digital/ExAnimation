package wee.digital.fpa.ui.screen

import androidx.core.text.HtmlCompat
import kotlinx.android.synthetic.main.fragment_info_device.*
import wee.digital.fpa.R
import wee.digital.fpa.base.view.BaseFragment

class InfoDeviceFragment : BaseFragment() {

    override fun layoutResource(): Int = R.layout.fragment_info_device

    override fun onViewCreated() {
        initUI()
    }

    private fun initUI() {
        val textColor = getString(
                R.string.done_name, "<b><font color='#1279DA'>No Name</font></b>"
        )
        frgInfoDeviceName.text = HtmlCompat.fromHtml(textColor, HtmlCompat.FROM_HTML_MODE_LEGACY)

        val rules = getString(
                R.string.done_terms, "<b><font color='#1279DA'>Điều khoản sử dụng</font></b>"
        )
        frgInfoDeviceTerm.text = HtmlCompat.fromHtml(rules, HtmlCompat.FROM_HTML_MODE_LEGACY)
    }


    override fun onLiveDataObserve() {
    }

}