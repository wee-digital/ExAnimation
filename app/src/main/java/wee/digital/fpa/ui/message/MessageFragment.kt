package wee.digital.fpa.ui.message

import android.view.View
import wee.digital.fpa.R
import wee.digital.fpa.ui.Main
import kotlin.reflect.KClass

class MessageFragment : Main.Dialog<MessageVM>() {

    private val messageView by lazy { MessageView(this) }

    override fun layoutResource(): Int {
        return R.layout.message
    }

    override fun localViewModel(): KClass<MessageVM> {
        return MessageVM::class
    }

    override fun onViewCreated() {
    }

    override fun onLiveDataObserve() {
        localVM.arg.observe {
            messageView.onBindArg(it)
        }
    }

    override fun onLiveEventChanged(event: Int) {
    }


    override fun onViewClick(v: View?) {
        when (v) {
        }
    }


}