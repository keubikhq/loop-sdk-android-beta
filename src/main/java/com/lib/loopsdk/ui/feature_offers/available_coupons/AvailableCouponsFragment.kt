package com.lib.loopsdk.ui.feature_offers.available_coupons

import android.content.ContentValues
import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.reflect.TypeToken
import com.lib.loopsdk.R
import com.lib.loopsdk.core.customViews.LoadingDialog
import com.lib.loopsdk.core.util.*
import com.lib.loopsdk.data.remote.dto.AvailableCouponDto
import com.lib.loopsdk.data.remote.dto.CouponDetailData
import com.lib.loopsdk.data.remote.dto.CouponTypeData
import com.lib.loopsdk.data.remote.dto.InitializeDto
import com.lib.loopsdk.data.remote.dto.RedeemCouponDto
import com.lib.loopsdk.data.remote.dto.TransferCouponDto
import com.lib.loopsdk.data.remote.dto.UnlockCouponDto
import com.lib.loopsdk.databinding.FragmentAvailableCouponsBinding
import com.lib.loopsdk.ui.feature_offers.coupon_detail.CouponDetailFragment
import com.lib.loopsdk.ui.feature_offers.coupon_detail.GiftCouponBSFragment
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import java.lang.Exception
import java.util.ArrayList
import kotlin.math.ceil

