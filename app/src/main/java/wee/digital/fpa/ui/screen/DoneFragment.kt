package wee.digital.fpa.ui.screen

import androidx.core.text.HtmlCompat
import kotlinx.android.synthetic.main.fragment_done.*
import wee.digital.fpa.R
import wee.digital.fpa.ui.base.BaseFragment

class DoneFragment : BaseFragment() {

    override fun layoutResource(): Int = R.layout.fragment_done

    override fun onViewCreated() {
        initView()
    }

    private fun initView() {
        val textColorGuideContent = getString(
                R.string.done_content,
                "<font color='#378AE1'>pos.facepay.vn</font>"
        )
        frgDoneContent.text = HtmlCompat.fromHtml(textColorGuideContent, HtmlCompat.FROM_HTML_MODE_LEGACY)
    }

    override fun onLiveDataObserve() {
    }

}