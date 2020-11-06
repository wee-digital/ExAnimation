package wee.digital.library.extension

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.annotation.ColorInt
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestListener
import wee.digital.library.Library

@GlideModule
class MyGlideApp : AppGlideModule()

interface SimpleRequestListener : RequestListener<Drawable> {

    fun onCompleted(resource: Drawable?)

    override fun onLoadFailed(e: GlideException?, model: Any?,
                              target: com.bumptech.glide.request.target.Target<Drawable>?,
                              isFirstResource: Boolean): Boolean {
        onCompleted(null)
        return false
    }

    override fun onResourceReady(resource: Drawable?, model: Any?,
                                 target: com.bumptech.glide.request.target.Target<Drawable>?, dataSource: DataSource?,
                                 isFirstResource: Boolean): Boolean {
        onCompleted(resource)
        return false
    }
}

fun ImageView?.loadImage(url: String, onCompleted: (Drawable?) -> Unit) {
    this ?: return
    GlideApp
            .with(Library.app)
            .load(url)
            .addListener(object : SimpleRequestListener {
                override fun onCompleted(resource: Drawable?) {
                    onCompleted(resource)
                }
            })
            .into(this)
}

fun ImageView?.load(url: String?) {
    this ?: return
    GlideApp.with(context)
            .load(url)
            .into(this)
}

fun ImageView?.load(res: Int) {
    this ?: return
    GlideApp.with(context)
            .load(res)
            .into(this)
}

fun ImageView?.load(bitmap: Bitmap?) {
    this ?: return
    GlideApp.with(context)
            .load(bitmap)
            .into(this)
}

fun ImageView?.reload(res: Int) {
    this ?: return
    GlideApp.with(context)
            .load(res)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .into(this)
}

fun ImageView?.clear() {
    this ?: return
    val bitmap: Bitmap? = null
    GlideApp.with(context)
            .load(bitmap)
            .into(this)
}

fun ImageView.tint(@ColorInt color: Int) {
    this.post { this.setColorFilter(color) }
}
