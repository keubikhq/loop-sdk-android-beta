package com.lib.loopsdk.core.util

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AlertDialog


open class CommonDialogUtils {
    companion object {
        fun showSingleButtonAlertDialog(context: Context, message: String) {
            showSingleButtonAlertDialog(context, message, null)
        }

        fun showSingleButtonAlertDialog(
            context: Context,
            message: String,
            singleButtonClickListener: SingleButtonClickListener?
        ) {
            if (message.isNullOrEmpty()) return
            val isUserDeactivated = message == "Unauthorized" || message == "Token Expired"
            val accountDeactivatedStr = "your account has been disabled"
            val isNetworkOrTokenError = message == Constants.NO_INTERNET_MESSAGE || message == "Invalid Token"
                val dialogBuilder = AlertDialog.Builder(context)
                    .setMessage(if (isUserDeactivated) accountDeactivatedStr else message)
                    .setCancelable(false)//!isUserDeactivated && !isNetworkOrTokenError
                    .setPositiveButton("okay") { _, _ ->
                        if (isUserDeactivated) {
                  //          (context as Activity?)?.logout()
                            return@setPositiveButton
                        }
                        singleButtonClickListener?.onButtonClicked()
                    }
                showDialog(dialogBuilder, context)
        }

        fun showSingleButtonLogoutDialog(
            context: Context,
            message: String,
            singleButtonClickListener: SingleButtonClickListener?
        ) {
            if (message.isNullOrEmpty()) return
        //    val isUserDeactivated = message == "Unauthorized" || message == "Token Expired"
            val accountDeactivatedStr = "your account has been disabled"
            val isNetworkOrTokenError = message == Constants.NO_INTERNET_MESSAGE || message == "Invalid Token"
         //   if(message.equals(accountDeactivatedStr) || isUserDeactivated || message.contains("session expired", true)) {
                val dialogBuilder = AlertDialog.Builder(context)
                    .setMessage(message)
                    .setCancelable(false)//!isUserDeactivated && !isNetworkOrTokenError
                    .setPositiveButton("okay") { _, _ ->
                        singleButtonClickListener?.onButtonClicked()
                   //     (context as Activity?)?.logout()
                        return@setPositiveButton
                    }
                showDialog(dialogBuilder, context)
        }



        fun showDialog(dialogBuilder: AlertDialog.Builder, context: Context) {
            if(!(context as Activity).isFinishing)
                dialogBuilder.show()
        }

        fun showTwoButtonAlertDialog(
            context: Context,
            positiveButtonText: String,
            negativeButtonText: String,
            message: String,
            twoButtonClickListener: TwoButtonClickListener,
            title: String = "") {
            if (message.isNullOrEmpty()) return
            val dialogBuilder = AlertDialog.Builder(context)
                .setMessage(message)
                .setTitle(title)
                .setPositiveButton(positiveButtonText)
                { _, _ ->
                    twoButtonClickListener.onPositiveButtonClicked()
                }
                .setNegativeButton(negativeButtonText)
                { _, _ ->
                    twoButtonClickListener.onNegativeButtonClicked()
                }
            showDialog(dialogBuilder, context)
        }
    }
    interface SingleButtonClickListener {
        fun onButtonClicked()
    }
    interface TwoButtonClickListener {
        fun onPositiveButtonClicked()
        fun onNegativeButtonClicked()
    }


}