package com.lib.loopsdk.core.customViews

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.ResourcesCompat
import com.lib.loopsdk.R
import com.lib.loopsdk.core.util.ThemeColorUtils
import com.lib.loopsdk.core.util.Utils
import com.lib.loopsdk.core.util.setGradientColor

open class CustomTVForExpandableTwoLineTV @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0):
    AppCompatTextView(context, attrs, defStyleAttr) {

    var colorTypeActive:Int = 1
    var colorTypeInActive: Int = 1
    var alphaValue = "P_100"
    var isGradient = false
    var isShadow = false
    init {
        context.theme.obtainStyledAttributes(attrs, R.styleable.CustomViews, 0, 0).apply {
            try {
                colorTypeInActive = getInteger(R.styleable.CustomViews_colorTypeInActive, 129)
                colorTypeActive = getInteger(R.styleable.CustomViews_colorTypeActive, 129)
                alphaValue = getString(R.styleable.CustomViews_alphaValue) ?: "P_100"
                isGradient = getBoolean(R.styleable.CustomViews_isGradient, false)
                isShadow = getBoolean(R.styleable.CustomViews_isShadow, false)
            } finally {
                recycle()
            }
        }
        typeface = if (!isInEditMode) {
            ResourcesCompat.getFont(context, R.font.poppins_light)
        } else {
            Typeface.DEFAULT
        }
        textSize = 12f
        setColor()
    }

    fun setColor(isActive: Boolean= true) {
        val colorType = if (isActive) colorTypeActive else colorTypeInActive
        val color = ThemeColorUtils.ColorTypes.values()[colorType-1]
        setColorWithColorType(color, isActive)
    }

    fun setColorWithColorType(colorType: ThemeColorUtils.ColorTypes, isActive: Boolean = true) {
        val colorList = colorType.defaultValues
        if (isGradient) {
            setGradientColor()
            if(isShadow) setShadowLayer(10f, 8f, 0f, resources.getColor(R.color.white))
        }else {
            var color = colorList.first().trim()
            color = ThemeColorUtils.getColorStringWithAlphaValue(color, alphaValue)
            paint.shader = null
            setTextColor(Utils.getColorInt(color))
        }
    }
}