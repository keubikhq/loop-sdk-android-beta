package com.lib.loopsdk.ui.feature_earn

import android.animation.Animator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.google.gson.reflect.TypeToken
import com.lib.loopsdk.R
import com.lib.loopsdk.core.util.Colors
import com.lib.loopsdk.core.util.Constants
import com.lib.loopsdk.core.util.Prefs
import com.lib.loopsdk.core.util.hideView
import com.lib.loopsdk.core.util.showView
import com.lib.loopsdk.data.remote.dto.InitializeDto
import com.lib.loopsdk.databinding.FragmentEarnZoneRedeemDialogBinding
import com.lib.loopsdk.ui.feature_tier_details_points_wallet.presentation.TierDetailsPointsWalletActivity

class EarnZoneRedeemDialogFragment(
    private  val benefitValue:Int) : DialogFragment() {


    private lateinit var binding: FragmentEarnZoneRedeemDialogBinding

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
            R.layout.fragment_earn_zone_redeem_dialog,
            container,
            false
        )
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.brandTheme = Prefs.getNonPrimitiveData<InitializeDto.Data.BrandTheme>(object: TypeToken<InitializeDto.Data.BrandTheme>(){}.type, "BRAND_THEME")
        binding.brandThemeColors = Colors

        binding.tvPointsWonText.text = "${benefitValue} ${if(benefitValue> 1) Constants.init.pointsIdentifier.pointsLabelPlural else Constants.init.pointsIdentifier.pointsLabelPlural}"
        showSuccessAnimation()
        binding.btnViewPoint.setOnClickListener {
            TierDetailsPointsWalletActivity.startActivity(requireContext(),2)

        }
        binding.btnClose.setOnClickListener {
            this.dismiss()
        }
    }
    private fun showSuccessAnimation(){
        binding.successAnimation.showView()
        binding.successAnimation.playAnimation()
        binding.successAnimation.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(p0: Animator) {
            }
            override fun onAnimationEnd(p0: Animator) {
                binding.successAnimation.hideView()
            }
            override fun onAnimationCancel(p0: Animator) {
            }
            override fun onAnimationRepeat(p0: Animator) {
            }

        })
    }


}