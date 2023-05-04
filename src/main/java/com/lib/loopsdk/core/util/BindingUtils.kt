package com.lib.loopsdk.core.util

import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.PorterDuff
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.BindingAdapter
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieOnCompositionLoadedListener
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.SimpleColorFilter
import com.airbnb.lottie.model.KeyPath
import com.airbnb.lottie.value.LottieValueCallback
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.tabs.TabLayout
import com.jaygoo.widget.RangeSeekBar
import com.lib.loopsdk.R
import com.sinaseyfi.advancedcardview.AdvancedCardView
import com.zhpan.indicator.IndicatorView


@BindingAdapter("bgColor")
fun setViewBackgroundColor(view: View, color: String?){
    if (color.isNullOrEmpty()) return
    view.setBackgroundColor(Color.parseColor(color))
}

@BindingAdapter("textViewColor")
fun setTextColor(textView: TextView, color: String?){
    if (color.isNullOrEmpty()) return
    textView.setTextColor(Color.parseColor(color))
}

@BindingAdapter("textColorIfInsideViewBenefit")
fun setTextColorIfInsideViewBenefit(textView: TextView, style: String?){
    if (style.isNullOrEmpty()) return
    when {
        style.contains("fill", true) -> {
            textView.setTextColor(Color.parseColor(Colors.PRIMARY_BRAND_COLOR))
        }
        style.contains("outline", true) -> {
            textView.setTextColor(Color.parseColor(Colors.FONT_COLOR))
        }

    }

}

@BindingAdapter("tintColor")
fun setTintColor(imageView: ImageView, color: String?){
    if (color.isNullOrEmpty()) return
    imageView.setColorFilter(Color.parseColor(color), PorterDuff.Mode.SRC_IN)
}

@BindingAdapter("indiViewCheckedColor")
fun setIndicatorViewCheckedColor(indicatorView: IndicatorView, color: String?){
    if (color.isNullOrEmpty()) return
    indicatorView.setCheckedColor(Color.parseColor(color))
}

@BindingAdapter("indiViewUnCheckedColor")
fun setIndicatorViewUnCheckedColor(indicatorView: IndicatorView, color: String?){
    if (color.isNullOrEmpty()) return
    indicatorView.setNormalColor(Color.parseColor(color))
}

@BindingAdapter("advancedCardStrokeColor")
fun setAdvancedCardStrokeColor(card: AdvancedCardView, color: String?){
    if (color.isNullOrEmpty()) return
    card.stroke_Color = Color.parseColor(color)
}

@BindingAdapter("advancedCardFillColor")
fun setAdvancedCardFillColor(card: AdvancedCardView, color: String?){
    if (color.isNullOrEmpty()) return
    card.background_Color = Color.parseColor(color)
}

@BindingAdapter("cardBgColor")
fun setCardBackgroundColor(card: MaterialCardView, color: String?){
    if(color.isNullOrEmpty()) return
    card.setCardBackgroundColor(Color.parseColor(color))
}

@BindingAdapter(value = ["shape", "style"], requireAll = true)
fun setCardViewShapeAndColor(card: MaterialCardView, shape: String?, style: String?){
    if (shape.isNullOrEmpty() || style.isNullOrEmpty()) return
    when {
        shape.contains("square", true) -> {
            card.radius = Utils.dpToPx(card.context, 4f).toFloat()
        }
        shape.contains("round", true) -> {
            card.radius = Utils.dpToPx(card.context, 24f).toFloat()
        }
    }
    when {
        style.contains("outline", true) -> {
            card.strokeWidth = 2
            card.strokeColor = Color.parseColor(Colors.PRIMARY_BRAND_COLOR)
            card.setCardBackgroundColor(ContextCompat.getColor(card.context, R.color.white))
        }
        style.contains("fill", true) -> {
            card.strokeWidth = 0
            card.setCardBackgroundColor(Color.parseColor(Colors.PRIMARY_BRAND_COLOR))
        }
    }
}