class AvailableCouponsFragment : Fragment(),FeaturedCouponsAdapter.Interaction ,
AvailableCouponsAdapter.Interaction,View.OnClickListener,
    CouponDetailFragment.CouponInteractionListener, GiftCouponBSFragment.GiftCouponListener,
    CouponTypeListAdapter.Interaction,CouponFilterFragment.RangeChangeListener{

    private lateinit var binding: FragmentAvailableCouponsBinding
    private val viewModel: CouponsViewModel by activityViewModels()
    private var availableCouponList: ArrayList<AvailableCouponDto.Data.CouponsData> = arrayListOf()
    private var featuredCouponList: ArrayList<AvailableCouponDto.Data.CouponsData> = arrayListOf()
    private var defaultCouponList: ArrayList<AvailableCouponDto.Data.CouponsData> = arrayListOf()


    private var availableSearchCouponList: ArrayList<AvailableCouponDto.Data.CouponsData> = arrayListOf()
    private var featuredSearchCouponList: ArrayList<AvailableCouponDto.Data.CouponsData> = arrayListOf()

    private lateinit var myContext: Context
    private lateinit var couponDetailBottomSheet: CouponDetailFragment
    private lateinit var giftCouponBSFragment: GiftCouponBSFragment


    private var areFiltersApplied = false
    private var isFirstTimeGetFocused= true
    private var couponUnlocked = false
    private var currentPageDefaultList = 1
    private var totalPagesDefaultList = 1
    private var isScrollDataLoadingDefaultList = false
    private var currentPageSearchAndFilterList = 1
    private var totalPagesSearchAndFilterList = 1
    private var isScrollDataLoadingSearchAndFilterList = false
    private lateinit var defaultFeaturedListAdapter: FeaturedCouponsAdapter
    private lateinit var searchFeaturedListAdapter: FeaturedCouponsAdapter
    private lateinit var defaultListAdapter: AvailableCouponsAdapter
    private lateinit var searchListAdapter: AvailableCouponsAdapter
    private lateinit var categoriesFilterChipsListAdapter: CouponTypeChipAdapter
    private val loadingDialog: LoadingDialog by lazy { LoadingDialog(
        requireContext()
    ) }
    private var maxPrice: Int = 1
    private var minPrice: Int = 0
    private var userSelectedMin: Int = 0
    private var userSelectedMax: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_available_coupons, container,false)

        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initVariables()
        setupAdapters()
        //addCouponTypeList()
        setUpViewModel()
        registerScrollListenerForDefaultList()
        registerScrollListenerForSearchFilter()
        resetDefaultListParams()
    }

    private fun initVariables(){
        binding.lifecycleOwner = viewLifecycleOwner
        binding.brandTheme = Prefs.getNonPrimitiveData<InitializeDto.Data.BrandTheme>(object: TypeToken<InitializeDto.Data.BrandTheme>(){}.type, "BRAND_THEME")
        binding.brandThemeColors = Colors
        userSelectedMax = maxPrice
        userSelectedMin = minPrice

        binding.btnFilter.setOnClickListener(this)
        binding.btnClearFilter.setOnClickListener(this)
        binding.btnClearSearch.setOnClickListener(this)



        binding.etSearch.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (s!!.isNotEmpty()) {
                    binding.btnClearSearch.showView()
                    if (areFiltersApplied){
//                        searchResultsWithFilterUi()
                    } else {
//                        onlySearchResultsUi()
                        viewModel.selectedCategoriesList.clear()
                    }
                    viewModel.searchAndFilterJob?.cancel()
                    resetSearchResultsListParams()
                    viewModel.searchAndFilterCouponsList(s.toString().trim(),currentPageSearchAndFilterList, myContext)
                } else if (s!!.isEmpty()) {
                    binding.btnClearSearch.hideView()
                    viewModel.searchAndFilterJob?.cancel()
                    if (areFiltersApplied) {
                        resetSearchResultsListParams()
                        searchResultsWithFilterUi()
                        viewModel.searchAndFilterCouponsList(s.toString().trim(),currentPageSearchAndFilterList, myContext)
                    } else {
                        viewModel.selectedCategoriesList.clear()

                        //restoreOriginalUiState()
                        getDefaultCoupons()
                    }
                }
            }

        })

        binding.etSearch.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                if(binding.etSearch.text.isNullOrEmpty()  && isFirstTimeGetFocused) {
                    binding.etSearch.clearFocus()
                    isFirstTimeGetFocused=false
                }
            }
        }


    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        myContext = context
    }
    private fun setUpViewModel(){
        viewModel.unlockingUiStateFlow.onEach { it ->
            when (it){
                is UIState.Loading ->{
                    //loadingDialog.show()
                    Timber.d("Loading state")
                }
                is UIState.Success<*> ->{
//                    noNetworkView!!.hideView()
                    //loadingDialog.cancel()
                    when(it.data){
                        is UnlockCouponDto.Data->{
                            couponUnlocked=true
                            if(::couponDetailBottomSheet.isInitialized && couponDetailBottomSheet.isVisible) {
                                couponDetailBottomSheet.showGetCodeDetail(it.data)
                            }
                        }
                        is RedeemCouponDto.Data->{
                            if(::couponDetailBottomSheet.isInitialized && couponDetailBottomSheet.isVisible) {
                                couponDetailBottomSheet.showCodeDetailAfterRedeem(it.data)
                            }
                        }
                        is TransferCouponDto.Data->{
                            if(::giftCouponBSFragment.isInitialized && giftCouponBSFragment.isVisible) {
                                giftCouponBSFragment.showDetailAfterTransfer(it.data)
                            }
                        }
                        else->{
                        }
                    }
                }

                is UIState.Error ->{
                    //loadingDialog.cancel()
                    try {
                        val errorCode = it.message.toInt()
                        when (errorCode) {
                            401 -> {
                                // CommonDialogUtils.showSingleButtonLogoutDialog(requireActivity(), "Session Expired!", null)
                            }
                            422 -> {
                            }
                        }
                    }catch (e: Exception){
                        if(it.message.contains("NetworkError")){
                            FullScreenNetworkErrorActivity.startActivity(this, myContext)
                        }else{
                            if(::couponDetailBottomSheet.isInitialized && couponDetailBottomSheet.isVisible) {
                                couponDetailBottomSheet.showErrorState()
                            }
                        }
                    }
                }
                else->{
                    //loadingDialog.cancel()
                }

            }
        }.observeInLifecycle(this)

        viewModel.defaultListUiStateFlow.onEach { uiState ->
            when (uiState) {
                is UIState.Loading -> {
                    //loadingDialog.show()
                }
                is UIState.Success<*> -> {
                    //loadingDialog.cancel()
                    //binding.noNetworkUi.clEmptyStateNoInternet.hideView()
                    when(uiState.data) {
                        is AvailableCouponDto.Data -> {
                            if(currentPageDefaultList == 1){
                                Handler().postDelayed({
                                    binding.llHeader.animate()
                                        .translationY(0f)
                                        .alpha(1f)
                                        .setDuration(750)
                                },200)
                            }
                            restoreOriginalUiState()
                            isScrollDataLoadingDefaultList = false
                            totalPagesDefaultList = ceil(uiState.data.totalEntries.toDouble() / Constants.IDEAL_PAGE_SIZE).toInt()
                            if(!uiState.data.couponsData.isNullOrEmpty()) {
                                this.defaultCouponList=uiState.data.couponsData
                                onDefaultCouponReceived(uiState.data.couponsData)
//                                defaultBrandsListAdapter.addData(uiState.data.data)
                                }else{
                                noResultsUi()
                            }
                        }
                        is String ->{

                        }
                    }
                }
                is UIState.Error -> {
                    //loadingDialog.cancel()
                    try {
                        val errorCode = uiState.message.toInt()
                        when (errorCode) {

                            401 -> {
                                CommonDialogUtils.showSingleButtonLogoutDialog(myContext, "Session Expired!", null)
                            }

                            422 -> {
                                //showServerErrorDialog()
                            }

                        }
                    }catch (e: Exception){
                        if(uiState.message.contains("NetworkError")){
                            FullScreenNetworkErrorActivity.startActivity(this, myContext)
                        }
                    }
                }
                else -> {
                    //loadingDialog.cancel()
                }
            }
        }.observeInLifecycle(this)

        viewModel.searchFilterUiStateFlow.onEach { uiState ->
            when (uiState) {
                is UIState.Loading -> {
                    //loadingDialog.show()
                    /*if (binding.cvSearchInputBg.activeColorType != 4){
                        loadingDialog.show()
                    }*/
                }
                is UIState.Success<*> -> {
                    //loadingDialog.cancel()
                    //binding.noNetworkUi.clEmptyStateNoInternet.hideView()
                    when(uiState.data) {


                        is AvailableCouponDto.Data -> {
                            if(currentPageSearchAndFilterList == 1){
                                Handler().postDelayed({
                                    binding.llHeader.animate()
                                        .translationY(0f)
                                        .alpha(1f)
                                        .setDuration(750)
                                },200)
                            }
                            if(areFiltersApplied) searchResultsWithFilterUi()
                            else onlySearchResultsUi()
                            isScrollDataLoadingSearchAndFilterList = false
                            totalPagesSearchAndFilterList = ceil(uiState.data.totalEntries.toDouble() / Constants.IDEAL_PAGE_SIZE).toInt()
                            if(!uiState.data.couponsData.isNullOrEmpty()) {
                                onSearchCouponReceived(uiState.data.couponsData)
                            }else{
                                noResultsUi()
                            }
                        }
                        is String ->{

                        }
                    }
                }
                is UIState.Error -> {
                    //loadingDialog.cancel()
                    try {
                        val errorCode = uiState.message.toInt()
                        when (errorCode) {

                            401 -> {
                                CommonDialogUtils.showSingleButtonLogoutDialog(myContext, "Session Expired!", null)
                            }
                            422 -> {
                               // showServerErrorDialog()
                            }
                        }
                    }catch (e: Exception){
                        if(uiState.message.contains("NetworkError")){
                            FullScreenNetworkErrorActivity.startActivity(this, myContext)
                        }
                    }
                }
                else -> {}
            }
        }.observeInLifecycle(this)

        viewModel.filterSheetEventFlow.onEach { event ->
            when (event){
                is CouponsViewModel.Event.OnApplyFilters ->{
                    areFiltersApplied = true
                    //show chips
                    searchResultsWithFilterUi()
                    if(viewModel.selectedChipList.size!=0){
                        categoriesFilterChipsListAdapter.swapData(viewModel.selectedChipList.sortedBy { it.CouponTypeSerialNo })
                        binding.clFilterChipContainer.showView()
                        updateRVWidht()
                    }else{
                        binding.clFilterChipContainer.hideView()
                    }
                    viewModel.searchAndFilterJob?.cancel()
                    if(binding.etSearch.editableText.toString().trim().isEmpty()){
                        resetSearchResultsListParams()
                    }
                    viewModel.searchAndFilterCouponsList(binding.etSearch.editableText.toString().trim(),currentPageSearchAndFilterList, myContext)
                }

                is CouponsViewModel.Event.OnClearAllFilters ->{
                    //show chips
                    areFiltersApplied = false
                    binding.clFilterChipContainer.hideView()
                    if(!binding.etSearch.editableText.toString().trim().isEmpty()){
                        onlySearchResultsUi()
                        resetSearchResultsListParams()
                        viewModel.searchAndFilterCouponsList(binding.etSearch.editableText.toString().trim(),currentPageSearchAndFilterList, myContext)
                    } else {
                        //restoreOriginalUiState()
                        //getDefaultCoupons()
                        restoreOriginalState()
                    }
                }
                else -> {}
            }
        }.observeInLifecycle(this)
    }
    private  fun restoreOriginalState()
    {
        updateFilterIconState(false)
        toggleSearchResultsUi(false)
        onDefaultCouponReceived(defaultCouponList)
    }

    private fun updateRVWidht(){
        val params: ViewGroup.LayoutParams = binding.rvFilterChip.getLayoutParams()
        if(viewModel.selectedChipList.size > 2){
            params.width = Utils.dpToPx(myContext, 260)
        }else{
            params.width = ViewGroup.LayoutParams.WRAP_CONTENT
        }
        binding.rvFilterChip.setLayoutParams(params)
    }
    override fun onResume() {
        super.onResume()
        binding.llHeader.alpha = 0f
        binding.llHeader.translationY = Utils.dpToPx(requireContext(), 10).toFloat()
        resetToInitialState()
    }
    fun resetToInitialState(){
        //reset all list and clear all filters
        viewModel.onClearAllFiltersButtonClicked(requireContext())
        resetDefaultListParams()
        resetSearchResultsListParams()
        getDefaultCoupons()
        getPoints()
    }

    private fun updateFilterIconState(shouldShowAsApplied: Boolean) {
        if(shouldShowAsApplied){
            //filter Applied
            binding.btnFilter.setCardBackgroundColor(Color.parseColor(binding.brandTheme!!.themeColors.primary.hexCode))
            binding.ivFilter.setColorFilter(Color.parseColor(binding.brandTheme!!.themeColors.fontIconColor.hexCode), PorterDuff.Mode.SRC_IN)
        }else{
            //default
            binding.btnFilter.setCardBackgroundColor(ContextCompat.getColor(myContext,R.color.white))
            binding.btnFilter.strokeWidth = Utils.dpToPx(myContext, 1)
            binding.btnFilter.strokeColor =
                Color.parseColor(binding.brandTheme!!.themeColors.primary.hexCode)
            binding.ivFilter.setColorFilter(Color.parseColor(binding.brandTheme!!.themeColors.primary.hexCode), PorterDuff.Mode.SRC_IN)
        }
    }

    private fun toggleSearchResultsUi(shouldShow: Boolean) {
        if(shouldShow){
            binding.rvSearchCoupons.showView()
            binding.rvSearchFeaturedCoupons.showView()
        }else{
            binding.rvSearchCoupons.hideView()
            binding.rvSearchFeaturedCoupons.hideView()
        }
    }

    private fun toggleNoResultsUi(shouldShow: Boolean) {
        if(shouldShow){
            binding.clCouponsContainer.hideView()
            binding.clFeaturedCouponsContainer.hideView()
            binding.clEmptyState.showView()
        }else{
            binding.clCouponsContainer.showView()
            binding.clFeaturedCouponsContainer.showView()
            binding.clEmptyState.hideView()
        }
    }

    private fun toggleDefaultListUi(shouldShow: Boolean) {
        if(shouldShow){
            binding.rvDefaultCoupons.showView()
            binding.rvFeaturedCoupons.showView()
        }else{
            binding.rvDefaultCoupons.hideView()
            binding.rvFeaturedCoupons.hideView()
        }
    }
    private fun restoreOriginalUiState(){
        updateFilterIconState(false)
        toggleSearchResultsUi(false)
        toggleDefaultListUi(true)
        toggleNoResultsUi(false)

    }

    private fun onlySearchResultsUi(){
        updateFilterIconState(false)
        toggleNoResultsUi(false)
        toggleDefaultListUi(false)
        toggleSearchResultsUi(true)
    }

    private fun searchResultsWithFilterUi(){
        updateFilterIconState(true)
        toggleNoResultsUi(false)
        toggleDefaultListUi(false)
        toggleSearchResultsUi(true)
    }


    private fun noResultsUi(){
        toggleNoResultsUi(true)
    }


    private fun setupAdapters() {
        defaultFeaturedListAdapter = FeaturedCouponsAdapter(this)
        binding.rvFeaturedCoupons.layoutManager = LinearLayoutManager(myContext, LinearLayoutManager.HORIZONTAL, false)
        binding.rvFeaturedCoupons.adapter = defaultFeaturedListAdapter

        searchFeaturedListAdapter = FeaturedCouponsAdapter(this)
        binding.rvSearchFeaturedCoupons.layoutManager = LinearLayoutManager(myContext, LinearLayoutManager.HORIZONTAL, false)
        binding.rvSearchFeaturedCoupons.adapter = searchFeaturedListAdapter


        defaultListAdapter = AvailableCouponsAdapter(this)
        binding.rvDefaultCoupons.layoutManager = LinearLayoutManager(myContext, LinearLayoutManager.VERTICAL, false)
        binding.rvDefaultCoupons.adapter = defaultListAdapter

        searchListAdapter = AvailableCouponsAdapter(this)
        binding.rvSearchCoupons.layoutManager = LinearLayoutManager(myContext, LinearLayoutManager.VERTICAL, false)
        binding.rvSearchCoupons.adapter = searchListAdapter

        categoriesFilterChipsListAdapter = CouponTypeChipAdapter()
        binding.rvFilterChip.layoutManager = LinearLayoutManager(
            myContext,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        binding.rvFilterChip.adapter = categoriesFilterChipsListAdapter
//        categoriesFilterChipsListAdapter.setViewType(1)
//        categoriesFilterChipsListAdapter.addFilterChips(viewModel.selectedChipList)

    }
    private fun registerScrollListenerForDefaultList() {
        binding.rvDefaultCoupons.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!binding.rvDefaultCoupons.canScrollVertically(1) &&
                    newState == RecyclerView.SCROLL_STATE_IDLE &&
                    !isScrollDataLoadingDefaultList &&
                    currentPageDefaultList < totalPagesDefaultList
                ) {
                    currentPageDefaultList++
                    isScrollDataLoadingDefaultList = true
                    getDefaultCoupons()
                }
            }
        })
    }

    private fun registerScrollListenerForSearchFilter() {
        binding.rvSearchCoupons.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!binding.rvSearchCoupons.canScrollVertically(1) &&
                    newState == RecyclerView.SCROLL_STATE_IDLE &&
                    !isScrollDataLoadingSearchAndFilterList &&
                    currentPageSearchAndFilterList < totalPagesSearchAndFilterList
                ) {
                    currentPageSearchAndFilterList++
                    isScrollDataLoadingSearchAndFilterList = true
                    viewModel.searchAndFilterCouponsList(binding.etSearch.editableText.toString().trim(),currentPageSearchAndFilterList, myContext)
                }
            }
        })
    }

    private fun getDefaultCoupons() {
        viewModel.getDefaultCouponsList(
            currentPageDefaultList
        )
    }

    private fun getPoints() {
        viewModel.getPointsAndTier()
    }

    private fun resetDefaultListParams(){
        currentPageDefaultList = 1
        totalPagesDefaultList = 1
        isScrollDataLoadingDefaultList = false
        defaultListAdapter.swapData(arrayListOf())
        defaultFeaturedListAdapter.swapData(arrayListOf())

    }
    private fun resetSearchResultsListParams() {
        currentPageSearchAndFilterList = 1
        totalPagesSearchAndFilterList = 1
        isScrollDataLoadingSearchAndFilterList = false
        searchFeaturedListAdapter.swapData(mutableListOf())
        searchListAdapter.swapData(mutableListOf())
    }

    private fun onDefaultCouponReceived(couponList: List<AvailableCouponDto.Data.CouponsData>) {

        Timber.d("onDefaultCouponReceived>>>>>"+couponList)

        var value: ArrayList<Int> = ArrayList()
        couponList.forEachIndexed { index, couponsData ->
            value.add(couponList[index].pointsToUnlock)
        }

        val maxPrice = value.maxOrNull() ?: 1

        Prefs.putInt("maxPrice", maxPrice)
        userSelectedMax = Prefs.getInt("maxPrice")
        //this.maxPrice = Prefs.getInt("maxPrice")



        featuredCouponList.clear()
        availableCouponList.clear()

        for (coupon in couponList) {
            if(coupon.isFeature==1){
                featuredCouponList.add(coupon)
            }else{
                availableCouponList.add(coupon)
            }
        }
        if(featuredCouponList.size!=0){
            binding.clFeaturedCouponsContainer.showView()
            binding.rvFeaturedCoupons.showView()
            binding.rvSearchFeaturedCoupons.hideView()
            defaultFeaturedListAdapter.swapData(featuredCouponList)
        }else{
            //show empty View
            binding.clFeaturedCouponsContainer.hideView()
        }
        if(availableCouponList.size!=0){
            binding.clCouponsContainer.showView()
            binding.rvDefaultCoupons.showView()
            binding.rvSearchCoupons.hideView()
            defaultListAdapter.swapData(availableCouponList)
        }else{
            binding.clCouponsContainer.hideView()
        }

        Timber.d("onDefaultCouponReceived++++"+availableCouponList)

        Timber.d("onDefaultCouponReceived____"+featuredCouponList)

        Timber.d("onDefaultCouponReceived>>>>>"+binding.rvDefaultCoupons.isVisible())
        Timber.d("onDefaultCouponReceived>>>>>"+binding.rvFeaturedCoupons.isVisible())

    }

    private fun onSearchCouponReceived(couponList: List<AvailableCouponDto.Data.CouponsData>) {

        if(!featuredSearchCouponList.isEmpty()){
            featuredSearchCouponList.clear()
        }
        if(!availableSearchCouponList.isEmpty()){
            availableSearchCouponList.clear()
        }
        Timber.d("onSearchCouponReceived  : ${couponList}")

        for (coupon in couponList) {
            if(coupon.isFeature==1){
                featuredSearchCouponList.add(coupon)
            }else{
                availableSearchCouponList.add(coupon)
            }
        }
        if(featuredSearchCouponList.size!=0){
            binding.clFeaturedCouponsContainer.showView()
            searchFeaturedListAdapter.swapData(featuredSearchCouponList)
        }else{
            //show empty View
            binding.clFeaturedCouponsContainer.hideView()
        }
        if(availableSearchCouponList.size!=0){
            binding.clCouponsContainer.showView()
            searchListAdapter.swapData(availableSearchCouponList)
        }else{
            binding.clCouponsContainer.hideView()
        }
    }

    override fun onCouponClicked(position: Int, item: AvailableCouponDto.Data.CouponsData) {
        openCouponDetail(item)
    }
    private fun openCouponDetail(item: AvailableCouponDto.Data.CouponsData) {
        val coupon = CouponDetailData()
        coupon.id=item.id
        coupon.couponClassification=item.couponClassification.toString()
        coupon.isUnlockable=item.isUnlockable.toString()
        coupon.description=item.description
        coupon.displayName=item.displayName
        coupon.discountPercent=item.discountPercent.toString()
        coupon.featuredImage=item.featuredImage?:""
        coupon.isFeature=item.isFeature
        coupon.isExpiringSoon=item.isExpiringSoon
        coupon.maxDiscountValue=item.maxDiscountValue.toString()
        coupon.pointsToUnlock=item.pointsToUnlock.toString()
        coupon.termsAndConditions=item.termsAndConditions
        coupon.remaningPoints=item.remaningPoints.toString()
        coupon.threshold=item.threshold.toString()

        couponDetailBottomSheet =  CouponDetailFragment(coupon,this)
        if(!couponDetailBottomSheet.isVisible)
            couponDetailBottomSheet.show(childFragmentManager.beginTransaction(), couponDetailBottomSheet.getTag())
    }
    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btnFilter -> {
                Timber.d("selectedCategoriesList  maxPrice"+maxPrice)
                val fragment = CouponFilterFragment(
                    maxPrice,
                    minPrice,
                    userSelectedMin,
                    userSelectedMax,this
                )
                if(!fragment.isVisible)
                    fragment.show(parentFragmentManager, ContentValues.TAG)
            }
            R.id.btnClearFilter->{
                viewModel.onClearAllFiltersButtonClicked(requireContext())

                userSelectedMax =Prefs.getInt("maxPrice")
                userSelectedMin =0
                viewModel.userSelectedMaxRange=userSelectedMax
                viewModel.userSelectedMinRange=userSelectedMin
                //resetToInitialState()
            }
            R.id.btnClearSearch->{
                binding.etSearch.editableText.clear()
                binding.etSearch.clearFocus()
            }
        }
    }

    override fun onUnlockCouponClicked(id: String) {
        //Unlock Coupon
        viewModel.unlockCoupon(id)
    }

    override fun onRedeemCouponClicked(couponTransactionId: String) {
            viewModel.redeemCoupon(couponTransactionId)
    }

    override fun onGiftCouponClicked(couponTransactionId: String) {
        //Open Gifting BS
        giftCouponBSFragment = GiftCouponBSFragment(this,couponTransactionId)
        if(!giftCouponBSFragment.isVisible)
            giftCouponBSFragment.show(parentFragmentManager, ContentValues.TAG)
    }

    override fun onCouponDetailSheetCancelled() {
        //refresh when unlocked a coupon
        if(couponUnlocked){
            if(areFiltersApplied || binding.etSearch.editableText.toString().trim().isNotEmpty()){
                // search or filter
                resetSearchResultsListParams()
                viewModel.searchAndFilterCouponsList(binding.etSearch.editableText.toString().trim(),currentPageSearchAndFilterList, myContext)
            }else {
                //default
                resetDefaultListParams()
                getDefaultCoupons()
            }
            getPoints()
            couponUnlocked=false
        }
    }

    override fun onGiftViaEmail(email: String, couponTransactionId: String) {
        viewModel.transferCouponEmail(couponTransactionId,email=email)
    }

    override fun onGiftViaMobile(mobile: String, countryCode: String, couponTransactionId: String) {
        viewModel.transferCouponMobile(mobile,countryCode,couponTransactionId)

    }

    override fun onClearAllFilterChips() {
    }

    override fun onCategoryFilterChipClicked(item: CouponTypeData, position: Int) {
    }

    override fun onCategoryFilterTileClicked(item: CouponTypeData, position: Int) {

    }

    override fun onRangeChangeListener(maxPrice: Int, minPrice: Int) {
        if (viewModel.isPriceRangeSelected) {

            userSelectedMax = maxPrice
            userSelectedMin = minPrice
        } else {
            userSelectedMax = this.maxPrice
            userSelectedMin = this.minPrice
        }
        viewModel.userSelectedMaxRange=userSelectedMax
        viewModel.userSelectedMinRange=userSelectedMin
    }
    override fun onClearFilterSubmission(maxPrice: Int, minPrice: Int) {
        userSelectedMax =maxPrice
        userSelectedMin =minPrice
        viewModel.userSelectedMaxRange=userSelectedMax
        viewModel.userSelectedMinRange=userSelectedMin

    }
}