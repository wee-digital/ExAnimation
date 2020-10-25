package sample

import wee.digital.fpa.MainDirections
import wee.digital.fpa.R
import wee.digital.fpa.app.toast
import wee.digital.fpa.ui.Main
import wee.digital.fpa.ui.base.BaseFragment
import wee.digital.fpa.ui.confirm.ConfirmArg
import wee.digital.fpa.ui.message.MessageArg

private class NavigateMessageDialog : BaseFragment() {

    override fun layoutResource(): Int = 0

    override fun onViewCreated() {
        view?.setOnClickListener {
            alertMessage()
            alertConfirm()
        }
    }

    override fun onLiveDataObserve() {

    }
}

private fun NavigateMessageDialog.alertMessage() {
    Main.messageArg.value = MessageArg(
            icon = R.mipmap.img_checked_flat,
            title = "Sample title",
            message = "Sample message"
    )
    navigate(MainDirections.actionGlobalMessageFragment())
}

private fun NavigateMessageDialog.alertConfirm() {
    Main.confirmArg.value = ConfirmArg(
            icon = R.mipmap.img_checked_flat,
            title = "Sample title",
            message = "Sample message",
            onAccept = {
                toast("on accept")
            }
    )
    navigate(MainDirections.actionGlobalConfirmFragment())
}