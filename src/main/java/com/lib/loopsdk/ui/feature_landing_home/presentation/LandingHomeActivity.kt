package com.lib.loopsdk.ui.feature_landing_home.presentation

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.gson.reflect.TypeToken
import com.lib.loopsdk.BuildConfig
import com.lib.loopsdk.R
import com.lib.loopsdk.core.customViews.LoadingDialog
import com.lib.loopsdk.core.util.*
import com.lib.loopsdk.core.util.Constants.init
import com.lib.loopsdk.data.remote.dto.CouponDetailData
import com.lib.loopsdk.data.remote.dto.EarnZoneRedeemDto
import com.lib.loopsdk.data.remote.dto.EarnZoneTaskData
import com.lib.loopsdk.data.remote.dto.GetHomeScreenDetailsDto
import com.lib.loopsdk.data.remote.dto.InitializeDto
import com.lib.loopsdk.data.remote.dto.RedeemCouponDto
import com.lib.loopsdk.data.remote.dto.TransferCouponDto
import com.lib.loopsdk.data.remote.dto.UnlockCouponDto
import com.lib.loopsdk.databinding.ActivityLandingHomeBinding
import com.lib.loopsdk.ui.BaseActivity
import com.lib.loopsdk.ui.EarnZoneTypeBottmSheetFragment
import com.lib.loopsdk.ui.feature_offers.OffersActivity
import com.lib.loopsdk.ui.feature_offers.coupon_detail.CouponDetailFragment
import com.lib.loopsdk.ui.feature_offers.coupon_detail.GiftCouponBSFragment
import com.lib.loopsdk.ui.feature_earn.EarnZoneActivity
import com.lib.loopsdk.ui.feature_earn.EarnZoneRedeemDialogFragment
import com.lib.loopsdk.ui.feature_scratch_card.ScratchCardsListHomeActivity
import com.lib.loopsdk.ui.feature_spin_the_wheel.spin_wheel_list.SpinWheelListActivity
import com.lib.loopsdk.ui.feature_survey.presentation.quizzlet_list.QuizzletActivity
import com.lib.loopsdk.ui.feature_tier_details_points_wallet.presentation.TierDetailsPointsWalletActivity
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import java.lang.Exception

