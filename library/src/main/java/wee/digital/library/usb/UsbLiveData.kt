package wee.digital.library.usb

import android.hardware.usb.UsbDevice
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

val usbLiveData by lazy { UsbLiveData() }

class UsbLiveData : MutableLiveData<UsbDevice>() {

    private var vendorIds: IntArray = intArrayOf()

    fun devices(vararg vendorIds: Int): UsbLiveData {
        this.vendorIds = vendorIds
        return this
    }

    fun observe(activity: AppCompatActivity, block: (UsbDevice) -> Unit) {
        for (vendorId in vendorIds) {
            val usb = Usb.getDevice(vendorId) ?: continue
            block(usb)
        }
        observe(activity, Observer {
            if (null != it) {
                block(it)
            }
        })
        Usb.observer(activity, *vendorIds)
    }

}