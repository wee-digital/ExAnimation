package wee.digital.library.extension

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

fun Activity.isGranted(@RequiresPermission vararg permissions: String, onGranted: () -> Unit) {
    val list = mutableListOf<String>()
    permissions.forEach {
        if (ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED) {
            list.add(it)
        }
    }
    if (list.isNullOrEmpty()) {
        onGranted()
        return
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        requestPermissions(permissions, 1)
    } else {
        ActivityCompat.requestPermissions(this, permissions, 1)
    }
}



