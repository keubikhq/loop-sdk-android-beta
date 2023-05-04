package com.lib.loopsdk.ui.feature_survey.presentation.survey_questions

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.text.*
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ScrollView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.reflect.TypeToken
import com.lib.loopsdk.R
import com.lib.loopsdk.core.util.*
import com.lib.loopsdk.data.remote.dto.InitializeDto
import com.lib.loopsdk.data.remote.dto.SurveyQuestionsDto
import com.lib.loopsdk.databinding.*
import com.warkiz.widget.IndicatorSeekBar
import com.warkiz.widget.OnSeekChangeListener
import com.warkiz.widget.SeekParams
import timber.log.Timber

class SurveyQuestionAdapter(
    private val context: Context,
    private val questionsList: List<SurveyQuestionsDto.Data.Question>,
    private val listener: SCOtherSelectedListener

) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var on_attach = true
    var DURATION: Long = 500


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        when (viewType) {
            SHORT -> {
                val shortBinding = FragmentQuestionShortBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                shortBinding.brandTheme =
                    Prefs.getNonPrimitiveData<InitializeDto.Data.BrandTheme>(object :
                        TypeToken<InitializeDto.Data.BrandTheme>() {}.type, "BRAND_THEME")
                shortBinding.brandThemeColors = Colors
                return ViewHolderShort(shortBinding)
            }
            LONG -> {
                val longBinding = FragmentQuestionLongBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                longBinding.brandTheme =
                    Prefs.getNonPrimitiveData<InitializeDto.Data.BrandTheme>(object :
                        TypeToken<InitializeDto.Data.BrandTheme>() {}.type, "BRAND_THEME")
                longBinding.brandThemeColors = Colors
                return ViewHolderLong(longBinding)
            }
            SLIDER -> {
                val sliderBinding = FragmentQuestionSliderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                sliderBinding.brandTheme =
                    Prefs.getNonPrimitiveData<InitializeDto.Data.BrandTheme>(object :
                        TypeToken<InitializeDto.Data.BrandTheme>() {}.type, "BRAND_THEME")
                sliderBinding.brandThemeColors = Colors
                return ViewHolderSlider(sliderBinding)
            }
            CHECKBOX -> {
                val checkBoxBinding = FragmentQuestionMultiChoiceBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                checkBoxBinding.brandTheme =
                    Prefs.getNonPrimitiveData<InitializeDto.Data.BrandTheme>(object :
                        TypeToken<InitializeDto.Data.BrandTheme>() {}.type, "BRAND_THEME")
                checkBoxBinding.brandThemeColors = Colors
                return ViewHolderCheckBox(checkBoxBinding)
            }
            DROPDOWN -> {
                val dropDownBinding = FragmentQuestionDropDownBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                dropDownBinding.brandTheme =
                    Prefs.getNonPrimitiveData<InitializeDto.Data.BrandTheme>(object :
                        TypeToken<InitializeDto.Data.BrandTheme>() {}.type, "BRAND_THEME")
                dropDownBinding.brandThemeColors = Colors
                return ViewHolderDropDown(dropDownBinding)
            }
            RADIO -> {
                val singlechoiceBinding = FragmentQuestionSingleChoiceBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                singlechoiceBinding.brandTheme =
                    Prefs.getNonPrimitiveData<InitializeDto.Data.BrandTheme>(object :
                        TypeToken<InitializeDto.Data.BrandTheme>() {}.type, "BRAND_THEME")
                singlechoiceBinding.brandThemeColors = Colors
                return ViewHolderRadio(singlechoiceBinding)
            }
        }

        val singlechoiceBinding = FragmentQuestionSingleChoiceBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        singlechoiceBinding.brandTheme =
            Prefs.getNonPrimitiveData<InitializeDto.Data.BrandTheme>(object :
                TypeToken<InitializeDto.Data.BrandTheme>() {}.type, "BRAND_THEME")
        singlechoiceBinding.brandThemeColors = Colors
        return ViewHolderRadio(singlechoiceBinding)

    }

    class ViewHolderShort(itemView: FragmentQuestionShortBinding) :
        RecyclerView.ViewHolder(itemView.rootView) {
        fun bind(
            view: View,
        ) {
            view.setOnClickListener {

            }
        }

        var clQuestion = itemView.clQuestion
        var questionNo = itemView.tvQuestionNo
        var tvDescription = itemView.tvDescription
        var etAnswer = itemView.etAnswer
        var tvQuestion = itemView.tvQuestion
        var cvAnswer = itemView.cvAnswer
        var scrollView = itemView.scrollView
        var rootView = itemView.rootView
    }

    class ViewHolderLong(itemView: FragmentQuestionLongBinding) :
        RecyclerView.ViewHolder(itemView.rootView) {

        var clQuestion = itemView.clQuestion
        var questionNo = itemView.tvQuestionNo
        var tvDescription = itemView.tvDescription
        var etAnswer = itemView.etAnswer
        var tvQuestion = itemView.tvQuestion
        var cvAnswer = itemView.cvAnswer
        var scrollView = itemView.scrollView
        var rootView = itemView.rootView


    }

    class ViewHolderSlider(itemView: FragmentQuestionSliderBinding) :
        RecyclerView.ViewHolder(itemView.rootView) {
        var questionNo = itemView.tvQuestionNo
        var clQuestion = itemView.clQuestion
        var tvDescription = itemView.tvDescription
        var tvQuestion = itemView.tvQuestion
        var tvMinValue = itemView.tvMinValue
        var tvMaxValue = itemView.tvMaxValue
        var tvMinText = itemView.tvMinText
        var tvMaxText = itemView.tvMaxText
        var slider = itemView.answerSlider
        var ivDragArrow = itemView.ivDragArrow
        var tvDrag = itemView.tvDrag
        var shrinkExpandAnim = itemView.shrinkExpandAnim
        var moveAnimView = itemView.moveAnimView
    }

    class ViewHolderCheckBox(itemView: FragmentQuestionMultiChoiceBinding) :
        RecyclerView.ViewHolder(itemView.rootView) {
        var questionNo = itemView.tvQuestionNo
        var clQuestion = itemView.clQuestion
        var tvDescription = itemView.tvDescription
        var tvQuestion = itemView.tvQuestion
        var rvOptions = itemView.rvOptions
        var cvOther = itemView.cvChoiceOther
        var cvOtherSelected = itemView.cvChoiceOtherSelected
 //       var cbOther = itemView.cbChoiceOther
        var cbOtherSelected = itemView.cbChoiceOtherSelected
        var tvOther = itemView.tvChoiceOther
        var rlOther = itemView.rlOther
        var clOtherAdd = itemView.clOtherAdd
        var etOther = itemView.etOtherAnswer
        var cvAdd = itemView.cvAdd
        var ivCheck = itemView.ivCheckOther
    }

    class ViewHolderDropDown(itemView: FragmentQuestionDropDownBinding) :
        RecyclerView.ViewHolder(itemView.rootView) {
        var questionNo = itemView.tvQuestionNo
        var clQuestion = itemView.clQuestion
        var tvDescription = itemView.tvDescription
        var tvQuestion = itemView.tvQuestion
        var cvAnswer = itemView.cvAnswer
        var selectFromDropdown = itemView.selectFromDropdown
        var tvSelected = itemView.tvSelected
        var ivDownIcon = itemView.ivIcon

    }

    class ViewHolderRadio(itemView: FragmentQuestionSingleChoiceBinding) :
        RecyclerView.ViewHolder(itemView.rootView) {
        var questionNo = itemView.tvQuestionNo
        var clQuestion = itemView.clQuestion
        var tvDescription = itemView.tvDescription
        var tvQuestion = itemView.tvQuestion
        var rvOptions = itemView.rvOptions
        var cvOther = itemView.cvChoiceOther
        var cvOtherSelected = itemView.cvChoiceOtherSelected
        var tvOther = itemView.tvChoiceOther
        var clOtherAdd = itemView.clAdd
        var etOther = itemView.etOtherAnswer
        var cvAdd = itemView.cvAdd

    }

    override fun getItemViewType(position: Int): Int {

        return when (questionsList[position].optionType) {
            1 -> return SHORT
            2 -> return LONG
            3 -> return SLIDER
            4 -> return CHECKBOX
            5 -> return DROPDOWN
            else -> return RADIO
        }
    }

    //    override fun getItemId(position: Int): Int {
