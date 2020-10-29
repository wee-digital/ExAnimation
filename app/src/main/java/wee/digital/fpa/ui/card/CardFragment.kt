package wee.digital.fpa.ui.card

import kotlinx.android.synthetic.main.card.*
import wee.digital.fpa.R
import wee.digital.fpa.ui.Main

class CardFragment : Main.Dialog() {

    private val cardVM by lazy { viewModel(CardVM::class) }

    private val adapter = CardAdapter()

    override fun layoutResource(): Int {
        return R.layout.card
    }

    override fun onViewCreated() {
        adapter.bind(paymentRecyclerViewCard, 2)
    }

    override fun onLiveDataObserve() {
        cardVM.fetchCardList()
        cardVM.cardList.observe {
            adapter.set(it)
        }
    }

}