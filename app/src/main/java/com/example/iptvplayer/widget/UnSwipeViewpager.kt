package com.example.iptvplayer.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

class UnSwipeViewpager(context: Context?, attrs: AttributeSet?) : ViewPager(context!!, attrs) {

    private var mIsEnable = true

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (mIsEnable) {
            super.onTouchEvent(event)
        } else false
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        return if (mIsEnable) {
            super.onInterceptTouchEvent(event)
        } else false
    }

    fun setPagingEnabled(enabled: Boolean) {
        mIsEnable = enabled
    }
}