package com.lib.loopsdk.core.util

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import java.io.Serializable
import kotlin.math.roundToInt

object ThemeColorUtils {
    enum class ColorTypes(val value: Int, var defaultValues: ArrayList<String> = ArrayList(), var angle: Double = 0.0, var positions: FloatArray? = null): Serializable {
        BRAND_COLOR_FULL_OPACITY(1, arrayListOf("#EF6351")),
        BRAND_COLOR_LOW_OPACITY(2, arrayListOf("#1AEF6351")),
        COLOUR_WHITE(3, arrayListOf("#FFFFFF")),
        COLOUR_BG_GREY(4, arrayListOf("#F7F8FA")),
        COLOUR_BLACK(5, arrayListOf("#0C0C0C")),
        COLOUR_DARK_GREY(8, arrayListOf("#555150")),
        COLOUR_MEDIUM_GREY(9, arrayListOf("#716F6F")),
        COLOUR_LIGHT_GREY(10, arrayListOf("#D1D1D4")),
        COLOR_INFO_BLUE(11,arrayListOf("#4C69FF")),
        COLOUR_ERROR_RED(11, arrayListOf("#F04848")),
        COLOUR_SUCCESS_GREEN(12, arrayListOf("#39AC67")),

    }

//    fun updateEnumValues(context: Context) {
//        try {
//            val themeColorData = SharedPref(context).getThemeColorData() ?: return
//            for (prop in ThemeColorData::class.declaredMemberProperties) {
//                val rawData1  = (prop.get(themeColorData) as String?) ?: continue
//                if (!rawData1.contains("background:", true)) {
//                    ColorTypes.valueOf(prop.name).defaultValues = arrayListOf(rawData1)
//                } else {
//                    val rawData2 = rawData1.substring(rawData1.indexOf("(")+1, rawData1.indexOf(")")).trim()
//                    val colorList = ArrayList<String>()
//                    val positionArray = ArrayList<Float>()
//                    val startPositionList = ArrayList<Double>()
//                    val degree = rawData2.substring(0, rawData2.indexOf("deg")).toDouble() ?: 0.0
//                    val rawData3 = rawData2.substring(rawData2.indexOf("deg,")+4).trim().split(",")
//                    for(item in rawData3) {
//                        val data = item.trim().split("")
//                        if (data.size == 2) {
//                            val percentString = data[1].trim()
//                            val percentRaw: Float = percentString.substring(0, percentString.indexOf("%")).toFloat() / 100
//                            positionArray.add(percentRaw)
//                        }
//                        val colorString  = data.first().trim()
//                        val startPosition = data.last().trim().substring(0, data.last().trim().indexOf("%")).toDouble() ?: 0.0
//                        if (colorString.isNotEmpty()) colorList.add(colorString)
//                        startPositionList.add(startPosition)
//                    }
//                    ColorTypes.valueOf(prop.name).defaultValues = colorList
//                    ColorTypes.valueOf(prop.name).angle = degree
//                    ColorTypes.valueOf(prop.name).positions = positionArray.toFloatArray()
//                }
//            }
//        } catch (e: Exception) {
//            Timber.e(e)
//        }
//    }

    /**
     * Add new values if required.
     * Reference - https://gist.github.com/lopspower/03fb1cc0ac9f32ef38f4
     */
    enum class AlphaPercentMapping(val percent: Float, val alphaColor: String) {
        P_100(100f, "#FF"),
        P_90(90f, "#E6"),
        P_80(80f, "#CC"),
        P_75(75f, "#BF"),
        P_70(70f, "#B3"),
        P_60(60f, "#99"),
        P_50(50f, "#80"),
        P_45(45f, "#73"),
        P_40(40f, "#66"),
        P_35(35f, "#59"),
        P_30(30f, "#4D"),
        P_25(25f, "#40"),
        P_20(20f, "#33"),
        P_10(10f, "#1A"),
        P_00(0f, "#00")
    }

    fun getColorStringWithAlphaValue(colorString: String, alphaValue: String) : String {
        return colorString.trim().replace("#", AlphaPercentMapping.valueOf(alphaValue).alphaColor)
    }

