package wee.digital.fpa.ui

import android.os.Bundle
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import kotlinx.android.synthetic.main.activity_connect.*
import wee.digital.fpa.R
import wee.digital.fpa.ui.base.BaseActivity
import wee.digital.fpa.ui.screen.QRFragment

class ConnectActivity : BaseActivity() {

    override fun layoutResource(): Int = R.layout.activity_connect

    override fun onViewCreated() {
        viewClick()
    }

    override fun onLiveDataObserve() {
    }

    private fun viewClick() {
        actConnectAction.setOnClickListener {
            findNavController(R.id.actConnectHost).navigate(R.id.QRFragment, null, navAnim())
        }
    }

}