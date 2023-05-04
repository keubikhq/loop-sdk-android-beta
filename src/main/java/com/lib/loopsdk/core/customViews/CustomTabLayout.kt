package com.lib.loopsdk.core.customViews

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.tabs.TabLayout
import com.lib.loopsdk.R
import com.lib.loopsdk.core.util.ThemeColorUtils
import com.lib.loopsdk.core.util.Utils
import java.lang.reflect.Field

class CustomTabLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0):
    TabLayout(context, attrs, defStyleAttr) {

    var colorTypeActive:Int = 1
    var colorTypeInActive: Int = 1
    var alphaValue = "P_100"
    val WIDTH_INDEX = 0
    val TAB_MIN_WIDTH = "requestedTabMinWidth"
    init {
        context.theme.obtainStyledAttributes(attrs, R.styleable.CustomViews, 0, 0).apply {
            try {
                colorTypeInActive = getInteger(R.styleable.CustomViews_colorTypeInActive, 1)
                colorTypeActive = getInteger(R.styleable.CustomViews_colorTypeActive, 9)
                alphaValue = getString(R.styleable.CustomViews_alphaValue) ?: "P_100"
            } finally {
                recycle()
            }
        }
        setColor()
    }

    fun setColor() {
        val activeColorList = ThemeColorUtils.ColorTypes.values()[colorTypeActive - 1].defaultValues
        val inActiveColorList =
            ThemeColorUtils.ColorTypes.values()[colorTypeInActive - 1].defaultValues
        if (activeColorList.isNotEmpty() && inActiveColorList.isNotEmpty()) {
            val activeColor = activeColorList.first().trim()
            var inActiveColor = inActiveColorList.first().trim()
            if (activeColor.isEmpty()) return
            if (inActiveColor.isEmpty()) inActiveColor = activeColor
            val selectedColor = Utils.getColorInt(activeColor)
            val normalColor = Utils.getColorInt(
                ThemeColorUtils.getColorStringWithAlphaValue(
                    inActiveColor,
                    alphaValue
                )
            )
            setTabTextColors(normalColor, selectedColor)
            setSelectedTabIndicatorColor(selectedColor)
        }
    }

    fun initTabMinWidth(DIVIDER_FACTOR: Int) {
        val wh: IntArray? = Utils.getScreenSize(context)
        val tabMinWidth: Int = (wh?.get(WIDTH_INDEX) ?: 0) / DIVIDER_FACTOR
        val field: Field
        try {
            field = TabLayout::class.java.getDeclaredField(TAB_MIN_WIDTH)
            field.setAccessible(true)
            field.set(this, tabMinWidth)
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
    }
}