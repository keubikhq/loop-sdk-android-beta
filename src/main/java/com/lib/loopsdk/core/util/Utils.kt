package com.lib.loopsdk.core.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.Color
import android.graphics.Point
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.Display
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import java.net.URLEncoder
import java.text.DecimalFormat
import kotlin.math.roundToInt


object Utils {
    fun dpToPx(context: Context, dp: Number): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), context.resources.displayMetrics).roundToInt();
    }

    fun startURLIntent(context: Context, url: String) {
        val intent = getURLIntent(url)
        context.startActivity(intent)
    }

    fun getURLIntent(url: String): Intent {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        return intent
    }

    @ColorInt
    fun getColorInt(context: Context, colorRes: Int): Int {
        return ContextCompat.getColor(context, colorRes)
    }

    @ColorInt
    fun getColorInt(colorHex: String): Int {
        return try {
            if(!colorHex.startsWith("#"))
                Color.parseColor("#$colorHex")
            else
                Color.parseColor(colorHex)
        } catch (e:Exception) {
            //Timber.e(e)
            Color.BLACK
        }
    }
    private const val WIDTH_INDEX = 0
    private const val HEIGHT_INDEX = 1
    fun getScreenSize(context: Context): IntArray? {
        val widthHeight = IntArray(2)
        widthHeight[WIDTH_INDEX] = 0
        widthHeight[HEIGHT_INDEX] = 0
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display: Display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        widthHeight[WIDTH_INDEX] = size.x
        widthHeight[HEIGHT_INDEX] = size.y
        if (!isScreenSizeRetrieved(widthHeight)) {
            val metrics = DisplayMetrics()
            display.getMetrics(metrics)
            widthHeight[0] = metrics.widthPixels
            widthHeight[1] = metrics.heightPixels
        }

        // Last defense. Use deprecated API that was introduced in lower than API 13
        if (!isScreenSizeRetrieved(widthHeight)) {
            widthHeight[0] = display.getWidth() // deprecated
            widthHeight[1] = display.getHeight() // deprecated
        }
        return widthHeight
    }

    private fun isScreenSizeRetrieved(widthHeight: IntArray): Boolean {
        return widthHeight[WIDTH_INDEX] != 0 && widthHeight[HEIGHT_INDEX] != 0
    }

    fun showFragment(
        supportFragmentManager: FragmentManager,
        fragment: Fragment,
        name: String,
        argsBundle: Bundle? = null
    ) {
        val transaction = supportFragmentManager.beginTransaction()
        if (null != argsBundle) fragment.arguments = argsBundle
        transaction.add(android.R.id.content, fragment)
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        transaction.addToBackStack(name)
        transaction.commit()
    }
    fun digitCountUpdate(number: Number): String? {
        val suffix = charArrayOf(' ', 'K', 'M', 'B', 'T', 'P', 'E')
        val numValue = number.toLong()
        val value = Math.floor(Math.log10(numValue.toDouble())).toInt()
        val base = value / 3
        return if (value >= 3 && base < suffix.size) {
            DecimalFormat("#0").format(
                numValue / Math.pow(
                    10.0,
                    (base * 3).toDouble()
                )
            ) + suffix[base]
        } else {
            DecimalFormat("#,##0").format(numValue)
        }
    }


    fun shareTextOnFacebookWeb(context: Context, text: String) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/$text"))
            context.startActivity(intent)
    }
    fun shareTextOnTwitterWeb(context: Context, text: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/$text"))
        context.startActivity(intent)
    }
    fun shareTextOnInstagramWeb(context: Context, text: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/$text"))
        context.startActivity(intent)
    }
    fun shareTextOnLinkedinWeb(context: Context, text: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/$text"))
        context.startActivity(intent)
    }
    fun playYouTubeVideo(context: Context, videoId: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=$videoId"))
        context.startActivity(intent)
    }



    fun showToast(context: Context,message:String){
       Toast.makeText(context,message,Toast.LENGTH_SHORT).show()
    }
}