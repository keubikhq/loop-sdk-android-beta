package com.lib.loopsdk.ui.feature_spin_the_wheel.spin_wheel_detail

import android.animation.Animator
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.google.gson.reflect.TypeToken
import com.lib.loopsdk.R
import com.lib.loopsdk.core.util.*
import com.lib.loopsdk.data.remote.dto.InitializeDto
import com.lib.loopsdk.data.remote.dto.RedeemSpinWheelDto
import com.lib.loopsdk.databinding.FragmentSucessSpinWheelDialogBinding
import com.lib.loopsdk.ui.feature_offers.OffersActivity
import com.lib.loopsdk.ui.feature_tier_details_points_wallet.presentation.TierDetailsPointsWalletActivity

class SpinWheelSuccessDialogFragment(
    private val benefit: RedeemSpinWheelDto.Data,
    private val remainingAttempts: Int? = null,
    private val shouldHideTryAgain: Boolean = false
): DialogFragment() {

    var mediaPlayer: MediaPlayer? = null
    private lateinit var binding: FragmentSucessSpinWheelDialogBinding
    private val viewModel: SpinWheelDetailViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Theme_AppCompat_Light_NoActionBar_FullScreen)
        isCancelable = false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_sucess_spin_wheel_dialog,
            container,
            false
        )
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.brandTheme = Prefs.getNonPrimitiveData<InitializeDto.Data.BrandTheme>(object: TypeToken<InitializeDto.Data.BrandTheme>(){}.type, "BRAND_THEME")
        binding.brandThemeColors = Colors
        if(shouldHideTryAgain) {
            binding.tvAllAttemptsExhausted.hideView()
            binding.flTryAgainContainer.hideView()
        }else{
            binding.flTryAgainContainer.showView()
            if(remainingAttempts != null){
                if(remainingAttempts < 1) {
                    binding.disableHighlightOnTryAgain.showView()
                    binding.tvAllAttemptsExhausted.showView()
                }else{
                    binding.disableHighlightOnTryAgain.hideView()
                    binding.tvAllAttemptsExhausted.hideView()
                }
            }
        }
        binding.btnViewBenefit.setOnClickListener {
            when (benefit.benefitType) {
                1 -> {
                    Prefs.putString(Constants.REDIRECT_TO_UNLOCK, "game")
                    OffersActivity.startActivity(requireContext(),1)
                }
                2 -> {
                    TierDetailsPointsWalletActivity.startActivity(requireContext(),2)
                }
                else -> {}
            }
        }
        binding.btnClose.setOnClickListener {
            viewModel.onClickCloseDialog()
        }

        binding.btnTryAgain.setOnClickListener {
            viewModel.onClickTryAgain()
        }

        if(remainingAttempts != null){
            if(remainingAttempts < 1) {
                binding.disableHighlightOnTryAgain.showView()
                binding.tvAllAttemptsExhausted.showView()
            }else{
                binding.disableHighlightOnTryAgain.hideView()
                binding.tvAllAttemptsExhausted.hideView()
            }
        }
        when (benefit.benefitType) {
            1 -> {
                showSuccessAnimation()
                binding.tvPrizeText.text = "Congratulations!"
                binding.tvPrizeSubText.text = "You won a coupon"
                binding.llMsgPrize.hideView()
                binding.llPointsPrize.hideView()
                if(benefit.benefitDetails.isFeature == 1){
                    binding.flNonFeaturedCouponPrize.hideView()
                    binding.flFeaturedCouponPrize.showView()
                    if(!benefit.benefitDetails.featuredImage.isNullOrEmpty()){
                        Glide.with(this)
                            .load(benefit.benefitDetails.featuredImage)
                            .into(binding.ivCouponImage)
                    }
                    binding.tvCouponDescription.text = benefit.benefitDetails.displayName

                    when (benefit.benefitDetails.couponClassification) {
                        1, 2 -> {
                            binding.matCvDiscount.showView()
                            binding.tvCouponDiscount.text = "${Constants.init.defaultCurrency.symbol}${Utils.digitCountUpdate(benefit.benefitDetails.maxDiscountValue)}"
                        }
                        3, 4 -> {
                            binding.matCvDiscount.showView()
                            binding.tvCouponDiscount.text = "${benefit.benefitDetails.discountPercent} %"
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

                    binding.tvCouponDesc.text = benefit.benefitDetails.displayName

                    when (benefit.benefitDetails.couponClassification) {
                        1, 2 -> {
                            binding.tvCouponClassText.text = "${Constants.init.defaultCurrency.symbol}${Utils.digitCountUpdate(
                                benefit.benefitDetails.maxDiscountValue)} Voucher"
                        }
                        3, 4 -> {
                            binding.tvCouponClassText.text = "${benefit.benefitDetails.discountPercent}% Discount"
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
                showSuccessAnimation()
                binding.tvPrizeText.text = "Congratulations!"
                binding.tvPrizeSubText.text = "You won"
                binding.flNonFeaturedCouponPrize.hideView()
                binding.flFeaturedCouponPrize.hideView()
                binding.llMsgPrize.hideView()
                binding.llPointsPrize.showView()
                binding.tvPointsWonText.text = "${benefit!!.amount} ${if(benefit!!.amount > 1) Constants.init.pointsIdentifier.pointsLabelPlural else Constants.init.pointsIdentifier.pointsLabelPlural}"
            }
            3 -> {
                //startMediaPlayer("negative sound")
                binding.tvPrizeText.text = "We have a message!"
                binding.tvPrizeSubText.text = "for you"
                binding.btnViewBenefit.hideView()
                binding.flNonFeaturedCouponPrize.hideView()
                binding.flFeaturedCouponPrize.hideView()
                binding.llPointsPrize.hideView()
                binding.llMsgPrize.showView()
                binding.tvMsgText.text = "${benefit.messageText}"
            }
        }
    }
    private fun showSuccessAnimation(){
        binding.successAnimation.showView()
        binding.successAnimation.playAnimation()
        //startMediaPlayer("success")
        binding.successAnimation.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(p0: Animator) {
            }
            override fun onAnimationEnd(p0: Animator) {
                binding.successAnimation.hideView()
                //stopMediaPlayer()
            }
            override fun onAnimationCancel(p0: Animator) {
            }
            override fun onAnimationRepeat(p0: Animator) {
            }

        })
    }
    fun startMediaPlayer(view: String) {
        if (view == "success") {
            mediaPlayer = MediaPlayer.create(requireContext(), R.raw.wheel_success_state_sound)
            mediaPlayer!!.start()
        }else {
            mediaPlayer = MediaPlayer.create(requireContext(), R.raw.negative_state_sound)
            mediaPlayer!!.start()
        }
    }
    fun stopMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer!!.release()
            mediaPlayer = null
        }
    }
}