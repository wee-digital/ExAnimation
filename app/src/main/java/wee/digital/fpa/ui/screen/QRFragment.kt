package wee.digital.fpa.ui.screen

import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_qr.*
import wee.digital.fpa.R
import wee.digital.fpa.ui.base.BaseFragment


class QRFragment : BaseFragment() {

    override fun layoutResource(): Int = R.layout.fragment_qr

    override fun onViewCreated() {
        viewOnClick()
    }

    private fun viewOnClick() {
        frgQRTitle.actionCancelClick { findNavController().popBackStack() }
    }

    override fun onLiveDataObserve() {

    }

}