package com.lib.loopsdk.ui.feature_loyalty_intro.presentation

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.reflect.TypeToken
import com.lib.loopsdk.R
import com.lib.loopsdk.core.util.*
import com.lib.loopsdk.data.remote.dto.ErrorDto
import com.lib.loopsdk.data.remote.dto.InitializeDto
import com.lib.loopsdk.databinding.FragmentReferralBSBinding
import com.lib.loopsdk.ui.feature_landing_home.presentation.LandingHomeActivity
import kotlinx.coroutines.flow.onEach

class ReferralBSFragment : BottomSheetDialogFragment() {

    private var invalidReferralCount = 0
    private val viewModel: IntroductionViewModel by activityViewModels()
    private lateinit var binding: FragmentReferralBSBinding

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
//        setStyle(STYLE_NORMAL, R.style.InputBottomSheetDialog)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_AppCompat_Light_NoActionBar_FullScreen)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentReferralBSBinding.inflate(
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

        binding.etReferralCode.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(text: Editable?) {
                if(binding.etReferralCode.text?.length!! <= 6) {
                    binding.errorContainer.hideView()
                    binding.viewErrorLine.setBackgroundColor(ContextCompat.getColor(
                        requireContext(),
                        R.color.text_light_gray
                    ))
                    binding.disableHighlight.hideView()
                }
            }

        })

        binding.btnJoinNow.setOnClickListener {
            if(validateReferral()){
                if(invalidReferralCount < 2)
                    viewModel.addReferral(binding.etReferralCode.text.toString(), 0)
                else
                    viewModel.addReferral(binding.etReferralCode.text.toString(), 1)
            }
        }

        viewModel.referralBsStateFlow.onEach {
            when (it) {
                is UIState.Loading -> {
                }
                is UIState.Success<*> -> {
                    when (it.data) {
                        is String -> {
                            if(it.data.contains("benefit awarded successfully", true)){
                                Prefs.putBoolean("IS_JOIN_NOW_CLICKED", true)
                                Intent(requireContext(), LandingHomeActivity::class.java).apply {
                                    startActivity(this)
                                }
                                activity?.finish()
                            }
                        }
                    }
                }
                is UIState.Error -> {

                }

                is UIState.ErrorWithData -> {
                    if(it.error!!.code == 429){
                        binding.errorContainer.showView()
                        binding.viewErrorLine.setBackgroundColor(ContextCompat.getColor(
                            requireContext(),
                            R.color.error_red
                        ))
                        binding.disableHighlight.showView()
                        invalidReferralCount++
                    } else if (it.error!!.code == 400){
                        Prefs.putBoolean("IS_JOIN_NOW_CLICKED", true)
                        Intent(requireContext(), LandingHomeActivity::class.java).apply {
                            startActivity(this)
                        }
                        activity?.finish()
                    }
                }
            }
        }.observeInLifecycle(viewLifecycleOwner)

    }

    private fun validateReferral(): Boolean {
        if(binding.etReferralCode.text.isNullOrEmpty()){
            binding.errorContainer.showView()
            binding.viewErrorLine.setBackgroundColor(ContextCompat.getColor(
                requireContext(),
                R.color.error_red
            ))
            binding.disableHighlight.showView()
            return false
        }else if (binding.etReferralCode.text.toString().trim().length < 6){
            binding.errorContainer.showView()
            binding.viewErrorLine.setBackgroundColor(ContextCompat.getColor(
                requireContext(),
                R.color.error_red
            ))
            binding.disableHighlight.showView()
            return false
        } else {
            binding.errorContainer.hideView()
            binding.viewErrorLine.setBackgroundColor(ContextCompat.getColor(
                requireContext(),
                R.color.text_light_gray
            ))
            binding.disableHighlight.hideView()
            return true
        }
    }
}