@BindingAdapter("shapeParam")
fun setCardViewShape(card: MaterialCardView, shapeParam: String?){
    if(shapeParam.isNullOrEmpty()) return
    when {
        shapeParam.contains("square", true) -> {
            card.radius = Utils.dpToPx(card.context, 4f).toFloat()
        }
        shapeParam.contains("round", true) -> {
            card.radius = Utils.dpToPx(card.context, 24f).toFloat()
        }
    }
}

@BindingAdapter("containerShape")
fun setCardViewContainerShape(card: MaterialCardView, containerShape: String?){
    if (containerShape.isNullOrEmpty()) return
    when {
        containerShape.contains("square", true) -> {
            card.radius = Utils.dpToPx(card.context, 4f).toFloat()
        }
        containerShape.contains("round", true) -> {
            card.radius = Utils.dpToPx(card.context, 12f).toFloat()
        }
    }
}

@BindingAdapter("advancedCardContainerShape")
fun setAdvancedCardContainerShape(card: AdvancedCardView, advancedCardContainerShape: String?){
    if (advancedCardContainerShape.isNullOrEmpty()) return
    when {
        advancedCardContainerShape.contains("square", true) -> {
            card.cornerRadius_ = Utils.dpToPx(card.context, 4f).toFloat()
        }
        advancedCardContainerShape.contains("round", true) -> {
            card.cornerRadius_ = Utils.dpToPx(card.context, 12f).toFloat()
        }
    }
}

@BindingAdapter("advUpperCorners")
fun setAdvancedCardUpperCorners(card: AdvancedCardView, shape: String?){
    if (shape.isNullOrEmpty()) return
    when {
        shape.contains("square", true) -> {
            card.cornerRadius_TopRight = Utils.dpToPx(card.context, 4f).toFloat()
            card.cornerRadius_TopLeft = Utils.dpToPx(card.context, 4f).toFloat()
        }
        shape.contains("round", true) -> {
            card.cornerRadius_TopRight = Utils.dpToPx(card.context, 12f).toFloat()
            card.cornerRadius_TopLeft = Utils.dpToPx(card.context, 12f).toFloat()
        }
    }
}

@BindingAdapter("advLowerCorners")
fun setAdvancedCardLowerCorners(card: AdvancedCardView, shape: String?){
    if (shape.isNullOrEmpty()) return
    when  {
        shape.contains("square", true) -> {
            card.cornerRadius_BottomRight = Utils.dpToPx(card.context, 4f).toFloat()
            card.cornerRadius_BottomLeft = Utils.dpToPx(card.context, 4f).toFloat()
        }
        shape.contains("round", true) -> {
            card.cornerRadius_BottomRight = Utils.dpToPx(card.context, 12f).toFloat()
            card.cornerRadius_BottomLeft = Utils.dpToPx(card.context, 12f).toFloat()
        }
    }
}

@BindingAdapter("colorIfInsideButton")
fun setTextViewColorIfInsideButton(textView: TextView, style: String?) {
    if (style.isNullOrEmpty()) return
    when {
        style.contains("outline", true) -> {
            textView.setTextColor(Color.parseColor(Colors.PRIMARY_BRAND_COLOR))
        }
        style.contains("fill", true) -> {
            textView.setTextColor(Color.parseColor(Colors.FONT_COLOR))
        }
    }
}

@BindingAdapter(value = ["styleIfInsideButton", "colorTint"], requireAll = false)
fun setCurrencyIcon(imageView: ImageView, style: String?, color: String?) {
    if (style.isNullOrEmpty() && color.isNullOrEmpty()) return
    if(Constants.init.pointsIdentifier.isDefaultCurrencyIcon == 1){
        Glide.with(imageView.context)
            .load(Constants.init.pointsIdentifier.currencyIconPng)
            .into(imageView)
        if (!style.isNullOrEmpty() && color.isNullOrEmpty()) {
            when {
                style.contains("outline", true) -> {
                    imageView.setColorFilter(Color.parseColor(Colors.PRIMARY_BRAND_COLOR), PorterDuff.Mode.SRC_IN)
                }
                style.contains("fill", true) -> {
                    imageView.setColorFilter(Color.parseColor(Colors.FONT_COLOR), PorterDuff.Mode.SRC_IN)
                }
            }
        } else if (!color.isNullOrEmpty() && style.isNullOrEmpty()){
            imageView.setColorFilter(Color.parseColor(color), PorterDuff.Mode.SRC_IN)
        }
    }else{
        Glide.with(imageView.context)
            .load(Constants.init.pointsIdentifier.currencyIcon)
            .into(imageView)
    }
}

