package com.example.iptvplayer.home.common

import android.content.Context
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.AspectRatioFrameLayout

class ResizeVideo(val context : Context) {
    @OptIn(UnstableApi::class)
     fun getResizeModeForMode(mode: String): Int {
        return when (mode) {
            "4:3" ->  {
                Toast.makeText(context, "4:3", Toast.LENGTH_SHORT).show()
                AspectRatioFrameLayout.RESIZE_MODE_FIT
            }
            "16:9" ->{
                Toast.makeText(context, "16:9", Toast.LENGTH_SHORT).show()
                AspectRatioFrameLayout.RESIZE_MODE_FILL
            }
            "Fit Screen" -> {
                Toast.makeText(context, "fit", Toast.LENGTH_SHORT).show()
                AspectRatioFrameLayout.RESIZE_MODE_FIT
            }
            "Original" ->  {
                Toast.makeText(context, "original", Toast.LENGTH_SHORT).show()
                AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH
            }
            "Streak" -> {
                Toast.makeText(context, "streak", Toast.LENGTH_SHORT).show()
                AspectRatioFrameLayout.RESIZE_MODE_FIT
            }
            else -> AspectRatioFrameLayout.RESIZE_MODE_FIT // Default case
        }
    }

}