//        return when (questionsList[position].optionType) {
//            1-> return SHORT
//            2-> return LONG
//            3-> return SLIDER
//            4-> return CHECKBOX
//            5-> return DROPDOWN
//            else-> return RADIO
//        }
//    }
    override fun getItemCount(): Int {
        return questionsList.size
    }

    companion object {
        private const val SHORT = 1
        private const val LONG = 2
        private const val SLIDER = 3
        private const val CHECKBOX = 4
        private const val DROPDOWN = 5
        private const val RADIO = 6
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val brandTheme = Prefs.getNonPrimitiveData<InitializeDto.Data.BrandTheme>(object :
            TypeToken<InitializeDto.Data.BrandTheme>() {}.type, "BRAND_THEME")
        val ss1 = SpannableString("(required)")
        ss1.setSpan(
            StyleSpan(Typeface.NORMAL),
            0,
            10,
            Spannable.SPAN_INCLUSIVE_INCLUSIVE
        ) // set size
        ss1.setSpan(AbsoluteSizeSpan(30), 0, 10, Spannable.SPAN_INCLUSIVE_INCLUSIVE) // set size
        ss1.setSpan(
            ForegroundColorSpan(context.resources.getColor(R.color.error_red)),
            0,
            10,
            Spannable.SPAN_INCLUSIVE_INCLUSIVE
        ) // set color
        holder.setIsRecyclable(false);
        if (position > 0) {
            val animation: Animation =
                AnimationUtils.loadAnimation(context, R.anim.slide_in_right).apply {
                    duration = 200
                }
            animation.setInterpolator(AccelerateDecelerateInterpolator())
            holder.itemView.startAnimation(animation)
            animation.start()
        }
        when (holder.getItemViewType()) {
            SHORT -> {
                holder as ViewHolderShort

                val list = questionsList[position]
                (context as SurveyQuestionsActivity).showHidePrevious(position)
                viewDelayAnimation(holder, position)
                holder.tvDescription.text =context.getHTMLTagsToSpannedString( list.description)
                if (list.isRequired) {
                    holder.tvQuestion.text = TextUtils.concat(list.question, " ", ss1)
                    (context as SurveyQuestionsActivity).disableNextButton()
                } else {
                    holder.tvQuestion.text = list.question
                    (context as SurveyQuestionsActivity).enableNextButton()
                }
                holder.questionNo.text =
                    "Question " + (position + 1).toString() + "/" + itemCount.toString()
                if (list.answer != null) {
                    if (list.updatedAnswerString.isNullOrEmpty()) {
                        when (list.answer as String) {
                            is String -> {
                                Timber.d("Saved answer short: ${list.answer as String}")
                                list.updatedAnswerString = list.answer as String
                            }
                        }
                    }
                }


                if (!list.updatedAnswerString.isNullOrEmpty()) {
                    Timber.d("Updated ans short: ${list.updatedAnswerString}")
                    holder.etAnswer.setText(list.updatedAnswerString)
                    holder.etAnswer.setSelection(list.updatedAnswerString.length)
                    holder.cvAnswer.strokeWidth = Utils.dpToPx(context, 1)
                    holder.cvAnswer.strokeColor =
                        Color.parseColor(brandTheme.themeColors.primary.hexCode)
                    holder.cvAnswer.setCardBackgroundColor(
                        ContextCompat.getColor(
                            context,
                            R.color.white
                        )
                    )
                    context.enableNextButton()
                }
                holder.etAnswer.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                        holder.scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                    }

                    override fun afterTextChanged(s: Editable?) {
                        if (!TextUtils.isEmpty(s!!.trim()) && !TextUtils.isEmpty(s)) {
                            list.updatedAnswerString = s.toString()
//                            holder.cvAnswer.setColor(false)
                            Timber.d("Short ans: $s")
                            holder.cvAnswer.strokeWidth = Utils.dpToPx(context, 1)
                            holder.cvAnswer.strokeColor =
                                Color.parseColor(brandTheme.themeColors.primary.hexCode)
                            holder.cvAnswer.setCardBackgroundColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.white
                                )
                            )
                            context.enableNextButton()
                        } else if (!list.isRequired) {
                            holder.etAnswer.text?.clear()
                            list.updatedAnswerString = ""
                            holder.cvAnswer.strokeWidth = Utils.dpToPx(context, 0)
                            holder.cvAnswer.setCardBackgroundColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.bg_gray
                                )
                            )
                            context.enableNextButton()
                        } else {
                            holder.etAnswer.text?.clear()
                            list.updatedAnswerString = ""
                            holder.cvAnswer.strokeWidth = Utils.dpToPx(context, 0)
                            holder.cvAnswer.setCardBackgroundColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.bg_gray
                                )
                            )
                            context.disableNextButton()
                        }
                    }

                })
                holder.rootView.viewTreeObserver.addOnGlobalLayoutListener(object :
                    ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        val heightDiff: Int =
                            holder.rootView.getRootView().getHeight() - holder.rootView.getHeight()
                        if (heightDiff > 100) { // Value should be less than keyboard's height
                            holder.scrollView.fullScroll(ScrollView.FOCUS_DOWN)

                        } else {
                            holder.scrollView.fullScroll(ScrollView.FOCUS_DOWN)

                        }
                    }
                })
                holder.bind(holder.itemView.rootView)
            }
            LONG -> {
                holder as ViewHolderLong
                (context as SurveyQuestionsActivity).showHidePrevious(position)

                val listLong = questionsList[position]
                viewDelayAnimation(holder, position)
                holder.tvDescription.text = context.getHTMLTagsToSpannedString( listLong.description)
                if (listLong.isRequired) {
                    holder.tvQuestion.text = TextUtils.concat(listLong.question, " ", ss1)
                    (context as SurveyQuestionsActivity).disableNextButton()
                } else {
                    holder.tvQuestion.text = listLong.question
                    (context as SurveyQuestionsActivity).enableNextButton()
                }
                holder.questionNo.text =
                    "Question " + (position + 1).toString() + "/" + itemCount.toString()
                if (listLong.answer != null) {
                    if (listLong.updatedAnswerString.isNullOrEmpty()) {
                        when (listLong.answer as String) {
                            is String -> {
                                Timber.d("Saved answer long: ${listLong.answer as String}")
                                listLong.updatedAnswerString = listLong.answer as String
                            }
                        }
                    }
                }
                if (!listLong.updatedAnswerString.isNullOrEmpty()) {
                    Timber.d("Updated ans long: ${listLong.updatedAnswerString}")
                    holder.etAnswer.setText(listLong.updatedAnswerString)
                    holder.etAnswer.setSelection(listLong.updatedAnswerString.length)
                    holder.cvAnswer.strokeWidth = Utils.dpToPx(context, 1)
                    holder.cvAnswer.strokeColor =
                        Color.parseColor(brandTheme.themeColors.primary.hexCode)
                    holder.cvAnswer.setCardBackgroundColor(
                        ContextCompat.getColor(
                            context,
                            R.color.white
                        )
                    )
//                    holder.cvAnswer.setColor(false)
                    context.enableNextButton()
                }
                holder.etAnswer.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                        holder.scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                    }

                    override fun afterTextChanged(s: Editable?) {
                        if (!TextUtils.isEmpty(s!!.trim()) && !TextUtils.isEmpty(s)) {
                            listLong.updatedAnswerString = s.toString()
//                            holder.cvAnswer.setColor(false)
                            holder.cvAnswer.strokeWidth = Utils.dpToPx(context, 1)
                            holder.cvAnswer.strokeColor =
                                Color.parseColor(brandTheme.themeColors.primary.hexCode)
                            holder.cvAnswer.setCardBackgroundColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.white
                                )
                            )
                            Timber.d("Long ans: $s")
                            if(listLong.updatedAnswerString.length > 9){
                                context.enableNextButton()
                            }else context.disableNextButton()

                        } else if (!listLong.isRequired) {
                            holder.etAnswer.text?.clear()
                            listLong.updatedAnswerString = ""
                            holder.cvAnswer.strokeWidth = Utils.dpToPx(context, 0)
                            holder.cvAnswer.setCardBackgroundColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.bg_gray
                                )
                            )
                            context.enableNextButton()
                        } else {
                            holder.etAnswer.text?.clear()
                            listLong.updatedAnswerString = ""
                            holder.cvAnswer.strokeWidth = Utils.dpToPx(context, 0)
                            holder.cvAnswer.setCardBackgroundColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.bg_gray
                                )
                            )
                            context.disableNextButton()
                        }
                    }

                })

                holder.rootView.viewTreeObserver.addOnGlobalLayoutListener(object :
                    ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        val heightDiff: Int =
                            holder.rootView.getRootView().getHeight() - holder.rootView.getHeight()
                        if (heightDiff > 100) { // Value should be less than keyboard's height
                            holder.scrollView.fullScroll(ScrollView.FOCUS_DOWN)

                        } else {
                            holder.scrollView.fullScroll(ScrollView.FOCUS_DOWN)

                        }
                    }
                })

            }
            SLIDER -> {
                holder as ViewHolderSlider
                (context as SurveyQuestionsActivity).showHidePrevious(position)
                val list = questionsList[position]
                holder.tvDescription.text =context.getHTMLTagsToSpannedString( list.description)
                if (list.isRequired) {
                    holder.tvQuestion.text = TextUtils.concat(list.question, " ", ss1)
                    (context as SurveyQuestionsActivity).disableNextButton()
                } else {
                    holder.tvQuestion.text = list.question
                    (context as SurveyQuestionsActivity).enableNextButton()
                }

                //set indicator top layout color
                val inflater: LayoutInflater = context.getLayoutInflater()
                val sliderContentView: View = inflater.inflate(R.layout.custom_indicator_survey, null)
                val imageView = sliderContentView.findViewById<AppCompatImageView>(R.id.ivIndicator)
                imageView.setColorFilter(Color.parseColor(Colors.PRIMARY_BRAND_COLOR), PorterDuff.Mode.SRC_IN)
                val textView = sliderContentView.findViewById<TextView>(R.id.tvIndicator)
                textView.setTextColor(Color.parseColor(Colors.FONT_COLOR))

                setSliderThumbInnerColor()
                setSliderThumbOuterColor()

                //Create tick drawable
                val tickIcon = GradientDrawable()
                tickIcon.shape = GradientDrawable.OVAL
                tickIcon.setSize(Utils.dpToPx(context, 8),Utils.dpToPx(context, 8))
                tickIcon.setColor(context.getColor(R.color.white))
                tickIcon.setStroke(1, Color.parseColor(Colors.PRIMARY_BRAND_COLOR))

                val drawable2 = context.resources.getDrawable(R.drawable.slider_thumb3)
                drawable2.setTint(Color.parseColor(Colors.PRIMARY_BRAND_COLOR))
                holder.moveAnimView.background = context.resources.getDrawable(R.drawable.slider_thumb)
                holder.shrinkExpandAnim.background = context.resources.getDrawable(R.drawable.slider_thumb3)

                holder.questionNo.text =
                    "Question " + (position + 1).toString() + "/" + itemCount.toString()
                holder.tvMinValue.text = list.options[0].min.toString()
                holder.tvMaxValue.text = list.options[1].max.toString()
                holder.tvMinText.text = list.options[0].lablel
                holder.tvMaxText.text = list.options[1].lablel
                holder.slider.setTickMarksDrawable(tickIcon)
                holder.slider.setMin(list.options[0].min.toFloat())
                holder.slider.setMax(list.options[1].max.toFloat())
                holder.slider.setTickCount((list.options[1].max.toInt() - list.options[0].min.toInt()) + 1)
                holder.slider.tickTextsColor(Color.parseColor(Colors.PRIMARY_BRAND_COLOR))

                holder.slider.indicator.contentView = sliderContentView

                Timber.d("Slider answer: ${list.answer} ${list.updatedAnswerInt}")
                if (list.answer != null && list.updatedAnswerInt.isNullOrEmpty()) {
                    when (list.answer) {
                        is Double -> {
                            Timber.d("Slider saved answer: ${(list.answer as Double).toFloat()} ")
                            val savedAns = list.answer as Double
                            holder.slider.setProgress(savedAns.toFloat())
                            setThumbDrawable(savedAns.toInt(), holder, position)
                            holder.shrinkExpandAnim.hideView()
                            holder.moveAnimView.hideView()
                            context.enableNextButton()
                        }
                    }
                } else if (!list.updatedAnswerInt.isNullOrEmpty()) {
                    Timber.d("Slider updated answer: ${list.updatedAnswerInt.toFloat()} ")
                    holder.slider.setProgress(list.updatedAnswerInt.toFloat())
                    setThumbDrawable(list.updatedAnswerInt.toInt(), holder, position)
                    holder.shrinkExpandAnim.hideView()
                    holder.moveAnimView.hideView()
                    context.enableNextButton()
                } else startSliderMoveAnim(holder)
//                startSliderAnim(holder as ViewHolderSlider)

                holder.slider.setOnSeekChangeListener(object : OnSeekChangeListener {
                    override fun onSeeking(seekParams: SeekParams) {
                        textView.text = seekParams.progress.toString()
                    }

                    override fun onStartTrackingTouch(seekBar: IndicatorSeekBar) {
                        holder.shrinkExpandAnim.hideView()
                        holder.ivDragArrow.animate().alpha(0.0f)
                        holder.tvDrag.animate().alpha(0.0f)
                        Timber.d("Start Tracking")
                        setSliderThumbOuterColor()
                        setSliderThumbInnerColor()
                        holder.slider.setThumbDrawable(context.resources.getDrawable(R.drawable.slider_custom_thumb))
                    }

                    override fun onStopTrackingTouch(seekBar: IndicatorSeekBar) {
                        val progress = seekBar.progress
                        setThumbDrawable(progress, holder, position)
                    }

                })

            }
            CHECKBOX -> {
                holder as ViewHolderCheckBox
                (context as SurveyQuestionsActivity).showHidePrevious(position)
                val list = questionsList[position]
                holder.etOther.setOnTouchListener(View.OnTouchListener { view, motionEvent ->
                    view.parent.requestDisallowInterceptTouchEvent(true)
                    false
                })
//                FromRightToLeft(holder.itemView, position);
                viewDelayAnimation(holder, position)
                holder.tvDescription.text = context.getHTMLTagsToSpannedString( list.description)
                Timber.d("Updated answer CB: ${list.updatedAnswerIntArray} ")
                if (list.isRequired && list.updatedAnswerIntArray.isNullOrEmpty()) {
                    holder.tvQuestion.text = TextUtils.concat(list.question, " ", ss1)
                    (context as SurveyQuestionsActivity).disableNextButton()
                } else {
                    holder.tvQuestion.text = list.question
                    (context as SurveyQuestionsActivity).enableNextButton()
                }
                holder.questionNo.text =
                    "Question " + (position + 1).toString() + "/" + itemCount.toString()
                val viewAdapter =
                    SurveyCBOptionsAdapter(
                        context,
                        list.options,
                        (context as SurveyQuestionsActivity)
                    )
                holder.rvOptions.apply {
                    isNestedScrollingEnabled = false
                    setHasFixedSize(false)
                    adapter = viewAdapter
                }
                if (list.isOther) {
                    holder.cvOther.showView()
                    holder.tvOther.showView()

                } else {
                    holder.cvOther.hideView()
                    holder.tvOther.hideView()
                }
                if (list.answer != null) {
                    if (list.updatedAnswerIntArray.isNullOrEmpty()) {
                        when (list.answer as ArrayList<*>) {
                            is ArrayList<*> -> {
                                list.options.forEach { it.isSelected = false }
                                val savedAns: ArrayList<Long> = list.answer as ArrayList<Long>
                                Timber.d("Saved answer CB :${list.answer as ArrayList<*>}")
//
                                val savedAnsStringArray: ArrayList<String> = ArrayList()
                                for (i in 0 until savedAns.size) {
                                    savedAnsStringArray.add(String.format("%.0f", savedAns[i]))
                                }

                                Timber.d("Saved answer CB string array: $savedAnsStringArray")
                                list.updatedAnswerIntArray = savedAnsStringArray
                            }
                        }
                    }
                }
                if (!list.updatedAnswerIntArray.isNullOrEmpty()) {
                    for (i in list.updatedAnswerIntArray) {
                        list.options.forEach { j ->
                            if (j.id == i) {
                                j.isSelected = true
                                holder.rvOptions.adapter?.notifyDataSetChanged()
                            }
                        }
                    }
                    context.enableNextButton()
                }

                if (!list.other.isNullOrEmpty() && list.updatedOther.isNullOrEmpty()) {
//                holder.tvOther.text = list.other
//                holder.rlOther.showView()
//                holder.cbOther.showView()
//                holder.cbOther.setColor(false)
//                holder.ivCheck.showView()
//                holder.cvOther.hideView()
//                holder.cvOtherSelected.showView()
                    list.updatedOther = list.other
                    context.enableNextButton()
                }
                if (!list.updatedOther.isNullOrEmpty()) {
                    holder.tvOther.text = list.updatedOther
                    holder.rlOther.showView()
                    holder.ivCheck.showView()
                    holder.cvOther.hideView()
                    holder.cvOtherSelected.showView()
                    holder.cbOtherSelected.showView()
                    context.enableNextButton()
                }
                holder.cvOther.setOnClickListener {
                    if (list.updatedOther.isNullOrEmpty()) {
                        holder.clOtherAdd.showView()
                        holder.rlOther.hideView()
                    } else {
                        holder.rlOther.showView()
                        holder.cvOther.showView()
                        holder.cvOtherSelected.hideView()
                    }
                }
                holder.cvOtherSelected.setOnClickListener {
                    if (list.updatedOther.isNullOrEmpty()) {
                        holder.clOtherAdd.showView()
                        holder.rlOther.hideView()
                    } else {
                        list.updatedOther = ""
                        holder.etOther.text?.clear()
                        holder.clOtherAdd.hideView()
                        holder.rlOther.showView()
                        holder.cvOther.showView()
             //           holder.cbOther.hideView()
                        holder.cbOtherSelected.hideView()
                        holder.ivCheck.hideView()
                        holder.tvOther.text = "Other"
                        holder.cvOtherSelected.hideView()
                        holder.cvOther.strokeWidth = Utils.dpToPx(context, 0)
                        holder.cvOther.setCardBackgroundColor(
                            ContextCompat.getColor(
                                context,
                                R.color.bg_gray
                            )
                        )
                    }
                }
                holder.etOther.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {

                    }

                    override fun afterTextChanged(s: Editable?) {
                        if (!TextUtils.isEmpty(s!!.trim()) && !TextUtils.isEmpty(s)) {
                            Timber.d("Other text : ${s.toString()}")
                            list.updatedOther = s.toString()
                            holder.tvOther.text = s.toString()
                            holder.cvOther.strokeWidth = Utils.dpToPx(context, 1)
                            holder.cvOther.strokeColor =
                                Color.parseColor(brandTheme.themeColors.primary.hexCode)
                            holder.cvOther.setCardBackgroundColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.white
                                )
                            )

                        } else{
                            Timber.d("String empty")
                            holder.etOther.text?.clear()
                            list.updatedOther = ""
                            holder.clOtherAdd.hideView()
                            holder.rlOther.showView()
                            holder.cvOther.showView()
            //                holder.cbOther.hideView()
                            holder.cbOtherSelected.hideView()
                            holder.ivCheck.hideView()
                            holder.tvOther.text = "Other"
                            holder.cvOtherSelected.hideView()
                            holder.cvOther.strokeWidth = Utils.dpToPx(context, 0)
                            holder.cvOther.setCardBackgroundColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.bg_gray
                                )
                            )
                            if (list.isRequired) context.disableNextButton() else context.enableNextButton()
                        }
                    }

                })
                holder.cvAdd.setOnClickListener {
                    if (!list.updatedOther.isNullOrEmpty()) {
                        if (list.updatedOther.trim().length > 200) {
                            context.showToast("Characters Must be less than 200")
                        } else {
                            holder.clOtherAdd.hideView()
                            holder.rlOther.showView()
             //               holder.cbOther.hideView()
                            holder.cbOtherSelected.showView()
                            holder.ivCheck.showView()
                            holder.cvOther.hideView()
                            holder.cvOtherSelected.showView()
                            context.enableNextButton()
                        }
                    } else {
                        holder.clOtherAdd.hideView()
                        holder.rlOther.showView()
                        holder.cvOther.showView()
                        holder.cvOtherSelected.hideView()
                        holder.cvOther.strokeWidth = Utils.dpToPx(context, 0)
                        holder.cvOther.setCardBackgroundColor(
                            ContextCompat.getColor(
                                context,
                                R.color.bg_gray
                            )
                        )
                        if (!list.updatedAnswerIntArray.isNullOrEmpty()) {
                            context.enableNextButton()
                        } else context.disableNextButton()
                    }

                }

            }
            DROPDOWN -> {
                holder as ViewHolderDropDown
                (context as SurveyQuestionsActivity).showHidePrevious(position)
                val list = questionsList[position]
                viewDelayAnimation(holder, position)
                holder.tvDescription.text = context.getHTMLTagsToSpannedString( list.description)
                if (list.isRequired) {
                    holder.tvQuestion.text = TextUtils.concat(list.question, " ", ss1)
                    (context as SurveyQuestionsActivity).disableNextButton()
                } else {
                    holder.tvQuestion.text = list.question
                    (context as SurveyQuestionsActivity).enableNextButton()
                }
                holder.questionNo.text =
                    "Question " + (position + 1).toString() + "/" + itemCount.toString()

                if (!list.updatedAnswerIntArray.isNullOrEmpty()) {
                    Timber.d("Updated ans DD :${list.updatedAnswerIntArray} at position ${position}")
                    Timber.d("List data: ${list}")
                    var savedAnsValue: String = ""
                    list.options.forEach {
                        if (it.id == list.updatedAnswerIntArray[0]) {
                            savedAnsValue = it.value
                            it.isSelected = true
                            Timber.d("Saved ans DD value: $savedAnsValue")
                            holder.selectFromDropdown.hideView()
                            holder.tvSelected.showView()
                            holder.tvSelected.text = savedAnsValue
                            holder.cvAnswer.strokeWidth = Utils.dpToPx(context, 1)
                            holder.cvAnswer.strokeColor =
                                Color.parseColor(brandTheme.themeColors.primary.hexCode)
                            holder.cvAnswer.setCardBackgroundColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.white
                                )
                            )
                            holder.ivDownIcon.setColorFilter(ContextCompat.getColor(context,R.color.text_black), PorterDuff.Mode.SRC_IN)
                            context.enableNextButton()
                        }
                    }
                } else if (list.answer != null) {
                    if (list.updatedAnswerIntArray.isNullOrEmpty()) {
                        when (list.answer as ArrayList<*>) {
                            is ArrayList<*> -> {
                                Timber.d("Saved answer DD: ${list.answer}")
                                val savedAns: ArrayList<Long> = list.answer as ArrayList<Long>
                                val savedStringArray: ArrayList<String> = ArrayList()
                                savedStringArray.add(String.format("%.0f", savedAns[0]))
                                Timber.d("Saved answer DD string: $savedStringArray")
                                list.savedAnsArray = savedStringArray
                                var savedAnsValue: String = ""
                                list.options.forEach {
                                    if (it.id == list.savedAnsArray[0]) {
                                        savedAnsValue = it.value
                                        it.isSelected = true
                                        Timber.d("Saved ans DD value 2: $savedAnsValue")
                                        holder.selectFromDropdown.hideView()
                                        holder.tvSelected.showView()
                                        holder.tvSelected.text = savedAnsValue
                                        holder.cvAnswer.strokeWidth = Utils.dpToPx(context, 1)
                                        holder.cvAnswer.strokeColor =
                                            Color.parseColor(brandTheme.themeColors.primary.hexCode)
                                        holder.cvAnswer.setCardBackgroundColor(
                                            ContextCompat.getColor(
                                                context,
                                                R.color.white
                                            )
                                        )
                                        holder.ivDownIcon.setColorFilter(ContextCompat.getColor(context,R.color.text_black), PorterDuff.Mode.SRC_IN)
                                        context.enableNextButton()
                                    }
                                }
                            }
                        }
                    }
                } else {
                    holder.selectFromDropdown.showView()
                    holder.tvSelected.hideView()
                    holder.cvAnswer.strokeWidth = Utils.dpToPx(context, 0)
                    holder.cvAnswer.setCardBackgroundColor(
                        ContextCompat.getColor(
                            context,
                            R.color.bg_gray
                        )
                    )
                    holder.ivDownIcon.setColorFilter(ContextCompat.getColor(context,R.color.text_light_gray), PorterDuff.Mode.SRC_IN)
                }
                holder.cvAnswer.setOnClickListener(View.OnClickListener {
                    (context as SurveyQuestionsActivity).callDropDownBottomSheet(
                        list.options,
                        position
                    )
                })

