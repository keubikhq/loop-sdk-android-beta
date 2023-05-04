package com.lib.loopsdk.ui.feature_survey.presentation.quizzlet_list

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import com.google.gson.reflect.TypeToken
import com.lib.loopsdk.R
import com.lib.loopsdk.core.customViews.LoadingDialog
import com.lib.loopsdk.core.util.*
import com.lib.loopsdk.data.remote.dto.InitializeDto
import com.lib.loopsdk.data.remote.dto.SurveyAvailableDto
import com.lib.loopsdk.databinding.FragmentSurveyAvailableBinding
import com.lib.loopsdk.ui.feature_landing_home.presentation.LandingHomeActivity
import com.lib.loopsdk.ui.feature_survey.presentation.survey_quiz_detail.SurveyQuizDetailActivity
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import java.lang.Exception


class SurveyAvailableFragment : Fragment(), SurveyAvailableAdapter.SurveyListener {
    private lateinit var binding: FragmentSurveyAvailableBinding
    private val viewModel: QuizzletListViewModel by activityViewModels()
    private val loadingDialog: LoadingDialog by lazy { LoadingDialog(requireContext()) }
    private lateinit var surveyAdapter: SurveyAvailableAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_survey_available, container,false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        val brandTheme = Prefs.getNonPrimitiveData<InitializeDto.Data.BrandTheme>(object: TypeToken<InitializeDto.Data.BrandTheme>(){}.type, "BRAND_THEME")
        binding.brandTheme = brandTheme
        binding.brandThemeColors = Colors
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        binding.root.alpha = 0f
        binding.root.translationY = Utils.dpToPx(requireContext(), 10).toFloat()

        viewModel.getAvailableSurveyList()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnExploreGames.setOnClickListener {
            LandingHomeActivity.startActivity(requireContext(), "gameArena")
            activity?.finish()
        }
        setupSurveyAdapter()

        viewModel.surveyUiStateFlow.onEach {
            when(it){
                is UIState.Loading -> {
 //                   loadingDialog.show()
                    Timber.d("Loading state")
                }
                is UIState.Success<*> ->{
                    loadingDialog.cancel()
                    when(it.data){
                        is SurveyAvailableDto.Data ->{
                            Handler().postDelayed({
                                binding.root.animate()
                                    .translationY(0f)
                                    .alpha(1f)
                                    .setDuration(750)
                            },200)
                            if(it.data.survey.isNullOrEmpty()){
                                binding.nothingFoundContainer.showView()
                                binding.rvSurveys.hideView()
                            }else{
                                binding.nothingFoundContainer.hideView()
                                binding.rvSurveys.showView()
                                surveyAdapter.clearAllData()
                                surveyAdapter.addData(it.data.survey)
                            }
                        }
                    }
                }
                is UIState.Error ->{
                    loadingDialog.cancel()
                    if (it.message == "NetworkError") {
                        FullScreenNetworkErrorActivity.startActivity(this, requireContext())
                    }
                    try {
//                        val errorCode = it.message.toInt()
//                        when (errorCode) {
//                            401 -> {
//                                CommonDialogUtils.showSingleButtonLogoutDialog(requireActivity(), "Session Expired!", null)
//                            }
//                            422 -> {
//                                binding.clServerError.showView()
//                                binding.rvAvailableTask.hideView()
//                            }
//                        }
                    }catch (e: Exception){
                        if(it.message == "NetworkError"){
           //                 FullScreenNetworkErrorActivity.startActivity(this, requireContext())
                        }
                    }
                }
                else -> {}
            }
        }.observeInLifecycle(viewLifecycleOwner)
    }

    private fun setupSurveyAdapter(){
        surveyAdapter = SurveyAvailableAdapter(requireContext(), listener =  this)
        binding.rvSurveys.apply {
            setHasFixedSize(false)
            adapter = surveyAdapter
            isNestedScrollingEnabled = false
        }
    }

    override fun onSurveyClicked(
        position: Int,
        surveyId: String,
        survey: SurveyAvailableDto.Data.Survey
    ) {
        SurveyQuizDetailActivity.startActivity(requireContext(),surveyId, true, 0)
//        val map = HashMap<String, Any>()
//        map["Survey ID"] = survey.id
//        map["Survey Name"] = survey.name
//        map["Survey Description"] = survey.description
//        if(survey.partiallyFilled){
//            map["Survey Status"] = "Partially Filled"
//        }else map["Survey Status"] = "Available"
//        map["Total Questions"] = survey.totalQuestion
//        map["Total Survey Benefit"] = "1"
//        map["Validity"] = survey.endDate
//        when(survey.benefitType) {
//            1 -> {
//                map["Benefit Type"] = "Coupons"
//            }
//            2 -> {
//                map["Benefit Type"] = "Rewards"
//            }
//            3 -> {
//                map["Benefit Type"] = "Points"
//            }
//            4 -> {
//                map["Benefit Type"] = "Gems"
//            }
//            5 -> {
//                map["Benefit Type"] = "XP"
//            }
//        }
//        CleverTapUtils.postCleverTapEvent(requireContext(), "Survey Detail", map)
    }
}