    fun getGradientDrawable(colorList: ArrayList<String>, angle: Double = 0.0, isRadial: Boolean = false): GradientDrawable? {
        return try {
            val intColorList = convertColorListToColorIntList(colorList)
            val gradientDrawable = GradientDrawable(getGradientOrientationFromAngle(angle), intColorList)
            if (isRadial) {
                //gradientDrawable.gradientType = GradientDrawable.RECTANGLE
                gradientDrawable.gradientRadius = 1000f
            }
            gradientDrawable
        } catch (e: Exception) {
//            Timber.e(e)
            null
        }
    }

    fun getGradientDrawableWithStroke(context: Context?, colorList: ArrayList<String>, drawable: Drawable, stroke: Number = 1, dashWidth: Float = 0f, dashGap: Float = 0f): GradientDrawable? {
        if (context == null) return null
        return try {
            val intColorList = convertColorListToColorIntList(colorList)
            val gradientDrawable = drawable.mutate() as GradientDrawable
            gradientDrawable.setStroke(Utils.dpToPx(context, stroke), intColorList.first(), dashWidth, dashGap)
            gradientDrawable.setColor(Color.TRANSPARENT)
            gradientDrawable
        } catch (e: Exception) {
//            Timber.e(e)
            null
        }
    }

    fun getGradientWithStrokeAndFill(context: Context?, colorList: ArrayList<String>, drawable: Drawable, stroke: Number = 1, fillColorList: ArrayList<String>, angle: Double, dashWidth: Float = 0f, dashGap: Float = 0f): GradientDrawable? {
        if (context == null) return null
        return try {
            val intColorList = convertColorListToColorIntList(colorList)
            val gradientDrawable = drawable.mutate() as GradientDrawable
            gradientDrawable.gradientType = GradientDrawable.LINEAR_GRADIENT
            if (fillColorList.size >= 2) {
                val intFillColorList = convertColorListToColorIntList(fillColorList)
                gradientDrawable.orientation = getGradientOrientationFromAngle(angle)
                gradientDrawable.colors = intFillColorList
            } else if(fillColorList.size == 1) {
                if(fillColorList.first().trim().isNotEmpty()) {
                    val color = Utils.getColorInt(fillColorList.first().trim())
                    gradientDrawable.color = ColorStateList.valueOf(color)
                }
            }
            gradientDrawable.setStroke(Utils.dpToPx(context, stroke), intColorList.first(),dashWidth, dashGap)
            gradientDrawable
        } catch (e: Exception) {
//            Timber.e(e)
            null
        }
    }

    fun convertColorListToColorIntList(colorList: ArrayList<String>): IntArray {
        val intColorList = IntArray(colorList.size)
        for ((i,color) in colorList.withIndex()) {
            intColorList[i] = Utils.getColorInt(color.trim())
        }
        return intColorList
    }

    /**
     * GradientDrawable requires the orientation to be a multiple of 45.
     * The below method converts the angle and returns the closest GradientDrawable.Orientation
     */
    fun getGradientOrientationFromAngle(angle: Double): GradientDrawable.Orientation {
        try {
            val intAngle = angle.toInt()
            val remainder = intAngle % 45
            val quotient = (angle / 45).roundToInt()
            return when(if (remainder == 0) intAngle else 45 * quotient) {
                45 -> GradientDrawable.Orientation.BL_TR
                90 -> GradientDrawable.Orientation.LEFT_RIGHT
                135 -> GradientDrawable.Orientation.TL_BR
                180 -> GradientDrawable.Orientation.TOP_BOTTOM
                225 -> GradientDrawable.Orientation.TR_BL
                270 -> GradientDrawable.Orientation.RIGHT_LEFT
                315 -> GradientDrawable.Orientation.BR_TL
                else -> GradientDrawable.Orientation.TL_BR
            }
        } catch (e: Exception) {
//            Timber.e(e)
            return GradientDrawable.Orientation.LEFT_RIGHT
        }
    }

    fun getColorListWithAlpha(defaultValues: ArrayList<String>, alphaValue: String): ArrayList<String> {
        val alphaAppliedList = ArrayList<String>()
        defaultValues.forEach {
            if (it.isNotEmpty()) {
                alphaAppliedList.add(getColorStringWithAlphaValue(it, alphaValue))
            }
        }
        return alphaAppliedList
    }
}