class LandingHomeActivity : BaseActivity(),
    FeaturedCouponsListAdapter.Interaction,
    NonFeaturedCouponsListAdapter.Interaction,
    GamesListAdapter.Interaction,
    EarnZoneAvailableTasksListAdapter.Interaction,
    CouponDetailFragment.CouponInteractionListener, GiftCouponBSFragment.GiftCouponListener,
    EarnZoneTypeBottmSheetFragment.TaskCompleteListener{

    private var isReferralBlocked: Int? = 0
    private lateinit var viewModel: LandingHomeViewModel
    private lateinit var binding: ActivityLandingHomeBinding
    private lateinit var gameListAdapter: GamesListAdapter
    private lateinit var featuredCouponsListAdapter: FeaturedCouponsListAdapter
    private lateinit var nonFeaturedCouponsListAdapter: NonFeaturedCouponsListAdapter
    private lateinit var earnZoneAvailableTasksListAdapter: EarnZoneAvailableTasksListAdapter
    private lateinit var couponDetailBottomSheet: CouponDetailFragment
    private lateinit var giftCouponBSFragment: GiftCouponBSFragment
    private val loadingDialog: LoadingDialog by lazy { LoadingDialog(this) }
    private var couponUnlocked = false
    private var customerReferralUrl: String = ""
    private var referralMsg: String = ""
    private var referralCode: String = ""

    override fun onResume() {
        toggleSandboxUi()
        resetRecyclers()
        binding.llHeader.alpha = 0f
        binding.llHeader.translationY = Utils.dpToPx(this, 10).toFloat()
        binding.pointsContainer.alpha = 0f
        binding.pointsContainer.translationY = Utils.dpToPx(this, 10).toFloat()
        binding.flCoupons.alpha = 0f
        binding.flCoupons.translationY = Utils.dpToPx(this, 10).toFloat()
        binding.flGameArena.alpha = 0f
        binding.flGameArena.translationY = Utils.dpToPx(this, 10).toFloat()
        binding.matCvInviteEarn.alpha = 0f
        binding.matCvInviteEarn.translationY = Utils.dpToPx(this, 10).toFloat()
        binding.llShareInviteLarge.hideView()
        binding.llShareInviteLarge.alpha = 0f
        binding.llShareInviteLarge.translationY = Utils.dpToPx(this, 10).toFloat()
        viewModel.getHomeScreenData()
        super.onResume()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    private fun resetRecyclers() {
        featuredCouponsListAdapter.clearAllData()
        nonFeaturedCouponsListAdapter.submitList(arrayListOf())
        gameListAdapter.clearAllData()
        earnZoneAvailableTasksListAdapter.clearAllData()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_landing_home)
        binding.lifecycleOwner = this
        binding.brandTheme = Prefs.getNonPrimitiveData<InitializeDto.Data.BrandTheme>(object: TypeToken<InitializeDto.Data.BrandTheme>(){}.type, "BRAND_THEME")
        binding.brandThemeColors = Colors
        viewModel = ViewModelProvider(this)[LandingHomeViewModel::class.java]
        setupAdapters()
        setUpUnlockCouponViewModel()
        binding.tvLoyaltyName.text = init.brandTheme.loyaltyProgramName
        binding.tvMyLoyaltyPointsText.text = "My Loyalty ${init.pointsIdentifier.pointsLabelPlural}"
        binding.tvGameArenaSubText.text = "play games to win ${init.pointsIdentifier.pointsLabelPlural}, coupons & rewards"

        binding.mainScrollView.setOnScrollChangeListener { view, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (scrollY > (binding.pointsContainer.y + 200) ){
                binding.stickyView.showView()
            }else{
                binding.stickyView.hideView()
            }
        }


        binding.btnViewAllTasks.setOnClickListener {
            redirectToEarnZoneModule()
        }

        binding.btnShareInvite.setOnClickListener {
            with(InviteEarnBSFragment()){
                show(supportFragmentManager, "InviteEarnBS")
            }
        }

        binding.btnCloseSandbox.setOnClickListener {
            binding.btnSandbox.hideView()
        }

        binding.btnPointsWallet.setOnClickListener {
//            Intent(this, TierDetailsPointsWalletActivity::class.java).apply {
//                startActivity(this)
//            }
            TierDetailsPointsWalletActivity.startActivity(this,2)
        }

        binding.btnPointsWalletSticky.setOnClickListener {
//            Intent(this, TierDetailsPointsWalletActivity::class.java).apply {
//                startActivity(this)
//            }
            TierDetailsPointsWalletActivity.startActivity(this,2)
        }

        binding.btnViewCoupons.setOnClickListener {
           redirectToOffersModule()
        }

        binding.btnClose.setOnClickListener {
            onBackPressed()
        }

        binding.btnCloseSticky.setOnClickListener {
            onBackPressed()
        }

        viewModel.uiStateFlow.onEach { uiState ->
            when (uiState) {
                is UIState.Loading -> {
                    loadingDialog.show()
                }
                is UIState.Success<*> -> {
                    when (uiState.data) {
                        is GetHomeScreenDetailsDto.Data -> {
                            /*Handler().postDelayed({
                                loadingDialog.cancel()
                            },1000)*/
                            loadingDialog.cancel()
                            onHomeScreenDataReceived(uiState.data)
                            handleRedirect()
                        }

                    }
                }
                is UIState.Error -> {
                    loadingDialog.cancel()
                    try {
                        val errorCode = uiState.message.toInt()
                        when (errorCode) {
                            401 -> {
                                // CommonDialogUtils.showSingleButtonLogoutDialog(requireActivity(), "Session Expired!", null)
                            }
                            422 -> {

                            }
                        }
                    }catch (e: Exception){
                        if(uiState.message == "NetworkError"){
                            FullScreenNetworkErrorActivity.startActivity(this)
                        }
                    }
                }

                else -> {
                    loadingDialog.cancel()
                }
            }

        }.observeInLifecycle(this)


    }

    private fun toggleSandboxUi() {
        if(BuildConfig.DEBUG) binding.btnSandbox.showView()
        else binding.btnSandbox.hideView()
    }

    private fun handleRedirect() {
        if (intent?.getStringExtra("redirectTo").isNullOrEmpty()) return
        else {
            val redirectTo = intent?.getStringExtra("redirectTo")
            when {
                redirectTo!!.contains("game", true) -> {
                    binding.mainScrollView.postDelayed({
                        binding.mainScrollView.smoothScrollTo(
                            0,
                            binding.flGameArena.y.toInt()
                        )
                    }, 100)
                }

                else -> {

                }

            }
        }
        intent?.removeExtra("redirectTo")
    }

    private fun onHomeScreenDataReceived(data: GetHomeScreenDetailsDto.Data) {
        isReferralBlocked = data.referralData.isBlockedReferral
        binding.llHeader.animate()
            .translationY(0f)
            .alpha(1f)
            .setDuration(750)

        Handler().postDelayed({
            binding.pointsContainer.animate()
                .translationY(0f)
                .alpha(1f)
                .setDuration(750)
        },100)

        binding.tvCurrentTier.text = data.tierData.tierName?:""
        binding.tvPointsBalance.text = getFormattedPoints(data.totalPoints)
        binding.tvPointsBalanceSticky.text = getFormattedPoints(data.totalPoints)
        Prefs.putString(Constants.USER_BALANCE, data.totalPoints)
        val gamesList = arrayListOf<Game>()
        if (data.spinwheel.isEnabled == 1 || data.spinwheel.isUnlockSpinEnabled == 1) {
            gamesList.add(
                Game(
                    "Wheel of Fortune",
                    data.spinwheel.isEnabled,
                    data.spinwheel.isUnlockSpinEnabled,
                    data.spinwheel.pointsStartsAt
                )
            )
        }
        if (data.scratchcard.isEnabled == 1 || data.scratchcard.isUnlockScratchEnabled == 1) {
            gamesList.add(
                Game(
                    "Scratch Card",
                    data.scratchcard.isEnabled,
                    data.scratchcard.isUnlockScratchEnabled,
                    data.scratchcard.pointsStartsAt
                )
            )
        }
        if (data.quizlets.isEnabled == 1){
            gamesList.add(
                Game(
                    "Quizlet",
                    data.quizlets.isEnabled,
                    0,
                    data.quizlets.pointsStartsAt,
                    data.quizlets.resume
                )
            )
        }

        if(isCouponsDataAvailable(data) && !isGamesDataAvailable(gamesList) && !isEarnZoneDataAvailable(data) && !isInviteDataAvailable()){
            binding.flCoupons.animate()
                .translationY(0f)
                .alpha(1f)
                .setDuration(750)
            binding.flGameArena.hideView()
            binding.flEarnZone.hideView()
            binding.matCvInviteEarn.hideView()
            binding.flAllEmpty.hideView()
            showCouponsData(data)
        }else if (!isCouponsDataAvailable(data) && isGamesDataAvailable(gamesList) && !isInviteDataAvailable() && !isEarnZoneDataAvailable(data)){
            binding.flCoupons.hideView()
            binding.flGameArena.animate()
                .translationY(0f)
                .alpha(1f)
                .setDuration(750)
            binding.flEarnZone.hideView()
            binding.flAllEmpty.hideView()
            binding.matCvInviteEarn.hideView()
            showGamesData(gamesList)
        }else if (!isCouponsDataAvailable(data) && !isGamesDataAvailable(gamesList) && isEarnZoneDataAvailable(data) && !isInviteDataAvailable()){
            binding.flCoupons.hideView()
            binding.flGameArena.hideView()
            binding.flEarnZone.animate()
                .translationY(0f)
                .alpha(1f)
                .setDuration(750)
            binding.flAllEmpty.hideView()
            binding.matCvInviteEarn.hideView()
        }else if (!isCouponsDataAvailable(data) && !isGamesDataAvailable(gamesList) && !isEarnZoneDataAvailable(data) && isInviteDataAvailable()){
            binding.flCoupons.hideView()
            binding.flGameArena.hideView()
            binding.flAllEmpty.hideView()
            binding.flEarnZone.hideView()
            binding.matCvInviteEarn.hideView()
            /*binding.matCvInviteEarn.animate()
                .translationY(0f)
                .alpha(1f)
                .setDuration(750)*/
            showInviteEarnLarge(data)
        }else if (!isCouponsDataAvailable(data) && !isGamesDataAvailable(gamesList) && !isEarnZoneDataAvailable(data) && !isInviteDataAvailable()){
            binding.flCoupons.hideView()
            binding.flGameArena.hideView()
            binding.flEarnZone.hideView()
            binding.matCvInviteEarn.hideView()
            binding.flAllEmpty.showView()
        }else {
            binding.flAllEmpty.hideView()
            Handler().postDelayed({
                binding.flCoupons.animate()
                    .translationY(0f)
                    .alpha(1f)
                    .setDuration(750)
            },200)
            Handler().postDelayed({
                binding.flGameArena.animate()
                    .translationY(0f)
                    .alpha(1f)
                    .setDuration(750)
            },300)
            Handler().postDelayed({
                binding.flEarnZone.animate()
                    .translationY(0f)
                    .alpha(1f)
                    .setDuration(750)
            },400)
            Handler().postDelayed({
                binding.matCvInviteEarn.animate()
                    .translationY(0f)
                    .alpha(1f)
                    .setDuration(750)
            },500)
            showCouponsData(data)
            showGamesData(gamesList)
            showEarnZoneData(data)
            showInviteEarnData(data)
        }

    }

    private fun showInviteEarnLarge(response: GetHomeScreenDetailsDto.Data){
        if (response.referralData == null) return
        else{
            binding.llShareInviteLarge.hideView()
            binding.llShareInviteLarge.animate()
                .translationY(0f)
                .alpha(1f)
                .setDuration(750)
            referralCode = response.referralData.code
            customerReferralUrl = response.referralData.link?:""
            binding.tvReferralCode.text = response.referralData.code
            if (response.referralData.benefitTypeReferrer == 1 && response.referralData.benefitTypeReferee == 1){
                binding.tvReferrerText.text = "you both get coupons"
                referralMsg = "Join the fun at ${Constants.init.brandTheme.loyaltyProgramName?:""}, use your friend's code $referralCode and get rewarded with a coupon."
            } else if (response.referralData.benefitTypeReferrer == 1 && response.referralData.benefitTypeReferee == 2) {
                binding.tvReferrerText.text = "you get a coupon"
                binding.tvRefereeText.showView()
                binding.tvRefereeText.text = "and they get"
                binding.ivPointsIconReferee.showView()
                binding.tvRefereePointsText.showView()
                binding.tvRefereePointsText.text = "${response.referralData.benefitValueReferee} ${if (response.referralData.benefitValueReferee > 1) Constants.init.pointsIdentifier.pointsLabelPlural else Constants.init.pointsIdentifier.pointsLabelSingular}"
                referralMsg = "Join the fun at ${Constants.init.brandTheme.loyaltyProgramName?:""}, use your friend's code $referralCode and get rewarded with ${response.referralData.benefitValueReferee} ${if (response.referralData.benefitValueReferee > 1) Constants.init.pointsIdentifier.pointsLabelPlural else Constants.init.pointsIdentifier.pointsLabelSingular}."
            } else if (response.referralData.benefitTypeReferrer == 2 && response.referralData.benefitTypeReferee == 1) {
                binding.tvReferrerText.text = "you get"
                binding.ivPointsIconReferrer.showView()
                binding.tvRefereeText.showView()
                binding.tvRefereeText.text = "${response.referralData.benefitValueReferrer} ${if (response.referralData.benefitValueReferrer > 1) Constants.init.pointsIdentifier.pointsLabelPlural else Constants.init.pointsIdentifier.pointsLabelSingular} and they get a coupon"
                binding.ivPointsIconReferee.hideView()
                binding.tvRefereePointsText.hideView()
                referralMsg = "Join the fun at ${Constants.init.brandTheme.loyaltyProgramName?:""}, use your friend's code $referralCode and get rewarded with a coupon."
            } else if (response.referralData.benefitTypeReferrer == 2 && response.referralData.benefitTypeReferee == 2) {
                if(response.referralData.benefitValueReferrer == response.referralData.benefitValueReferee){
                    binding.tvReferrerText.text = "you both get"
                    binding.ivPointsIconReferrer.showView()
                    binding.tvRefereeText.showView()
                    binding.ivPointsIconReferee.hideView()
                    binding.tvRefereePointsText.hideView()
                    binding.tvRefereeText.text = "${response.referralData.benefitValueReferrer} ${if (response.referralData.benefitValueReferrer > 1) Constants.init.pointsIdentifier.pointsLabelPlural else Constants.init.pointsIdentifier.pointsLabelSingular}"
                    referralMsg = "Join the fun at ${Constants.init.brandTheme.loyaltyProgramName?:""}, use your friend's code $referralCode and get rewarded with ${response.referralData.benefitValueReferee} ${if (response.referralData.benefitValueReferee > 1) Constants.init.pointsIdentifier.pointsLabelPlural else Constants.init.pointsIdentifier.pointsLabelSingular}."
                }else {
                    binding.tvReferrerText.text = "you get"
                    binding.ivPointsIconReferrer.showView()
                    binding.tvRefereeText.showView()
                    binding.tvRefereeText.text = "${response.referralData.benefitValueReferrer} ${if (response.referralData.benefitValueReferrer > 1) Constants.init.pointsIdentifier.pointsLabelPlural else Constants.init.pointsIdentifier.pointsLabelSingular} and they get"
                    binding.ivPointsIconReferee.showView()
                    binding.tvRefereePointsText.showView()
                    binding.tvRefereePointsText.text = "${response.referralData.benefitValueReferee} ${if (response.referralData.benefitValueReferee > 1) Constants.init.pointsIdentifier.pointsLabelPlural else Constants.init.pointsIdentifier.pointsLabelSingular}"
                    referralMsg = "Join the fun at ${Constants.init.brandTheme.loyaltyProgramName?:""}, use your friend's code $referralCode and get rewarded with ${response.referralData.benefitValueReferee} ${if (response.referralData.benefitValueReferee > 1) Constants.init.pointsIdentifier.pointsLabelPlural else Constants.init.pointsIdentifier.pointsLabelSingular}."
                }
            }
            referralMsg+="\n${customerReferralUrl?:""}"
        }
    }

    private fun showInviteEarnData(data: GetHomeScreenDetailsDto.Data) {
        if (!isInviteDataAvailable()) binding.matCvInviteEarn.hideView()
        else {
            binding.matCvInviteEarn.showView()
            if (data.referralData.benefitTypeReferrer == 1) {
                binding.tvInviteEarnMsg.text =
                    "Get a coupon everytime you invite a friend to try Loyalty."
            } else if (data.referralData.benefitTypeReferrer == 2) {
                binding.tvInviteEarnMsg.text =
                    "Get ${data.referralData.benefitValueReferrer} ${if (data.referralData.benefitValueReferrer > 1) init.pointsIdentifier.pointsLabelPlural else init.pointsIdentifier.pointsLabelSingular} everytime you invite a friend to try Loyalty."
            }
        }
    }

    private fun showGamesData(gamesList: ArrayList<Game>) {
        if (gamesList.isEmpty()) {
            binding.flGameArena.hideView()
        } else {
            binding.flGameArena.showView()
            gameListAdapter.addData(gamesList)
        }
    }

    private fun showCouponsData(data: GetHomeScreenDetailsDto.Data) {
        if (!data.coupons.isNullOrEmpty()) {
            binding.llNoCoupons.hideView()
            if (data.coupons.size > 1) binding.tvViewCoupons.text = "View All"
            else binding.tvViewCoupons.text = "View Coupons"
            if (data.isFeaturedCouponPresent == 1) {
                binding.rvCoupons.showView()
                binding.rvNonFeatCoupons.hideView()
                featuredCouponsListAdapter.addData(data.coupons)
            } else {
                binding.rvCoupons.hideView()
                binding.rvNonFeatCoupons.showView()
                nonFeaturedCouponsListAdapter.swapData(data.coupons)
            }
        } else {
            if (data.isUnlockedCouponsEnabled == 1) {
                binding.tvViewCoupons.text = "View Unlocked"
                binding.llNoCoupons.showView()
            }else{
                binding.flCoupons.hideView()
            }

        }
    }

    private fun showEarnZoneData(data: GetHomeScreenDetailsDto.Data) {
        if (!data.earnZoneTasks.isNullOrEmpty()) {
            binding.llNoTasks.hideView()
            if (data.earnZoneTasks.size > 1) binding.tvEarnZoneViewTaskText.text = "View All"
            else binding.tvEarnZoneViewTaskText.text = "View Tasks"
            earnZoneAvailableTasksListAdapter.addData(data.earnZoneTasks)
        } else {
            if (data.isEarnZoneTaskCompleted == 1) {
                binding.tvEarnZoneViewTaskText.text = "View Completed"
                binding.llNoTasks.showView()
            }else{
                binding.flEarnZone.hideView()
            }
        }
    }

    private fun isCouponsDataAvailable(data: GetHomeScreenDetailsDto.Data):Boolean{
        return !data.coupons.isNullOrEmpty() || data.isUnlockedCouponsEnabled == 1
    }

    private fun isEarnZoneDataAvailable(data: GetHomeScreenDetailsDto.Data):Boolean{
        return !data.earnZoneTasks.isNullOrEmpty() || data.isEarnZoneTaskCompleted == 0
    }

    private fun isGamesDataAvailable(gamesList: ArrayList<Game>):Boolean{
        return gamesList.isNotEmpty()
    }

    private fun isInviteDataAvailable():Boolean {
        return if(isReferralBlocked != null){
            init.isReferalAdded == 1 && isReferralBlocked == 0
        }else {
            init.isReferalAdded == 1
        }
    }

    private fun setupAdapters() {
        featuredCouponsListAdapter = FeaturedCouponsListAdapter(interaction = this)
        binding.rvCoupons.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvCoupons.adapter = featuredCouponsListAdapter
        (binding.rvCoupons?.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

        nonFeaturedCouponsListAdapter = NonFeaturedCouponsListAdapter(interaction = this)
        binding.rvNonFeatCoupons.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvNonFeatCoupons.adapter = nonFeaturedCouponsListAdapter
        (binding.rvNonFeatCoupons?.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

        gameListAdapter = GamesListAdapter(interaction = this)
        binding.rvGames.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvGames.adapter = gameListAdapter
        (binding.rvGames?.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

        earnZoneAvailableTasksListAdapter = EarnZoneAvailableTasksListAdapter(interaction = this)
        binding.rvEarnZoneTasks.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvEarnZoneTasks.adapter = earnZoneAvailableTasksListAdapter
        (binding.rvEarnZoneTasks?.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

    }

    override fun onFeaturedCouponClicked(
        position: Int,
        coupon: GetHomeScreenDetailsDto.Data.Coupon) {
        openCouponDetail(coupon)
    }

    override fun onNonFeaturedCouponClicked(
        position: Int,
        coupon: GetHomeScreenDetailsDto.Data.Coupon
    ) {
        openCouponDetail(coupon)
    }
    private fun redirectToOffersModule(){
        if(binding.tvViewCoupons.text.equals("View Unlocked")){
            OffersActivity.startActivity(this@LandingHomeActivity,1)
        }else{
            OffersActivity.startActivity(this@LandingHomeActivity)
        }
    }
    private fun redirectToEarnZoneModule(){
        if(binding.tvEarnZoneViewTaskText.text.equals("View Completed")){
            EarnZoneActivity.startActivity(this@LandingHomeActivity,1)
        }else{
            EarnZoneActivity.startActivity(this@LandingHomeActivity,0)
        }
    }

    override fun onExploreGameClicked(position: Int, game: Game) {
        when {
            game.gameName.contains("scratch card", true) -> {
                Intent(this, ScratchCardsListHomeActivity::class.java).apply {
                    startActivity(this)
                }
            }
            game.gameName.contains("quiz", true) -> {
                QuizzletActivity.startActivity(this, 0)
            }
            game.gameName.contains("wheel", true) -> {
                Intent(this, SpinWheelListActivity::class.java).apply {
                    startActivity(this)
                }
            }
        }
    }

    private fun openCouponDetail(item:  GetHomeScreenDetailsDto.Data.Coupon) {
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
        coupon.remaningPoints=item.remainingPoints.toString()
        coupon.threshold=item.threshold.toString()

        couponDetailBottomSheet =  CouponDetailFragment(coupon,this)
        if(!couponDetailBottomSheet.isVisible)
            couponDetailBottomSheet.show(supportFragmentManager.beginTransaction(), couponDetailBottomSheet.getTag())
    }

    private fun setUpUnlockCouponViewModel(){
        viewModel.unlockingCouponUiStateFlow.onEach { it ->
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
//                    CommonDialogUtils.showSingleButtonAlertDialog(myContext, it.message)
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
                        if(it.message == "NetworkError"){
                            FullScreenNetworkErrorActivity.startActivity(this)
                        }else{
                            if(::couponDetailBottomSheet.isInitialized && couponDetailBottomSheet.isVisible) {
                                couponDetailBottomSheet.showErrorState()
                            }
                        }
                    }
                }
                else->{}

            }
        }.observeInLifecycle(this)

        viewModel.earnZoneUiStateFlow.onEach { uiState ->
            when (uiState) {
                is UIState.Loading -> {
//                    loadingDialog.show()
                }
                is UIState.Success<*> -> {
//                    loadingDialog.cancel()
                    when (uiState.data) {
                        is EarnZoneRedeemDto.Data->{
                            Handler().postDelayed({
                                val fragment = EarnZoneRedeemDialogFragment(uiState.data.value)
                                fragment.show(supportFragmentManager, "TAG")
                            }, 500)
                        }
                    }
                }
                is UIState.Error -> {
//                    loadingDialog.cancel()
                    if (!uiState.message.isNullOrEmpty()){
                        if (uiState.message.contains("token", true) ||
                            uiState.message.contains("unauthorized", true) ||
                            uiState.message.contains("details not found", true)) {
                            CommonDialogUtils.showSingleButtonLogoutDialog(binding.root.context, uiState.message, null)
                        } else if(uiState.message == "NetworkError"){
                            FullScreenNetworkErrorActivity.startActivity(this)
                        }
                        else {
                            CommonDialogUtils.showSingleButtonAlertDialog(binding.root.context, uiState.message)
                        }
                    }
                }
                else -> {
//                    loadingDialog.cancel()
                }
            }

        }.observeInLifecycle(this)

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
            giftCouponBSFragment.show(supportFragmentManager.beginTransaction(), ContentValues.TAG)
    }

    override fun onCouponDetailSheetCancelled() {
       // DO nothing
        if(couponUnlocked){
            this.onResume()
            couponUnlocked=false
        }

    }

    override fun onGiftViaEmail(email: String, couponTransactionId: String) {
        viewModel.transferCouponEmail(couponTransactionId,email=email)
    }
    override fun onGiftViaMobile(mobile: String, countryCode: String, couponTransactionId: String) {
        viewModel.transferCouponMobile(mobile,countryCode,couponTransactionId)


    }

    companion object{

        fun startActivity(context: Context, redirectTo: String) {
            val intent = Intent(context, LandingHomeActivity::class.java)
            intent.putExtra("redirectTo", redirectTo)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

    override fun onEarnZoneTaskClicked(
        position: Int,
        task: GetHomeScreenDetailsDto.Data.EarnZoneTask
    ) {

        val earnZoneTaskData=EarnZoneTaskData()
        earnZoneTaskData.type=task.type
        if (task.url!=null){
            earnZoneTaskData.url=task.url
        }
        earnZoneTaskData.value=task.value
        val  earnZoneTypeBSFragment =  EarnZoneTypeBottmSheetFragment(earnZoneTaskData,this)
        if(!earnZoneTypeBSFragment.isVisible)
            earnZoneTypeBSFragment.show(supportFragmentManager.beginTransaction(), earnZoneTypeBSFragment.getTag())

    }

    override fun onTaskCompleteClicked(task: EarnZoneTaskData, date: String?) {
        //call api
        when(task.type){
            Constants.EARN_ZONE_TYPE.BIRTHDAY.type->{
                viewModel.earnToRedeemPointViaBirthDay(task.type,date!!)
            }
            Constants.EARN_ZONE_TYPE.ANNIVERSARY.type->{
                viewModel.earnToRedeemPointViaAnni(task.type,date!!)
            }
            else->{
                viewModel.earnToRedeemPointViaSocial(task.type)

                when(task.type){
                    Constants.EARN_ZONE_TYPE.YOUTUBE.type -> {

                        task.url.let { Utils.playYouTubeVideo(this@LandingHomeActivity, it) }
                    }

                    Constants.EARN_ZONE_TYPE.FACEBOOK.type -> {
                        task.url.let { Utils.shareTextOnFacebookWeb(this@LandingHomeActivity, it) }

                    }
                    Constants.EARN_ZONE_TYPE.LINKEDIN.type -> {
                        task.url.let { Utils.shareTextOnLinkedinWeb(this@LandingHomeActivity, it) }
                    }
                    Constants.EARN_ZONE_TYPE.TWITTER.type -> {
                        task.url.let { Utils.shareTextOnTwitterWeb(this@LandingHomeActivity, it) }
                    }

                    Constants.EARN_ZONE_TYPE.INSTALIKE.type -> {
                        task.url.let { Utils.shareTextOnInstagramWeb(this@LandingHomeActivity, it) }

                    }
                    Constants.EARN_ZONE_TYPE.INSTAFOLLOW.type -> {
                        task.url.let { Utils.shareTextOnInstagramWeb(this@LandingHomeActivity, it) }

                    }
                }
            }
        }
    }

}