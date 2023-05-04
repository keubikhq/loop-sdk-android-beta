package com.lib.loopsdk.ui.feature_survey.presentation.trivia_quiz

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.reflect.TypeToken
import com.lib.loopsdk.R
import com.lib.loopsdk.core.util.Colors
import com.lib.loopsdk.core.util.Prefs
import com.lib.loopsdk.core.util.hideView
import com.lib.loopsdk.data.remote.dto.InitializeDto
import com.lib.loopsdk.data.remote.dto.QuizQuestionsDto
import com.lib.loopsdk.databinding.FragmentQuestionSingleChoiceBinding
import timber.log.Timber


class TriviaQuesAdapter(
    private val context: Context,
    private val questionsList: List<QuizQuestionsDto.Data.Question>
) : RecyclerView.Adapter<TriviaQuesAdapter.MyVH>() {

    inner class MyVH(val view: FragmentQuestionSingleChoiceBinding) :
        RecyclerView.ViewHolder(view.root) {
        fun bind(
            view: View,
            options: List<QuizQuestionsDto.Data.Question>,
            position: Int,
            id: String
        ) {
            view.setOnClickListener {

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyVH {
        val view =
            FragmentQuestionSingleChoiceBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return MyVH(view)
    }

    override fun onBindViewHolder(holder: MyVH, position: Int) {
        val view = holder.view
        view.brandTheme = Prefs.getNonPrimitiveData<InitializeDto.Data.BrandTheme>(object :
            TypeToken<InitializeDto.Data.BrandTheme>() {}.type, "BRAND_THEME")
        view.brandThemeColors = Colors

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
        val list = questionsList[position]
//        viewDelayAnimation(holder, position)
//        view.tvDescription.text = list.description
        view.tvDescription.hideView()
        view.tvQuestion.text = list.question
        view.cvQuestionCount.hideView()

//        view.tvQuestionNo.text =
//            "Question " + (position + 1).toString() + "/" + itemCount.toString()

        val viewAdapter = TriviaOptionsAdapter(
            list.options,
            position,
            (context as TriviaQuizQuesActivity)
        )

        view.cvChoiceOther.hideView()
        view.llOther.hideView()

        view.rvOptions.apply {
            isNestedScrollingEnabled = false
            setHasFixedSize(false)
            adapter = viewAdapter
        }
//        if (TriviaOptionsAdapter.selectedOption.isNullOrEmpty()) {
//            list.updatedAnsString = TriviaOptionsAdapter.selectedOption
//            Timber.d("RadioAnswer: ${list.updatedAnsString}")
//        }
////        if (list.answeredOptionId != null && list.updatedAnsString.isNullOrEmpty()) {
////            Timber.d("Updating.....")
////
////            list.options.forEach {
////                if (it.id == list.answeredOptionId) {
////                    it.isSelected = true
////                    view.rvOptions.adapter?.notifyDataSetChanged()
////                }
////            }
//// //           context.enableNextButton()
////        } else
//            if (!list.updatedAnsString.isNullOrEmpty()) {
//            Timber.d("Updated ans radio1: ${list.updatedAnsString}")
//            list.options.forEach {
//                if (it.id == list.updatedAnsString) {
//                    it.isSelected = true
//                    view.rvOptions.adapter?.notifyDataSetChanged()
//                }
//            }
// //           context.enableNextButton()
//        } else {
//            Timber.d("Updated ans radio: ${list.updatedAnsString}")
//            list.options.forEach {
////                    if (it.id == list.updatedAnswerInt || it.id == list.savedRadioAns) {
//                it.isSelected = false
//                view.rvOptions.adapter?.notifyDataSetChanged()
//                //    }
//            }
//        }
        holder.bind(view.root, questionsList, position, list.id)
    }

    private fun viewDelayAnimation(holder: MyVH, position: Int) {
        if (position > 0) {

            val animation: Animation =
                AnimationUtils.loadAnimation(context, R.anim.slide_in_right).apply {
                    duration = 300
                }
            animation.setInterpolator(AccelerateDecelerateInterpolator())
            holder.view.clQuestion.startAnimation(animation)
            animation.start()

        }
    }

    override fun getItemCount(): Int {
        return questionsList.size
    }
}