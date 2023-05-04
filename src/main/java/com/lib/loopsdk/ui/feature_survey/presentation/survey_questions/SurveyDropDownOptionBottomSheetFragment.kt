package com.lib.loopsdk.ui.feature_survey.presentation.survey_questions

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.reflect.TypeToken
import com.lib.loopsdk.R
import com.lib.loopsdk.core.util.Colors
import com.lib.loopsdk.core.util.Prefs
import com.lib.loopsdk.data.remote.dto.InitializeDto
import com.lib.loopsdk.data.remote.dto.SurveyQuestionsDto
import com.lib.loopsdk.databinding.FragmentSurveyDropDownOptionBottomSheetBinding


class SurveyDropDownOptionBottomSheetFragment(
    val options: List<SurveyQuestionsDto.Data.Question.Option>,
    private val myContext: Context,
    private val listener: DDSelectListener,
    private val position: Int
) : BottomSheetDialogFragment(), SurveyDDOptionsAdapter.DDSelectedListener{

    private lateinit var binding:  FragmentSurveyDropDownOptionBottomSheetBinding

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_AppCompat_Light_NoActionBar_FullScreen)
    }
    private fun setupRecyclerview() {
        val viewAdapter= SurveyDDOptionsAdapter(myContext,options,this)
        val viewManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvdropDownChoice.apply {
            isNestedScrollingEnabled = false
            adapter = viewAdapter
            layoutManager = viewManager
            setHasFixedSize(true)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSurveyDropDownOptionBottomSheetBinding.inflate(
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

        setupRecyclerview()
    }

    private fun closeBottomSheet() {
        dismiss()
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
    }

    override fun onDDClicked(id: String) {
        listener.onDDSelected(position, id)
        closeBottomSheet()
    }
    interface DDSelectListener {
        fun onDDSelected(position: Int, id: String)
    }
}