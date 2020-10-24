package wee.digital.fpa.ui.device

import android.text.Editable
import android.text.TextWatcher
import androidx.core.text.HtmlCompat
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_input_device.*
import wee.digital.fpa.R
import wee.digital.fpa.app.toast
import wee.digital.fpa.camera.FrameUtil
import wee.digital.fpa.data.repository.Shared
import wee.digital.fpa.ui.base.BaseFragment
import wee.digital.fpa.ui.base.viewModel

class InputDeviceFragment : BaseFragment(), TextWatcher {

    private lateinit var inputVM: InputDeviceVM

    private var mRegister = false

    private var mBeforeData: String? = null

    var fullName = "fullName"

    override fun layoutResource(): Int = R.layout.fragment_input_device

    override fun onViewCreated() {
        inputVM = viewModel(InputDeviceVM::class)
        initUI()
    }

    private fun initUI() {
        frgInputDeviceError.text = ""

        val rules = getString(
                R.string.done_terms, "<b><font color='#1279DA'>Điều khoản sử dụng</font></b>"
        )
        frgInputDeviceTerm?.text = HtmlCompat.fromHtml(rules, HtmlCompat.FROM_HTML_MODE_LEGACY)

        val objectData = FrameUtil.decryptQRCode(Shared.deviceInfo.value?.qrCode ?: "")
        if (objectData != null) fullName = objectData["FullName"].asString
        val textColor = getString(R.string.done_name, "<b><font color='#1279DA'>$fullName</font></b>")
        frgInputDeviceName?.text = HtmlCompat.fromHtml(textColor, HtmlCompat.FROM_HTML_MODE_LEGACY)

        frgInputDeviceTitle?.actionBackClick {
            if (mRegister) return@actionBackClick
            findNavController().popBackStack()
        }
        frgInputDeviceTitle?.actionCancelClick {
            if (mRegister) return@actionCancelClick
            findNavController().navigate(R.id.action_InfoDeviceFragment_to_splashFragment)
        }

        frgInputDeviceAction?.setOnClickListener {
            if (!checkDeviceName()) return@setOnClickListener
            if (mRegister) return@setOnClickListener
            mRegister = true
            registerDevice()
        }
    }

    private fun registerDevice() {
        Shared.deviceInfo.value?.name = frgInputDeviceInput.text.toString()
        inputVM.registerDevice()
    }

    override fun onLiveDataObserve() {
        inputVM.statusRegister.observe {
           if (it) {
                findNavController().navigate(R.id.action_InfoDeviceFragment_to_doneFragment)
            } else {
                findNavController().navigate(R.id.action_InfoDeviceFragment_to_failConnectFragment)
            }
        }
    }

    /**
     * block 2 consecutive spaces
     */
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (!frgInputDeviceError?.text.isNullOrEmpty()) frgInputDeviceError.text = ""
        mBeforeData = frgInputDeviceInput?.text.toString().trimStart().replace("  ", " ")

        if (s.toString() != mBeforeData) {
            frgInputDeviceInput?.setText(mBeforeData)
            frgInputDeviceInput?.setSelection(mBeforeData!!.length)
        }
    }

    override fun afterTextChanged(s: Editable?) {
        val text = frgInputDeviceInput.text.toString()
        if (text.length < 2 || text.length >= 19) return

        val spaceOne = text[text.length - 1].toString()
        val spaceTwo = text[text.length - 2].toString()
        if (spaceOne == " " && spaceTwo == " ") {
            frgInputDeviceInput?.removeTextChangedListener(this)
            frgInputDeviceInput?.setText(text.trimStart().replace("  ", " "))
            frgInputDeviceInput?.setSelection(frgInputDeviceInput.text.toString().length)
            frgInputDeviceInput?.addTextChangedListener(this)
        }
    }

    /**
     * check name device
     */
    private fun checkDeviceName(): Boolean {
        frgInputDeviceError?.text = ""
        //trim space last input
        val deviceName = frgInputDeviceInput.text.toString().trimEnd()
        frgInputDeviceInput?.setText(deviceName)
        frgInputDeviceInput?.setSelection(frgInputDeviceInput.text.toString().length)

        if (deviceName.isEmpty() || frgInputDeviceInput?.text.toString().replace(" ", "").length < 5) {
            frgInputDeviceError?.text = "Tên thiết bị phải từ 5 đến 20 ký tự"
            return false
        }
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModelStore.clear()
    }

}