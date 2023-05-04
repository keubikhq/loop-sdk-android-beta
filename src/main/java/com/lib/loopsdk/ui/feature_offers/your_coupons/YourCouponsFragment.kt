package com.lib.loopsdk.ui.feature_offers.your_coupons

import android.content.ContentValues
import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.reflect.TypeToken
import com.lib.loopsdk.R
import com.lib.loopsdk.core.customViews.LoadingDialog
import com.lib.loopsdk.core.util.Colors
import com.lib.loopsdk.core.util.Constants
import com.lib.loopsdk.core.util.FullScreenNetworkErrorActivity
import com.lib.loopsdk.core.util.Prefs
import com.lib.loopsdk.core.util.UIState
import com.lib.loopsdk.core.util.Utils
import com.lib.loopsdk.core.util.hideView
import com.lib.loopsdk.core.util.observeInLifecycle
import com.lib.loopsdk.core.util.showView
import com.lib.loopsdk.data.remote.dto.CouponDetailData
import com.lib.loopsdk.data.remote.dto.CouponHistoryDto
import com.lib.loopsdk.data.remote.dto.InitializeDto
import com.lib.loopsdk.data.remote.dto.RedeemCouponDto
import com.lib.loopsdk.data.remote.dto.TransferCouponDto
import com.lib.loopsdk.databinding.FragmentYourCouponsBinding
import com.lib.loopsdk.ui.feature_offers.coupon_detail.CouponDetailFragment
import com.lib.loopsdk.ui.feature_offers.coupon_detail.GiftCouponBSFragment
import kotlinx.coroutines.flow.onEach
import timber.log.Timber


