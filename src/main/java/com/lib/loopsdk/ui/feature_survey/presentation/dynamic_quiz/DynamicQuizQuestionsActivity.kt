package com.lib.loopsdk.ui.feature_survey.presentation.dynamic_quiz

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.reflect.TypeToken
import com.lib.loopsdk.R
import com.lib.loopsdk.core.customViews.LoadingDialog
import com.lib.loopsdk.core.util.*
import com.lib.loopsdk.data.remote.dto.*
import com.lib.loopsdk.databinding.ActivityDynamicQuizQuestionsBinding
import com.lib.loopsdk.ui.BaseActivity
import com.lib.loopsdk.ui.feature_survey.data.BenefitData
import com.lib.loopsdk.ui.feature_survey.presentation.LeaveBottomSheetFragment
import com.lib.loopsdk.ui.feature_survey.presentation.HelpBottomSheetFragment
import com.lib.loopsdk.ui.feature_survey.presentation.SurveyQuizSuccessFragment
import kotlinx.coroutines.flow.onEach
import timber.log.Timber

class DynamicQuizQuestionsActivity : BaseActivity(), View.OnClickListener,
    LeaveBottomSheetFragment.LeaveSurveyBottomSheetListener, DynamicOptionsAdapter.SCSelectorListener {

    private lateinit var binding: ActivityDynamicQuizQuestionsBinding
    private val viewModel: DynamicQuesAnsViewModel by viewModels()
    private val loadingDialog: LoadingDialog by lazy { LoadingDialog(this) }
    private lateinit var myContext: Context
    var isSuccess: Boolean = false
    var isHalfShow: Boolean = false
    var isLessThan10Show: Boolean = false
    var isMoreThan10Show: Boolean = false
    var savedAnswerAPICalled: Boolean = false
    var submitSurveyAPICalled: Boolean = false
    private var quizId: String = ""
    private var benefitType: Int = 0
    private var currentProgress: Int = 0
    private lateinit var quiz: QuizDetailDto.Data
    private lateinit var questionList: List<QuizQuestionsDto.Data.Question>
    private lateinit var viewAdapter: DynamicQuesAdapter
    private lateinit var brandTheme: InitializeDto.Data.BrandTheme
    private lateinit var customToast: Toast

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_dynamic_quiz_questions)

        binding.lifecycleOwner = this
        binding.viewModel = this.viewModel
        binding.brandTheme = Prefs.getNonPrimitiveData<InitializeDto.Data.BrandTheme>(object :
            TypeToken<InitializeDto.Data.BrandTheme>() {}.type, "BRAND_THEME")
        binding.brandThemeColors = Colors

        brandTheme = Prefs.getNonPrimitiveData(object :
            TypeToken<InitializeDto.Data.BrandTheme>() {}.type, "BRAND_THEME")

        overridePendingTransition(0, 0);
        initializeVariables()
        setUpListener()

        viewModel.getQuizQuestions(quizId)

        viewModel.questionUiStateFlow.onEach {
            when (it) {
                is UIState.Loading -> {
                    loadingDialog.show()
                }
                is UIState.Success<*> -> {
                    loadingDialog.cancel()
                    when (it.data) {
                        is QuizQuestionsDto.Data -> {
                            questionList = it.data.questions
                            onQuestionsReceived(questionList)
                        }
                    }
                }
                is UIState.Error -> {
                    loadingDialog.cancel()
                    if (it.message == "NetworkError") {
                        FullScreenNetworkErrorActivity.startActivity(this)
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
                    } catch (e: java.lang.Exception) {
//                        if (it.message == "NetworkError") {
//                            //                          FullScreenNetworkErrorActivity.startActivity(this)
//                        }
                    }
                }
                else -> {}
            }
        }.observeInLifecycle(this)

        viewModel.answerUiStateFlow.onEach {
            when (it) {
                is UIState.Loading -> {
                    loadingDialog.show()
                }
                is UIState.Success<*> -> {
                    loadingDialog.cancel()
                    when (it.data) {
                        is QuizAnswerSaveDto.Status -> {
                            Timber.d("string sucess: ${it.data.message}")
                            onAnswerSaved(it.data.message)
                        }
                    }
                }
                is UIState.Error -> {
                    loadingDialog.cancel()
                    if (it.message == "NetworkError") {
                        FullScreenNetworkErrorActivity.startActivity(this)
                    }
                    AlertDialogUtils.showSingleButtonAlertDialog(myContext, it.message)
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
                    } catch (e: java.lang.Exception) {
//                        if (it.message == "NetworkError") {
//                            //                        FullScreenNetworkErrorActivity.startActivity(this)
//                        }
                    }
                }
                else -> {}
            }
        }.observeInLifecycle(this)

        viewModel.submitUiStateFlow.onEach {
            when (it) {
                is UIState.Loading -> {
                    loadingDialog.show()
                }
                is UIState.Success<*> -> {
                    loadingDialog.cancel()
                    when (it.data) {
                        is QuizSubmitDto.Data -> {
                            onSurveySubmitted(it.data)
                        }
                    }
                }
                is UIState.Error -> {
                    loadingDialog.cancel()
                    if (it.message == "NetworkError") {
                        FullScreenNetworkErrorActivity.startActivity(this)
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
                    } catch (e: java.lang.Exception) {
//                        if (it.message == "NetworkError") {
//                            //                     FullScreenNetworkErrorActivity.startActivity(this)
//                        }
                    }
                }
                else -> {}
            }
        }.observeInLifecycle(this)
    }

    companion object {
        fun startActivity(context: Context, id: String, type: Int) {
            val intent = Intent(context, DynamicQuizQuestionsActivity::class.java)
            intent.putExtra("quizId", id)
            intent.putExtra(Constants.BENIFIT_TYPE, type)
            context.startActivity(intent)
        }
    }

    private fun initializeVariables() {
        myContext = this
        customToast = Toast(myContext)
        quizId = intent.getStringExtra("quizId").toString()
        benefitType = intent.getIntExtra(Constants.BENIFIT_TYPE, 0)
        quiz = intent.getSerializableExtra("Single Quiz") as QuizDetailDto.Data
        questionList = ArrayList()
        val ld = binding.progressBar.indeterminateDrawable as LayerDrawable
        val d1 = ld.getDrawable(1)
        d1.setColorFilter(Color.parseColor(Colors.PRIMARY_BRAND_COLOR), PorterDuff.Mode.SRC_IN)
        binding.pbHozQuestions.progress = currentProgress

    }
    private fun setUpListener() {
        binding.ivClose.setOnClickListener(this)
        binding.ivInfo.setOnClickListener(this)
        binding.cvPrevious.setOnClickListener(this)
    }

    private fun setupQuizAdapter() {
        val viewManager =
            object : LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false) {
                override fun canScrollHorizontally(): Boolean {
                    return false
                }
            }
        viewAdapter =
            DynamicQuesAdapter(this, questionList)
        binding.rvQuizQue.apply {
            layoutManager = viewManager
            setHasFixedSize(false)
            adapter = viewAdapter
        }
    }
    private fun progressAnim(benefitData: BenefitData) {
        //remove Top handler
        isSuccess = true
        binding.progressBar.showView()
        binding.backProgress.hideView()
        Handler().postDelayed({
            binding.progressBar.invisibleView()
            binding.backProgressComplete.setColorFilter(Color.parseColor(Colors.PRIMARY_BRAND_COLOR), PorterDuff.Mode.SRC_IN)
            binding.backProgressComplete.showView()
            Handler().postDelayed({
                SurveyQuizSuccessFragment(benefitData, Constants.QuizzTabType.QUIZ.type).show(supportFragmentManager, "")
            }, 100)
        }, 1000)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivClose -> {
                onBackPressed()
            }
            R.id.ivInfo -> {
                HelpBottomSheetFragment(quiz.termsAndConditions, false).show(
                    supportFragmentManager,
                    ""
                )
                val map = HashMap<String, Any>()
//                map["Survey ID"] = surveyId
//                map["Survey Name"] = survey.name
//                map["Survey Description"] = survey.description
//                CleverTapUtils.postCleverTapEvent(myContext, "Survey Information", map)
            }
            R.id.cvNext -> {
                Timber.d("Next clicked")
                val position = binding.rvQuizQue.getCurrentPosition()
                if (binding.cvNext.isEnabled) {
                    //call API
                    callSaveAnswerAPI(position)

                }
            }
            R.id.cvSubmit -> {
                Timber.d("Submit clicked")
                val position = binding.rvQuizQue.getCurrentPosition()
                if (binding.cvSubmit.isEnabled && !submitSurveyAPICalled) {
                    //call API
                    callSaveAnswerAPI(position)
                    binding.cvSubmit.setOnClickListener(null)

                }
            }
            R.id.cvPrevious -> {
                val position = binding.rvQuizQue.getCurrentPosition()
                binding.rvQuizQue.adapter?.notifyDataSetChanged()
                if (position > 0) {
                    binding.cvNext.showView()
                    binding.cvSubmit.hideView()
                    binding.rvQuizQue.scrollToPosition(position - 1)
                    updateProgress(3, position)
                }

            }
        }
    }

    private fun goToNextQuestion(position: Int) {
        updateProgress(2, position)
        binding.rvQuizQue.scrollToPosition(position + 1)
        binding.rvQuizQue.adapter?.notifyDataSetChanged()
        checkLastQuestion(position + 1)
    }
    private fun callSaveAnswerAPI(position: Int) {
        if (questionList[position].updatedAnsString != null) {
            createAnswerStringType(position)
        } else {
            goToNextQuestion(position)
        }
    }

    fun enableNextButton() {
        binding.cvNext.alpha = 1f
        binding.cvSubmit.alpha = 1f
        binding.cvNext.setOnClickListener(this)
        binding.cvSubmit.setOnClickListener(this)
    }

    fun disableNextButton() {
        binding.cvNext.alpha = 0.3f
        binding.cvSubmit.alpha = 0.3f
        binding.cvNext.setOnClickListener(null)
        binding.cvSubmit.setOnClickListener(null)
    }

    fun showHidePrevious(position: Int) {
        Timber.d("Show hide previous called: $position")
        if (position > 0) {
            binding.cvPrevious.showView()
        } else if (position == 0) {
            binding.cvPrevious.hideView()
        }
    }

    override fun onBackPressed() {
        if (isSuccess) {
            finish()
        } else {
            LeaveBottomSheetFragment(this, false, 1).show(supportFragmentManager, "")
        }
    }

    override fun onLeaveSurveyClicked() {
        finish()

//        val map = HashMap<String, Any>()
//        map["Survey ID"] = surveyId
//        map["Survey Name"] = survey.name
//        map["Survey Description"] = survey.description
//        map["Survey Benefits"] = survey.benefitDetails
//        map["Total Questions Filled"] = survey.answeredQuestion
//        map["Validity"] = survey.endDateTime
//        if(survey.defaultType != 0 && survey.defaultType != survey.benefitType){
//            map["Survey Benefits"] = "[Coupons, Points]"
//        }else map["Survey Benefits"] = if(survey.benefitType == 1) "Coupons" else "Points"
//        CleverTapUtils.postCleverTapEvent(myContext, "Exit Survey", map)

    }

    private fun submitSurveyAPI() {
        //To submit Survey
        isSuccess = true
        binding.cvNext.setOnClickListener(null)
        binding.cvPrevious.setOnClickListener(null)
        binding.cvSubmit.setOnClickListener(null)
        viewModel.submitQuiz(quizId)
        submitSurveyAPICalled = true


//            val map = HashMap<String, Any>()
//            map["Survey ID"] = surveyId
//            map["Survey Name"] = survey.name
//            map["Survey Description"] = survey.description
//            if(survey.defaultType != 0 && survey.defaultType != survey.benefitType){
//                map["Survey Benefits"] = "[Coupons, Points]"
//            }else map["Survey Benefits"] = if(survey.benefitType == 1) "Coupons" else "Points"
//            map["Total Questions"] = survey.totalQuestion
//            map["Validity"] = survey.endDateTime
//            map["Survey Status"] = if(survey.answeredQuestion != 0) "Partially Filled" else "Available"
//            when(survey.benefitType) {
//            1 -> {
//                map["Benefit Earned"] = "Coupons"
//            }
//            3 -> {
//                map["Benefit Earned"] = "Points"
//             }
//          }
//            CleverTapUtils.postCleverTapEvent(myContext, "Submit Survey", map)

    }

    private fun createAnswerStringType(pos: Int) {
        viewModel.postAnswerQuiz(
            quizId,
            questionList[pos].id,
            questionList[pos].updatedAnsString
        )
    }

    private fun onQuestionsReceived(questions: List<QuizQuestionsDto.Data.Question>) {
        questionList = questions
        val maxProgress = questionList.size
        binding.pbHozQuestions.max = maxProgress
        setupQuizAdapter()
        val position = binding.rvQuizQue.getCurrentPosition()
        val temp = position + 1
        checkLastQuestion(temp)
        progressStatus()
    }

    private fun onSurveySubmitted(quizSubmit: QuizSubmitDto.Data) {
        val benefitData = BenefitData(quizSubmit.benefitType, quizSubmit.benefitValue, quizSubmit.messageText, quizSubmit.benefitDetails.couponClassification, quizSubmit.benefitDetails.couponLabel, quizSubmit.benefitDetails.featuredImage, quizSubmit.benefitDetails.maxDiscountValue, quizSubmit.benefitDetails.pointsToUnlock, quizSubmit.benefitDetails.discountPercent, quizSubmit.description, quizSubmit.benefitDetails.couponName, quizSubmit.defaultSuccessMessage)
        progressAnim(benefitData)
    }

    private fun updateProgress(answerStatus: Int, position: Int) {
        //set Progress
        //
        when (answerStatus) {
            1 -> {
                //if answer saved ---progress ++
                if (questionList[position].progress != 1) {
                    questionList[position].progress = 1
                    if (binding.pbHozQuestions.progress < binding.pbHozQuestions.max) {
                        val progress =
                            binding.pbHozQuestions.progress + questionList[position].progress

                        binding.pbHozQuestions.progress = progress
                    }
                }
            }
            2 -> {
                //if not required && not saved  ----progress ++
                if (questionList[position].progress != 1) {
                    questionList[position].progress = 1
                    if (binding.pbHozQuestions.progress < binding.pbHozQuestions.max) {
                        val progress =
                            binding.pbHozQuestions.progress + questionList[position].progress
                        binding.pbHozQuestions.progress = progress
                    }
                }
            }
            3 -> {
                //previous clicked  && not required ----progress --
                if (questionList[position - 1].progress == 1) {

                    if (questionList[position - 1].updatedAnsString == null && questionList[position - 1].answeredOptionId == null) {
                        questionList[position - 1].progress = 0
                        val progress = binding.pbHozQuestions.progress - 1
                        binding.pbHozQuestions.progress = progress
                            }


                }
            }
        }
    }

    private fun progressStatus() {
        //partially filled question's progress status
        var currentPosition: Int = 0
        for (i in 0 until questionList.size) {
            if ((questionList[i].answeredOptionId != null)) {
                questionList[i].progress = 1
                currentProgress = currentProgress + questionList[i].progress
                currentPosition = currentProgress
            }
        }
        binding.pbHozQuestions.progress = currentProgress
        if (currentProgress > 0) {
            binding.cvPrevious.showView()
            binding.rvQuizQue.scrollToPosition(currentPosition)
            checkLastQuestion(currentPosition)
        }
    }

    private fun checkLastQuestion(temp: Int) {
        //check last Question
//        val position = binding.rvSurveyQue.getCurrentPosition()
//        val temp=position+1

        if (temp >= questionList.size - 1 || 1 == questionList.size) {
            binding.cvNext.hideView()
            binding.cvSubmit.showView()
        } else {
            binding.cvSubmit.hideView()
            binding.cvNext.showView()
            binding.llNext.showView()
            binding.tvContinueHome.text = "Next"
            Timber.d("Done@{${binding.tvContinueHome.text}}")

            //        //show Anim for half completed survey
            if (binding.pbHozQuestions.progress == (questionList.size / 2) && temp == (questionList.size / 2) && !(questionList.size == 1) && !isHalfShow) {
                isHalfShow = true
                if(quiz.partiallyFilled == 1 && quiz.hasAnsweredAll != questionList.size / 2) {
                    customToast = Toast(myContext).showCustomToast(
                        myContext,
                        "Hurray! you are half way there",
                        R.drawable.ic_gift_emoji,
                        this
                    )
                    customToast.show()
                }else if(quiz.partiallyFilled == 0){
                    customToast = Toast(myContext).showCustomToast(
                        myContext,
                        "Hurray! you are half way there",
                        R.drawable.ic_gift_emoji,
                        this
                    )
                    customToast.show()
                }

            }
//            //Quest less than 10  && progress greater than half --> hurray almost
//            if (questionList.size < 10 && binding.pbHozQuestions.progress > (questionList.size / 2) && temp > (questionList.size / 2) && !(questionList.size == 1) && !isLessThan10Show) {
//                isLessThan10Show = true
//                if(quiz.partiallyFilled == 1 && quiz.hasAnsweredAll < questionList.size / 2) {
//                    customToast = Toast(myContext).showCustomToast(
//                        myContext,
//                        "Hurray! You are almost there",
//                        R.drawable.ic_gift_emoji,
//                        this
//                    )
//                    customToast.show()
//                }else if(quiz.partiallyFilled == 0){
//                    customToast = Toast(myContext).showCustomToast(
//                        myContext,
//                        "Hurray! You are almost there",
//                        R.drawable.ic_gift_emoji,
//                        this
//                    )
//                    customToast.show()
//                }
//            }

            //Quest more than 8, last 3 ques
            if (questionList.size > 8 && binding.pbHozQuestions.progress == (questionList.size - 3) && temp == (questionList.size - 3) && !isMoreThan10Show) {
                isMoreThan10Show = true

                if(quiz.partiallyFilled == 1 && quiz.hasAnsweredAll != (questionList.size - 3)) {
                    customToast = Toast(myContext).showCustomToast(
                        myContext,
                        "Almost there! just 3 more to go",
                        R.drawable.ic_star_emoji,
                        this
                    )
                    customToast.show()
                }else if(quiz.partiallyFilled == 0){
                    customToast = Toast(myContext).showCustomToast(
                        myContext,
                        "Almost there! just 3 more to go",
                        R.drawable.ic_star_emoji,
                        this
                    )
                    customToast.show()
                }
            }

        }

    }

    override fun onDestroy() {
        super.onDestroy()
        if (customToast != null) {
            customToast.cancel()
        }
    }
    fun onAnswerSaved(message: String) {
        Timber.d("On answer saved called")
        val position = binding.rvQuizQue.getCurrentPosition()
        updateProgress(1, position)
        checkLastQuestion(position + 1)

        if (binding.pbHozQuestions.progress == questionList.size) {
            savedAnswerAPICalled = true
            submitSurveyAPI()
        } else {
            binding.rvQuizQue.adapter?.notifyDataSetChanged()
            binding.rvQuizQue.scrollToPosition(position + 1)
        }

    }

    override fun onSCSelected(view: View, position: Int, id: String) {
        Timber.d("Checkkkk: ${questionList[position].updatedAnsString}")
        if (questionList[position].updatedAnsString.isNullOrEmpty() || questionList[position].updatedAnsString != id) {
            questionList[position].updatedAnsString = id
        } else {
            questionList[position].updatedAnsString = ""
            disableNextButton()
        }

        if (questionList[position].updatedAnsString.isNotEmpty()) {
            Timber.d("Radio ans: ${questionList[position].updatedAnsString}")
//            questionList[position].updatedOther = ""
//            questionList[position].savedRadioAns = ""
            //               binding.rvSurveyQue.setItemAnimator(null)
            //               binding.rvSurveyQue.adapter?.notifyDataSetChanged()

            enableNextButton()
        }
    }
}