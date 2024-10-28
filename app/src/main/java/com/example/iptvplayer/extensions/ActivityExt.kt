package com.example.iptvplayer.extensions

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.ContextCompat

fun Activity.lightStatusBar() {
    var flags: Int = window.decorView.systemUiVisibility // get current flag
    flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR // add LIGHT_STATUS_BAR to flag
    window.decorView.systemUiVisibility = flags
}

fun Activity.lightNavigationBar() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        var flags: Int = window.decorView.systemUiVisibility // get current flag
        flags = flags or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR // add LIGHT_STATUS_BAR to flag
        window.decorView.systemUiVisibility = flags
    }
}

fun Activity.darkStatusBar() {
    var flags = window.decorView.systemUiVisibility // get current flag
    flags =
        flags xor View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR // use XOR here for remove LIGHT_STATUS_BAR from flags
    window.decorView.systemUiVisibility = flags
}

fun Activity.setStatusNaviColor(statusColor: Int = Color.WHITE, naviColor: Int = Color.WHITE) {
    window.apply {
        statusBarColor = statusColor
        navigationBarColor = naviColor
    }
}

fun Activity.darkNavigationBar() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        var flags = window.decorView.systemUiVisibility // get current flag
        flags =
            flags xor View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR // use XOR here for remove LIGHT_STATUS_BAR from flags
        window.decorView.systemUiVisibility = flags
    }
}

fun Activity.transparentStatusBar(
    isHideNavigation: Boolean = false,
    colorNavigation: Int = Color.WHITE
) {
    window.decorView.systemUiVisibility = when {
        isHideNavigation -> View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        else -> View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
    }
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    window.statusBarColor = Color.TRANSPARENT
    window.navigationBarColor = colorNavigation
}

fun Activity.transparent(hideNavigationBar: Boolean = true) {
    window.decorView.systemUiVisibility = when {
        hideNavigationBar -> View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        else -> View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
    }
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    window.statusBarColor = Color.TRANSPARENT
    window.navigationBarColor = Color.TRANSPARENT
}

fun Context.getColorCompat(colorRes: Int): Int {
    //return black as a default color, in case an invalid color ID was passed in
    return tryOrNull { ContextCompat.getColor(this, colorRes) } ?: Color.BLACK
}

fun <T> tryOrNull(logOnError: Boolean = true, body: () -> T?): T? {
    return try {
        body()
    } catch (e: Exception) {
        if (logOnError) {
//            Timber.w(e)
        }

        null
    }
}

fun Context.makeToast(toast: String) {
    Toast.makeText(this, toast, Toast.LENGTH_SHORT).show()
}