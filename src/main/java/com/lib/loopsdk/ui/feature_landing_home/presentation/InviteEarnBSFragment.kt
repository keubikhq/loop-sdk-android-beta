package com.lib.loopsdk.ui.feature_landing_home.presentation

import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.reflect.TypeToken
import com.lib.loopsdk.R
import com.lib.loopsdk.core.util.*
import com.lib.loopsdk.data.remote.dto.InitializeDto
import com.lib.loopsdk.data.remote.dto.ProfileReferralBenefitsDto
import com.lib.loopsdk.databinding.FragmentInviteEarnBSBinding
import kotlinx.coroutines.flow.onEach
import org.json.JSONObject

class InviteEarnBSFragment : BottomSheetDialogFragment(), View.OnClickListener {

    private val viewModel: LandingHomeViewModel by activityViewModels()
    private lateinit var binding: FragmentInviteEarnBSBinding
    private var customerReferralUrl: String = ""
    private var referralMsg: String = ""
    private var referralCode: String = ""

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
        binding = FragmentInviteEarnBSBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.brandTheme = Prefs.getNonPrimitiveData<InitializeDto.Data.BrandTheme>(object: TypeToken<InitializeDto.Data.BrandTheme>(){}.type, "BRAND_THEME")
        binding.brandThemeColors = Colors

        viewModel.getInviteEarnData()
        binding.btnCopyCode.setOnClickListener(this)
        binding.btnShareOther.setOnClickListener(this)
        binding.btnShareSms.setOnClickListener(this)
        binding.btnShareWhatsApp.setOnClickListener(this)

        viewModel.referEarnBsStateFlow.onEach {
            when (it) {
                is UIState.Loading -> {
                }
                is UIState.Success<*> -> {
                    when (it.data) {
                        is ProfileReferralBenefitsDto.Data -> {
                            onInviteEarnDataReceived(it.data)
                        }
                    }
                }
                is UIState.Error -> {

                }
                else -> {}
            }
        }.observeInLifecycle(viewLifecycleOwner)

    }

    private fun onInviteEarnDataReceived(response: ProfileReferralBenefitsDto.Data) {
        if (response.referralData == null) return
        else{
            referralCode = response.referralData.code
            customerReferralUrl = response.link
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
            referralMsg+="\n${customerReferralUrl}"
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btnCopyCode -> {
                val clipboardManager: ClipboardManager = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText(
                    "loopReferAndEarnCode",
                    referralCode
                )
                clipboardManager.setPrimaryClip(clip)
                Toast.makeText(requireContext(), "Referral code copied", Toast.LENGTH_SHORT).show()
            }

            R.id.btnShareWhatsApp -> {
                if (!referralCode.isNullOrEmpty() && !customerReferralUrl.isNullOrEmpty()) {
                    val pm: PackageManager = requireContext().packageManager
                    try {
                        val waIntent = Intent(Intent.ACTION_SEND)
                        waIntent.type = "text/plain"
                        val text = referralMsg
                        waIntent.setPackage("com.whatsapp")
                        waIntent.putExtra(Intent.EXTRA_TEXT, text)
                        startActivity(Intent.createChooser(waIntent, "Share with"))
                    } catch (e: PackageManager.NameNotFoundException) {
                        Toast.makeText(requireContext(), "WhatsApp not Installed", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            R.id.btnShareSms -> {

                if (!referralCode.isNullOrEmpty() && !customerReferralUrl.isNullOrEmpty()) {
                    val phoneNo = "" //The phone number you want to text

                    val smsIntent = Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", phoneNo, null))
                    smsIntent.putExtra("sms_body", referralMsg)
                    startActivity(smsIntent)
                }

            }

            R.id.btnShareOther -> {
                if (!referralCode.isNullOrEmpty() && !customerReferralUrl.isNullOrEmpty()) {
                    this.requireContext().startSharingTextIntent(
                        referralMsg,
                        subject = "Hey! Grab your 1st Free Reward Now"
                    )

                }

            }

        }
    }
}