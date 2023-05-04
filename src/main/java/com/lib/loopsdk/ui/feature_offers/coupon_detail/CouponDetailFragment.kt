package com.lib.loopsdk.ui.feature_offers.coupon_detail

import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.reflect.TypeToken
import com.lib.loopsdk.R
import com.lib.loopsdk.core.customViews.SwipeToUnlockView
import com.lib.loopsdk.core.util.*
import com.lib.loopsdk.data.remote.dto.CouponDetailData
import com.lib.loopsdk.data.remote.dto.InitializeDto
import com.lib.loopsdk.data.remote.dto.RedeemCouponDto
import com.lib.loopsdk.data.remote.dto.UnlockCouponDto
import com.lib.loopsdk.databinding.FragmentCouponDetailBinding
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream


class CouponDetailFragment(
    private val couponDetailData: CouponDetailData,
    private val listener: CouponInteractionListener
    ) : BottomSheetDialogFragment() ,View.OnClickListener, SwipeToUnlockView.Interaction {
    private var behavior: BottomSheetBehavior<View>? = null
    private lateinit var binding: FragmentCouponDetailBinding
    private var couponTransactionId:String=""
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog: BottomSheetDialog =
            super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener { dialogInterface ->
            val mDialog: BottomSheetDialog = dialogInterface as BottomSheetDialog
            val bottomSheet: FrameLayout = mDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet)!!
            BottomSheetBehavior.from(bottomSheet).state = BottomSheetBehavior.STATE_EXPANDED
        }
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#ccffffff")))
        return dialog
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_AppCompat_Light_NoActionBar_FullScreen)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCouponDetailBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initVariables()
        setUpListener()
        showCouponData()
    }
    private fun initVariables(){
        binding.lifecycleOwner = viewLifecycleOwner
        binding.brandTheme = Prefs.getNonPrimitiveData<InitializeDto.Data.BrandTheme>(object: TypeToken<InitializeDto.Data.BrandTheme>(){}.type, "BRAND_THEME")
        binding.brandThemeColors = Colors

        val params =
            (binding.root.parent as View).layoutParams as CoordinatorLayout.LayoutParams
        behavior = params.behavior as BottomSheetBehavior<View>?

        if (behavior != null && behavior is BottomSheetBehavior) {
            //expandBottomSheet()
            behavior!!.addBottomSheetCallback(object :
                BottomSheetBehavior.BottomSheetCallback() {
                override fun onSlide(bottomSheet: View, slideOffset: Float) {

                }
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    when (newState) {
                        BottomSheetBehavior.STATE_HIDDEN -> closeBottomSheet()
                        BottomSheetBehavior.STATE_DRAGGING -> {
//                            BottomSheetBehavior.from(bottomSheet).state = BottomSheetBehavior.STATE_EXPANDED
                        }
                        /*BottomSheetBehavior.STATE_EXPANDED -> behavior?.setPeekHeight(Utils.dpToPx(myContext, 650))
                        BottomSheetBehavior.STATE_HALF_EXPANDED -> behavior?.setPeekHeight(Utils.dpToPx(myContext, 650))*/
                        else -> {
                            //Do Nothing
                        }
                    }
                }

            })
        }
    }
    private fun showDate(date:String):String{
        val dateArray = date.split(",").toTypedArray()
        return dateArray[0]
    }
    private fun setUpListener() {
        binding.btnGetCode.setOnClickListener(this)
        binding.btnCopyCouponCode.setOnClickListener(this)
        binding.btnSwipeToUnlock.setOnClickListener(this)
        binding.btnDownload.setOnClickListener(this)
        binding.llGiftCoupon.setOnClickListener(this)
        binding.btnTryAgain.setOnClickListener(this)

    }
    private fun showCouponData(){

        val themeData = Prefs.getNonPrimitiveData<InitializeDto.Data>(object: TypeToken<InitializeDto.Data>(){}.type, "INIT_DATA")

        when(couponDetailData.couponClassification.toInt()){
            1,2->{
                binding.tvCouponName.text=themeData.defaultCurrency.symbol+ couponDetailData.maxDiscountValue+" Voucher"
            }
            3,4->{
                binding.tvCouponName.text=couponDetailData.discountPercent+"% Discount"
            }
            5,6->{
                //free shipping
                binding.tvCouponName.text="Freebie"
            }
        }
        binding.tvCouponDesc.text=couponDetailData.displayName
        binding.tvCouponDetail.text =
            binding.root.context.getHTMLTagsToSpannedString(couponDetailData.termsAndConditions)
        binding.tvCouponDetail.setMovementMethod(ScrollingMovementMethod())
        try {
            chagneThumbColor()
        }catch (e:java.lang.Exception){}





        //check Is Expiring
        if(couponDetailData.isExpiringSoon == 1 ){
            if(!couponDetailData.isExpired.isNullOrBlank() && couponDetailData.isExpired.toInt()==1)
                binding.cvExpired.hideView()
            else
                binding.cvExpired.showView()
        }else{
            binding.cvExpired.hideView()
        }

        if(couponDetailData.pointsToUnlock.isNotEmpty())
        {
            //Coupon Initial State
            checkCouponDefaultState()
        }else{
            checkCouponState()
        }
    }
    private fun checkCouponDefaultState(){
        val themeData = Prefs.getNonPrimitiveData<InitializeDto.Data>(object: TypeToken<InitializeDto.Data>(){}.type, "INIT_DATA")
        //initial state
        if(couponDetailData.isUnlockable.toInt()==1 && couponDetailData.remaningPoints.toInt()==0) {
            binding.tvUnlockFor.text = "Unlock for"
            if(couponDetailData.pointsToUnlock.toInt()>1){
                binding.tvPoints.text = couponDetailData.pointsToUnlock+" "+themeData.pointsIdentifier.pointsLabelPlural
            }else{
                if(couponDetailData.pointsToUnlock.toInt()!=0) {
                    binding.tvPoints.text =
                        couponDetailData.pointsToUnlock + " " + themeData.pointsIdentifier.pointsLabelSingular
                }else{
                    binding.ivPointIcon.hideView()
                    binding.tvPoints.text ="Free"
                }
            }
//            binding.swipeToUnlock.enableSwipeToUnlock()
            enableUnlockButton()
        }else if(couponDetailData.isUnlockable.toInt()!=1 && couponDetailData.remaningPoints.toInt()!=0)
        {
            binding.tvUnlockFor.text = "Earn"
            if(couponDetailData.remaningPoints.toInt()>1){
                binding.tvPoints.text = couponDetailData.remaningPoints+" "+themeData.pointsIdentifier.pointsLabelPlural+" more to unlock"
            }else{
                binding.tvPoints.text = couponDetailData.remaningPoints+" "+themeData.pointsIdentifier.pointsLabelSingular+" more to unlock"
            }
//            binding.swipeToUnlock.disableSwipeToUnlock()
            disableUnlockButton()
        }
        binding.llUnlock.showView()
        binding.btnSwipeToUnlock.showView()

    }
    private fun checkCouponState(){

            //is  Expired
            if(!couponDetailData.isExpired.isNullOrBlank() && couponDetailData.isExpired.toInt()==1 && couponDetailData.expiredOn.isNotEmpty())
            {
                //show expired state
                binding.ivCouponStatus.setImageDrawable(ContextCompat.getDrawable(binding.root.context,
                    R.drawable.ic_error_circle))
                binding.ivCouponStatus.setColorFilter(ContextCompat.getColor(binding.root.context,R.color.error_red), PorterDuff.Mode.SRC_IN)
                binding.tvCouponStatus.setTextColor(ContextCompat.getColor(binding.root.context,R.color.error_red))
//                binding.tvCouponStatus.text="expired on "+showDate(couponDetailData.expiredOn)
                binding.tvCouponStatus.text="Expired"
                binding.llCouponStatus.showView()

            }else if(couponDetailData.couponStatus.toInt()==1){
                // coupon unlocked

                if(couponDetailData.unlockOrigin==Constants.CouponUnlockType.UNLOCKED.type){
                    if(couponDetailData.isTransferable==1){
                        binding.llGiftCoupon.showView()
                    }else binding.llGiftCoupon.hideView()
                }else if(couponDetailData.unlockOrigin==Constants.CouponUnlockType.GIFT_FROM_FRIEND.type){
                    // gift from friend
                    binding.ivCouponStatus.setImageDrawable(ContextCompat.getDrawable(binding.root.context,
                        R.drawable.ic_unlock))

                    binding.ivCouponStatus.setColorFilter(ContextCompat.getColor(binding.root.context,R.color.info_blue), PorterDuff.Mode.SRC_IN)
                    binding.tvCouponStatus.setTextColor(ContextCompat.getColor(binding.root.context,R.color.info_blue))
                    binding.tvCouponStatus.text="Received as a gift on "+showDate(couponDetailData.unlockedOn)
                    binding.llCouponStatus.showView()
                } else{
                    // reward type
                    binding.ivCouponStatus.setImageDrawable(ContextCompat.getDrawable(binding.root.context,
                        R.drawable.ic_gift))

                    binding.ivCouponStatus.setColorFilter(ContextCompat.getColor(binding.root.context,R.color.info_blue), PorterDuff.Mode.SRC_IN)
                    binding.tvCouponStatus.setTextColor(ContextCompat.getColor(binding.root.context,R.color.info_blue))
                    binding.tvCouponStatus.text="Reward received on "+showDate(couponDetailData.unlockedOn)
                    binding.llCouponStatus.showView()
                }

            }else if(couponDetailData.couponStatus.toInt()==2){
                // coupon redeemed
                binding.ivCouponStatus.setImageDrawable(ContextCompat.getDrawable(binding.root.context,
                    R.drawable.ic_checkmark_starburst))

                binding.ivCouponStatus.setColorFilter(ContextCompat.getColor(binding.root.context,R.color.success_green), PorterDuff.Mode.SRC_IN)
                binding.tvCouponStatus.setTextColor(ContextCompat.getColor(binding.root.context,R.color.success_green))
                binding.tvCouponStatus.text="You redeemed this coupon on "+showDate(couponDetailData.usedOn)
                binding.llCouponStatus.showView()

            }else if(couponDetailData.couponStatus.toInt()==3||couponDetailData.couponStatus.toInt()==4){
                // coupon gifted
                binding.ivCouponStatus.setImageDrawable(ContextCompat.getDrawable(binding.root.context,
                    R.drawable.ic_gift))

                binding.ivCouponStatus.setColorFilter(ContextCompat.getColor(binding.root.context,R.color.success_green), PorterDuff.Mode.SRC_IN)
                binding.tvCouponStatus.setTextColor(ContextCompat.getColor(binding.root.context,R.color.success_green))
                binding.tvCouponStatus.text="You gifted this coupon on "+showDate(couponDetailData.giftedOn)
                binding.llCouponStatus.showView()
            }

            // show Button state
            if(couponDetailData.couponStatus.toInt()==5){
                //used state
                binding.cvCouponCode.showView()
                binding.tvCouponCodeHeader.showView()
                binding.btnGetCode.hideView()
                binding.llGiftCoupon.hideView()
                //show code
                when(couponDetailData.couponCodeType.toInt()){
                    2,3,5,6->{
                        // Bar & QR
                        binding.llCodeTypeOther.showView()
                        binding.llCodeTypeOne.hideView()
                        binding.ivCodeType.let {
                            Glide.with(this).load(couponDetailData.couponCode).into(it)
                        }
                    }
                    1,4->{
                        //code
                        binding.llCodeTypeOne.showView()
                        binding.llCodeTypeOther.hideView()
                        binding.tvCouponCode.text=couponDetailData.couponCode
                    }
                }
            }else if(couponDetailData.couponStatus.toInt()==2){
                binding.cvCouponCode.hideView()
                binding.btnGetCode.hideView()
            }else{
                binding.btnGetCode.showView()
                this.couponTransactionId=couponDetailData.transactionId
            }
            if((!couponDetailData.isExpired.isNullOrBlank() &&couponDetailData.isExpired.toInt()==1) ||
                couponDetailData.couponStatus.toInt()==3||couponDetailData.couponStatus.toInt()==4 ){
                binding.cvCouponCode.alpha=0.5f
                binding.btnGetCode.alpha=0.5f
                binding.cvCouponCode.setOnClickListener(null)
                binding.btnGetCode.isEnabled=false
                binding.btnGetCode.setOnClickListener(null)
            }else{
                binding.cvCouponCode.alpha=1f
                binding.btnGetCode.alpha=1f
            }
    }
    public fun closeBottomSheet() {
        this.dismiss()
    }
    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        listener.onCouponDetailSheetCancelled()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        dialog.cancel()
    }
    interface CouponInteractionListener{
        fun onUnlockCouponClicked(id: String)
        fun onRedeemCouponClicked(couponTransactionId: String)

        fun onGiftCouponClicked(couponTransactionId: String)

        fun onCouponDetailSheetCancelled()

    }
    fun showErrorState() {
        binding.mainLayout.hideView()
//        binding.swipeToUnlock.resetSwipeToUnlock()
        binding.clErrorState.showView()
    }
    fun showGetCodeDetail(data: UnlockCouponDto.Data) {
        this.couponTransactionId=data.transactionId
        binding.btnSwipeToUnlock.hideView()
//        binding.swipeToUnlock.resetSwipeToUnlock()
        binding.llUnlock.hideView()
        if(data.isTransferable.toInt()==1){
            binding.llGiftCoupon.showView()
        }else binding.llGiftCoupon.hideView()
        binding.btnGetCode.showView()
    }
    fun showCodeDetailAfterRedeem(couponData : RedeemCouponDto.Data) {
        binding.btnGetCode.hideView()
        binding.llGiftCoupon.hideView()
        binding.tvCouponCodeHeader.showView()
        binding.cvCouponCode.showView()
        when(couponData.couponCodeType){
            2,3,5,6->{
                // Bar & QR
                binding.llCodeTypeOther.showView()
                binding.llCodeTypeOne.hideView()
                binding.ivCodeType.let {
                    Glide.with(this).load(couponData.couponCode).into(it)
                }
            }
            1,4->{
                //code
                binding.llCodeTypeOne.showView()
                binding.llCodeTypeOther.hideView()
                binding.tvCouponCode.text=couponData.couponCode
            }
        }
    }
    fun showCoupon_Code_viewed_State(){

    }

    override fun onClick(view: View?) {
       when(view!!.id){
           R.id.btnGetCode->{
               listener.onRedeemCouponClicked(couponTransactionId)
               binding.btnGetCode.isEnabled=false
           }
           R.id.btnCopyCouponCode -> {
               val clipboardManager: ClipboardManager = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
               val clip = ClipData.newPlainText(
                   "CouponCode",
                   couponDetailData.couponCode
               )
               clipboardManager.setPrimaryClip(clip)
               Toast.makeText(requireContext(), "Code copied", Toast.LENGTH_SHORT).show()
           }
           R.id.llGiftCoupon->{
               //gift Coupon Flow
               listener.onGiftCouponClicked(couponTransactionId)
               closeBottomSheet()
           }
           R.id.btnDownload->{
               val thread = Thread {
                   try {
                       val image = getBitmapFromURL(couponDetailData.couponCode)
                       mSaveMediaToStorage(image)
                   } catch (e: Exception) {
                       e.printStackTrace()
                   }
               }
               thread.start()
           }
           R.id.btnTryAgain->{
               binding.clErrorState.hideView()
               binding.mainLayout.showView()
               showCouponData()
           }
           R.id.btnSwipeToUnlock->{
               //call unlock API
               listener.onUnlockCouponClicked(couponDetailData.id)
           }
       }
    }
    override fun onSwipeEndReachedAndReleased() {
        //call unlock API
        //listener.onUnlockCouponClicked(couponDetailData.id)
    }
    override fun onSwipeStartReachedAndReleased() {
    }
    private fun mSaveMediaToStorage(bitmap: Bitmap?) {
        val filename = "${System.currentTimeMillis()}.jpg"
        var fos: OutputStream? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            requireActivity().contentResolver?.also { resolver ->
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }
                val imageUri: Uri? = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                fos = imageUri?.let { resolver.openOutputStream(it) }
            }
        } else {
            val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val image = File(imagesDir, filename)
            fos = FileOutputStream(image)
        }
        fos?.use {
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, it)
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(context , "Saved to Gallery" , Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun chagneThumbColor(){
        binding.tvCouponDetail.verticalScrollbarThumbDrawable!!.setTint(Color.parseColor(Colors.PRIMARY_BRAND_COLOR))
        binding.tvCouponDetail.verticalScrollbarTrackDrawable!!.setTint(Color.parseColor(Colors.SECONDARY_BRAND_COLOR))
    }

    private fun enableUnlockButton() {
        binding.btnSwipeToUnlock.isEnabled=true
        binding.btnSwipeToUnlock.alpha=1f
    }

    private fun disableUnlockButton() {
        binding.btnSwipeToUnlock.isEnabled=false
        binding.btnSwipeToUnlock.alpha=0.5f
    }

}