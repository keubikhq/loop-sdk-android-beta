package com.lib.loopsdk.ui.feature_survey.presentation

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.reflect.TypeToken
import com.lib.loopsdk.R
import com.lib.loopsdk.core.util.Colors
import com.lib.loopsdk.core.util.Prefs
import com.lib.loopsdk.data.remote.dto.InitializeDto
import com.lib.loopsdk.databinding.FragmentLeaveSurveyBinding

class LeaveBottomSheetFragment (
    private val listener: LeaveSurveyBottomSheetListener,
    private val isSurvey: Boolean,
    private val quizType: Int
): BottomSheetDialogFragment() ,View.OnClickListener {

    private lateinit var binding : FragmentLeaveSurveyBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog: BottomSheetDialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
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

    interface LeaveSurveyBottomSheetListener {
        fun onLeaveSurveyClicked()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_AppCompat_Light_NoActionBar_FullScreen)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLeaveSurveyBinding.inflate(
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
        binding.tvSurveyHeader.text = if(isSurvey) requireContext().getString(R.string.leave_surevy_quiz).format("survey") else requireContext().getString(R.string.leave_surevy_quiz).format("quiz")
        if(isSurvey){
            binding.tvDesc.text = requireContext().getString(R.string.leave_survey_quiz_info).format("survey")
        }else{
            if(quizType == 0){
                binding.tvDesc.text = requireContext().getString(R.string.leave_trivia_quiz_info)
            }else binding.tvDesc.text = requireContext().getString(R.string.leave_survey_quiz_info).format("quiz")
        }

        setUpListener()
    }

    private fun setUpListener()
    {
        binding.btnLeave.setOnClickListener(this)
        binding.btnCancel.setOnClickListener(this)
        binding.cvSlider.setOnClickListener(this)

    }
    private fun closeBottomSheet() {
        dismiss()
    }
    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.ivSBack, R.id.btnCancel ->{
                closeBottomSheet()
            }
            R.id.btnLeave ->{
                listener.onLeaveSurveyClicked()
                closeBottomSheet()
            }
        }
    }
}