@BindingAdapter("tintIfInsideButton")
fun setImageViewTintIfInsideButton(imageView: ImageView, style: String?){
    if (style.isNullOrEmpty()) return
    when {
        style.contains("outline", true) -> {
            imageView.setColorFilter(Color.parseColor(Colors.PRIMARY_BRAND_COLOR), PorterDuff.Mode.SRC_IN)
        }
        style.contains("fill", true) -> {
            imageView.setColorFilter(Color.parseColor(Colors.FONT_COLOR), PorterDuff.Mode.SRC_IN)
        }
    }
}
@BindingAdapter("colorOnlyForViewBenefit")
fun setColorOnlyForViewBenefit(card: MaterialCardView, style: String?) {
    if (style.isNullOrEmpty()) return
    when {
        style.contains("fill", true) -> {
            card.strokeWidth = 0
            card.setCardBackgroundColor(Color.parseColor(Colors.FONT_COLOR))
        }
        style.contains("outline", true) -> {
            card.strokeWidth = 2
            card.strokeColor = Color.parseColor(Colors.FONT_COLOR)
            card.setCardBackgroundColor(Color.parseColor(Colors.PRIMARY_BRAND_COLOR))
        }
    }
}

@BindingAdapter("cardBorderColor")
fun setCardBorderColor(card: MaterialCardView, color: String?){
    if (color.isNullOrEmpty()) return
    card.strokeColor = Color.parseColor(color)
}

@BindingAdapter("imgSrc")
fun setImageResourceFromRemote(imageView: ImageView, resString: Any){
    if(resString is Int) imageView.setImageResource(resString)
    else if (resString is String) {
        Glide.with(imageView.context)
            .load(resString)
            .into(imageView)
    }
}


@BindingAdapter("tabSelectedTextViewColor")
fun setTabSelectedTextViewColor(tabLayout: TabLayout, color: String?){
    if (color.isNullOrEmpty()) return
    tabLayout.setTabTextColors(ContextCompat.getColor(tabLayout.context,R.color.tab_light_gray),Color.parseColor(color))
    tabLayout.setSelectedTabIndicatorColor(Color.parseColor(color))

}

@BindingAdapter("customIndicatorColor")
fun setProgressBarIndicatorColor(linearProgressIndicator: LinearProgressIndicator, color: String?){
    if (color.isNullOrEmpty()) return
    linearProgressIndicator.setIndicatorColor(Color.parseColor(color))
}

@BindingAdapter("customTrackColor")
fun setProgressBarTrackColor(linearProgressIndicator: LinearProgressIndicator, color: String?){
    if (color.isNullOrEmpty()) return
    linearProgressIndicator.trackColor = Color.parseColor(color)
}

@BindingAdapter("customProgressColor")
fun setProgressBarIndicatorColor(circularProgressIndicator: CircularProgressIndicator, color: String?){
    if (color.isNullOrEmpty()) return
    circularProgressIndicator.setIndicatorColor(Color.parseColor(color))
}

