package com.lib.loopsdk.ui.feature_survey.presentation.trivia_quiz

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
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
import com.lib.loopsdk.databinding.ActivityTriviaQuizQuesBinding
import com.lib.loopsdk.ui.BaseActivity
import com.lib.loopsdk.ui.feature_survey.data.BenefitData
import com.lib.loopsdk.ui.feature_survey.presentation.HelpBottomSheetFragment
import com.lib.loopsdk.ui.feature_survey.presentation.LeaveBottomSheetFragment
import com.lib.loopsdk.ui.feature_survey.presentation.SurveyQuizSuccessFragment
import com.lib.loopsdk.ui.feature_survey.presentation.dynamic_quiz.DynamicQuizQuestionsActivity
import kotlinx.coroutines.flow.onEach
import timber.log.Timber

class TriviaQuizQuesActivity : BaseActivity(), View.OnClickListener,
    LeaveBottomSheetFragment.LeaveSurveyBottomSheetListener,
    TriviaOptionsAdapter.SCSelectorListener {

    private lateinit var binding: ActivityTriviaQuizQuesBinding
    private val viewModel: TriviaQuizViewModel by viewModels()
    private val loadingDialog: LoadingDialog by lazy { LoadingDialog(this) }
    private lateinit var myContext: Context
    private var isSuccess = false
    private var quizId: String = ""
    private var benefitType: Int = 0
    private var currentProgress: Int = 0
    private lateinit var quiz: QuizDetailDto.Data
    private lateinit var questionList: List<QuizQuestionsDto.Data.Question>
    private lateinit var viewAdapter: TriviaQuesAdapter
    private lateinit var brandTheme: InitializeDto.Data.BrandTheme
    private lateinit var customToast: Toast
    private var isTimeUp = false
    private var correctCount = 0
    private var wrongCount = 0
    private var selectedAns = ""
    private var isAnswerApiCalled = false
    private var optionPosition = 0
    private var timerValue = 0
    private var currentPosition = 0
    private var correctSeq = 0
    private var wrongSeq = 0
    private lateinit var countDownTimer: CountDownTimer


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_trivia_quiz_ques)

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
                        is QuizAnswerSaveDto.Data -> {
                            onAnswerSaved(it.data)
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

        viewModel.submitQuizUiStateFlow.onEach {
            when (it) {
                is UIState.Loading -> {
                    loadingDialog.show()
                }
                is UIState.Success<*> -> {
                    loadingDialog.cancel()
                    Timber.d("Observe data submit quiz outside: ${it.data}")
                    when (it.data) {
                        is QuizSubmitDto.Data -> {
                            Timber.d("Observe data submit quiz: ${it.data}")
                            onQuizSubmitted(it.data)
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
            viewModel.leaveUiStateFlow.onEach {
                when (it) {
                    is UIState.Loading -> {
                        loadingDialog.show()
                    }
                    is UIState.Success<*> -> {
                        loadingDialog.cancel()
                        when (it.data) {
                            is QuizLeaveDto.Data -> {
                                finish()
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
//                            if (it.message == "NetworkError") {
//                                //                     FullScreenNetworkErrorActivity.startActivity(this)
//                            }
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
        binding.progressBar.setRounded(true)
        binding.progressBar.setProgressBackgroundColor(Color.TRANSPARENT)
        binding.progressBar.setProgressWidth(16f)
        binding.progressBar.setProgressColor(Color.parseColor(Colors.PRIMARY_BRAND_COLOR))

    }

    private fun setUpListener() {
        binding.ivClose.setOnClickListener(this)
        binding.ivInfo.setOnClickListener(this)
    }

    private fun setupQuizAdapter() {
        val viewManager =
            object : LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false) {
                override fun canScrollHorizontally(): Boolean {
                    return false
                }
            }
        viewAdapter =
            TriviaQuesAdapter(this, questionList)
        binding.rvTriviaQuizQue.apply {
            layoutManager = viewManager
            setHasFixedSize(false)
            adapter = viewAdapter
        }
        binding.tvQuestionNo.text =
            "Question " + "1" + "/" + questionList.size.toString()
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
        }
    }

//    private fun goToNextQuestion(position: Int) {
////        isAnswerApiCalled = false
//        binding.rvTriviaQuizQue.scrollToPosition(position + 1)
//        binding.rvTriviaQuizQue.adapter?.notifyDataSetChanged()
//        binding.cvNextQues.hideView()
//        binding.clprogressBar.showView()
//        setUpTimer()
//    }

    override fun onBackPressed() {
        if (isSuccess) {
            finish()
        } else {
            LeaveBottomSheetFragment(this, false, 0).show(supportFragmentManager, "")
        }
    }

    override fun onLeaveSurveyClicked() {
        viewModel.postLeaveQuiz(quizId)
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
        viewModel.submitQuiz(quizId)
//        submitSurveyAPICalled = true


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
//

    private fun onQuestionsReceived(questions: List<QuizQuestionsDto.Data.Question>) {
        questionList = questions

        setupQuizAdapter()
        if(questions[currentPosition].timer != 0){
            setUpTimer(currentPosition)
            val totalTimeCountInMilliseconds = (questionList[currentPosition].timer * 1000).toLong()
            startTimer(totalTimeCountInMilliseconds)
        }else binding.clprogressBar.hideView()

    }

    private fun onQuizSubmitted(quizSubmit: QuizSubmitDto.Data) {
        Timber.d("Quiz submitted: ${quizSubmit}")
        val benefitData = BenefitData(
            quizSubmit.benefitType,
            quizSubmit.benefitValue,
            quizSubmit.messageText,
            quizSubmit.benefitDetails.couponClassification,
            quizSubmit.benefitDetails.couponLabel,
            quizSubmit.benefitDetails.featuredImage,
            quizSubmit.benefitDetails.maxDiscountValue,
            quizSubmit.benefitDetails.pointsToUnlock,
            quizSubmit.benefitDetails.discountPercent,
            quizSubmit.description,
            quizSubmit.benefitDetails.couponName,
            quizSubmit.defaultSuccessMessage
        )
        Handler().postDelayed({
            SurveyQuizSuccessFragment(benefitData, Constants.QuizzTabType.QUIZ.type).show(supportFragmentManager, "")
        }, 1500)

    }

    private fun setUpTimer(position: Int) {
 //       val currentPosition = binding.rvTriviaQuizQue.getCurrentPosition()
        Timber.d("Position: $position, timer: ${questionList[position].timer}")
        binding.progressBar.setMaxProgress(questionList[position].timer * 1000)
//        val totalTimeCountInMilliseconds = (questionList[currentPosition].timer * 1000).toLong()
//        Timber.d("totalTimeCountInMilliseconds: ${totalTimeCountInMilliseconds}")

    }

    private fun startTimer(totalTimeCountInMilliseconds: Long) {
        Timber.d("totalTimeCountInMilliseconds: ${totalTimeCountInMilliseconds}")
        countDownTimer = object : CountDownTimer(totalTimeCountInMilliseconds, 1) {
            override fun onTick(leftTimeInMilliseconds: Long) {
                val seconds = leftTimeInMilliseconds / 1000
                binding.progressBar.setProgress(leftTimeInMilliseconds.toFloat())
                binding.tvTimer.text =
                    String.format("%02d", seconds / 60) + ":" + String.format("%02d", seconds % 60)
            }

            override fun onFinish() {
                binding.tvTimer.text = "00:00"
                if(!isAnswerApiCalled){
                    isTimeUp = true
                    binding.cvTimeUp.showView()
                    binding.clprogressBar.hideView()
                    postAnswer(currentPosition, "",1)
                }


            }
        }.start()
    }

    private fun start5secTimer(position: Int) {
        binding.cvTimeUp.hideView()
        binding.cvNextQues.showView()
        setUpTimer(currentPosition + 1)
        var countDownTimer = object : CountDownTimer(6*1000, 1) {
            override fun onTick(leftTimeInMilliseconds: Long) {
                val seconds = leftTimeInMilliseconds / 1000
       //         binding.progressBar.setProgress(leftTimeInMilliseconds.toFloat())
                val timeLeft = String.format("%02d", seconds / 60) + ":" + String.format("%02d", seconds % 60)
                binding.tvNextQuesIn.text = myContext.getString(R.string.next_question_in_5s).format(timeLeft)
            }

            override fun onFinish() {
                isAnswerApiCalled = false
                isTimeUp = false
                val rvPosition = binding.rvTriviaQuizQue.getCurrentPosition()
                Timber.d("rvPosition: ${rvPosition}")
                binding.rvTriviaQuizQue.scrollToPosition(currentPosition + 1)
                binding.rvTriviaQuizQue.adapter?.notifyDataSetChanged()
                binding.tvQuestionNo.text =
                    "Question " + (currentPosition + 2) + "/" + questionList.size.toString()
                currentPosition++
                binding.cvNextQues.hideView()
                binding.cvTimeUp.hideView()
                binding.clprogressBar.showView()
                if(questionList[currentPosition].timer != 0) {
                    val totalTimeCountInMilliseconds =
                        (questionList[currentPosition].timer * 1000).toLong()
                    startTimer(totalTimeCountInMilliseconds)
                }else binding.clprogressBar.hideView()


            }
        }.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (customToast != null) {
            customToast.cancel()
        }
    }

    private fun onAnswerSaved(data: QuizAnswerSaveDto.Data) {
        Timber.d("On answer saved called")
        isAnswerApiCalled = true
        countDownTimer.cancel()
        val position = binding.rvTriviaQuizQue.getCurrentPosition()
        if (data.isCorrect == 1) {
            correctCount++
            correctSeq++
            if(wrongSeq > 0) wrongSeq = 0
            TriviaOptionsAdapter.isCorrect = true
        } else {
            TriviaOptionsAdapter.isCorrect = false
            if(!isTimeUp) {
                TriviaOptionsAdapter.correctAnswerId = data.correctOptionId
                questionList[position].options.forEach {
                    if (it.id == data.correctOptionId) {
                        it.setCorrect = true
                    }
                }
                questionList[position].options.forEach {
                    if (it.id == selectedAns) {
                        it.isWrong = true
                    }
                }
                binding.rvTriviaQuizQue.adapter?.notifyDataSetChanged()
            }else{
                TriviaOptionsAdapter.correctAnswerId = data.correctOptionId
                questionList[position].options.forEach {
                    if (it.id == data.correctOptionId) {
                        it.setCorrect = true
                    }
                }
                questionList[position].options.forEach {
                   it.isWrong = false
                }
                binding.rvTriviaQuizQue.adapter?.notifyDataSetChanged()
            }
            if(!isTimeUp){
                wrongCount++
            }
            wrongSeq++
            if(correctSeq > 0) correctSeq = 0

        }

        binding.tvCorrectCount.text = correctCount.toString()
        binding.tvWrongCount.text = wrongCount.toString()

        if(!isTimeUp) {
            when (correctSeq) {
                1 -> {
                    customToast = Toast(myContext).showCustomToast(
                        myContext,
                        "Correct!",
                        R.drawable.ic_star_emoji,
                        this
                    )
                    customToast.show()
                }
                2 -> {
                    customToast = Toast(myContext).showCustomToast(
                        myContext,
                        "Right Again",
                        R.drawable.ic_raise_hand_emoji,
                        this
                    )
                    customToast.show()
                }
                3 -> {
                    customToast = Toast(myContext).showCustomToast(
                        myContext,
                        "You’re on a roll!",
                        R.drawable.ic_shades_emoji,
                        this
                    )
                    customToast.show()
                }
                4 -> {
                    customToast = Toast(myContext).showCustomToast(
                        myContext,
                        "4 in a row!",
                        R.drawable.ic_flash_emoji,
                        this
                    )
                    customToast.show()
                }
                in 5..questionList.size -> {
                    customToast = Toast(myContext).showCustomToast(
                        myContext,
                        "$correctSeq in a row!",
                        R.drawable.ic_flash_emoji,
                        this
                    )
                    customToast.show()
                }
            }
            when (wrongSeq) {
                1 -> {
                    customToast = Toast(myContext).showCustomToast(
                        myContext,
                        "Oops",
                        R.drawable.ic_oops_emoji,
                        this
                    )
                    customToast.show()
                }
                2 -> {
                    customToast = Toast(myContext).showCustomToast(
                        myContext,
                        "Nope",
                        R.drawable.ic_oops_emoji,
                        this
                    )
                    customToast.show()
                }
                3 -> {
                    customToast = Toast(myContext).showCustomToast(
                        myContext,
                        "Alas",
                        R.drawable.ic_oops_emoji,
                        this
                    )
                    customToast.show()
                }
                4 -> {
                    customToast = Toast(myContext).showCustomToast(
                        myContext,
                        "Wrong",
                        R.drawable.ic_wrong_emoji,
                        this
                    )
                    customToast.show()
                }
                in 5..questionList.size -> {
                    customToast = Toast(myContext).showCustomToast(
                        myContext,
                        "Wrong",
                        R.drawable.ic_wrong_emoji,
                        this
                    )
                    customToast.show()
                }
            }
        }

        Timber.d("Position last: ${position}")
        if (position == questionList.size - 1) {
            //         savedAnswerAPICalled = true
            Timber.d("Submit survey called")
            submitSurveyAPI()
        } else {
            binding.clprogressBar.hideView()


            if(isTimeUp) {
                Handler().postDelayed({
                    start5secTimer(position)
                }, 3000)
            }else start5secTimer(position)

//            binding.rvTriviaQuizQue.adapter?.notifyDataSetChanged()
//            binding.rvTriviaQuizQue.scrollToPosition(position + 1)
            //     setUpTimer()
        }

    }

    private fun postAnswer(currentQuestionPosition: Int, answerId: String, isTimeUp: Int) {
        if (!isAnswerApiCalled) {
            isAnswerApiCalled = true
            viewModel.postAnswerQuiz(
                quizId,
                questionList[currentPosition].id,
                answerId,
                isTimeUp
            )
        }
    }

    override fun onSCSelected(
        view: View,
        currentQuestionPosition: Int,
        position: Int,
        answerId: String
    ) {
        selectedAns = answerId
        Timber.d("isAnswerApiCalled: $isAnswerApiCalled")
        postAnswer(currentQuestionPosition, answerId, 0)
    }

}