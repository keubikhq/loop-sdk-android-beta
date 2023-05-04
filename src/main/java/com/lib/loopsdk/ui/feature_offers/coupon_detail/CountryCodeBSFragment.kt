package com.lib.loopsdk.ui.feature_offers.coupon_detail

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.reflect.TypeToken
import com.lib.loopsdk.R
import com.lib.loopsdk.core.util.Colors
import com.lib.loopsdk.core.util.Prefs
import com.lib.loopsdk.data.remote.dto.CountryCodesDto
import com.lib.loopsdk.data.remote.dto.InitializeDto
import com.lib.loopsdk.databinding.FragmentCountryCodeBSBinding

class CountryCodeBSFragment (
    private  val listener: CountryCodeSelectedListener
): BottomSheetDialogFragment() , CountryCodeAdapter.OnCountryCodeClickListener {
    private lateinit var countryCodeAdapter: CountryCodeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_AppCompat_Light_NoActionBar_FullScreen)
    }
    interface  CountryCodeSelectedListener{
        fun onCountryCodeSelected(countryCode: CountryCodesDto.Data.CountryCode)
    }

    private lateinit var binding: FragmentCountryCodeBSBinding
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCountryCodeBSBinding.inflate(
            inflater,
            container,
            false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.brandTheme = Prefs.getNonPrimitiveData<InitializeDto.Data.BrandTheme>(object: TypeToken<InitializeDto.Data.BrandTheme>(){}.type, "BRAND_THEME")
        binding.brandThemeColors = Colors
        setupAdapters()
    }

    override fun onCountryCodeItemClicked(list: CountryCodesDto.Data.CountryCode) {
        listener.onCountryCodeSelected(list)
        this.dismiss()
    }

    private fun setupAdapters() {
        val countryCodesList = Prefs.getNonPrimitiveData<List<CountryCodesDto.Data.CountryCode>>(object: TypeToken<List<CountryCodesDto.Data.CountryCode>>(){}.type, "COUNTRY_CODE_LIST")
        countryCodeAdapter= CountryCodeAdapter(countryCodesList,this)
        binding.rvCountryCodeList.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvCountryCodeList.adapter = countryCodeAdapter
    }

}