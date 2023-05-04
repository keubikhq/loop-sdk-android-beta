package com.lib.loopsdk.ui.feature_survey.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.google.gson.reflect.TypeToken
import com.lib.loopsdk.R
import com.lib.loopsdk.core.util.*
import com.lib.loopsdk.data.remote.dto.InitializeDto
import com.lib.loopsdk.databinding.SurveySuccessBinding
import com.lib.loopsdk.ui.feature_offers.OffersActivity
import com.lib.loopsdk.ui.feature_survey.data.BenefitData
import com.lib.loopsdk.ui.feature_survey.presentation.quizzlet_list.QuizzletActivity
import com.lib.loopsdk.ui.feature_tier_details_points_wallet.presentation.TierDetailsPointsWalletActivity


class SurveyQuizSuccessFragment(private val benefit: BenefitData, private val type: Int): DialogFragment() {
    private lateinit var binding: SurveySuccessBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_AppCompat_Light_NoActionBar_FullScreen)
        isCancelable = false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.survey_success,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.brandTheme = Prefs.getNonPrimitiveData<InitializeDto.Data.BrandTheme>(object: TypeToken<InitializeDto.Data.BrandTheme>(){}.type, "BRAND_THEME")
        binding.brandThemeColors = Colors
        setUpData()
        binding.ivClose.setOnClickListener {
            QuizzletActivity.startActivity(requireContext(), type)
            activity?.finish()
        }
        binding.cvViewBenefit.setOnClickListener {
            when (benefit.benefitType) {
                1 -> {
                    Prefs.putString(Constants.REDIRECT_TO_UNLOCK, "game")
                    OffersActivity.startActivity(requireContext(),1)
                    activity?.finish()
                }

                2 -> {
                    TierDetailsPointsWalletActivity.startActivity(requireContext(),2)
                    activity?.finish()
                }

                else -> {

                }
            }
        }

    }

    private fun setUpData() {
        if(benefit.benefitType == null){
            binding.llMsgBenefit.showView()
            binding.tvCongrats.text = this.getString(R.string.we_have_a_message)
            binding.tvMessage.text = benefit.defaultSuccessMessage
            binding.cvViewBenefit.hideView()
        }
        when (benefit.benefitType) {
            1 -> {
                binding.llCouponBenefit.showView()
                binding.successAnimation.showView()
                binding.successAnimation.playAnimation()

                if (benefit.featuredImage.isNullOrEmpty()) {
                    binding.clCouponDetailFeatured.hideView()
                    binding.llCouponBenefitNonFeatured.showView()
                    binding.tvBenefitDes.text = benefit.couponName

                } else {
                    binding.clCouponDetailFeatured.showView()
                    binding.llCouponBenefitNonFeatured.hideView()
                    Glide.with(requireContext())
                        .load(benefit.featuredImage)
                        .into(binding.ivCouponImage)
                }

                binding.tvCouponDesc.text = benefit.couponName

                when (benefit.couponClassification) {
                    1, 2 -> {
                        binding.ivClassification.hideView()
                        binding.tvClassification.showView()
                        binding.tvClassification.text = Constants.init.defaultCurrency.symbol + benefit.maxDiscountValue?.let {
                            Utils.digitCountUpdate(
                                it
                            )
                        }
                        binding.tvBenefitInfo.text = Constants.init.defaultCurrency.symbol + benefit.maxDiscountValue?.let {
                            Utils.digitCountUpdate(
                                it
                            )
                        }  + " Voucher"
                    }
                    3, 4 -> {
                        binding.ivClassification.hideView()
                        binding.tvClassification.showView()
                        binding.tvClassification.text = benefit.discountPercent.toString() + "%"
                        binding.tvBenefitInfo.text =
                            benefit.discountPercent.toString() + "%" + " Discount"
                    }
                    5 -> {
                        binding.tvClassification.hideView()
                        binding.ivClassification.showView()
                        binding.ivClassification.setImageDrawable(
                            ContextCompat.getDrawable(
                                binding.root.context,
                                R.drawable.ic_vehicle_truck
                            )
                        )
                        binding.tvBenefitInfo.text = "Free Delivery"
                    }
                    6 -> {
                        //Other
                        binding.tvClassification.hideView()
                        binding.ivClassification.showView()
                        binding.ivClassification.setImageDrawable(
                            ContextCompat.getDrawable(
                                binding.root.context,
                                R.drawable.ic_gift
                            )
                        )
                    }
                }
            }
            2 -> {
                binding.llPointsBenefit.showView()
                binding.successAnimation.showView()
                binding.successAnimation.playAnimation()

                binding.tvPointsEarned.text = getFormattedPoints(benefit.benefitValue.toString()) + " " + Constants.init.pointsIdentifier.pointsLabelPlural
            }
            3 -> {
                binding.llMsgBenefit.showView()
                binding.tvCongrats.text = this.getString(R.string.we_have_a_message)
                binding.tvMessage.text = benefit.messageText
                binding.cvViewBenefit.hideView()
            }
            0 -> {
                binding.llMsgBenefit.showView()
                binding.tvCongrats.text = this.getString(R.string.we_have_a_message)
                binding.tvMessage.text = benefit.defaultSuccessMessage
                binding.cvViewBenefit.hideView()
            }
        }
    }

    override fun onDestroyView() {
        if (dialog != null) {
            dialog!!.setDismissMessage(null)
        }
        super.onDestroyView()
    }
    fun closeDialog(){
        this.dismiss()
    }

    interface SuccessDialogClosed{
        fun onSuccessDialogClosed()
    }



}