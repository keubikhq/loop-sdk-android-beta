package com.lib.loopsdk.ui.feature_offers.your_coupons

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.reflect.TypeToken
import com.lib.loopsdk.R
import com.lib.loopsdk.core.util.Colors
import com.lib.loopsdk.core.util.Constants
import com.lib.loopsdk.core.util.Prefs
import com.lib.loopsdk.core.util.hideView
import com.lib.loopsdk.core.util.showView
import com.lib.loopsdk.data.remote.dto.InitializeDto
import com.lib.loopsdk.databinding.FragmentSortCouponBinding


class SortCouponFragment(
    private val listener: SortByListener
    ) : BottomSheetDialogFragment() ,View.OnClickListener{
    private lateinit var binding: FragmentSortCouponBinding
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
        binding = FragmentSortCouponBinding.inflate(
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
    }
    private fun setUpListener() {
        binding.btnCancel.setOnClickListener(this)
        binding.btnApply.setOnClickListener(this)
        binding.llCouponStatus.setOnClickListener(this)
        binding.llCodeView.setOnClickListener(this)
        binding.llExpired.setOnClickListener(this)
        binding.llExpiringSoon.setOnClickListener(this)
    }
    private fun initVariables(){
        binding.lifecycleOwner = viewLifecycleOwner
        binding.brandTheme = Prefs.getNonPrimitiveData<InitializeDto.Data.BrandTheme>(object: TypeToken<InitializeDto.Data.BrandTheme>(){}.type, "BRAND_THEME")
        binding.brandThemeColors = Colors

        //initial state
        binding.tvCouponStatus.text =Prefs.getString(Constants.COUPON_TYPE_SELECTED)+" first"
        if(Prefs.getString(Constants.COUPON_TYPE_SELECTED).equals(Constants.COUPONSORTType.UNLOCKED.title))
            binding.llCodeView.showView()
        else
            binding.llCodeView.hideView()
        updateButtonsUI()
    }
    interface SortByListener {
        fun onClearFilterClicked()
        fun onApplyFilterClicked()
    }
    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btnCancel -> {
                Prefs.putString(Constants.COUPON_SORT_TYPE, "")
                listener.onClearFilterClicked()
                closeBottomSheet()
            }
            R.id.btnApply -> {
                listener.onApplyFilterClicked()
                closeBottomSheet()
            }
            R.id.llExpired->{
                Prefs.putString(Constants.COUPON_SORT_TYPE, Constants.COUPONSORTType.EXPIRED.title)
                updateButtonsUI()
            }
            R.id.llCouponStatus->{
                val tabSelected=Prefs.getString(Constants.COUPON_TYPE_SELECTED)?:""
                Prefs.putString(Constants.COUPON_SORT_TYPE,tabSelected)
                updateButtonsUI()
            }
            R.id.llCodeView->{
                Prefs.putString(Constants.COUPON_SORT_TYPE, Constants.COUPONSORTType.CODE_VIEWED.title)
                updateButtonsUI()
            }
            R.id.llExpiringSoon->{
                Prefs.putString(Constants.COUPON_SORT_TYPE, Constants.COUPONSORTType.EXPIRING_SOON.title)
                updateButtonsUI()
            }
        }
    }
    private fun updateButtonsUI(
    ){
        var couponType=false
        var codeCopy=false
        var expiringSoon=false
        var expired=false
        val tabSelected=Prefs.getString(Constants.COUPON_TYPE_SELECTED)?:""
        val selectedSortType=Prefs.getString(Constants.COUPON_SORT_TYPE)
        if(selectedSortType.equals(Constants.COUPONSORTType.CODE_VIEWED.title)){
            codeCopy=true
        }else if(selectedSortType.equals(Constants.COUPONSORTType.EXPIRED.title)){
            expired=true
        }else if(selectedSortType.equals(Constants.COUPONSORTType.EXPIRING_SOON.title)){
            expiringSoon=true
        }else if(selectedSortType.equals(tabSelected)){
            couponType=true
        }
        if(couponType){
            binding.ivCouponStatusUnCheck.hideView()
            binding.ivCouponStatusCheck.showView()

        }else{
//            if(!selectedSortType!!.isEmpty()) binding.llCouponStatus.alpha=0.5f
            binding.ivCouponStatusUnCheck.showView()
            binding.ivCouponStatusCheck.hideView()
        }
        if(codeCopy){
            binding.ivCodeViewUnCheck.hideView()
            binding.ivCodeViewCheck.showView()

        }else{
            binding.ivCodeViewUnCheck.showView()
            binding.ivCodeViewCheck.hideView()
        }
        if(expiringSoon){
//            binding.llExpiringSoon.alpha=1f
            binding.ivExpiringSoonUnCheck.hideView()
            binding.ivExpiringSoonCheck.showView()

        }else{
//            if(!selectedSortType!!.isEmpty()) binding.llExpiringSoon.alpha=0.5f
            binding.ivExpiringSoonUnCheck.showView()
            binding.ivExpiringSoonCheck.hideView()
        }
        if(expired){
//            binding.llExpired.alpha=1f
            binding.ivExpiredUnCheck.hideView()
            binding.ivExpiredCheck.showView()
        }else{
//            if(!selectedSortType!!.isEmpty()) binding.llExpired.alpha=0.5f
            binding.ivExpiredUnCheck.showView()
            binding.ivExpiredCheck.hideView()
        }
        if(selectedSortType!!.isNotEmpty()){
            enableButton()
        }else {
           disableButton()
        }
    }

    public fun closeBottomSheet() {
        this.dismiss()
    }

    private fun enableButton() {
        binding.btnApply.isEnabled=true
        binding.btnApply.alpha=1f
    }

    private fun disableButton() {
        binding.btnApply.isEnabled=false
        binding.btnApply.alpha=0.5f
    }
}