package com.lib.loopsdk.ui.feature_offers.available_coupons

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.reflect.TypeToken
import com.lib.loopsdk.R
import com.lib.loopsdk.core.customViews.EsRangebar
import com.lib.loopsdk.core.util.Colors
import com.lib.loopsdk.core.util.Prefs
import com.lib.loopsdk.core.util.hideView
import com.lib.loopsdk.core.util.showView
import com.lib.loopsdk.data.remote.dto.CouponTypeData
import com.lib.loopsdk.data.remote.dto.InitializeDto
import com.lib.loopsdk.databinding.FragmentCouponFilterBinding
import timber.log.Timber


class CouponFilterFragment(
    private var maxPrice: Int = 1,
    private var minPrice: Int = 0,
    private var userSelectedMin: Int = minPrice,
    private var userSelectedMax: Int = maxPrice,
    private var listener: RangeChangeListener
) : BottomSheetDialogFragment() ,View.OnClickListener ,CouponTypeListAdapter.Interaction{

    private lateinit var viewModel: CouponsViewModel

    private lateinit var binding: FragmentCouponFilterBinding
    private lateinit var categoriesFilterChipsListAdapter: CouponTypeListAdapter
    var categoriesList: ArrayList<CouponTypeData> = arrayListOf()
    val couponTypeData= CouponTypeData()

    private var isSheetTouchedOutside=false

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
        binding = FragmentCouponFilterBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
     
        binding.lifecycleOwner = viewLifecycleOwner
        viewModel = ViewModelProvider(requireActivity())[CouponsViewModel::class.java]
        initVariables()
        setupListeners()
        setupAdapters()
        disableButton()
        restoreUserSelectedCategories()
        initView()
        setupCheckBox()
    }

    interface RangeChangeListener {
        fun onRangeChangeListener(
            maxPrice: Int,
            minPrice: Int
        )
        fun onClearFilterSubmission(
            maxPrice: Int,
            minPrice: Int
        )
    }
    private fun initVariables(){
        categoriesList=viewModel.categoriesList
        binding.lifecycleOwner = viewLifecycleOwner
        binding.brandTheme = Prefs.getNonPrimitiveData<InitializeDto.Data.BrandTheme>(object: TypeToken<InitializeDto.Data.BrandTheme>(){}.type, "BRAND_THEME")
        binding.brandThemeColors = Colors

        couponTypeData.CouponTypeSerialNo=1
        couponTypeData.CouponTypeId="13"

    }
    private fun setupListeners() {
        binding.btnApply.setOnClickListener(this)
        binding.btnCancel.setOnClickListener(this)
        binding.ivExpiringSoonUnCheck.setOnClickListener(this)
        binding.ivExpiringSoonCheck.setOnClickListener(this)
    }

    private fun restoreUserSelectedCategories() {

        if (!viewModel.selectedCategoriesList.isNullOrEmpty()) {

            viewModel.selectedCategoriesList.forEachIndexed { i, selectedId ->
                categoriesList.forEachIndexed { j, category ->
                    if (category.CouponTypeId.equals(selectedId)) {
                        categoriesFilterChipsListAdapter.toggleCategoryTileSelection(j, true)
                    } else {
                        Timber.d("not found")
                    }
                }
            }
            enableButton()
        } else {
            disableButton()
        }
    }

    private fun setupAdapters() {
        categoriesFilterChipsListAdapter = CouponTypeListAdapter(this)
        categoriesFilterChipsListAdapter.setViewType(2)

        binding.rvCategoryList.layoutManager = LinearLayoutManager( requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvCategoryList.adapter = categoriesFilterChipsListAdapter
        categoriesFilterChipsListAdapter.addFilterTiles(categoriesList)

    }

    private fun enableButton() {
        binding.btnApply.isEnabled=true
        binding.btnApply.alpha=1f
        isSheetTouchedOutside=true
    }

    private fun disableButton() {
        binding.btnApply.isEnabled=false
        binding.btnApply.alpha=0.5f

    }
    private fun setupCheckBox() {

        if (viewModel.filterByBalance) {

            userSelectedMax = viewModel.userBalance
            userSelectedMin = minPrice
            maxPrice =  viewModel.userBalance

            binding.ivExpiringSoonCheck.showView()
            binding.ivExpiringSoonUnCheck.hideView()
        } else {
            userSelectedMax = maxPrice
            userSelectedMin = minPrice

            binding.ivExpiringSoonCheck.hideView()
            binding.ivExpiringSoonUnCheck.showView()
        }
        updateFilterButtonsUI()
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        if(isSheetTouchedOutside){
            binding.btnCancel.performClick()
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        dialog.cancel()
    }
    private fun updateFilterButtonsUI() {
        var isAnySelected = false
        if (!viewModel.selectedCategoriesList.isNullOrEmpty()) {
            viewModel.selectedCategoriesList.forEachIndexed { i, selectedId ->
                categoriesList.forEachIndexed { j, category ->
                    if (category.CouponTypeId.equals(selectedId)) {
                        isAnySelected = true
                    }
                }
            }
        }
        if (isAnySelected || viewModel.filterByBalance || viewModel.isPriceRangeSelected) {
            enableButton()
        } else {
            disableButton()
        }
    }
    fun rangeChipAddRemove(min:Int,max:Int){
        Timber.d("isPriceRangeSelected  "+viewModel.filterByBalance )
        Timber.d("isPriceRangeSelected  "+viewModel.isPriceRangeSelected)
        if (viewModel.filterByBalance || viewModel.isPriceRangeSelected) {

            couponTypeData.CouponTypeTitle=min.toString()+" - "+max.toString()
            viewModel.addRemoveSelectedRange(true,couponTypeData)
        } else {
            viewModel.addRemoveSelectedRange(false,couponTypeData)
        }
    }
    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btnApply -> {
                isSheetTouchedOutside=false
                rangeChipAddRemove(userSelectedMin,userSelectedMax)
                listener.onRangeChangeListener(userSelectedMax,userSelectedMin)
                viewModel.onApplyFiltersButtonClicked(requireContext())
                this.dismiss()
            }
            R.id.btnCancel -> {
                userSelectedMax = maxPrice
                userSelectedMin = minPrice
                listener.onClearFilterSubmission(maxPrice,0)
                viewModel.onClearAllFiltersButtonClicked(requireContext())
                rangeChipAddRemove(0,maxPrice)
                this.dismiss()
            }
            R.id.ivExpiringSoonUnCheck->{
                viewModel.filterByBalance=true
                setupCheckBox()
                initView()
            }
            R.id.ivExpiringSoonCheck->{
                viewModel.filterByBalance=false
                maxPrice = Prefs.getInt("maxPrice")
                setupCheckBox()
                initView()
            }
        }
    }

    override fun onClearAllFilterChips() {}

    override fun onCategoryFilterChipClicked(
        item: CouponTypeData,
        position: Int
    ) {}

    override fun onCategoryFilterTileClicked(
        item: CouponTypeData,
        position: Int
    ) {
        viewModel.addRemoveSelectedCategory(item.isSelected, item)
        updateFilterButtonsUI()
    }

    private fun initView() {

        Timber.d("selectedCategoriesList  MIN"+minPrice)
        Timber.d("selectedCategoriesList  MAX"+maxPrice)
        Timber.d("selectedCategoriesList  viewModel"+userSelectedMin)
        Timber.d("selectedCategoriesList  viewModel"+userSelectedMax)

        binding.rangeBar.listener = object : EsRangebar.OnRangeUpdatedListener {
            override fun onRangeUpdated(minValue: Int, maxValue: Int) {

                var rangeText = "$minValue"
                if (!viewModel.filterByBalance) {
                    rangeText += if (maxValue == maxPrice) {
                        " - Any"
                    } else {
                        " - $maxValue"
                    }
                    Timber.d("selectedCategoriesList T  MIN"+minValue.toString())
                    Timber.d("selectedCategoriesList T MAX"+maxPrice.toString())
                    Timber.d("selectedCategoriesList T viewModel"+viewModel.filterByBalance)
                    Timber.d("selectedCategoriesList T viewModel"+userSelectedMax)

                } else {
                    maxPrice = Prefs.getInt("maxPrice")
                    rangeText += if (maxValue == maxPrice) {
                        " - $maxValue"
                    } else {
                        " - $maxValue"
                    }
                    Timber.d("selectedCategoriesList F  MIN"+minPrice.toString())
                    Timber.d("selectedCategoriesList F MAX"+maxPrice.toString())
                    Timber.d("selectedCategoriesList F viewModel"+viewModel.filterByBalance)
                    Timber.d("selectedCategoriesList F viewModel"+userSelectedMax)

                }
                binding.tvPoints.text = rangeText
                userSelectedMax = maxValue
                userSelectedMin = minValue
                viewModel.isPriceRangeSelected = userSelectedMin != minPrice || userSelectedMax != maxPrice
                updateFilterButtonsUI()
                chagneThumbColor()
            }
        }

        if (viewModel.filterByBalance) {
            maxPrice = viewModel.userBalance
            binding.ivExpiringSoonUnCheck.hideView()
            binding.ivExpiringSoonCheck.showView()
        }else{
            maxPrice = Prefs.getInt("maxPrice")
            binding.ivExpiringSoonUnCheck.showView()
            binding.ivExpiringSoonCheck.hideView()
        }

        if (userSelectedMax == 1) {
            userSelectedMax = maxPrice
        }

//        Timber.d("selectedCategoriesList   MIN"+minPrice.toString())
//        Timber.d("selectedCategoriesList  MAX"+maxPrice.toString())
//        Timber.d("selectedCategoriesList  viewModel"+viewModel.filterByBalance)
//        Timber.d("selectedCategoriesList  viewModel"+userSelectedMax)

        binding.rangeBar.doTheMagicIn(
            requireContext(),
            originalMinValue = minPrice,
            originalMaxValue = maxPrice,
            uMin = userSelectedMin,
            uMax = userSelectedMax
        )
    }

    private fun chagneThumbColor(){
        val id = binding.rangeBar.rightSeekBar!!.thumbDrawableId
        val leftId = binding.rangeBar.leftSeekBar!!.thumbDrawableId
        val drawable2 = resources.getDrawable(id)
        val drawableLeft = resources.getDrawable(leftId)
        drawable2.setTint(Color.parseColor(Colors.PRIMARY_BRAND_COLOR))
        drawableLeft.setTint(Color.parseColor(Colors.PRIMARY_BRAND_COLOR))
    }



}