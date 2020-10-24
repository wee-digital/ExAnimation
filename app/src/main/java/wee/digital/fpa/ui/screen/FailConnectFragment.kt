package wee.digital.fpa.ui.screen

import androidx.core.text.HtmlCompat
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_fail_connect.*
import wee.digital.fpa.R
import wee.digital.fpa.data.repository.Shared
import wee.digital.fpa.repository.utils.FailType
import wee.digital.fpa.ui.base.BaseFragment

class FailConnectFragment : BaseFragment() {

    override fun layoutResource(): Int = R.layout.fragment_fail_connect

    override fun onViewCreated() {
        initUI()
    }

    private fun initUI() {
        frgFailConnectAction.setOnClickListener { findNavController().popBackStack() }
        when (Shared.statusFailConnect.value) {
            FailType.QR_FAIL -> {
                frgFailConnectContent.text = "Có lỗi phát sinh, bạn vui lòng\nthử lại lần nữa"

            }
            FailType.CONNECT_FAIL -> {
                val textFail = activity?.getString(
                        R.string.fail_content,
                        "<b><font face='svn_gilroy_bold' color='#212121'>Hotline: 1900 2323</font></b>"
                )
                frgFailConnectContent.text = HtmlCompat.fromHtml(textFail
                        ?: "", HtmlCompat.FROM_HTML_MODE_LEGACY)

            }
        }
    }

    override fun onLiveDataObserve() {

    }

}