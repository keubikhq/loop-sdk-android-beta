package com.lib.loopsdk.ui.feature_scratch_card

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.graphics.PorterDuff
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.gson.reflect.TypeToken
import com.lib.loopsdk.R
import com.lib.loopsdk.core.customViews.LoadingDialog
import com.lib.loopsdk.core.customViews.SwipeToUnlockView
import com.lib.loopsdk.core.util.*
import com.lib.loopsdk.data.remote.dto.GetSingleScratchCardDetailsDto
import com.lib.loopsdk.data.remote.dto.InitializeDto
import com.lib.loopsdk.data.remote.dto.RedeemScratchCardDto
import com.lib.loopsdk.data.remote.dto.UnlockScratchCardDto
import com.lib.loopsdk.databinding.ActivityScratchCardLandingBinding
import com.lib.loopsdk.ui.BaseActivity
import com.lib.loopsdk.ui.feature_tier_details_points_wallet.presentation.TierDetailsPointsWalletActivity
import kotlinx.coroutines.flow.onEach
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

class ScratchCardLandingActivity :
    BaseActivity(),
    SwipeToUnlockView.Interaction,
    View.OnClickListener{

    private var benefit: RedeemScratchCardDto.Data? = null
    private var isScratchCardUnlocked: Boolean = false
    private var scratchCardIdReceived = ""
    private var scratchCardTransactionId = ""
    private var scratchCardTnC = ""
    private var remainingAttempts: Int = -1
    private lateinit var binding: ActivityScratchCardLandingBinding
    private lateinit var viewModel: ScratchCardLandingViewModel
    private val loadingDialog: LoadingDialog by lazy { LoadingDialog(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_scratch_card_landing)
        binding.lifecycleOwner = this

        val brandTheme = Prefs.getNonPrimitiveData<InitializeDto.Data.BrandTheme>(object: TypeToken<InitializeDto.Data.BrandTheme>(){}.type, "BRAND_THEME")
        binding.brandTheme = brandTheme
        binding.brandThemeColors = Colors

        viewModel = ViewModelProvider(this)[ScratchCardLandingViewModel::class.java]

        initialUi()

        binding.btnBack.setOnClickListener(this)
        binding.btnTnC.setOnClickListener(this)
        binding.btnPointsWallet.setOnClickListener(this)
//        binding.swipeToUnlock.setSwipeToUnlockListener(this)
        binding.btnSwipeToUnlock.setOnClickListener(this)
        scratchCardIdReceived = intent?.getStringExtra("scratchCardId")?:""
        scratchCardTransactionId = intent?.getStringExtra("scratchCardTransactionId")?:""

        viewModel.getSingleScratchCardDetails(scratchCardIdReceived, true)

        binding.scratchView.setOnScratchListener { scratchCard, visiblePercent ->
            if (visiblePercent > 0.3) {
                binding.scratchView.hideView()
                val benefitGeneric = BenefitGeneric(
                    benefit!!.benefitType,
                    benefit!!.description?:"",
                    benefit!!.messageText?:"",
                    benefit!!.name?:"",
                    benefit!!.value,
                    benefit!!.benefitDetails?.couponClassification?:-1,
                    benefit!!.benefitDetails?.discountPercent?:-1,
                    benefit!!.benefitDetails?.displayName?:"",
                    benefit!!.benefitDetails?.featuredImage?:"",
                    benefit!!.benefitDetails?.id?:"",
                    benefit!!.benefitDetails?.isFeature?:-1,
                    benefit!!.benefitDetails?.maxDiscountValue?:-1,
                    benefit!!.benefitDetails?.threshold?:-1,
                )
                Utils.showFragment(
                    supportFragmentManager,
                    GamesRewardPopUpDialogFragment(
                        benefitGeneric,
                        remainingAttempts,
                        !intent?.getStringExtra("scratchCardTransactionId").isNullOrEmpty()
                    ),
                    "GAMES_REWARD_POPUP_DIALOG_FRAGMENT"
                )
            }
        }

        viewModel.uiStateFlow.onEach { uiState ->
            when (uiState){
                is UIState.Loading -> {
                    loadingDialog.show()
                }
                is UIState.Success<*> -> {
                    when (uiState.data){
                        is GetSingleScratchCardDetailsDto.Data -> {
                            Handler().postDelayed({
                                loadingDialog.cancel()
                            },1000)
                            scratchCardTnC = uiState.data.termsAndConditions
                            remainingAttempts = uiState.data.remaingAttempts
                            if(!scratchCardTransactionId.isNullOrEmpty()){
                                isScratchCardUnlocked = true
                                setPointsUi(uiState.data.totalPoints)
                                setScreenUiForAlreadyUnlocked(uiState.data)
                                setScratchCardColors(uiState.data.backgroundImage.id)
                            } else if(isScratchCardUnlocked) {
                                updatePointsAndAttemptsUi(
                                    uiState.data.totalPoints,
                                    uiState.data.remaingAttempts
                                )
                            } else {
                                setPointsUi(uiState.data.totalPoints)
                                setScreenUi(uiState.data)
                                setScratchCardColors(uiState.data.backgroundImage.id)
                            }

                        }
                        is UnlockScratchCardDto.Data -> {
                            viewModel.getSingleScratchCardDetails(scratchCardIdReceived)
                            isScratchCardUnlocked = true
                            scratchCardTransactionId = uiState.data.scratchcardTransactionId
                            setUiAfterUnlock()
                            loadingDialog.cancel()
                        }
                        is RedeemScratchCardDto.Data -> {
                            benefit = uiState.data
                            setUiAfterTapOnCard()
                            readyUpPrizeBackStage()
                            loadingDialog.cancel()
                        }
                    }
                }
                is UIState.Error -> {
                    loadingDialog.cancel()
                    if(uiState.message == "NetworkError"){
                        FullScreenNetworkErrorActivity.startActivity(this)
                    }
                }
                is UIState.ErrorWithData -> {
                    loadingDialog.cancel()
                    when (uiState.error?.code){
                        422 -> {
                            Toast.makeText(this, uiState.error?.message?:"", Toast.LENGTH_LONG).show()
                            onBackPressed()
                        }
                    }
                }
                else -> {
                    loadingDialog.cancel()
                }
            }
        }.observeInLifecycle(this)

        viewModel.fragmentToActivityEventFlow.onEach { event ->
            when (event) {
                is ScratchCardLandingViewModel.Event.OnBenefitDialogCloseClicked ->{
                    supportFragmentManager.popBackStackImmediate()
                    onBackPressed()
                }
                is ScratchCardLandingViewModel.Event.OnTryAgainButtonClicked ->{
                    supportFragmentManager.popBackStackImmediate()
                    this.recreate()
                }
            }
        }.observeInLifecycle(this)

    }


    private fun readyUpPrizeBackStage() {
        if(benefit == null) return
        when (benefit!!.benefitType) {
            1 -> {
                binding.llMsgPrize.hideView()
                binding.llPointsPrize.hideView()
                if(benefit!!.benefitDetails.isFeature == 1){
                    binding.flNonFeaturedCouponPrize.hideView()
                    binding.flFeaturedCouponPrize.showView()
                    if(!benefit!!.benefitDetails.featuredImage.isNullOrEmpty()){
                        Glide.with(this)
                            .load(benefit!!.benefitDetails.featuredImage)
                            .into(binding.ivCouponImage)
                    }
                    binding.tvCouponDescription.text = benefit!!.benefitDetails.displayName

                    when (benefit!!.benefitDetails.couponClassification) {
                        1, 2 -> {
                            binding.matCvDiscount.showView()
                            binding.tvCouponDiscount.text = "${Constants.init.defaultCurrency.symbol}${Utils.digitCountUpdate(benefit!!.benefitDetails.maxDiscountValue)}"
                        }
                        3, 4 -> {
                            binding.matCvDiscount.showView()
                            binding.tvCouponDiscount.text = "${benefit!!.benefitDetails.discountPercent} %"
                        }
                        5 -> {
                            binding.matCvDiscount.showView()
                            binding.tvCouponDiscount.hideView()
                            binding.ivClassIcon.showView()
                            binding.ivClassIcon.setImageResource(R.drawable.ic_vehicle_truck)
                        }
                        6-> {
                            binding.matCvDiscount.showView()
                            binding.tvCouponDiscount.hideView()
                            binding.ivClassIcon.showView()
                            binding.ivClassIcon.setImageResource(R.drawable.ic_gift)
                        }
                        else -> {
                            binding.matCvDiscount.hideView()
                        }
                    }
                }else{
                    binding.flFeaturedCouponPrize.hideView()
                    binding.flNonFeaturedCouponPrize.showView()

                    binding.tvCouponDesc.text = benefit!!.benefitDetails.displayName

                    when (benefit!!.benefitDetails.couponClassification) {
                        1, 2 -> {
                            binding.tvCouponClassText.text = "${Constants.init.defaultCurrency.symbol}${Utils.digitCountUpdate(
                                benefit!!.benefitDetails.maxDiscountValue)} Voucher"
                        }
                        3, 4 -> {
                            binding.tvCouponClassText.text = "${benefit!!.benefitDetails.discountPercent}% Discount"
                        }
                        5 -> {
                            binding.ivCouponClassIcon.showView()
                            binding.ivCouponClassIcon.setImageResource(R.drawable.ic_vehicle_truck)
                            binding.tvCouponClassText.text = "Free Delivery"
                        }
                        6-> {
                            binding.ivCouponClassIcon.showView()
                            binding.ivCouponClassIcon.setImageResource(R.drawable.ic_gift)
                            binding.tvCouponClassText.text = "Free Gift"
                        }
                        else -> {
                            binding.matCvDiscount.hideView()
                        }
                    }
                }
            }
            2 -> {
                binding.flNonFeaturedCouponPrize.hideView()
                binding.flFeaturedCouponPrize.hideView()
                binding.llMsgPrize.hideView()
                binding.llPointsPrize.showView()
                binding.tvPointsWonText.text = "${benefit!!.value} ${if(benefit!!.value.toInt() > 1) Constants.init.pointsIdentifier.pointsLabelPlural else Constants.init.pointsIdentifier.pointsLabelPlural}"
            }
            3 -> {
                binding.flNonFeaturedCouponPrize.hideView()
                binding.flFeaturedCouponPrize.hideView()
                binding.llPointsPrize.hideView()
                binding.llMsgPrize.showView()
                binding.tvMsgText.text = benefit!!.value
            }
        }
    }

    private fun playFlipAnimation() {
        val oa1: ObjectAnimator = ObjectAnimator.ofFloat(binding.flPlayArea, "scaleX", 1f, 0f)
        val oa2: ObjectAnimator = ObjectAnimator.ofFloat(binding.flPlayArea, "scaleX", 0f, 1f)
        oa1.interpolator = DecelerateInterpolator()
        oa2.interpolator = AccelerateDecelerateInterpolator()
        oa1.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                oa2.start()
            }
        })
        oa1.start()

    }

    private fun setScreenUi(data: GetSingleScratchCardDetailsDto.Data){
        binding.ivDashedLine.showView()
        binding.llPointsRequirement.showView()
        binding.tvScratchCardName.text = data.name
        binding.tvNumOfAttemptsRemaining.text = "${data.remaingAttempts}"
        if(data.remaingAttempts > 0) {
            if (data.pointsToUnlock == 0) {
                binding.tvTextBeforePointsIcon.text = "Unlock to Scratch Card"
                binding.ivPointsIcon.hideView()
                binding.tvTextAfterPointsIcon.hideView()
                displayEnabledUi()
            } else if(data.pointsToUnlock > data.totalPoints.toFloat().roundToInt()){
                binding.tvTextBeforePointsIcon.text = "Earn"
                binding.ivPointsIcon.showView()
                binding.tvTextAfterPointsIcon.showView()
                val diff = (data.totalPoints.toFloat().roundToInt() - data.pointsToUnlock).absoluteValue
                binding.tvTextAfterPointsIcon.text = "$diff ${if (diff > 1) Constants.init.pointsIdentifier.pointsLabelPlural else Constants.init.pointsIdentifier.pointsLabelSingular} more to unlock"
                displayDisabledUi()
            } else {
                binding.tvTextBeforePointsIcon.text = "Unlock for"
                binding.ivPointsIcon.showView()
                binding.tvTextAfterPointsIcon.showView()
                binding.tvTextAfterPointsIcon.text = "${data.pointsToUnlock} ${if (data.pointsToUnlock > 1) Constants.init.pointsIdentifier.pointsLabelPlural else Constants.init.pointsIdentifier.pointsLabelSingular}"
                displayEnabledUi()
            }
        }else {
            displayZeroAttemptsUi()
        }
    }

    private fun setScreenUiForAlreadyUnlocked(data: GetSingleScratchCardDetailsDto.Data){
        binding.tvScratchCardName.text = data.name
        binding.tvNumOfAttemptsRemaining.text = "${data.remaingAttempts}"
        setUiAfterUnlock()
    }

    private fun setUiAfterUnlock() {
        binding.llAttemptsRemaining.invisibleView()
//        binding.swipeToUnlock.resetSwipeToUnlock()
//        binding.swipeToUnlock.hideView()
        binding.btnSwipeToUnlock.hideView()
        binding.ivDashedLine.hideView()
        binding.llPointsRequirement.hideView()
        binding.noUseClickSurface.showView()
        binding.noUseClickSurface.setOnClickListener(this)
        binding.llTutorial.showView()
        binding.tvTutorialText.text = "Tap on the card to begin"
    }

    private fun setUiAfterTapOnCard() {
        binding.llAttemptsRemaining.invisibleView()
//        binding.swipeToUnlock.resetSwipeToUnlock()
//        binding.swipeToUnlock.hideView()
        binding.btnSwipeToUnlock.hideView()
        binding.ivDashedLine.hideView()
        binding.llPointsRequirement.hideView()
        binding.noUseClickSurface.hideView()
        binding.llTutorial.showView()
        binding.tvTutorialText.text = "Move your finger across the card\nto reveal prize"
    }

    private fun initialUi() {
//        binding.swipeToUnlock.hideView()
        binding.btnSwipeToUnlock.hideView()
        binding.ivDashedLine.hideView()
        binding.llPointsRequirement.hideView()
    }

    private fun displayZeroAttemptsUi() {
//        binding.swipeToUnlock.hideView()
        binding.btnSwipeToUnlock.hideView()
        binding.cvBgForLock.showView()
        binding.flLockUi.showView()
        binding.ivDashedLine.hideView()
        binding.llPointsRequirement.hideView()
        binding.llTutorial.hideView()
        binding.llAttemptsZero.showView()
    }

    private fun displayDisabledUi() {
//        binding.swipeToUnlock.showView()
//        binding.swipeToUnlock.disableSwipeToUnlock()
        binding.btnSwipeToUnlock.showView()
        disableUnlockButton()
        binding.cvBgForLock.showView()
        binding.flLockUi.showView()
    }

    private fun displayEnabledUi() {
//        binding.swipeToUnlock.enableSwipeToUnlock()
//        binding.swipeToUnlock.showView()
        binding.btnSwipeToUnlock.showView()
        enableUnlockButton()
        binding.cvBgForLock.hideView()
        binding.flLockUi.hideView()
    }

    private fun enableUnlockButton() {
        binding.btnSwipeToUnlock.isEnabled=true
        binding.btnSwipeToUnlock.alpha=1f
    }

    private fun disableUnlockButton() {
        binding.btnSwipeToUnlock.isEnabled=false
        binding.btnSwipeToUnlock.alpha=0.5f
    }

    private fun setPointsUi(points: String){
        binding.btnPointsWallet.showView()
        binding.tvTotalPoints.text = getFormattedPoints(points)
    }

    private fun updatePointsAndAttemptsUi(points: String, attempts: Int){
        binding.btnPointsWallet.showView()
        binding.tvTotalPoints.text = getFormattedPoints(points)
        binding.tvNumOfAttemptsRemaining.text = "$attempts"
    }

    private fun setScratchCardColors(backgroundImageId: Int) {
        when (backgroundImageId) {
            1 -> {
                binding.scratchView.setScratchDrawable(ContextCompat.getDrawable(this, R.drawable.sc_purple))
                binding.scratchView.showView()
                binding.ivLock.setColorFilter(ContextCompat.getColor(this, R.color.sc_purple), PorterDuff.Mode.SRC_IN)
                binding.cvBgForLock.setCardBackgroundColor(ContextCompat.getColor(this, R.color.sc_purple))
            }
            2 -> {
                binding.scratchView.setScratchDrawable(ContextCompat.getDrawable(this, R.drawable.sc_lime))
                binding.scratchView.showView()
                binding.ivLock.setColorFilter(ContextCompat.getColor(this, R.color.sc_lime), PorterDuff.Mode.SRC_IN)
                binding.cvBgForLock.setCardBackgroundColor(ContextCompat.getColor(this, R.color.sc_lime))
            }
            3 -> {
                binding.scratchView.setScratchDrawable(ContextCompat.getDrawable(this, R.drawable.sc_red))
                binding.scratchView.showView()
                binding.ivLock.setColorFilter(ContextCompat.getColor(this, R.color.sc_red), PorterDuff.Mode.SRC_IN)
                binding.cvBgForLock.setCardBackgroundColor(ContextCompat.getColor(this, R.color.sc_red))
            }
            4 -> {
                binding.scratchView.setScratchDrawable(ContextCompat.getDrawable(this, R.drawable.sc_yellow))
                binding.scratchView.showView()
                binding.ivLock.setColorFilter(ContextCompat.getColor(this, R.color.sc_yellow), PorterDuff.Mode.SRC_IN)
                binding.cvBgForLock.setCardBackgroundColor(ContextCompat.getColor(this, R.color.sc_yellow))
            }
            5 -> {
                binding.scratchView.setScratchDrawable(ContextCompat.getDrawable(this, R.drawable.sc_blue))
                binding.scratchView.showView()
                binding.ivLock.setColorFilter(ContextCompat.getColor(this, R.color.sc_blue), PorterDuff.Mode.SRC_IN)
                binding.cvBgForLock.setCardBackgroundColor(ContextCompat.getColor(this, R.color.sc_blue))
            }
            else -> {
                binding.scratchView.setScratchDrawable(ContextCompat.getDrawable(this, R.drawable.sc_purple))
                binding.scratchView.showView()
                binding.ivLock.setColorFilter(ContextCompat.getColor(this, R.color.sc_purple), PorterDuff.Mode.SRC_IN)
                binding.cvBgForLock.setCardBackgroundColor(ContextCompat.getColor(this, R.color.sc_purple))
            }
        }
    }

    override fun onSwipeEndReachedAndReleased() {
        viewModel.unlockScratchCard(scratchCardIdReceived)
    }

    override fun onSwipeStartReachedAndReleased() {

    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btnBack -> {
                onBackPressed()
            }

            R.id.noUseClickSurface -> {
                viewModel.redeemScratchCard(scratchCardTransactionId)
                playFlipAnimation()
                binding.noUseClickSurface.setOnClickListener(null)
            }

            R.id.btnTnC -> {
                ScratchCardHelpBSFragment(scratchCardTnC).show(
                    supportFragmentManager,
                    "ScratchCardHelpBSFragment"
                )
            }
            R.id.btnPointsWallet,R.id.btnPointsWalletSticky -> {
                TierDetailsPointsWalletActivity.startActivity(this,2)
            }
            R.id.btnSwipeToUnlock ->{
                viewModel.unlockScratchCard(scratchCardIdReceived)
            }
        }
    }

    override fun onBackPressed() {
        val count = supportFragmentManager.backStackEntryCount
        if (count == 1) {
            //do nothing
        } else {
            super.onBackPressed()
        }
    }
}