package com.lib.loopsdk.ui.feature_survey.presentation.survey_quiz_detail

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.AbsoluteSizeSpan
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.google.gson.reflect.TypeToken
import com.lib.loopsdk.R
import com.lib.loopsdk.core.customViews.LoadingDialog
import com.lib.loopsdk.core.util.*
import com.lib.loopsdk.data.remote.dto.InitializeDto
import com.lib.loopsdk.data.remote.dto.QuizDetailDto
import com.lib.loopsdk.data.remote.dto.SurveyDetailDto
import com.lib.loopsdk.databinding.ActivitySurveyDetailBinding
import com.lib.loopsdk.ui.BaseActivity
import com.lib.loopsdk.ui.feature_survey.presentation.dynamic_quiz.DynamicQuizQuestionsActivity
import com.lib.loopsdk.ui.feature_survey.presentation.survey_questions.SurveyQuestionsActivity
import com.lib.loopsdk.ui.feature_survey.presentation.trivia_quiz.TriviaQuizQuesActivity
import kotlinx.coroutines.flow.onEach
import timber.log.Timber

class SurveyQuizDetailActivity : BaseActivity(), View.OnClickListener {
    private lateinit var binding: ActivitySurveyDetailBinding
    private val loadingDialog: LoadingDialog by lazy { LoadingDialog(this) }
    private val viewModel: SurveyQuizDetailViewModel by viewModels()
    private var id: String = ""
    private var quizType: Int = 0
    private lateinit var survey: SurveyDetailDto.Data
    private lateinit var quiz: QuizDetailDto.Data
    private var isSurvey: Boolean = false
    private var benefitType: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_survey_detail)

        binding.lifecycleOwner = this
        binding.viewModel = this.viewModel
        binding.brandTheme = Prefs.getNonPrimitiveData<InitializeDto.Data.BrandTheme>(object: TypeToken<InitializeDto.Data.BrandTheme>(){}.type, "BRAND_THEME")
        binding.brandThemeColors = Colors

        id = intent.getStringExtra("id").toString()
        isSurvey = intent.getBooleanExtra("isSurvey", false)
        quizType = intent.getIntExtra("quizType", 0)

        binding.ivSBack.setOnClickListener(this)
        binding.btnStartSurvey.setOnClickListener(this)

        viewModel.surveyDetailUiStateFlow.onEach {
            when (it) {
                is UIState.Loading -> {
                    loadingDialog.show()
                }
                is UIState.Success<*> -> {
                    loadingDialog.cancel()
                    when (it.data) {
                        is SurveyDetailDto.Data -> {
                            onSurveyReceived(it.data)
                        }
                        is QuizDetailDto.Data -> {
                            onQuizReceived(it.data)
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
//                            //      FullScreenNetworkErrorActivity.startActivity(this)
//                        }
                    }
                }
                else -> {}
            }
        }.observeInLifecycle(this)
    }

    override fun onResume() {
        super.onResume()
        if(isSurvey) {
            viewModel.getSurveyDetail(id)
        }else viewModel.getQuizDetail(id)
    }

    companion object {
        fun startActivity(context: Context, id: String, isSurvey: Boolean, quizType: Int) {
            val intent = Intent(context, SurveyQuizDetailActivity::class.java)
            intent.putExtra("id",id)
            intent.putExtra("isSurvey", isSurvey)
            intent.putExtra("quizType", quizType)
            context.startActivity(intent)
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.ivSBack ->{
                onBackPressed()
            }
            R.id.btnStartSurvey ->{
                if(isSurvey) {
                    val intent = Intent(this, SurveyQuestionsActivity::class.java)
                    intent.putExtra(Constants.SURVEY_ID, id)
                    intent.putExtra(Constants.BENIFIT_TYPE, benefitType)
                    intent.putExtra("Single Survey", survey)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    this.startActivity(intent)
                }else if(quizType == 0){
                    val intent = Intent(this, TriviaQuizQuesActivity::class.java)
                    intent.putExtra("quizId", id)
                    intent.putExtra(Constants.BENIFIT_TYPE, benefitType)
                    intent.putExtra("Single Quiz", quiz)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    this.startActivity(intent)
                } else{
                    val intent = Intent(this, DynamicQuizQuestionsActivity::class.java)
                    intent.putExtra("quizId", id)
                    intent.putExtra(Constants.BENIFIT_TYPE, benefitType)
                    intent.putExtra("Single Quiz", quiz)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    this.startActivity(intent)
                }
                finish()

//                val map = HashMap<String, Any>()
//                map["Survey ID"] = survey.id
//                map["Survey Name"] = survey.name
//                map["Survey Description"] = survey.description
//                map["Survey Status"] = if(survey.answeredQuestion != 0) "Partially Filled" else "Available"
//                map["Total Questions"] = survey.totalQuestion
//                map["Survey Benefits"] = "1"
//                map["Validity"] = survey.endDateTime
//                CleverTapUtils.postCleverTapEvent(this, "Start Survey", map)
            }
        }
    }

    private fun onQuizReceived(quiz: QuizDetailDto.Data){
        this.quiz = quiz
        Timber.d("Quiz data: $quiz")
        if(!quiz.coverImage.isNullOrEmpty()){
            //        Picasso.get().load(survey?.backgroundImageUrl).into(binding.ivSurvayImage)
            Glide.with(this)
                .load(quiz.coverImage)
                .into(binding.ivSurvayImage)
        }
        binding.tvSurvayName.text = quiz.name

//        binding.tvDate.text = getDateWithSuffixHalfMonth(quiz.endDate, fromFormat = "dd MMM, yyyy")
        binding.tvDate.text = quiz.endDate
 //       binding.tvSurvayDesc.text  = getHTMLTagsToSpannedString(quiz.description)
//        binding.tvSurvayDesc.setAnimationDuration(0L)
//        isEllipsize(binding.tvSurvayDesc,quiz.description)
        Timber.d("descLength{${quiz.description.length}}")
        if(quiz.description.length >= 94) {
            addReadMore(quiz.description, binding.tvSurvayDesc)
        }else binding.tvSurvayDesc.text = getHTMLTagsToSpannedString(quiz.description)

        if(quiz.partiallyFilled == 0){
            binding.tvSurveyQues.text = quiz.totalQuestion.toString()
            if(quiz.totalQuestion == 1) {
                binding.tvQuesStatic.text = getString(R.string.question)
            }else binding.tvQuesStatic.text = getString(R.string.questions)
            binding.tvStartResume.text= getString(R.string.start_survey_quiz).format(getString(R.string.quiz))
        } else{
            binding.tvSurveyQues.text=String.format(this.getString(R.string.answered_question), quiz.hasAnsweredAll,
                quiz.totalQuestion)
            binding.tvStartResume.text= getString(R.string.resume_survey_quiz).format(getString(R.string.quiz))
        }
        val containsPoints = quiz.cohorts.filter { it.benefitType == 2 }
        val containsCoupons = quiz.cohorts.filter { it.benefitType == 1 }
        val containsMsg = quiz.cohorts.filter { it.benefitType == 3 }

            Timber.d("Check values: $containsCoupons, $containsPoints, $containsMsg")
            //all three
            if (containsPoints.isNotEmpty() && containsCoupons.isNotEmpty() && containsMsg.isNotEmpty()) {
                Timber.d("All three benefits")
                binding.tvStaticText1.text = this.getString(R.string.you_can_receive)
                binding.clBenefitCoupon.hideView()
                binding.clBenefitPoints.showView()
                binding.ivPoint.setImageDrawable(this.getDrawable(R.drawable.ic_gift))
                binding.tvBenefitInfo.text =
                    Constants.init.pointsIdentifier.pointsLabelPlural + this.getString(R.string.points_coupon_much_more)
            }
            //points and coupons
            else if (containsPoints.isNotEmpty() && containsCoupons.isNotEmpty() && containsMsg.isEmpty()) {
                binding.tvStaticText1.text = this.getString(R.string.you_can_receive)
                binding.clBenefitCoupon.hideView()
                binding.clBenefitPoints.showView()
                binding.ivPoint.setImageDrawable(this.getDrawable(R.drawable.ic_gift))
                binding.tvBenefitInfo.text =
                    Constants.init.pointsIdentifier.pointsLabelPlural + " and coupons"
            }//coupon n msg
            else if (containsPoints.isEmpty() && containsCoupons.isNotEmpty() && containsMsg.isNotEmpty()) {
                binding.tvStaticText1.text = this.getString(R.string.you_can_receive)
                binding.clBenefitCoupon.hideView()
                binding.clBenefitPoints.showView()
                binding.ivPoint.setImageDrawable(this.getDrawable(R.drawable.ic_gift))
                binding.tvBenefitInfo.text = this.getString(R.string.coupon_msg)
            }//points n msg
            else if (containsPoints.isNotEmpty() && containsCoupons.isEmpty() && containsMsg.isNotEmpty()) {
                binding.tvStaticText1.text = this.getString(R.string.you_can_receive)
                binding.clBenefitCoupon.hideView()
                binding.clBenefitPoints.showView()
                binding.ivPoint.setImageDrawable(this.getDrawable(R.drawable.ic_gift))
                binding.tvBenefitInfo.text =
                    Constants.init.pointsIdentifier.pointsLabelPlural + " & much more"
            }
            //only coupons
            else if(containsPoints.isEmpty() && containsCoupons.isNotEmpty() && containsMsg.isEmpty()){
                if(containsCoupons.size > 1){
                    setUpMultiCouponAdapter(containsCoupons)
                }else setBenefitCoupon(containsCoupons[0].benefitDetails)
            }//only points
            else if(containsPoints.isNotEmpty() && containsCoupons.isEmpty() && containsMsg.isEmpty()){
                if(containsPoints.size > 1){
                    setBenefitPoints(quiz.benefitBasicDetails.points)
                }else setBenefitPoints(containsPoints[0].benefitPoints.toString())
            }//only msg
            else if(containsPoints.isEmpty() && containsCoupons.isEmpty() && containsMsg.isNotEmpty()){
                setBenefitMsg()
            }

    }

    private fun setUpMultiCouponAdapter(containsCoupons: List<QuizDetailDto.Data.Cohort>) {
        binding.tvStaticText1.text = this.getString(R.string.you_will_earn)
        binding.clBenefitCoupon.hideView()
        binding.clBenefitPoints.hideView()
        binding.rvMultiCoupon.showView()
        val viewAdapter =
            MultiCouponAdapter(containsCoupons)
        binding.rvMultiCoupon.apply {
            setHasFixedSize(false)
            adapter = viewAdapter
            isNestedScrollingEnabled = false
        }
    }

    private fun onSurveyReceived(survey: SurveyDetailDto.Data) {

        Timber.d("data$survey")
        this.survey = survey
        if(!survey.backgroundImageUrl.isNullOrEmpty()){
    //        Picasso.get().load(survey?.backgroundImageUrl).into(binding.ivSurvayImage)
            Glide.with(this)
                .load(survey.backgroundImageUrl)
                .into(binding.ivSurvayImage)
        }
        binding.tvSurvayName.text = survey.name
//        binding.tvDate.text = getDateWithSuffixHalfMonth(survey.endDate, fromFormat = "dd MMM, yyyy")
        binding.tvDate.text = survey.endDate
//        binding.tvSurvayDesc.text  = getHTMLTagsToSpannedString(survey.description)
 //       binding.tvSurvayDesc.setAnimationDuration(0L)
        Timber.d("descLength{${survey.description.length}}")
 //       isEllipsize(binding.tvSurvayDesc,survey.description)
        if(survey.description.length >= 94) {
            addReadMore(survey.description, binding.tvSurvayDesc)
        }else binding.tvSurvayDesc.text = getHTMLTagsToSpannedString(survey.description)

        if(survey.hasAnswered == 0){
            binding.tvSurveyQues.text = survey.totalQuestion.toString()
            if(survey.totalQuestion == 1) {
                binding.tvQuesStatic.text = getString(R.string.question)
            }else binding.tvQuesStatic.text = getString(R.string.questions)
            binding.tvStartResume.text= getString(R.string.start_survey_quiz).format(getString(R.string.survey))
        } else{
            binding.tvSurveyQues.text=String.format(this.getString(R.string.answered_question), survey.hasAnswered,
                survey.totalQuestion)
            binding.tvStartResume.text= getString(R.string.resume_survey_quiz).format(getString(R.string.survey))
        }
        when(survey.benefitType){
            1 -> {
                binding.tvStaticText1.text = this.getString(R.string.you_will_earn)
                binding.clBenefitCoupon.showView()
                binding.clBenefitPoints.hideView()
                binding.tvBenefitDes.text=survey.benefitDetails.couponName
                when(survey.benefitDetails.couponClassification){
                    1,2->{
                        binding.tvCouponBenefit.text =
                            Constants.init.defaultCurrency.symbol +  Utils.digitCountUpdate(survey.benefitDetails.maxDiscountValue) + " Voucher"
                    }
                    3,4->{
                        binding.tvCouponBenefit.text =
                            survey.benefitDetails.discountPercent.toString() + "%" + " Discount"
                    }
                    5->{
                        //free shipping
                        binding.tvCouponBenefit.text = "Free Delivery"
                    }
                    6->{
                        //Other

                    }
                }
            }
            2-> {
                setBenefitPoints(survey.benefitValue.toString())
            }
            3 -> {
                setBenefitMsg()
            }
        }
        benefitType= survey.benefitType
    }

