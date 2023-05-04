package com.lib.loopsdk.core.customViews

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.lib.loopsdk.R
import com.lib.loopsdk.core.util.ThemeColorUtils
import com.lib.loopsdk.core.util.Utils

open class CustomCardView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : CardView(context, attrs, defStyleAttr) {

    var activeColorType = 1
    var inActiveColorType = 1
    var fillColorType = 1
    var isOutline = false
    var currentRadius = 0f
    var alphaValue = "P_100"
    var strokeWidth = 1f
    var dashGap = 0f
    var dashWidth = 0f
    var cust_elevation = 0f
    var isRadial = false

    init {
        activeColorType = 1
        inActiveColorType = 1
        isOutline = false
        currentRadius = this.radius
        context.theme.obtainStyledAttributes(attrs, R.styleable.CustomViews, 0, 0).apply {
            try {
                activeColorType = getInteger(R.styleable.CustomViews_colorTypeActive, 1)
                inActiveColorType = getInteger(R.styleable.CustomViews_colorTypeInActive, 1)
                isOutline = getBoolean(R.styleable.CustomViews_isOutline, false)
                alphaValue = getString(R.styleable.CustomViews_alphaValue) ?: "P_100"
                fillColorType = getInteger(R.styleable.CustomViews_fillColorType, -1)
                strokeWidth = getFloat(R.styleable.CustomViews_stroke, 1f)
                dashGap = getFloat(R.styleable.CustomViews_cust_dashGap, 0f)
                dashWidth = getFloat(R.styleable.CustomViews_cust_dashWidth, 0f)
                cust_elevation = getFloat(R.styleable.CustomViews_cust_elevation, 0f)
                isRadial = getBoolean(R.styleable.CustomViews_isRadial, false)
            } finally {
                recycle()
            }
        }
        setColor()
        this.cardElevation = Utils.dpToPx(context, cust_elevation).toFloat()
    }

    fun setColor(isActive: Boolean = true) {
        val colorType = if (isActive) activeColorType else inActiveColorType
        val themeData = ThemeColorUtils.ColorTypes.values()[colorType-1]
        val colorList = ThemeColorUtils.getColorListWithAlpha(themeData.defaultValues, alphaValue)
        if (colorList.isEmpty()) return
        if (colorList.size == 1) {
            val color = colorList.first()
            if (color.isEmpty()) return
            if (isOutline && fillColorType != -1) {
                this.background = ContextCompat.getDrawable(context, R.drawable.round_edge_transparent_button)?.mutate()
                val fillColorData = ThemeColorUtils.ColorTypes.values()[fillColorType-1]
                val fillColorList = ThemeColorUtils.getColorListWithAlpha(fillColorData.defaultValues, alphaValue)
                this.background = ThemeColorUtils.getGradientWithStrokeAndFill(context, colorList, this.background, strokeWidth, fillColorList, fillColorData.angle, dashWidth, dashGap)
            } else if (isOutline) {
                this.background = ContextCompat.getDrawable(context, R.drawable.round_edge_rect_outline_rad_8)?.mutate()
                this.background = ThemeColorUtils.getGradientDrawableWithStroke(context, colorList, this.background, strokeWidth, dashWidth, dashGap)
            }
            else {
                this.background = ContextCompat.getDrawable(context, R.drawable.round_edge_rect_filled_rad_8)?.mutate()
                this.background.setTint(Utils.getColorInt(color))
            }
        }
        else {
            if (isOutline && fillColorType != -1) {
                this.background = ContextCompat.getDrawable(context, R.drawable.round_edge_transparent_button)?.mutate()
                val fillColorData = ThemeColorUtils.ColorTypes.values()[fillColorType-1]
                val fillColorList = ThemeColorUtils.getColorListWithAlpha(fillColorData.defaultValues, alphaValue)
                this.background = ThemeColorUtils.getGradientWithStrokeAndFill(context, colorList, this.background, strokeWidth, fillColorList, fillColorData.angle, dashWidth, dashGap)
            } else if (isOutline) {
                this.background = ContextCompat.getDrawable(context, R.drawable.round_edge_rect_outline_rad_8)?.mutate()
                this.background = ThemeColorUtils.getGradientDrawableWithStroke(context, colorList, this.background, strokeWidth, dashWidth, dashGap)
            }
            else {
                this.background = ContextCompat.getDrawable(context, R.drawable.round_edge_rect_filled_rad_8)?.mutate()
                this.background = ThemeColorUtils.getGradientDrawable(colorList, themeData.angle, isRadial)
            }
        }
        (this.background as GradientDrawable).cornerRadius = currentRadius
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        setColor(enabled)
    }
}