package com.lib.loopsdk.ui.feature_survey.presentation.survey_questions

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.reflect.TypeToken
import com.lib.loopsdk.R
import com.lib.loopsdk.core.customViews.LoadingDialog
import com.lib.loopsdk.core.util.*
import com.lib.loopsdk.data.remote.dto.*
import com.lib.loopsdk.databinding.ActivitySurveyQuestionsBinding
import com.lib.loopsdk.ui.BaseActivity
import com.lib.loopsdk.ui.feature_survey.data.BenefitData
import com.lib.loopsdk.ui.feature_survey.presentation.HelpBottomSheetFragment
import com.lib.loopsdk.ui.feature_survey.presentation.LeaveBottomSheetFragment
import com.lib.loopsdk.ui.feature_survey.presentation.SurveyQuizSuccessFragment
import kotlinx.coroutines.flow.onEach
import timber.log.Timber

class SurveyQuestionsActivity : BaseActivity(), View.OnClickListener,
    LeaveBottomSheetFragment.LeaveSurveyBottomSheetListener,
    SurveyDropDownOptionBottomSheetFragment.DDSelectListener,
    SurveyCBOptionsAdapter.CBSelectorListener, SurveySCOptionsAdapter.SCSelectorListener,
    SurveyQuestionAdapter.SCOtherSelectedListener {
    private lateinit var binding: ActivitySurveyQuestionsBinding
    private val viewModel: SurveyQuesAnsViewModel by viewModels()
    private val loadingDialog: LoadingDialog by lazy { LoadingDialog(this) }
    private lateinit var myContext: Context
    var isSuccess: Boolean = false
    var isHalfShow: Boolean = false
    var isLessThan10Show: Boolean = false
    var isMoreThan10Show: Boolean = false
    var savedAnswerAPICalled: Boolean = false
    var submitSurveyAPICalled: Boolean = false
    private var surveyId: String = ""
    private var benefitType: Int = 0
    private var currentProgress: Int = 0
    private lateinit var survey: SurveyDetailDto.Data
    private lateinit var questionList: List<SurveyQuestionsDto.Data.Question>
    private lateinit var viewAdapter: SurveyQuestionAdapter
    private lateinit var brandTheme: InitializeDto.Data.BrandTheme
//    private lateinit var customToast: Toast


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_survey_questions)

        binding.lifecycleOwner = this
        binding.viewModel = this.viewModel
        binding.brandTheme = Prefs.getNonPrimitiveData<InitializeDto.Data.BrandTheme>(object :
            TypeToken<InitializeDto.Data.BrandTheme>() {}.type, "BRAND_THEME")
        binding.brandThemeColors = Colors

        brandTheme = Prefs.getNonPrimitiveData(object :
            TypeToken<InitializeDto.Data.BrandTheme>() {}.type, "BRAND_THEME")

        overridePendingTransition(0, 0); //0 for no animation

        setContentView(binding.root)
        initializeVariables()
        setUpListener()
        viewModel.getSurveyQuestions(surveyId)


        viewModel.questionUiStateFlow.onEach {
            when (it) {
                is UIState.Loading -> {
                    loadingDialog.show()
                }
                is UIState.Success<*> -> {
                    loadingDialog.cancel()
                    when (it.data) {
                        is SurveyQuestionsDto.Data -> {
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

        viewModel.answerStringUiStateFlow.onEach {
            when (it) {
                is UIState.Loading -> {
                    loadingDialog.show()
                }
                is UIState.Success<*> -> {
                    loadingDialog.cancel()
                    when (it.data) {
                        is SurveyAnswerSavedDto.Status -> {
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

        viewModel.answerIntUiStateFlow.onEach {
            when (it) {
                is UIState.Loading -> {
                    loadingDialog.show()
                }
                is UIState.Success<*> -> {
                    loadingDialog.cancel()
                    when (it.data) {
                        is SurveyAnswerSavedDto.Status -> {
                            Timber.d("int sucess: ${it.data}")
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
//                            //                      FullScreenNetworkErrorActivity.startActivity(this)
//                        }
                    }
                }
                else -> {}
            }
        }.observeInLifecycle(this)

        viewModel.answerArrayUiStateFlow.onEach {
            when (it) {
                is UIState.Loading -> {
                    loadingDialog.show()
                }
                is UIState.Success<*> -> {
                    loadingDialog.cancel()
                    when (it.data) {
                        is SurveyAnswerSavedDto.Status -> {
                            Timber.d("array sucess: ${it.data.message}")
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
//                            //                     FullScreenNetworkErrorActivity.startActivity(this)
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
                        is SurveySubmitDto.Data -> {
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
        fun startActivity(context: Context, id: Long, type: Int) {
            val intent = Intent(context, SurveyQuestionsActivity::class.java)
            intent.putExtra(Constants.SURVEY_ID, id)
            intent.putExtra(Constants.BENIFIT_TYPE, type)
            context.startActivity(intent)
        }

        private const val SHORT = 1
        private const val LONG = 2
        private const val SLIDER = 3
        private const val CHECKBOX = 4
        private const val DROPDOWN = 5
        private const val RADIO = 6
    }

    private fun initializeVariables() {
        myContext = this
//        customToast = Toast(myContext)
        surveyId = intent.getStringExtra(Constants.SURVEY_ID).toString()
        benefitType = intent.getIntExtra(Constants.BENIFIT_TYPE, 0)
        survey = intent.getSerializableExtra("Single Survey") as SurveyDetailDto.Data
        questionList = ArrayList()
//        binding.progressBar.setProgressTheme(
//            R.color.bg_gray,
//            Color.parseColor(brandTheme.themeColors.primary.hexCode)
//        )

        val ld = binding.progressBar.indeterminateDrawable as LayerDrawable
        val d1 = ld.getDrawable(1)
        d1.setColorFilter(Color.parseColor(Colors.PRIMARY_BRAND_COLOR), PorterDuff.Mode.SRC_IN)
        binding.pbHozQuestions.progress = currentProgress

//        val layout = this.layoutInflater.inflate (
//            R.layout.custom_indicator_survey,  this.findViewById(R.id.main)
//        )
//        val imageView = layout.findViewById<AppCompatImageView>(R.id.ivIndicator)
//        imageView.setColorFilter(Color.parseColor(Colors.PRIMARY_BRAND_COLOR), PorterDuff.Mode.SRC_IN)
//
//        val textView = layout.findViewById<AppCompatTextView>(R.id.isb_progress)
//        textView.setTextColor(Color.parseColor(Colors.FONT_COLOR))

    }

    private fun setUpListener() {
        binding.ivClose.setOnClickListener(this)
        binding.ivInfo.setOnClickListener(this)
        binding.cvPrevious.setOnClickListener(this)
    }

    private fun setupSurveyAdapter() {
        val viewManager =
            object : LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false) {
                override fun canScrollHorizontally(): Boolean {
                    return false
                }
            }
        viewAdapter =
            SurveyQuestionAdapter(this, questionList, this)
        binding.rvSurveyQue.apply {
            layoutManager = viewManager
            setHasFixedSize(false)
            adapter = viewAdapter
        }
    }

    override fun onPause() {
        super.onPause()
//        if (customToast != null && customToast.view?.windowVisibility == View.VISIBLE) customToast.cancel()
//        customToast = Toast(null)
        overridePendingTransition(0, 0)

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
                SurveyQuizSuccessFragment(benefitData, Constants.QuizzTabType.SURVEY.type).show(supportFragmentManager, "")
            }, 100)
        }, 1000)
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivClose -> {
                onBackPressed()
            }
            R.id.ivInfo -> {
                HelpBottomSheetFragment(survey.termsAndCondition, true).show(
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
                val position = binding.rvSurveyQue.getCurrentPosition()
                if (binding.cvNext.isEnabled) {
                    //call API
                    callSaveAnswerAPI(position)

                }
            }
            R.id.cvSubmit -> {
                Timber.d("Submit clicked")
                val position = binding.rvSurveyQue.getCurrentPosition()
                if (binding.cvSubmit.isEnabled && !submitSurveyAPICalled) {
                    //call API
                    callSaveAnswerAPI(position)
                    binding.cvSubmit.setOnClickListener(null)

                }
            }
            R.id.cvPrevious -> {
                val position = binding.rvSurveyQue.getCurrentPosition()
                binding.rvSurveyQue.adapter?.notifyDataSetChanged()
                if (position > 0) {
                    binding.cvNext.showView()
                    binding.cvSubmit.hideView()
                    binding.rvSurveyQue.scrollToPosition(position - 1)
                    updateProgress(3, position)
                }

            }
        }
    }

    private fun goToNextQuestion(position: Int) {
        updateProgress(2, position)
        binding.rvSurveyQue.scrollToPosition(position + 1)
        binding.rvSurveyQue.adapter?.notifyDataSetChanged()
        checkLastQuestion(position + 1)
    }

    private fun callSaveAnswerAPI(position: Int) {
        when (questionList[position].optionType) {
            SHORT, LONG -> {
                if (questionList[position].updatedAnswerString != null) {
                    createAnswerStringType(position)
                } else {
                    goToNextQuestion(position)
                }
            }
            SLIDER -> {
                if (questionList[position].updatedAnswerInt != null) {
                    createAnswerIntType(position)
                } else {
                    goToNextQuestion(position)
                }
            }
            RADIO -> {
                if (questionList[position].updatedAnswerInt != null || questionList[position].updatedOther != null) {
                    createAnswerIntType(position)
                } else {
                    goToNextQuestion(position)

                }
            }
            CHECKBOX -> {
                if (questionList[position].updatedAnswerIntArray != null || questionList[position].updatedOther != null) {
                    createAnswerArrayType(position)
                } else {
                    goToNextQuestion(position)

                }
            }
            DROPDOWN -> {
                if (questionList[position].updatedAnswerIntArray != null) {
                    createAnswerArrayType(position)
                } else {
                    goToNextQuestion(position)

                }
            }
        }
        if (binding.pbHozQuestions.progress == questionList.size && binding.cvSubmit.isVisible() && !questionList[position].isRequired) {
            submitSurveyAPI()
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
            this.finish()
        } else {
            LeaveBottomSheetFragment(this, true, -1).show(supportFragmentManager, "")
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
        binding.cvNext.setOnClickListener(null)
        binding.cvPrevious.setOnClickListener(null)
        binding.cvSubmit.setOnClickListener(null)
        viewModel.submitSurvey(surveyId)
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
        viewModel.postAnswerStringTypeSurvey(
            surveyId,
            questionList[pos].id,
            questionList[pos].updatedAnswerString
        )

    }

    private fun createAnswerIntType(pos: Int) {
        if (questionList[pos].optionType == SLIDER || questionList[pos].options == null) {
            viewModel.postAnswerIntTypeSurvey(
                surveyId,
                questionList[pos].id,
                questionList[pos].updatedAnswerInt
            )
        } else {
            val other =
                if (!questionList[pos].updatedOther.isNullOrEmpty()) questionList[pos].updatedOther else ""
            val answer =
                if (!questionList[pos].updatedAnswerInt.isNullOrEmpty()) questionList[pos].updatedAnswerInt else ""
            Timber.d("Radio answers: $other $answer")
            viewModel.postAnswerIntTypeSurvey(
                surveyId,
                questionList[pos].id,
                answer, other
            )
        }
    }


    private fun createAnswerArrayType(pos: Int) {

        if (questionList[pos].optionType == DROPDOWN || questionList[pos].options == null) {
            val updatedArray = "[" + questionList[pos].updatedAnswerIntArray.joinToString(",") + "]"
            viewModel.postAnswerArrayTypeSurvey(surveyId, questionList[pos].id, updatedArray)
        } else {
            val other =
                if (!questionList[pos].updatedOther.isNullOrEmpty()) questionList[pos].updatedOther else ""
            Timber.d("Check 1 ${questionList[pos].updatedAnswerIntArray}")
            Timber.d("Other: ${questionList[pos].updatedOther}")
            val updatedArray =
                if (!questionList[pos].updatedAnswerIntArray.isNullOrEmpty()) "[" + questionList[pos].updatedAnswerIntArray.joinToString(
                    ","
                ) + "]" else ""
            viewModel.postAnswerArrayTypeSurvey(
                surveyId,
                questionList[pos].id,
                updatedArray,
                other
            )
        }
    }


    private fun onQuestionsReceived(questions: List<SurveyQuestionsDto.Data.Question>) {
        questionList = questions
        val maxProgress = questionList.size
        binding.pbHozQuestions.max = maxProgress
        setupSurveyAdapter()
        val position = binding.rvSurveyQue.getCurrentPosition()
        val temp = position + 1
        checkLastQuestion(temp)
        progressStatus()
    }

    private fun onSurveySubmitted(surveySubmit: SurveySubmitDto.Data) {
        val benefitData = BenefitData(surveySubmit.benefitType, surveySubmit.benefitValue, surveySubmit.messageText, surveySubmit.benefitDetails.couponClassification, surveySubmit.benefitDetails.couponLabel, surveySubmit.benefitDetails.featuredImage, surveySubmit.benefitDetails.maxDiscountValue, surveySubmit.benefitDetails.pointsToUnlock, surveySubmit.benefitDetails.discountPercent, surveySubmit.description, surveySubmit.benefitDetails.couponName, " ")
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
                if (questionList[position].progress != 1 && !questionList[position].isRequired) {
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

                    when (questionList[position - 1].optionType) {
                        SHORT, LONG -> {
                            if (questionList[position - 1].updatedAnswerString == null && questionList[position - 1].answer == null) {
                                questionList[position - 1].progress = 0
                                val progress = binding.pbHozQuestions.progress - 1
                                binding.pbHozQuestions.progress = progress
                            }
                        }
                        SLIDER -> {
                            if (questionList[position - 1].updatedAnswerInt == null && questionList[position - 1].answer == null) {
                                questionList[position - 1].progress = 0
                                val progress = binding.pbHozQuestions.progress - 1
                                binding.pbHozQuestions.progress = progress
                            }
                        }
                        RADIO -> {
                            if (questionList[position - 1].updatedAnswerInt == null && questionList[position - 1].answer == null && questionList[position].updatedOther == null) {
                                questionList[position - 1].progress = 0
                                val progress = binding.pbHozQuestions.progress - 1
                                binding.pbHozQuestions.progress = progress
                            }
                        }
                        CHECKBOX -> {
                            if (questionList[position - 1].updatedAnswerIntArray == null && questionList[position - 1].answer == null && questionList[position].updatedOther == null) {
                                questionList[position - 1].progress = 0
                                val progress = binding.pbHozQuestions.progress - 1
                                binding.pbHozQuestions.progress = progress
                            }
                        }
                        DROPDOWN -> {
                            if (questionList[position - 1].updatedAnswerIntArray == null && questionList[position - 1].answer == null) {
                                questionList[position - 1].progress = 0
                                val progress = binding.pbHozQuestions.progress - 1
                                binding.pbHozQuestions.progress = progress
                            }
                        }
                    }

                }
            }
        }

    }

    private fun progressStatus() {
        //partially filled question's progress status
        var currentPosition: Int = 0
        for (i in 0 until questionList.size) {
            if ((questionList[i].answer != null)) {
                questionList[i].progress = 1
                currentProgress = currentProgress + questionList[i].progress
                currentPosition = currentProgress
            }
        }
        binding.pbHozQuestions.progress = currentProgress
        if (currentProgress > 0) {
            binding.cvPrevious.showView()
            binding.rvSurveyQue.scrollToPosition(currentPosition)
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
//                customToast = Toast(myContext).showCustomToast(
//                    myContext,
//                    "Hurray! you are half way there",
//                    R.drawable.ic_celebrate_emoji,
//                    this
//                )
//                customToast.show()
//                this.showSurveyToast(
//                    myContext, binding.root,
//                    "Hurray! you are half way there",
//                    R.drawable.ic_celebrate_emoji
//                )
                Timber.d("Checkkkkkkkk: ${survey.partiallyFilled}")
                if(survey.partiallyFilled && survey.hasAnswered != questionList.size / 2) {
                    this.showSurveyToast(
                        myContext, binding.root,
                        "Hurray! you are half way there",
                        R.drawable.ic_celebrate_emoji
                    )
                }else if(!survey.partiallyFilled){
                    this.showSurveyToast(
                        myContext, binding.root,
                        "Hurray! you are half way there",
                        R.drawable.ic_celebrate_emoji
                    )
                }
            }
            //Quest less than 10  && progress greater than half --> hurray almost
//            if (questionList.size < 10 && binding.pbHozQuestions.progress > (questionList.size / 2) && temp > (questionList.size / 2) && !(questionList.size == 1) && !isLessThan10Show) {
//                isLessThan10Show = true
////                customToast = Toast(myContext).showCustomToast(
////                    myContext,
////                    "Hurray! You are almost there",
////                    R.drawable.ic_celebrate_emoji,
////                    this
////                )
////                customToast.show()
//                if(survey.partiallyFilled && survey.hasAnswered < questionList.size / 2) {
//                    this.showSurveyToast(
//                        myContext, binding.root,
//                        "Hurray! You are almost there",
//                        R.drawable.ic_celebrate_emoji,
//                    )
//                }else if(!survey.partiallyFilled){
//                    this.showSurveyToast(
//                        myContext, binding.root,
//                        "Hurray! You are almost there",
//                        R.drawable.ic_celebrate_emoji,
//                    )
//                }
//
//            }

            //Quest more than 8, last 3 ques
            if (questionList.size > 8 && binding.pbHozQuestions.progress == (questionList.size - 3) && temp == (questionList.size - 3) && !isMoreThan10Show) {
                isMoreThan10Show = true
//                customToast = Toast(myContext).showCustomToast(
//                    myContext,
//                    "Almost there! just 3 more to go",
//                    R.drawable.ic_star_emoji,
//                    this
//                )
//                customToast.show()
                if(survey.partiallyFilled && survey.hasAnswered != (questionList.size - 3)) {
                    this.showSurveyToast(myContext,binding.root,
                        "Almost there! just 3 more to go",
                        R.drawable.ic_star_emoji)
                }else if(!survey.partiallyFilled){
                    this.showSurveyToast(myContext,binding.root,
                        "Almost there! just 3 more to go",
                        R.drawable.ic_star_emoji)
                }

            }

        }

    }

    override fun onDestroy() {
        super.onDestroy()
//        if (customToast != null) {
//            customToast.cancel()
//        }
    }

    fun onAnswerSaved(message: String) {
        Timber.d("On answer saved called")
        val position = binding.rvSurveyQue.getCurrentPosition()
        updateProgress(1, position)
        checkLastQuestion(position + 1)

        if (binding.pbHozQuestions.progress == questionList.size) {
            savedAnswerAPICalled = true
            submitSurveyAPI()
        } else {
            binding.rvSurveyQue.adapter?.notifyDataSetChanged()
            binding.rvSurveyQue.scrollToPosition(position + 1)
        }

    }

    fun callDropDownBottomSheet(
        options: List<SurveyQuestionsDto.Data.Question.Option>,
        position: Int
    ) {
        SurveyDropDownOptionBottomSheetFragment(options, this, this, position).show(
            supportFragmentManager,
            ""
        )
    }

    override fun onDDSelected(position: Int, id: String) {
        Timber.d("DD id: $id")
        val selectedDDOptions: ArrayList<String> = ArrayList()
        selectedDDOptions.add(id)
        questionList[position].updatedAnswerIntArray = selectedDDOptions
        binding.rvSurveyQue.adapter?.notifyDataSetChanged()

        if (questionList[position].updatedAnswerIntArray.isNullOrEmpty()) {
            enableNextButton()
        }
    }

    override fun onCBSelected(position: Int, id: String) {
        var selectedCBOptions: ArrayList<String> = ArrayList()
        val currentPosition = binding.rvSurveyQue.getCurrentPosition()
        if (!questionList[currentPosition].updatedAnswerIntArray.isNullOrEmpty()) {
            selectedCBOptions = questionList[currentPosition].updatedAnswerIntArray
            if (selectedCBOptions.contains(id)) {
                selectedCBOptions.remove(id)
            } else {
                selectedCBOptions.add(id)
            }
        } else {
            selectedCBOptions.add(id)
        }
        Timber.d("Selected CB: ${selectedCBOptions}")
        questionList[currentPosition].updatedAnswerIntArray = selectedCBOptions
        if (selectedCBOptions.isNotEmpty()) {
            enableNextButton()
        } else disableNextButton()
        binding.rvSurveyQue.scrollToPosition(currentPosition)
    }

    override fun onSCSelected(view: View, position: Int, id: String) {
        Timber.d("Checkkkk: ${questionList[position].updatedAnswerInt}")
        if (questionList[position].updatedAnswerInt.isNullOrEmpty() || questionList[position].updatedAnswerInt != id) {
            questionList[position].updatedAnswerInt = id
        } else {
            questionList[position].updatedAnswerInt = ""
            disableNextButton()
        }

        if (questionList[position].updatedAnswerInt.isNotEmpty()) {
            Timber.d("Radio ans: ${questionList[position].updatedAnswerInt}")
            questionList[position].updatedOther = ""
            questionList[position].savedRadioAns = ""
            //               binding.rvSurveyQue.setItemAnimator(null)
            //               binding.rvSurveyQue.adapter?.notifyDataSetChanged()

            enableNextButton()
        }
    }

    override fun onSCOtherSelected(position: Int, id: String) {
        questionList[position].updatedOther = id
        Timber.d("SC other clicked: ${questionList[position].updatedOther}")
        if (questionList[position].updatedOther.isNotEmpty()) {
            questionList[position].updatedAnswerInt = ""
            questionList[position].savedRadioAns = ""
            binding.rvSurveyQue.adapter?.notifyDataSetChanged()
            enableNextButton()
        }

    }

}