//    private fun isEllipsize(textView: ExpandableTwoLineTextView, description: String) {
//        textView.post {
//            val layout: Layout = textView.layout
//            val lines: Int = layout.lineCount
//            if (description.length >=110) {
//                textView.collapse()
//                binding.ivDot.showView()
//                binding.ivDot.setOnClickListener {
//                    addReadLess(description, textView)
//                    showHideReadMore()
//                }
//            } else {
//                binding.ivDot.hideView()
//                textView.setOnClickListener(null)
//                binding.ivDot.setOnClickListener(null)
//            }
//        }
//    }

    private fun setBenefitPoints(points: String){
        binding.clBenefitCoupon.hideView()
        binding.clBenefitPoints.showView()
        binding.tvStaticText1.text = this.getString(R.string.you_will_earn)
        binding.tvBenefitInfo.text = points + " " + Constants.init.pointsIdentifier.pointsLabelPlural
        binding.ivPoint.setImageDrawable(this.getDrawable(R.drawable.ic_point))
    }

    private fun setBenefitMsg(){
        binding.clBenefitCoupon.hideView()
        binding.clBenefitPoints.showView()
        binding.tvStaticText1.text = this.getString(R.string.you_will_receive)
        binding.ivPoint.setImageDrawable(this.getDrawable(R.drawable.ic_gift))
        binding.tvBenefitInfo.text = this.getString(R.string.a_surprise_message)
    }

    private fun setBenefitCoupon(benefits: QuizDetailDto.Data.Cohort.BenefitDetails) {
        binding.tvStaticText1.text = this.getString(R.string.you_will_earn)
        binding.clBenefitCoupon.showView()
        binding.clBenefitPoints.hideView()
        binding.tvBenefitDes.text=benefits.couponName
        when(benefits.couponClassification){
            1,2->{
                binding.tvCouponBenefit.text =
                    Constants.init.defaultCurrency.symbol +  Utils.digitCountUpdate(benefits.maxDiscountValue) + " Voucher"
            }
            3,4->{
                binding.tvCouponBenefit.text =
                    benefits.discountPercent.toString() + "%" + " Discount"
            }
            5->{
                //free shipping
                binding.tvCouponBenefit.text = "Free Delivery"
            }
            6->{
                //Other

            }
        }
    }

