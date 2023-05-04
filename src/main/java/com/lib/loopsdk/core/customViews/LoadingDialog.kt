package com.lib.loopsdk.core.customViews

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.view.Window
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.model.KeyPath
import com.airbnb.lottie.value.LottieValueCallback
import com.lib.loopsdk.R
import com.lib.loopsdk.core.util.Colors


class LoadingDialog(context: Context) {

    var mLoadingDialog: Dialog? = null
    init {
        mLoadingDialog = Dialog(context, R.style.Theme_AppCompat_Light_NoActionBar_FullScreen)
        mLoadingDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        mLoadingDialog?.setContentView( R.layout.custom_loading_dialog)
        mLoadingDialog?.setCanceledOnTouchOutside(false)
        mLoadingDialog?.setCancelable(false)
        changeAnimColor()
    }
    fun changeAnimColor(){
        val themeColor = Colors
        val lottieAnimationView = mLoadingDialog!!.findViewById<LottieAnimationView>((R.id.lottie_loading_anim))
        val colorFilter = PorterDuffColorFilter(Color.parseColor(themeColor.PRIMARY_BRAND_COLOR), PorterDuff.Mode.SRC_ATOP)
        val keyPath = KeyPath("**")
        val callback = LottieValueCallback<ColorFilter>(colorFilter)
        lottieAnimationView.addValueCallback<ColorFilter>(keyPath, LottieProperty.COLOR_FILTER, callback)
    }

    fun show() {
        try {
            if (!mLoadingDialog?.isShowing!!) {
                mLoadingDialog?.show()
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun cancel() {
        try {
            if (mLoadingDialog != null && mLoadingDialog?.isShowing!!) {
                mLoadingDialog?.cancel()
                mLoadingDialog?.dismiss()
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

}
