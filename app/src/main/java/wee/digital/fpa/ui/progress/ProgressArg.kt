package wee.digital.fpa.ui.progress

import wee.digital.fpa.R

data class ProgressArg(
        val image: Int = R.mipmap.img_progress,
        val title: String,
        val sound: Int = 0,
        val soundDelayed: Long = 0,
        val message: String
)