@BindingAdapter("fontName")
fun setTextViewFontFamilyAndStyle(textView: TextView, fontName: String?){
    if(fontName.isNullOrEmpty()) return
    when (fontName){
        "Poppins" -> {
            when (textView.tag) {
                "Header900"-> {
                    textView.typeface = ResourcesCompat.getFont(textView.context, R.font.poppins_bold)
                }
                "Header800"-> {
                    textView.typeface = ResourcesCompat.getFont(textView.context, R.font.poppins_semi_bold)
                }
                "Header700"-> {
                    textView.typeface = ResourcesCompat.getFont(textView.context, R.font.poppins_semi_bold)
                }
                "Header600"-> {
                    textView.typeface = ResourcesCompat.getFont(textView.context, R.font.poppins_semi_bold)
                }
                "Header500"-> {
                    textView.typeface = ResourcesCompat.getFont(textView.context, R.font.poppins_medium)
                }
                "Header400"-> {
                    textView.typeface = ResourcesCompat.getFont(textView.context, R.font.poppins_medium)
                }
                "Header300"-> {
                    textView.typeface = ResourcesCompat.getFont(textView.context, R.font.poppins_medium)
                }
                "Header200"-> {
                    textView.typeface = ResourcesCompat.getFont(textView.context, R.font.poppins_medium)
                }
                "Header100"-> {
                    textView.typeface = ResourcesCompat.getFont(textView.context, R.font.poppins_medium)
                }
                "BodyLarge"-> {
                    textView.typeface = ResourcesCompat.getFont(textView.context, R.font.poppins_regular)
                }
                "BodyRegular"-> {
                    textView.typeface = ResourcesCompat.getFont(textView.context, R.font.poppins_regular)
                }
                "BodyDefault"-> {
                    textView.typeface = ResourcesCompat.getFont(textView.context, R.font.poppins_regular)
                }
                "BodySmall"-> {
                    textView.typeface = ResourcesCompat.getFont(textView.context, R.font.poppins_regular)
                }
                "BodySmallSemiBold"-> {
                    textView.typeface = ResourcesCompat.getFont(textView.context, R.font.poppins_semi_bold)
                }
                "ParaDefault"-> {
                    textView.typeface = ResourcesCompat.getFont(textView.context, R.font.poppins_regular)
                }
                "ButtonLarge"-> {
                    textView.typeface = ResourcesCompat.getFont(textView.context, R.font.poppins_medium)
                }
                "ButtonDefault"-> {
                    textView.typeface = ResourcesCompat.getFont(textView.context, R.font.poppins_semi_bold)
                }
                "ButtonLight"-> {
                    textView.typeface = ResourcesCompat.getFont(textView.context, R.font.poppins_regular)
                }
            }
        }
        "Helvetica" -> {
            when (textView.tag) {
                "Header900"-> {
                    textView.typeface = ResourcesCompat.getFont(textView.context, R.font.helvetica_bold)
                }
                "Header800"-> {
                    textView.typeface = ResourcesCompat.getFont(textView.context, R.font.helvetica_bold)
                }
                "Header700"-> {
                    textView.typeface = ResourcesCompat.getFont(textView.context, R.font.helvetica_bold)
                }
                "Header600"-> {
                    textView.typeface = ResourcesCompat.getFont(textView.context, R.font.helvetica_bold)
                }
                "Header500"-> {
                    textView.typeface = ResourcesCompat.getFont(textView.context, R.font.helvetica)
                }
                "Header400"-> {
                    textView.typeface = ResourcesCompat.getFont(textView.context, R.font.helvetica)
                }
                "Header300"-> {
                    textView.typeface = ResourcesCompat.getFont(textView.context, R.font.helvetica)
                }
                "Header200"-> {
                    textView.typeface = ResourcesCompat.getFont(textView.context, R.font.helvetica)
                }
                "Header100"-> {
                    textView.typeface = ResourcesCompat.getFont(textView.context, R.font.helvetica)
                }
                "BodyLarge"-> {
                    textView.typeface = ResourcesCompat.getFont(textView.context, R.font.helvetica_light)
                }
                "BodyRegular"-> {
                    textView.typeface = ResourcesCompat.getFont(textView.context, R.font.helvetica_light)
                }
                "BodyDefault"-> {
                    textView.typeface = ResourcesCompat.getFont(textView.context, R.font.helvetica_light)
                }
                "BodySmall"-> {
                    textView.typeface = ResourcesCompat.getFont(textView.context, R.font.helvetica_light)
                }
                "BodySmallSemiBold"-> {
                    textView.typeface = ResourcesCompat.getFont(textView.context, R.font.helvetica_bold)
                }
                "ParaDefault"-> {
                    textView.typeface = ResourcesCompat.getFont(textView.context, R.font.helvetica_light)
                }
                "ButtonLarge"-> {
                    textView.typeface = ResourcesCompat.getFont(textView.context, R.font.helvetica)
                }
                "ButtonDefault"-> {
                    textView.typeface = ResourcesCompat.getFont(textView.context, R.font.helvetica_bold)
                }
                "ButtonLight"-> {
                    textView.typeface = ResourcesCompat.getFont(textView.context, R.font.helvetica_light)
                }
            }
        }
        "Roboto" -> {
            when (textView.tag) {
                "Header900"-> {
                    textView.typeface = ResourcesCompat.getFont(textView.context, R.font.roboto_bold)
                }
                "Header800"-> {
                    textView.typeface = ResourcesCompat.getFont(textView.context, R.font.roboto_bold)
                }
                "Header700"-> {
                    textView.typeface = ResourcesCompat.getFont(textView.context, R.font.roboto_bold)
                }
                "Header600"-> {
                    textView.typeface = ResourcesCompat.getFont(textView.context, R.font.roboto_bold)
                }
                "Header500"-> {
                    textView.typeface = ResourcesCompat.getFont(textView.context, R.font.roboto_medium)
                }
                "Header400"-> {
                    textView.typeface = ResourcesCompat.getFont(textView.context, R.font.roboto_medium)
                }
                "Header300"-> {
                    textView.typeface = ResourcesCompat.getFont(textView.context, R.font.roboto_medium)
                }
                "Header200"-> {
                    textView.typeface = ResourcesCompat.getFont(textView.context, R.font.roboto_medium)
                }
                "Header100"-> {
                    textView.typeface = ResourcesCompat.getFont(textView.context, R.font.roboto_medium)
                }
                "BodyLarge"-> {
                    textView.typeface = ResourcesCompat.getFont(textView.context, R.font.roboto_regular)
                }
                "BodyRegular"-> {
                    textView.typeface = ResourcesCompat.getFont(textView.context, R.font.roboto_regular)
                }
                "BodyDefault"-> {
                    textView.typeface = ResourcesCompat.getFont(textView.context, R.font.roboto_regular)
                }
                "BodySmall"-> {
                    textView.typeface = ResourcesCompat.getFont(textView.context, R.font.roboto_regular)
                }
                "BodySmallSemiBold"-> {
                    textView.typeface = ResourcesCompat.getFont(textView.context, R.font.roboto_bold)
                }
                "ParaDefault"-> {
                    textView.typeface = ResourcesCompat.getFont(textView.context, R.font.roboto_regular)
                }
                "ButtonLarge"-> {
                    textView.typeface = ResourcesCompat.getFont(textView.context, R.font.roboto_medium)
                }
                "ButtonDefault"-> {
                    textView.typeface = ResourcesCompat.getFont(textView.context, R.font.roboto_bold)
                }
                "ButtonLight"-> {
                    textView.typeface = ResourcesCompat.getFont(textView.context, R.font.roboto_regular)
                }
            }
        }
        "SF Pro Display" -> {
            when (textView.tag) {
                "Header900"-> {
                    textView.typeface = ResourcesCompat.getFont(textView.context, R.font.sf_pro_display_bold)
                }
                "Header800"-> {
                    textView.typeface = ResourcesCompat.getFont(textView.context, R.font.sf_pro_display_semi_bold)
                }
                "Header700"-> {
                    textView.typeface = ResourcesCompat.getFont(textView.context, R.font.sf_pro_display_semi_bold)
                }
                "Header600"-> {
                    textView.typeface = ResourcesCompat.getFont(textView.context, R.font.sf_pro_display_semi_bold)
                }
                "Header500"-> {
                    textView.typeface = ResourcesCompat.getFont(textView.context, R.font.sf_pro_display_medium)
                }
                "Header400"-> {
                    textView.typeface = ResourcesCompat.getFont(textView.context, R.font.sf_pro_display_medium)
                }
                "Header300"-> {
                    textView.typeface = ResourcesCompat.getFont(textView.context, R.font.sf_pro_display_medium)
                }
                "Header200"-> {
                    textView.typeface = ResourcesCompat.getFont(textView.context, R.font.sf_pro_display_medium)
                }
                "Header100"-> {
                    textView.typeface = ResourcesCompat.getFont(textView.context, R.font.sf_pro_display_medium)
                }
                "BodyLarge"-> {
                    textView.typeface = ResourcesCompat.getFont(textView.context, R.font.sf_pro_display_regular)
                }
                "BodyRegular"-> {
                    textView.typeface = ResourcesCompat.getFont(textView.context, R.font.sf_pro_display_regular)
                }
                "BodyDefault"-> {
                    textView.typeface = ResourcesCompat.getFont(textView.context, R.font.sf_pro_display_regular)
                }
                "BodySmall"-> {
                    textView.typeface = ResourcesCompat.getFont(textView.context, R.font.sf_pro_display_regular)
                }
                "BodySmallSemiBold"-> {
                    textView.typeface = ResourcesCompat.getFont(textView.context, R.font.sf_pro_display_semi_bold)
                }
                "ParaDefault"-> {
                    textView.typeface = ResourcesCompat.getFont(textView.context, R.font.sf_pro_display_regular)
                }
                "ButtonLarge"-> {
                    textView.typeface = ResourcesCompat.getFont(textView.context, R.font.sf_pro_display_medium)
                }
                "ButtonDefault"-> {
                    textView.typeface = ResourcesCompat.getFont(textView.context, R.font.sf_pro_display_semi_bold)
                }
                "ButtonLight"-> {
                    textView.typeface = ResourcesCompat.getFont(textView.context, R.font.sf_pro_display_regular)
                }
            }
        }
    }
}