class YourCouponsFragment : Fragment(),View.OnClickListener,SortCouponFragment.SortByListener,
    YourCouponsAdapter.Interaction, CouponDetailFragment.CouponInteractionListener,
    GiftCouponBSFragment.GiftCouponListener{
    private lateinit var binding: FragmentYourCouponsBinding
    private val viewModel: YourCouponsViewModel by viewModels()
    private lateinit var myContext: Context
    private var currentPage = 1
    private var totalPages = 1
    private var isScrollDataLoading = false
    private var isFirstTimeGetFocused= true
    private lateinit var yourCouponsAdapter: YourCouponsAdapter
    private lateinit var couponDetailBottomSheet: CouponDetailFragment
    private lateinit var giftCouponBSFragment: GiftCouponBSFragment
    private var primaryColor:String=""
    private var secondaryColor:String=""
    private var couponRedeemed = false
    private lateinit var couponsList: ArrayList<CouponHistoryDto.Data.CouponsData>

    private val loadingDialog: LoadingDialog by lazy { LoadingDialog(requireContext()) }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        myContext = context
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_your_coupons, container,false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initVariables()
        setupAdapters()
        registerScrollListener()
        setUpListener()
        resetListParams()
        setUpViewModel()
    }
    private fun initVariables(){
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = this.viewModel
        binding.brandTheme = Prefs.getNonPrimitiveData<InitializeDto.Data.BrandTheme>(object: TypeToken<InitializeDto.Data.BrandTheme>(){}.type, "BRAND_THEME")
        binding.brandThemeColors = Colors
        this.couponsList= arrayListOf()

        binding.etSearch.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (s!!.isNotEmpty()) {
                    binding.btnClearSearch.showView()
                }else{
                    binding.btnClearSearch.hideView()
                }
                getSelectedCouponList()
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
    private fun setUpViewModel(){
        viewModel.historyUiStateFlow.onEach { it ->
            when (it){
                is UIState.Loading ->{
//                    loadingDialog.show()
                    Timber.d("Loading state")
                }
                is UIState.Success<*> ->{
//                    Handler().postDelayed({
//                        loadingDialog.cancel()
//                    }, 500)

                    when(it.data){
                        is CouponHistoryDto.Data ->{
                            isScrollDataLoading = false
                            totalPages = it.data.totalPages
                            onCouponListReceived(it.data.couponsData)
                        }
                    }
                }
                is UIState.Error ->{
//                    loadingDialog.cancel()
                    try {
                        val errorCode = it.message.toInt()
                        when (errorCode) {
                            401 -> {
                                // CommonDialogUtils.showSingleButtonLogoutDialog(requireActivity(), "Session Expired!", null)
                            }
                            422 -> {
//                                binding.clEmptyState.showView()
//                                binding.rvAvailableTask.hideView()
                            }
                        }

                    }catch (e: Exception){
                        if(it.message.contains("NetworkError")){
                            FullScreenNetworkErrorActivity.startActivity(this, myContext)
                        }
                    }

                }
                else -> {
//                    loadingDialog.cancel()
                }

            }
        }.observeInLifecycle(viewLifecycleOwner)
        viewModel.unlockingInHistoryUiStateFlow.onEach { it ->
            when (it){
                is UIState.Loading ->{
                    loadingDialog.show()
                    Timber.d("Loading state")
                }
                is UIState.Success<*> ->{
                    Handler().postDelayed({
                        loadingDialog.cancel()
                    }, 500)
                    when(it.data){
                        is RedeemCouponDto.Data->{
                            couponRedeemed=true
                            if(::couponDetailBottomSheet.isInitialized && couponDetailBottomSheet.isVisible) {
                                couponDetailBottomSheet.showCodeDetailAfterRedeem(it.data)
                            }
                        }
                        is TransferCouponDto.Data->{
                            couponRedeemed=true
                            if(::giftCouponBSFragment.isInitialized && giftCouponBSFragment.isVisible) {
                                giftCouponBSFragment.showDetailAfterTransfer(it.data)
                            }
                        }
                    }
                }

                is UIState.Error ->{
                    loadingDialog.cancel()
//                    CommonDialogUtils.showSingleButtonAlertDialog(myContext, it.message)
                    Timber.d("Available task fragment ${it.message}")
                    try {
                        val errorCode = it.message.toInt()
                        when (errorCode) {
                            401 -> {
                                // CommonDialogUtils.showSingleButtonLogoutDialog(requireActivity(), "Session Expired!", null)
                            }
                            422 -> {
//                                binding.clEmptyState.showView()
//                                binding.rvAvailableTask.hideView()
                            }
                        }

                    }catch (e: Exception){
                        if(it.message.contains("NetworkError")){
                            FullScreenNetworkErrorActivity.startActivity(this, myContext)
                        }
                    }
                }
                else->{
//                    loadingDialog.cancel()
                }
            }
        }.observeInLifecycle(viewLifecycleOwner)
    }

    override fun onResume() {

        super.onResume()
        //default sellected tab
        resetListParams()
        Prefs.putString(Constants.COUPON_TYPE_SELECTED, Constants.CouponType.UNLOCKED.title)
        updateSelectedTabUI(isUnlocked = true)
        getSelectedCouponList()
    }
    override fun onStop() {
        super.onStop()
    }
    private fun getSelectedCouponList(){
        val selectedTab=Prefs.getString(Constants.COUPON_TYPE_SELECTED)
        if(selectedTab.equals(Constants.CouponType.UNLOCKED.title)) {
            binding.tvDesc.text="Looks like you haven’t unlocked any coupons"
            getUnlockedCoupons(binding.etSearch.editableText.toString().trim())
        }else if(selectedTab.equals(Constants.CouponType.REDEEMED.title)){
            binding.tvDesc.text="Looks like you haven’t redeeed any coupons"
            getRedeemedCoupons(binding.etSearch.editableText.toString().trim())
        }else if(selectedTab.equals(Constants.CouponType.GIFTED.title)){
            binding.tvDesc.text="Looks like you haven’t gifted any coupons"
            getGiftedCoupons(binding.etSearch.editableText.toString().trim())
        }
    }
    private fun resetListParams(){
        binding.llDetail.alpha = 0f
        binding.llDetail.translationY = Utils.dpToPx(requireContext(), 10).toFloat()
        currentPage = 1
        totalPages = 1
        isScrollDataLoading = false
        this.couponsList.clear()
        yourCouponsAdapter.swapData(mutableListOf())
    }

    private fun registerScrollListener() {
        binding.rvCouponsList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!binding.rvCouponsList.canScrollVertically(1) &&
                    newState == RecyclerView.SCROLL_STATE_IDLE &&
                    !isScrollDataLoading &&
                    currentPage < totalPages
                ) {
                    currentPage++
                    isScrollDataLoading = true
                    getSelectedCouponList()
                }
            }
        })
    }

    private fun getUnlockedCoupons(param:String) {
        viewModel.getCouponUnlockedList(currentPage,param)
    }
    private fun getRedeemedCoupons(param:String) {
        viewModel.getCouponRedeemedList(currentPage,param)
    }
    private fun getGiftedCoupons(param:String) {
        viewModel.getCouponGiftedList(currentPage,param)
    }

    private fun setUpListener() {
        binding.btnClearAll.setOnClickListener(this)
        binding.btnFilter.setOnClickListener(this)
        binding.btnUnlocked.setOnClickListener(this)
        binding.btnUsed.setOnClickListener(this)
        binding.btnGifted.setOnClickListener(this)
        binding.btnClearSearch.setOnClickListener(this)
    }
    private fun setupAdapters() {
        yourCouponsAdapter=YourCouponsAdapter(this)
        binding.rvCouponsList.layoutManager = LinearLayoutManager(myContext, LinearLayoutManager.VERTICAL, false)
        binding.rvCouponsList.apply {
            setHasFixedSize(true)
            adapter = yourCouponsAdapter
        }
    }
    private fun onCouponListReceived(data: List<CouponHistoryDto.Data.CouponsData>) {
        // not in page 2 update list
        // add pagination
        if(currentPage == 1){
            Handler().postDelayed({
                binding.llDetail.animate()
                    .translationY(0f)
                    .alpha(1f)
                    .setDuration(750)
            },200)
        }
        updateFilterIconState()
        updateFilterChipUI()
        if(data.size!=0){
            binding.clEmptyState.hideView()
            binding.rvCouponsList.showView()
            this.couponsList.addAll(data)
            yourCouponsAdapter.swapData(couponsList.toMutableList())
        }else
        {
            binding.clEmptyState.showView()
            binding.rvCouponsList.hideView()
        }

        //goTo unlocked detail if redirected from games
        goToUnlockedCouponDetail(data)
    }
    private fun goToUnlockedCouponDetail(data: List<CouponHistoryDto.Data.CouponsData>)
    {
        val selectedTab = Prefs.getString(Constants.COUPON_TYPE_SELECTED)
        val redirectedFrom = Prefs.getString(Constants.REDIRECT_TO_UNLOCK)?:""
        if (selectedTab.equals(Constants.CouponType.UNLOCKED.title)) {
            if (redirectedFrom.isNotEmpty()) {
                Handler().postDelayed({
                    onCouponClicked(0,data.get(0))
                    Prefs.putString(Constants.REDIRECT_TO_UNLOCK, "")
                },500)
            }
        }
    }

    fun updateFilterChipUI() {
        val sortType=Prefs.getString(Constants.COUPON_SORT_TYPE)?:""
        if(sortType.isNotEmpty()){
            if(!sortType.equals(Constants.COUPONSORTType.EXPIRED.title)&& !sortType.equals(Constants.COUPONSORTType.EXPIRING_SOON.title)
                && !sortType.equals(Constants.COUPONSORTType.CODE_VIEWED.title)){
                binding.tvFilterChip.text=Prefs.getString(Constants.COUPON_SORT_TYPE)+" first"
            }else {
                binding.tvFilterChip.text=Prefs.getString(Constants.COUPON_SORT_TYPE)
            }
            binding.llFilterChip.showView()
        }else{
            binding.llFilterChip.hideView()
        }
    }
    private fun updateFilterIconState() {
        val sortType=Prefs.getString(Constants.COUPON_SORT_TYPE)?:""
        if(sortType.isNotEmpty()){
            //filter Applied
            binding.btnFilter.setCardBackgroundColor(Color.parseColor(binding.brandTheme!!.themeColors.primary.hexCode))
            binding.ivFilter.setColorFilter(Color.parseColor(binding.brandTheme!!.themeColors.fontIconColor.hexCode), PorterDuff.Mode.SRC_IN)
        }else{
            //default
            binding.btnFilter.setCardBackgroundColor(ContextCompat.getColor(myContext,R.color.white))
            binding.btnFilter.strokeWidth = Utils.dpToPx(myContext, 1)
            binding.btnFilter.strokeColor = Color.parseColor(binding.brandTheme!!.themeColors.primary.hexCode)
            binding.ivFilter.setColorFilter(Color.parseColor(binding.brandTheme!!.themeColors.primary.hexCode), PorterDuff.Mode.SRC_IN)
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btnFilter -> {
                val fragment = SortCouponFragment(this)
                val args = Bundle()
                args.putString("name", Prefs.getString(Constants.COUPON_TYPE_SELECTED))
                fragment.arguments = args
                if(!fragment.isVisible)
                    fragment.show(parentFragmentManager, ContentValues.TAG)
            }
            R.id.btnUnlocked->{
                Prefs.putString(Constants.COUPON_TYPE_SELECTED, Constants.CouponType.UNLOCKED.title)
                updateSelectedTabUI(isUnlocked = true)
                resetListParams()
                getSelectedCouponList()
            }
            R.id.btnUsed->{
                Prefs.putString(Constants.COUPON_TYPE_SELECTED,Constants.CouponType.REDEEMED.title)
                updateSelectedTabUI(isUsed = true)
                resetListParams()
                getSelectedCouponList()
            }
            R.id.btnGifted->{
                Prefs.putString(Constants.COUPON_TYPE_SELECTED, Constants.CouponType.GIFTED.title)
                updateSelectedTabUI(isGifted = true)
                resetListParams()
                getSelectedCouponList()
            }
            R.id.btnClearAll->{
                Prefs.putString(Constants.COUPON_SORT_TYPE,"")
                clearAllFilter()
            }
            R.id.btnClearSearch->{
                binding.etSearch.editableText.clear()
                binding.etSearch.clearFocus()
            }
        }
    }
    private fun updateSelectedTabUI(
        isUnlocked: Boolean = false,
        isUsed: Boolean = false, isGifted: Boolean = false,
    ){
         primaryColor=binding.brandTheme!!.themeColors.primary.hexCode
         secondaryColor=binding.brandTheme!!.themeColors.secondary.hexCode
        if(isUnlocked){
            binding.btnUnlocked.setCardBackgroundColor(Color.parseColor(secondaryColor))
            binding.tvUnlocked.setTextColor(Color.parseColor(primaryColor))
            binding.ivUnlocked.setColorFilter(Color.parseColor(primaryColor), PorterDuff.Mode.SRC_IN)
            Prefs.putString(Constants.COUPON_SORT_TYPE,"")
            binding.etSearch.text!!.clear()
        }else{
            binding.btnUnlocked.setCardBackgroundColor(ContextCompat.getColor(myContext,R.color.bg_gray))
            binding.tvUnlocked.setTextColor(ContextCompat.getColor(binding.root.context,R.color.text_dark_gray))
            binding.ivUnlocked.setColorFilter(ContextCompat.getColor(binding.root.context,R.color.text_dark_gray), PorterDuff.Mode.SRC_IN)
        }
        if(isUsed){
            binding.btnUsed.setCardBackgroundColor(Color.parseColor(secondaryColor))
            binding.tvUsed.setTextColor(Color.parseColor(primaryColor))
            binding.ivUsed.setColorFilter(Color.parseColor(primaryColor), PorterDuff.Mode.SRC_IN)
            Prefs.putString(Constants.COUPON_SORT_TYPE,"")
            binding.etSearch.text!!.clear()
        }else{
            binding.btnUsed.setCardBackgroundColor(ContextCompat.getColor(myContext,R.color.bg_gray))
            binding.tvUsed.setTextColor(ContextCompat.getColor(binding.root.context,R.color.text_dark_gray))
            binding.ivUsed.setColorFilter(ContextCompat.getColor(binding.root.context,R.color.text_dark_gray), PorterDuff.Mode.SRC_IN)
        }
        if(isGifted){
            binding.btnGifted.setCardBackgroundColor(Color.parseColor(secondaryColor))
            binding.tvGifted.setTextColor(Color.parseColor(primaryColor))
            binding.ivGifted.setColorFilter(Color.parseColor(primaryColor), PorterDuff.Mode.SRC_IN)
            Prefs.putString(Constants.COUPON_SORT_TYPE,"")
            binding.etSearch.text!!.clear()
        }else{
            binding.btnGifted.setCardBackgroundColor(ContextCompat.getColor(myContext,R.color.bg_gray))
            binding.tvGifted.setTextColor(ContextCompat.getColor(binding.root.context,R.color.text_dark_gray))
            binding.ivGifted.setColorFilter(ContextCompat.getColor(binding.root.context,R.color.text_dark_gray), PorterDuff.Mode.SRC_IN)
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        Prefs.putString(Constants.COUPON_SORT_TYPE,"")
        Prefs.putString(Constants.COUPON_TYPE_SELECTED,"")
    }


    override fun onCouponClicked(position: Int, item: CouponHistoryDto.Data.CouponsData) {
        val coupon = CouponDetailData()
        coupon.transactionId=item.transactionId
        coupon.couponClassification=item.couponClassification.toString()
        coupon.description=item.description
        coupon.displayName=item.displayName
        coupon.termsAndConditions= item.termsAndConditions
        coupon.couponStatus= item.couponStatus.toString()
        coupon.couponCodeType=item.couponCodeType.toString()
        coupon.couponCode=item.couponCode?:""
        coupon.label=item.label
        coupon.unlockedOn=item.unlockedOn?:""
        coupon.giftedOn=item.giftedOn?:""
        coupon.expiredOn=item.expiredOn?:""
        coupon.usedOn=item.usedOn?:""
        coupon.isExpired= item.isExpired?:""
        coupon.isExpiringSoon=item.isExpiringSoon
        coupon.discountPercent=item.discountPercent.toString()
        coupon.maxDiscountValue=item.maxDiscountValue.toString()
        coupon.threshold=item.threshold.toString()
        coupon.isTransferable=item.isTransferable
        coupon.unlockOrigin=item.unlockOrigin

        couponDetailBottomSheet =  CouponDetailFragment(coupon,this)
        if(!couponDetailBottomSheet.isVisible)
            couponDetailBottomSheet.show(childFragmentManager.beginTransaction(), couponDetailBottomSheet.getTag())
    }

    override fun onUnlockCouponClicked(id: String) {
        // Do Nothing
    }

    override fun onRedeemCouponClicked(couponTransactionId: String) {
        viewModel.redeemCoupon(couponTransactionId)
    }
    override fun onGiftCouponClicked(couponTransactionId: String) {
        //Open Gifting BS
        giftCouponBSFragment = GiftCouponBSFragment(this, couponTransactionId)
        if(!giftCouponBSFragment.isVisible)
            giftCouponBSFragment.show(parentFragmentManager, ContentValues.TAG)
    }
    override fun onCouponDetailSheetCancelled() {
        if(couponRedeemed) {
            val selectedTab = Prefs.getString(Constants.COUPON_TYPE_SELECTED)
            if (selectedTab.equals(Constants.CouponType.UNLOCKED.title)) {
                this.onResume()
                couponRedeemed=false
            }
        }
    }

    override fun onGiftViaEmail(email: String, couponTransactionId: String) {
        //Call API
        viewModel.transferCouponEmail(couponTransactionId,email)
    }

    override fun onGiftViaMobile(mobile: String, countryCode: String, couponTransactionId: String) {
        //Call API
        viewModel.transferCouponMobile(mobile,countryCode,couponTransactionId)
    }

    override fun onClearFilterClicked() {
        clearAllFilter()
    }

    override fun onApplyFilterClicked() {
        getSelectedCouponList()
        updateFilterChipUI()
    }
    private fun clearAllFilter(){
        getSelectedCouponList()
        updateFilterChipUI()
    }

}