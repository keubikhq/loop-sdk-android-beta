package com.lib.loopsdk.core.customViews

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.widget.ImageViewCompat
import com.lib.loopsdk.R
import com.lib.loopsdk.core.util.ThemeColorUtils
import com.lib.loopsdk.core.util.Utils
import com.lib.loopsdk.core.util.applyGradient

class CustomImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : AppCompatImageView(context, attrs, defStyleAttr) {

    var activeColorType = 1
    var inActiveColorType = 1
    var alphaValue = "P_100"

    init {
        context.theme.obtainStyledAttributes(attrs, R.styleable.CustomViews, 0, 0).apply {
            try {
                activeColorType = getInteger(R.styleable.CustomViews_colorTypeActive, 35)
                inActiveColorType = getInteger(R.styleable.CustomViews_colorTypeInActive, 35)
                alphaValue = getString(R.styleable.CustomViews_alphaValue) ?: "P_100"
            } finally {
                recycle()
            }
        }
        setColor()
    }

    fun setColor(isActive: Boolean = true) {
        val colorType = if (isActive) activeColorType else inActiveColorType
        setColorWithColorType(ThemeColorUtils.ColorTypes.values()[colorType - 1])
    }

    fun setColorWithColorType(colorType: ThemeColorUtils.ColorTypes) {
        val colorList = colorType.defaultValues
        ImageViewCompat.setImageTintList(this, null)
        if (colorList.size == 1) {
            val color = Utils.getColorInt(ThemeColorUtils.getColorStringWithAlphaValue(colorList.first().trim(), alphaValue))
            ImageViewCompat.setImageTintList(this, ColorStateList.valueOf(color))
        }
        else if (colorList.size > 1)
            setGradientColor(colorType)
    }

    fun removeImageViewTint() {
        ImageViewCompat.setImageTintList(this, null)
    }

    fun setGradientColor(colorType: ThemeColorUtils.ColorTypes, drawable: Drawable? = this.drawable){
        ImageViewCompat.setImageTintList(this, null)
        setImageDrawable(BitmapDrawable(resources, drawable.applyGradient(colorType)))
    }

    fun setBackgroundColor(colorType: ThemeColorUtils.ColorTypes) {
        val color = colorType.defaultValues.first()
        setBackgroundColor(Utils.getColorInt(color))
    }

}