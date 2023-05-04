package com.lib.loopsdk.core.util

import android.app.Dialog
import android.content.Context
import android.view.View

class AlertDialogUtils: CommonDialogUtils() {
    companion object {

        fun showSingleButtonAlertDialog(context: Context, message: String) =
            CommonDialogUtils.showSingleButtonAlertDialog(context, message)

        fun showSingleButtonAlertDialog(
            context: Context,
            message: String,
            singleButtonClickListener: SingleButtonClickListener?
        ) = CommonDialogUtils.showSingleButtonAlertDialog(
            context,
            message,
            singleButtonClickListener
        )

//        fun showTwoButtonAlertDialog(
//            context: Context,
//            positiveButtonText: String,
//            negativeButtonText: String,
//            message: String,
//            twoButtonClickListener: TwoButtonClickListener) = CommonDialogUtils.showTwoButtonAlertDialog(context, positiveButtonText, negativeButtonText, message, twoButtonClickListener)

        private fun getCustomViewDialog(context: Context, view: View): Dialog {
            val dialog = Dialog(context)
            dialog.setContentView(view)
            return dialog
        }

//        fun showReactionDialog(
//            context: Context,
//            view: View,
//            listener: ReactionListener
//        ) {
//            val binding = DialogReactionsBinding.inflate(LayoutInflater.from(context), null, false)
//            val dialog = getCustomViewDialog(context, binding.root)
//
//            val window = dialog.window ?: return
//            val windowLayoutParams = window.attributes ?: return
//
//            windowLayoutParams.gravity = Gravity.TOP or Gravity.START
//
//            // Set dialog position
//            val location = IntArray(2)
//            view.getLocationOnScreen(location)
//            windowLayoutParams.x = location[0]
//            if(location[1] >= 500) {
//                windowLayoutParams.y = location[1] - (Utils.dpToPx(context, 72) * 1.5).toInt()
//            } else {
//                windowLayoutParams.y = location[1] + (Utils.dpToPx(context, 16))
//            }
//
//            // Set background transparent
//            window.setBackgroundDrawableResource(android.R.color.transparent)
//            window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
//
//            // Set click listener
//            binding.imgLike.setOnClickListener {
//                listener.onReactionClicked(Constants.Reactions.LIKE)
//                dialog.dismiss()
//            }
//
//            binding.imgLove.setOnClickListener {
//                listener.onReactionClicked(Constants.Reactions.LOVE)
//                dialog.dismiss()
//            }
//
//            binding.imgClap.setOnClickListener {
//                listener.onReactionClicked(Constants.Reactions.CLAP)
//                dialog.dismiss()
//            }
//
//            dialog.setOnDismissListener {
//                listener.onDismiss()
//            }
//
//            dialog.show()
//        }
//    }
//    interface ReactionListener {
//        fun onDismiss()
//        fun onReactionClicked(reaction: Constants.Reactions)
//    }
    }
}