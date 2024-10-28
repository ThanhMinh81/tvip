package com.example.iptvplayer.home.common
// ToggleAnimationHelper.kt
import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Context
import android.view.View

class ToggleAnimationHelper(private val context: Context) {

    private val screenWidth: Float
        get() = context.resources.displayMetrics.widthPixels.toFloat()

    fun openView(view: View, duration: Long = 300) {
        view.visibility = View.VISIBLE
        val animator = ObjectAnimator.ofFloat(view, "translationX", screenWidth, 0f)
        animator.duration = duration
        animator.start()
    }

    fun closeView(view: View, duration: Long = 300) {
        val animator = ObjectAnimator.ofFloat(view, "translationX", 0f, screenWidth)
        animator.duration = duration
        animator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(p0: Animator) {}
            override fun onAnimationEnd(p0: Animator) {
                view.visibility = View.INVISIBLE
            }
            override fun onAnimationCancel(p0: Animator) {}
            override fun onAnimationRepeat(p0: Animator) {}
        })
        animator.start()
    }
}