//    private fun showHideReadMore() {
//        if (binding.tvSurvayDesc.isExpanded) {
//            binding.tvSurvayDesc.collapse()
//            if(isSurvey) {
//                binding.tvSurvayDesc.text = getHTMLTagsToSpannedString(survey.description)
//            }else binding.tvSurvayDesc.text = getHTMLTagsToSpannedString(quiz.description)
//            binding.ivDot.showView()
//        } else {
//            binding.tvSurvayDesc.expand()
//            binding.ivDot.hideView()
//
//        }
//    }
    private fun addReadMore(text: String, textView: TextView) {
        val textWithoutHtmlTag = this.getHTMLTagsToSpannedString(text)
        val ss = SpannableString(textWithoutHtmlTag.substring(0, 94) + "... Read More")
        val clickableSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(view: View) {
                addReadLess(text, textView)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = true
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    ds.color = this@SurveyQuizDetailActivity.resources.getColor(R.color.white, this@SurveyQuizDetailActivity.theme)
                } else {
                    ds.color = this@SurveyQuizDetailActivity.resources.getColor(R.color.white)
                }
            }
        }
        val textSize = this@SurveyQuizDetailActivity.resources.getDimensionPixelSize(R.dimen.small_text_size)
        ss.setSpan(clickableSpan, ss.length - 9, ss.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
 //       ss.setSpan(StyleSpan(Typeface.BOLD), ss.length - 9, ss.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE) // set style
        ss.setSpan(AbsoluteSizeSpan(textSize), ss.length - 9, ss.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        textView.text = ss
        textView.movementMethod = LinkMovementMethod.getInstance()
    }
    private fun addReadLess(text: String, textView: TextView) {
        val textWithoutHtmlTag = this.getHTMLTagsToSpannedString(text)
        val ss = SpannableString("$textWithoutHtmlTag Read Less")
        val clickableSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(view: View) {
                addReadMore(text, textView)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = true
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    ds.color = this@SurveyQuizDetailActivity.getColor(R.color.white)
                } else {
                    ds.color = this@SurveyQuizDetailActivity.getColor(R.color.white)
                }
            }
        }
        val textSize = this.resources.getDimensionPixelSize(R.dimen.small_text_size)
        ss.setSpan(clickableSpan, ss.length - 9, ss.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
 //       ss.setSpan(StyleSpan(Typeface.BOLD), ss.length - 10, ss.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE) // set style
        ss.setSpan(AbsoluteSizeSpan(textSize), ss.length - 9, ss.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        textView.text = ss
        textView.movementMethod = LinkMovementMethod.getInstance()
    }


}