//                if(SurveyDDOptionsAdapter.selectedOptions.isNullOrEmpty()){
//                    list.updatedAnswerInt= SurveyDDOptionsAdapter.selectedOptions
//                }

            }
            else -> {
                holder as ViewHolderRadio
                (context as SurveyQuestionsActivity).showHidePrevious(position)
                val list = questionsList[position]
                viewDelayAnimation(holder, position)
                holder.etOther.setOnTouchListener(View.OnTouchListener { view, motionEvent ->
                    view.parent.requestDisallowInterceptTouchEvent(true)
                    false
                })
                holder.tvDescription.text = context.getHTMLTagsToSpannedString( list.description)
                if (list.isRequired) {
                    holder.tvQuestion.text = TextUtils.concat(list.question, " ", ss1)
                    (context as SurveyQuestionsActivity).disableNextButton()
                } else {
                    holder.tvQuestion.text = list.question
                    (context as SurveyQuestionsActivity).enableNextButton()
                }
                holder.questionNo.text =
                    "Question " + (position + 1).toString() + "/" + itemCount.toString()
                val viewAdapter = SurveySCOptionsAdapter(
                    list.options,
                    position,
                    (context as SurveyQuestionsActivity)
                )
                if (list.isOther) {
                    holder.cvOther.showView()
                    holder.tvOther.showView()

                } else {
                    holder.cvOther.hideView()
                    holder.tvOther.hideView()
                }
                holder.rvOptions.apply {
                    isNestedScrollingEnabled = false
                    setHasFixedSize(false)
                    adapter = viewAdapter
                }
                if (SurveySCOptionsAdapter.selectedOption.isNullOrEmpty()) {
                    list.updatedAnswerInt = SurveySCOptionsAdapter.selectedOption
                    Timber.d("RadioAnswer: ${list.updatedAnswerInt}")
                }
                if (list.answer != null && list.updatedAnswerInt.isNullOrEmpty() && list.updatedOther.isNullOrEmpty()) {
                    Timber.d("Updating.....")
                    when (list.answer) {
                        is Int -> {
                            val savedAns: String = String.format("%.0f", list.answer)
                            list.savedRadioAns = savedAns

                        }
                    }
                }
                if (!list.savedRadioAns.isNullOrEmpty()) {
                    Timber.d("Saved ans radio: ${list.savedRadioAns}")
                    list.updatedAnswerInt = list.savedRadioAns
                    list.options.forEach {
                        if (it.id == list.savedRadioAns) {
                            it.isSelected = true
                            holder.rvOptions.adapter?.notifyDataSetChanged()
                        }
                    }
                    context.enableNextButton()
                } else if (!list.updatedAnswerInt.isNullOrEmpty()) {
                    Timber.d("Updated ans radio1: ${list.updatedAnswerInt}")
                    list.options.forEach {
                        if (it.id == list.updatedAnswerInt) {
                            it.isSelected = true
                            holder.rvOptions.adapter?.notifyDataSetChanged()
                        }
                    }
                    list.updatedOther = ""
                    holder.etOther.text?.clear()
                    holder.clOtherAdd.hideView()
                    holder.cvOther.showView()
                    holder.tvOther.text = "Other"
                    holder.cvOtherSelected.hideView()
                    context.enableNextButton()
                } else {
                    Timber.d("Updated ans radio: ${list.updatedAnswerInt}")
                    list.options.forEach {
//                    if (it.id == list.updatedAnswerInt || it.id == list.savedRadioAns) {
                        it.isSelected = false
                        holder.rvOptions.adapter?.notifyDataSetChanged()
                        //    }
                    }
                }
                if (!list.other.isNullOrEmpty() && list.updatedAnswerInt.isNullOrEmpty() && list.updatedOther.isNullOrEmpty()) {
                    Timber.d("Other saved ans: ${list.other}")
                    list.updatedOther = list.other
//                holder.tvOther.text = list.other
//                holder.cvOther.hideView()
//                holder.cvOtherSelected.showView()
                    context.enableNextButton()
                }
                if (!list.updatedOther.isNullOrEmpty()) {
                    Timber.d("Updated other radio: ${list.updatedOther}")
                    holder.tvOther.text = list.updatedOther
                    holder.cvOther.hideView()
                    holder.cvOtherSelected.showView()
                    list.options.forEach {
                        if (it.id == list.updatedAnswerInt || it.id == list.savedRadioAns) {
                            it.isSelected = false
                            holder.rvOptions.adapter?.notifyDataSetChanged()
                        }
                    }
                    context.enableNextButton()
                } else {
                    list.updatedOther = ""
                    holder.etOther.text?.clear()
                    holder.clOtherAdd.hideView()
                    if (list.isOther) {
                        holder.cvOther.showView()
                        holder.tvOther.showView()
                    } else {
                        holder.cvOther.hideView()
                        holder.tvOther.hideView()
                    }
                    holder.tvOther.text = "Other"
                    holder.cvOtherSelected.hideView()
                }
                holder.cvOther.setOnClickListener {
                    holder.clOtherAdd.showView()
                    holder.tvOther.hideView()
                }
                holder.cvOtherSelected.setOnClickListener {
                    list.updatedOther = ""
                    holder.etOther.text?.clear()
                    holder.clOtherAdd.hideView()
                    holder.cvOther.showView()
                    holder.tvOther.text = "Other"
                    holder.cvOtherSelected.hideView()
                    holder.cvOther.strokeWidth = Utils.dpToPx(context, 0)
                    holder.cvOther.setCardBackgroundColor(
                        ContextCompat.getColor(
                            context,
                            R.color.bg_gray
                        )
                    )
                    if (list.isRequired) {
                        context.disableNextButton()
                    } else context.enableNextButton()
                }
                holder.rvOptions.addOnItemTouchListener(
                    RecyclerTouchListener(
                        context,
                        object : RecyclerTouchListener.ClickListener {
                            @SuppressLint("Recycle")
                            override fun onClick(view: View?, position: Int) {
                                if (list.isOther) {
                                    list.updatedOther = ""
                                    holder.etOther.text?.clear()
                                    holder.clOtherAdd.hideView()
                                    holder.tvOther.showView()
                                    holder.tvOther.text = "Other"
                                    holder.cvOther.showView()
                                    holder.cvOtherSelected.hideView()
                                    holder.cvOther.strokeWidth = Utils.dpToPx(context, 0)
                                    holder.cvOther.setCardBackgroundColor(
                                        ContextCompat.getColor(
                                            context,
                                            R.color.bg_gray
                                        )
                                    )
                                    Timber.d("Other visible? :  ${holder.cvOther.isVisible()}")
                                }
                            }
                        })
                )
//
                holder.etOther.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {

                    }

                    override fun afterTextChanged(s: Editable?) {
                        if (!TextUtils.isEmpty(s!!.trim()) && !TextUtils.isEmpty(s)) {
                            Timber.d("Other text : ${s.toString()}")
                            list.updatedOther = s.toString()
                            holder.tvOther.text = s.toString()
                            holder.cvOther.strokeWidth = Utils.dpToPx(context, 1)
                            holder.cvOther.strokeColor =
                                Color.parseColor(brandTheme.themeColors.primary.hexCode)
                            holder.cvOther.setCardBackgroundColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.white
                                )
                            )
                        } else {
                            Timber.d("String empty")
                            holder.etOther.text?.clear()
                            list.updatedOther = ""
                            holder.clOtherAdd.hideView()
                            holder.cvOther.showView()
                            holder.tvOther.text = "Other"
                            holder.tvOther.showView()
                            holder.cvOtherSelected.hideView()
                            holder.cvOther.strokeWidth = Utils.dpToPx(context, 0)
                            holder.cvOther.setCardBackgroundColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.bg_gray
                                )
                            )
                            if (list.isRequired) context.disableNextButton() else context.enableNextButton()
                        }
                    }

                })
                holder.cvAdd.setOnClickListener {
                    Timber.d("Add clicked")
                    if (!list.updatedOther.isNullOrEmpty()) {
                        if (list.updatedOther.trim().length > 200) {
                            context.showToast("Characters Must be less than 200")
                        } else {
                            holder.clOtherAdd.hideView()
                            holder.tvOther.showView()
                            holder.cvOther.hideView()
                            holder.cvOtherSelected.showView()
                            if (!list.updatedAnswerInt.isNullOrEmpty() || list.answer != null) {
                                list.options.forEach {
                                    if (it.id == list.updatedAnswerInt) {
                                        it.isSelected = false
                                        holder.rvOptions.adapter?.notifyDataSetChanged()
                                    }
                                }
                            }
                            listener.onSCOtherSelected(position, list.updatedOther)
                            //         startBlinkAnim(holder, position, list.updatedOther)
                            context.enableNextButton()
                        }
                    } else {
                        holder.clOtherAdd.hideView()
                        holder.cvOther.showView()
                        holder.tvOther.showView()
                        holder.cvOtherSelected.hideView()
                        context.disableNextButton()
                    }
                }

            }
        }
    }

    private fun getColorWithAlpha(alpha: Double): String{
        val alphaFixed = Math.round(alpha * 255)
        var alphaHex = java.lang.Long.toHexString(alphaFixed)
        if (alphaHex.length == 1) {
            alphaHex = "0$alphaHex"
        }
        val colorWithAlpha = Colors.PRIMARY_BRAND_COLOR.replace("#", "#$alphaHex")
        return colorWithAlpha
    }
    private fun setSliderThumbInnerColor(){
        val colorWithAlpha50percent = getColorWithAlpha(0.5)
        val layerDrawble = context.resources.getDrawable(R.drawable.slider_thumb) as LayerDrawable
        val l1 = layerDrawble.getDrawable(0)
        l1.setTint(Color.parseColor(Colors.PRIMARY_BRAND_COLOR))
        val gradientDrawable = layerDrawble.findDrawableByLayerId(R.id.innerCircle) as GradientDrawable
        gradientDrawable.setStroke(Utils.dpToPx(context, 9), Color.parseColor(colorWithAlpha50percent))
        val l2 = layerDrawble.getDrawable(1)
        l2.setTint(context.resources.getColor(R.color.white))
    }
    private fun setSliderThumbOuterColor(){
        val colorWithAlpha80percent = getColorWithAlpha(0.8)
        val ld = context.resources.getDrawable(R.drawable.slider_custom_thumb) as LayerDrawable
        val d1 = ld.getDrawable(0)
        d1.setTint(Color.parseColor(colorWithAlpha80percent))
    }
    private fun setThumbDrawable(progress: Int, holder: ViewHolderSlider, position: Int) {
        val list = questionsList[position]

        when (progress) {
            0 -> {
                val ld = context.resources.getDrawable(R.drawable.slider_thumb_0) as LayerDrawable
                val d1 = ld.getDrawable(0)
                d1.setTint(Color.parseColor(Colors.PRIMARY_BRAND_COLOR))
                val d2 = ld.getDrawable(1)
                d2.setTint(Color.parseColor(Colors.FONT_COLOR))
                holder.slider.setThumbDrawable(context.resources.getDrawable(R.drawable.slider_thumb_0))
                list.updatedAnswerInt = 0.toString()
            }
            1 -> {
                val ld = context.resources.getDrawable(R.drawable.slider_thumb_1) as LayerDrawable
                val d1 = ld.getDrawable(0)
                d1.setTint(Color.parseColor(Colors.PRIMARY_BRAND_COLOR))
                val d2 = ld.getDrawable(1)
                d2.setTint(Color.parseColor(Colors.FONT_COLOR))
                holder.slider.setThumbDrawable(context.resources.getDrawable(R.drawable.slider_thumb_1))
                list.updatedAnswerInt = 1.toString()
            }
            2 -> {
                val ld = context.resources.getDrawable(R.drawable.slider_thumb_2) as LayerDrawable
                val d1 = ld.getDrawable(0)
                d1.setTint(Color.parseColor(Colors.PRIMARY_BRAND_COLOR))
                val d2 = ld.getDrawable(1)
                d2.setTint(Color.parseColor(Colors.FONT_COLOR))
                holder.slider.setThumbDrawable(context.resources.getDrawable(R.drawable.slider_thumb_2))
                list.updatedAnswerInt = 2.toString()
            }
            3 -> {
                val ld = context.resources.getDrawable(R.drawable.slider_thumb_3) as LayerDrawable
                val d1 = ld.getDrawable(0)
                d1.setTint(Color.parseColor(Colors.PRIMARY_BRAND_COLOR))
                val d2 = ld.getDrawable(1)
                d2.setTint(Color.parseColor(Colors.FONT_COLOR))
                holder.slider.setThumbDrawable(context.resources.getDrawable(R.drawable.slider_thumb_3))
                list.updatedAnswerInt = 3.toString()
            }
            4 -> {
                val ld = context.resources.getDrawable(R.drawable.slider_thumb_4) as LayerDrawable
                val d1 = ld.getDrawable(0)
                d1.setTint(Color.parseColor(Colors.PRIMARY_BRAND_COLOR))
                val d2 = ld.getDrawable(1)
                d2.setTint(Color.parseColor(Colors.FONT_COLOR))
                holder.slider.setThumbDrawable(context.resources.getDrawable(R.drawable.slider_thumb_4))
                list.updatedAnswerInt = 4.toString()
            }
            5 -> {
                val ld = context.resources.getDrawable(R.drawable.slider_thumb_5) as LayerDrawable
                val d1 = ld.getDrawable(0)
                d1.setTint(Color.parseColor(Colors.PRIMARY_BRAND_COLOR))
                val d2 = ld.getDrawable(1)
                d2.setTint(Color.parseColor(Colors.FONT_COLOR))
                holder.slider.setThumbDrawable(context.resources.getDrawable(R.drawable.slider_thumb_5))
                list.updatedAnswerInt = 5.toString()
            }
            6 -> {
                val ld = context.resources.getDrawable(R.drawable.slider_thumb_6) as LayerDrawable
                val d1 = ld.getDrawable(0)
                d1.setTint(Color.parseColor(Colors.PRIMARY_BRAND_COLOR))
                val d2 = ld.getDrawable(1)
                d2.setTint(Color.parseColor(Colors.FONT_COLOR))
                holder.slider.setThumbDrawable(context.resources.getDrawable(R.drawable.slider_thumb_6))
                list.updatedAnswerInt = 6.toString()
            }
            7 -> {
                val ld = context.resources.getDrawable(R.drawable.slider_thumb_7) as LayerDrawable
                val d1 = ld.getDrawable(0)
                d1.setTint(Color.parseColor(Colors.PRIMARY_BRAND_COLOR))
                val d2 = ld.getDrawable(1)
                d2.setTint(Color.parseColor(Colors.FONT_COLOR))
                holder.slider.setThumbDrawable(context.resources.getDrawable(R.drawable.slider_thumb_7))
                list.updatedAnswerInt = 7.toString()
            }
            8 -> {
                val ld = context.resources.getDrawable(R.drawable.slider_thumb_8) as LayerDrawable
                val d1 = ld.getDrawable(0)
                d1.setTint(Color.parseColor(Colors.PRIMARY_BRAND_COLOR))
                val d2 = ld.getDrawable(1)
                d2.setTint(Color.parseColor(Colors.FONT_COLOR))
                holder.slider.setThumbDrawable(context.resources.getDrawable(R.drawable.slider_thumb_8))
                list.updatedAnswerInt = 8.toString()
            }
            9 -> {
                val ld = context.resources.getDrawable(R.drawable.slider_thumb_9) as LayerDrawable
                val d1 = ld.getDrawable(0)
                d1.setTint(Color.parseColor(Colors.PRIMARY_BRAND_COLOR))
                val d2 = ld.getDrawable(1)
                d2.setTint(Color.parseColor(Colors.FONT_COLOR))
                holder.slider.setThumbDrawable(context.resources.getDrawable(R.drawable.slider_thumb_9))
                list.updatedAnswerInt = 9.toString()
            }
            10 -> {
                val ld = context.resources.getDrawable(R.drawable.slider_thumb_10) as LayerDrawable
                val d1 = ld.getDrawable(0)
                d1.setTint(Color.parseColor(Colors.PRIMARY_BRAND_COLOR))
                val d2 = ld.getDrawable(1)
                d2.setTint(Color.parseColor(Colors.FONT_COLOR))
                holder.slider.setThumbDrawable(context.resources.getDrawable(R.drawable.slider_thumb_10))
                list.updatedAnswerInt = 10.toString()
            }
        }
        if (!list.updatedAnswerInt.isNullOrEmpty()) {
            (context as SurveyQuestionsActivity).enableNextButton()
        }
    }

    private fun startSliderMoveAnim(viewHolderSlider: ViewHolderSlider) {
        val translationX: ObjectAnimator =
            ObjectAnimator.ofFloat(viewHolderSlider.moveAnimView, "translationX", 0f, 60f)
        translationX.repeatMode = ObjectAnimator.REVERSE
        translationX.duration = 500
        translationX.startDelay = 400
        translationX.repeatCount = 1
        translationX.start()
        translationX.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                super.onAnimationStart(animation)
                viewHolderSlider.shrinkExpandAnim.hideView()
                viewHolderSlider.slider.hideThumb(true)
            }

            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                viewHolderSlider.slider.hideThumb(false)
                val drawable2 = context.resources.getDrawable(R.drawable.slider_thumb3)
                drawable2.setTint(Color.parseColor(Colors.PRIMARY_BRAND_COLOR))
                viewHolderSlider.slider.setThumbDrawable(context.resources.getDrawable(R.drawable.slider_custom_thumb))
                viewHolderSlider.moveAnimView.hideView()
                viewHolderSlider.shrinkExpandAnim.showView()
                startSliderAnim(viewHolderSlider)
            }
        })
    }

    private fun startSliderAnim(viewHolderSlider: ViewHolderSlider) {
        val animationSet = AnimatorSet()
        val scaleY: ObjectAnimator =
            ObjectAnimator.ofFloat(viewHolderSlider.shrinkExpandAnim, "scaleY", 1f, 2.8f)
        scaleY.repeatCount = 2
        scaleY.repeatMode = ObjectAnimator.RESTART
        scaleY.duration = 800
        val scaleX: ObjectAnimator =
            ObjectAnimator.ofFloat(viewHolderSlider.shrinkExpandAnim, "scaleX", 1f, 2.8f)
        scaleX.repeatCount = 2
        scaleX.repeatMode = ObjectAnimator.RESTART
        scaleX.duration = 800
        val alpha: ObjectAnimator =
            ObjectAnimator.ofFloat(viewHolderSlider.shrinkExpandAnim, "alpha", 0.7f, 0.0f)
        alpha.repeatCount = 2
        alpha.repeatMode = ObjectAnimator.RESTART
        alpha.duration = 800
        animationSet.playTogether(scaleX, scaleY, alpha)
        animationSet.addListener(object : AnimatorListenerAdapter() {
            var count: Int = 0
            override fun onAnimationStart(animation: Animator) {
            }

            override fun onAnimationCancel(animation: Animator) {
            }

            override fun onAnimationEnd(animation: Animator) {
                viewHolderSlider.shrinkExpandAnim.animate().alpha(0.0f)
            }
        })
        animationSet.startDelay = 400
        animationSet.start()
    }

    private fun viewDelayAnimation(holder: RecyclerView.ViewHolder, position: Int) {
        if (position > 0) {

            val animation: Animation =
                AnimationUtils.loadAnimation(context, R.anim.slide_in_right).apply {
                    duration = 300
                }
            animation.setInterpolator(AccelerateDecelerateInterpolator())
            if (holder is ViewHolderShort) {
                holder.clQuestion.startAnimation(animation)
            } else if (holder is ViewHolderLong) {
                holder.clQuestion.startAnimation(animation)
            } else if (holder is ViewHolderSlider) {
                holder.clQuestion.startAnimation(animation)
            } else if (holder is ViewHolderDropDown) {
                holder.clQuestion.startAnimation(animation)
            } else if (holder is ViewHolderCheckBox) {
                holder.clQuestion.startAnimation(animation)
            } else if (holder is ViewHolderRadio) {
                holder.clQuestion.startAnimation(animation)
            }
//            if (holder is ViewHolderShort || holder is ViewHolderLong
//                || holder is ViewHolderSlider || holder is ViewHolderDropDown
//                || holder is ViewHolderCheckBox || holder is ViewHolderRadio
//            ) {
//                holder.clQuestion.startAnimation(animation)
//            }
            animation.start()


        }
    }

    override fun onAttachedToRecyclerView(@NonNull recyclerView: RecyclerView) {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(@NonNull recyclerView: RecyclerView, newState: Int) {
                on_attach = false
                super.onScrollStateChanged(recyclerView, newState)
            }
        })
        super.onAttachedToRecyclerView(recyclerView)
    }

    interface SCOtherSelectedListener {
        fun onSCOtherSelected(position: Int, id: String)
    }
}