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
import com.lib.loopsdk.data.remote.dto.QuizAvailableDto
import com.lib.loopsdk.databinding.FragmentQuizAvailableBinding
import com.lib.loopsdk.ui.feature_landing_home.presentation.LandingHomeActivity
import com.lib.loopsdk.ui.feature_survey.presentation.survey_quiz_detail.SurveyQuizDetailActivity
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import java.lang.Exception

class QuizAvailableFragment : Fragment(), QuizAvailableAdapter.QuizListener {
    private lateinit var binding: FragmentQuizAvailableBinding
    private val viewModel: QuizzletListViewModel by activityViewModels()
    private val loadingDialog: LoadingDialog by lazy { LoadingDialog(requireContext()) }
    private lateinit var quizAdapter:QuizAvailableAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_quiz_available, container,false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        val brandTheme = Prefs.getNonPrimitiveData<InitializeDto.Data.BrandTheme>(object: TypeToken<InitializeDto.Data.BrandTheme>(){}.type, "BRAND_THEME")
        binding.brandTheme = brandTheme
        binding.brandThemeColors = Colors
        return binding.root
    }

    override fun onResume() {
        super.onResume()
//        val map = HashMap<String, Any>()
//        map["Tab Name"] = "Available"
//        CleverTapUtils.postCleverTapEvent(requireContext(), "Survey Tab", map)
        binding.root.alpha = 0f
        binding.root.translationY = Utils.dpToPx(requireContext(), 10).toFloat()
        viewModel.getAvailableQuizList()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupQuizAdapter()
        binding.btnExploreGames.setOnClickListener {
            LandingHomeActivity.startActivity(requireContext(), "gameArena")
            activity?.finish()
        }
        viewModel.quizUiStateFlow.onEach {
            when(it){
                is UIState.Loading -> {
//                    loadingDialog.show()
                    Timber.d("Loading state")
                }
                is UIState.Success<*> ->{
                    loadingDialog.cancel()
                    when(it.data){
                        is QuizAvailableDto.Data ->{
                            Handler().postDelayed({
                                binding.root.animate()
                                    .translationY(0f)
                                    .alpha(1f)
                                    .setDuration(750)
                            },200)
                            if(it.data.quiz.isNullOrEmpty()){
                                binding.nothingFoundContainer.showView()
                                binding.rvQuiz.hideView()
                            }else{
                                binding.nothingFoundContainer.hideView()
                                binding.rvQuiz.showView()
                                quizAdapter.clearAllData()
                                quizAdapter.addData(it.data.quiz)
                            }
                        }
                    }
                }
                is UIState.Error ->{
                    loadingDialog.cancel()
                    if(it.message == "NetworkError"){
                        Timber.d("Error inside: ${it.message}")
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
                        Timber.d("Error: ${it.message}")
//                        if(it.message == "NetworkError"){
//                            Timber.d("Error inside: ${it.message}")
//                            FullScreenNetworkErrorActivity.startActivity(this, requireContext())
//                        }
                    }
                }
                else -> {}
            }
        }.observeInLifecycle(viewLifecycleOwner)
    }

    private fun setupQuizAdapter(){
       quizAdapter =
            QuizAvailableAdapter(requireContext(), listener = this)
        binding.rvQuiz.apply {
            setHasFixedSize(false)
            adapter = quizAdapter
            isNestedScrollingEnabled = false
        }
    }

    override fun onQuizClicked(
        position: Int,
        quizId: String,
        quizType: Int,
        quiz: QuizAvailableDto.Data.Quiz
    ) {
        SurveyQuizDetailActivity.startActivity(requireContext(),quizId, false, quizType)
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