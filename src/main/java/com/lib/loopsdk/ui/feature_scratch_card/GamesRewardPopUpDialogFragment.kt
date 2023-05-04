package com.lib.loopsdk.ui.feature_scratch_card

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
import com.lib.loopsdk.databinding.FragmentGamesRewardPopUpDialogBinding
import com.lib.loopsdk.ui.feature_offers.OffersActivity
import com.lib.loopsdk.ui.feature_tier_details_points_wallet.presentation.TierDetailsPointsWalletActivity

class GamesRewardPopUpDialogFragment(
    private val benefit: BenefitGeneric,
    private val remainingAttempts: Int? = null,
    private val shouldHideTryAgain: Boolean = false
): DialogFragment() {

    private lateinit var binding: FragmentGamesRewardPopUpDialogBinding
    private val viewModel: ScratchCardLandingViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Theme_AppCompat_Light_NoActionBar_FullScreen)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_games_reward_pop_up_dialog,
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

                else -> {

                }
            }
        }

        binding.btnClose.setOnClickListener {
            viewModel.onClickCloseDialog()
        }

        binding.btnTryAgain.setOnClickListener {
            viewModel.onClickTryAgain()
        }

        when (benefit.benefitType) {
            1 -> {
                binding.tvPrizeText.text = "Congratulations!"
                binding.tvPrizeSubText.text = "You won a coupon"
                binding.llMsgPrize.hideView()
                binding.llPointsPrize.hideView()
                if(benefit.isFeature == 1){
                    binding.flNonFeaturedCouponPrize.hideView()
                    binding.flFeaturedCouponPrize.showView()
                    if(!benefit.featuredImage.isNullOrEmpty()){
                        Glide.with(this)
                            .load(benefit.featuredImage)
                            .into(binding.ivCouponImage)
                    }
                    binding.tvCouponDescription.text = benefit.displayName

                    when (benefit.couponClassification) {
                        1, 2 -> {
                            binding.matCvDiscount.showView()
                            binding.tvCouponDiscount.text = "${Constants.init.defaultCurrency.symbol}${Utils.digitCountUpdate(benefit.maxDiscountValue)}"
                        }
                        3, 4 -> {
                            binding.matCvDiscount.showView()
                            binding.tvCouponDiscount.text = "${benefit.discountPercent} %"
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

                    binding.tvCouponDesc.text = benefit.displayName

                    when (benefit.couponClassification) {
                        1, 2 -> {
                            binding.tvCouponClassText.text = "${Constants.init.defaultCurrency.symbol}${Utils.digitCountUpdate(
                                benefit.maxDiscountValue)} Voucher"
                        }
                        3, 4 -> {
                            binding.tvCouponClassText.text = "${benefit.discountPercent}% Discount"
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
                binding.tvPrizeText.text = "Congratulations!"
                binding.tvPrizeSubText.text = "You won"
                binding.flNonFeaturedCouponPrize.hideView()
                binding.flFeaturedCouponPrize.hideView()
                binding.llMsgPrize.hideView()
                binding.llPointsPrize.showView()
                binding.tvPointsWonText.text = "${benefit!!.value.toInt()} ${if(benefit!!.value.toInt() > 1) Constants.init.pointsIdentifier.pointsLabelPlural else Constants.init.pointsIdentifier.pointsLabelPlural}"
            }
            3 -> {
                binding.tvPrizeText.text = "We have a message!"
                binding.tvPrizeSubText.text = "for you"
                binding.btnViewBenefit.hideView()
                binding.flNonFeaturedCouponPrize.hideView()
                binding.flFeaturedCouponPrize.hideView()
                binding.llPointsPrize.hideView()
                binding.llMsgPrize.showView()
                binding.tvMsgText.text = benefit.value
            }
        }


    }




}