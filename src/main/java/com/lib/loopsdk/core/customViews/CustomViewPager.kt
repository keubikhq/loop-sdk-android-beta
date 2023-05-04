package com.lib.loopsdk.core.customViews

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

class CustomViewPager(context: Context, attrs: AttributeSet?) : ViewPager(context, attrs) {

    private var pagingEnabled = true

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        if (pagingEnabled)
            return super.onTouchEvent(ev)
        return false
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        if (pagingEnabled)
            return super.onInterceptTouchEvent(ev)
        return false
    }

    public fun setPagingEnabled(enable: Boolean) {
        pagingEnabled = enable
    }
}