@BindingAdapter("fontName")
fun setEditTextFontFamilyAndStyle(editText: EditText, fontName: String?){
    if(fontName.isNullOrEmpty()) return
    when (fontName){
        "Poppins" -> {
            when (editText.tag) {
                "Header900"-> {
                    editText.typeface = ResourcesCompat.getFont(editText.context, R.font.poppins_bold)
                }
                "Header800"-> {
                    editText.typeface = ResourcesCompat.getFont(editText.context, R.font.poppins_semi_bold)
                }
                "Header700"-> {
                    editText.typeface = ResourcesCompat.getFont(editText.context, R.font.poppins_semi_bold)
                }
                "Header600"-> {
                    editText.typeface = ResourcesCompat.getFont(editText.context, R.font.poppins_semi_bold)
                }
                "Header500"-> {
                    editText.typeface = ResourcesCompat.getFont(editText.context, R.font.poppins_medium)
                }
                "Header400"-> {
                    editText.typeface = ResourcesCompat.getFont(editText.context, R.font.poppins_medium)
                }
                "Header300"-> {
                    editText.typeface = ResourcesCompat.getFont(editText.context, R.font.poppins_medium)
                }
                "Header200"-> {
                    editText.typeface = ResourcesCompat.getFont(editText.context, R.font.poppins_medium)
                }
                "Header100"-> {
                    editText.typeface = ResourcesCompat.getFont(editText.context, R.font.poppins_medium)
                }
                "BodyLarge"-> {
                    editText.typeface = ResourcesCompat.getFont(editText.context, R.font.poppins_regular)
                }
                "BodyRegular"-> {
                    editText.typeface = ResourcesCompat.getFont(editText.context, R.font.poppins_regular)
                }
                "BodyDefault"-> {
                    editText.typeface = ResourcesCompat.getFont(editText.context, R.font.poppins_regular)
                }
                "BodySmall"-> {
                    editText.typeface = ResourcesCompat.getFont(editText.context, R.font.poppins_regular)
                }
                "BodySmallSemiBold"-> {
                    editText.typeface = ResourcesCompat.getFont(editText.context, R.font.poppins_semi_bold)
                }
                "ParaDefault"-> {
                    editText.typeface = ResourcesCompat.getFont(editText.context, R.font.poppins_regular)
                }
                "ButtonLarge"-> {
                    editText.typeface = ResourcesCompat.getFont(editText.context, R.font.poppins_medium)
                }
                "ButtonDefault"-> {
                    editText.typeface = ResourcesCompat.getFont(editText.context, R.font.poppins_semi_bold)
                }
                "ButtonLight"-> {
                    editText.typeface = ResourcesCompat.getFont(editText.context, R.font.poppins_regular)
                }
            }
        }
        "Helvetica" -> {
            when (editText.tag) {
                "Header900"-> {
                    editText.typeface = ResourcesCompat.getFont(editText.context, R.font.helvetica_bold)
                }
                "Header800"-> {
                    editText.typeface = ResourcesCompat.getFont(editText.context, R.font.helvetica_bold)
                }
                "Header700"-> {
                    editText.typeface = ResourcesCompat.getFont(editText.context, R.font.helvetica_bold)
                }
                "Header600"-> {
                    editText.typeface = ResourcesCompat.getFont(editText.context, R.font.helvetica_bold)
                }
                "Header500"-> {
                    editText.typeface = ResourcesCompat.getFont(editText.context, R.font.helvetica)
                }
                "Header400"-> {
                    editText.typeface = ResourcesCompat.getFont(editText.context, R.font.helvetica)
                }
                "Header300"-> {
                    editText.typeface = ResourcesCompat.getFont(editText.context, R.font.helvetica)
                }
                "Header200"-> {
                    editText.typeface = ResourcesCompat.getFont(editText.context, R.font.helvetica)
                }
                "Header100"-> {
                    editText.typeface = ResourcesCompat.getFont(editText.context, R.font.helvetica)
                }
                "BodyLarge"-> {
                    editText.typeface = ResourcesCompat.getFont(editText.context, R.font.helvetica_light)
                }
                "BodyRegular"-> {
                    editText.typeface = ResourcesCompat.getFont(editText.context, R.font.helvetica_light)
                }
                "BodyDefault"-> {
                    editText.typeface = ResourcesCompat.getFont(editText.context, R.font.helvetica_light)
                }
                "BodySmall"-> {
                    editText.typeface = ResourcesCompat.getFont(editText.context, R.font.helvetica_light)
                }
                "BodySmallSemiBold"-> {
                    editText.typeface = ResourcesCompat.getFont(editText.context, R.font.helvetica_bold)
                }
                "ParaDefault"-> {
                    editText.typeface = ResourcesCompat.getFont(editText.context, R.font.helvetica_light)
                }
                "ButtonLarge"-> {
                    editText.typeface = ResourcesCompat.getFont(editText.context, R.font.helvetica)
                }
                "ButtonDefault"-> {
                    editText.typeface = ResourcesCompat.getFont(editText.context, R.font.helvetica_bold)
                }
                "ButtonLight"-> {
                    editText.typeface = ResourcesCompat.getFont(editText.context, R.font.helvetica_light)
                }
            }
        }
        "Roboto" -> {
            when (editText.tag) {
                "Header900"-> {
                    editText.typeface = ResourcesCompat.getFont(editText.context, R.font.roboto_bold)
                }
                "Header800"-> {
                    editText.typeface = ResourcesCompat.getFont(editText.context, R.font.roboto_bold)
                }
                "Header700"-> {
                    editText.typeface = ResourcesCompat.getFont(editText.context, R.font.roboto_bold)
                }
                "Header600"-> {
                    editText.typeface = ResourcesCompat.getFont(editText.context, R.font.roboto_bold)
                }
                "Header500"-> {
                    editText.typeface = ResourcesCompat.getFont(editText.context, R.font.roboto_medium)
                }
                "Header400"-> {
                    editText.typeface = ResourcesCompat.getFont(editText.context, R.font.roboto_medium)
                }
                "Header300"-> {
                    editText.typeface = ResourcesCompat.getFont(editText.context, R.font.roboto_medium)
                }
                "Header200"-> {
                    editText.typeface = ResourcesCompat.getFont(editText.context, R.font.roboto_medium)
                }
                "Header100"-> {
                    editText.typeface = ResourcesCompat.getFont(editText.context, R.font.roboto_medium)
                }
                "BodyLarge"-> {
                    editText.typeface = ResourcesCompat.getFont(editText.context, R.font.roboto_regular)
                }
                "BodyRegular"-> {
                    editText.typeface = ResourcesCompat.getFont(editText.context, R.font.roboto_regular)
                }
                "BodyDefault"-> {
                    editText.typeface = ResourcesCompat.getFont(editText.context, R.font.roboto_regular)
                }
                "BodySmall"-> {
                    editText.typeface = ResourcesCompat.getFont(editText.context, R.font.roboto_regular)
                }
                "BodySmallSemiBold"-> {
                    editText.typeface = ResourcesCompat.getFont(editText.context, R.font.roboto_bold)
                }
                "ParaDefault"-> {
                    editText.typeface = ResourcesCompat.getFont(editText.context, R.font.roboto_regular)
                }
                "ButtonLarge"-> {
                    editText.typeface = ResourcesCompat.getFont(editText.context, R.font.roboto_medium)
                }
                "ButtonDefault"-> {
                    editText.typeface = ResourcesCompat.getFont(editText.context, R.font.roboto_bold)
                }
                "ButtonLight"-> {
                    editText.typeface = ResourcesCompat.getFont(editText.context, R.font.roboto_regular)
                }
            }
        }
        "SF Pro Display" -> {
            when (editText.tag) {
                "Header900"-> {
                    editText.typeface = ResourcesCompat.getFont(editText.context, R.font.sf_pro_display_bold)
                }
                "Header800"-> {
                    editText.typeface = ResourcesCompat.getFont(editText.context, R.font.sf_pro_display_semi_bold)
                }
                "Header700"-> {
                    editText.typeface = ResourcesCompat.getFont(editText.context, R.font.sf_pro_display_semi_bold)
                }
                "Header600"-> {
                    editText.typeface = ResourcesCompat.getFont(editText.context, R.font.sf_pro_display_semi_bold)
                }
                "Header500"-> {
                    editText.typeface = ResourcesCompat.getFont(editText.context, R.font.sf_pro_display_medium)
                }
                "Header400"-> {
                    editText.typeface = ResourcesCompat.getFont(editText.context, R.font.sf_pro_display_medium)
                }
                "Header300"-> {
                    editText.typeface = ResourcesCompat.getFont(editText.context, R.font.sf_pro_display_medium)
                }
                "Header200"-> {
                    editText.typeface = ResourcesCompat.getFont(editText.context, R.font.sf_pro_display_medium)
                }
                "Header100"-> {
                    editText.typeface = ResourcesCompat.getFont(editText.context, R.font.sf_pro_display_medium)
                }
                "BodyLarge"-> {
                    editText.typeface = ResourcesCompat.getFont(editText.context, R.font.sf_pro_display_regular)
                }
                "BodyRegular"-> {
                    editText.typeface = ResourcesCompat.getFont(editText.context, R.font.sf_pro_display_regular)
                }
                "BodyDefault"-> {
                    editText.typeface = ResourcesCompat.getFont(editText.context, R.font.sf_pro_display_regular)
                }
                "BodySmall"-> {
                    editText.typeface = ResourcesCompat.getFont(editText.context, R.font.sf_pro_display_regular)
                }
                "BodySmallSemiBold"-> {
                    editText.typeface = ResourcesCompat.getFont(editText.context, R.font.sf_pro_display_semi_bold)
                }
                "ParaDefault"-> {
                    editText.typeface = ResourcesCompat.getFont(editText.context, R.font.sf_pro_display_regular)
                }
                "ButtonLarge"-> {
                    editText.typeface = ResourcesCompat.getFont(editText.context, R.font.sf_pro_display_medium)
                }
                "ButtonDefault"-> {
                    editText.typeface = ResourcesCompat.getFont(editText.context, R.font.sf_pro_display_semi_bold)
                }
                "ButtonLight"-> {
                    editText.typeface = ResourcesCompat.getFont(editText.context, R.font.sf_pro_display_regular)
                }
            }
        }
    }

}

@BindingAdapter("rangeBarProgressColor")
fun setRangeBarProgressColor(esRangebar: RangeSeekBar, color: String?){
    if(color.isNullOrEmpty()) return
    esRangebar.progressColor=Color.parseColor(color)
}
@BindingAdapter("textHintColor")
fun setTextHintColor(editText: AppCompatEditText, color: String?){
    if(color.isNullOrEmpty()) return
    editText.setHintTextColor(Color.parseColor(color))
//    editText.setTextCursorDrawable(R.drawable.cursor_drawable)
    editText.textCursorDrawable?.setTint(Color.parseColor(color))
}
