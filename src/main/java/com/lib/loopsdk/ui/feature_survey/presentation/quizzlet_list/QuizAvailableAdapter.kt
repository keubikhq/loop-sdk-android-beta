package com.lib.loopsdk.ui.feature_survey.presentation.quizzlet_list

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.reflect.TypeToken
import com.lib.loopsdk.R
import com.lib.loopsdk.core.util.*
import com.lib.loopsdk.data.remote.dto.InitializeDto
import com.lib.loopsdk.data.remote.dto.QuizAvailableDto
import com.lib.loopsdk.data.remote.dto.SurveyAvailableDto
import com.lib.loopsdk.databinding.ListItemSurveyBinding
import timber.log.Timber


class QuizAvailableAdapter(private val myContext: Context,
                           private val list: ArrayList<QuizAvailableDto.Data.Quiz> = arrayListOf(),
                           private val listener: QuizListener
) : RecyclerView.Adapter<QuizAvailableAdapter.MyVH>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyVH {
        val view = ListItemSurveyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyVH(view)
    }

    class MyVH(val view: ListItemSurveyBinding) : RecyclerView.ViewHolder(view.root) {
        fun bind(
            view: View,
            listener: QuizListener,
            position: Int,
            quizId: String,
            quiz: QuizAvailableDto.Data.Quiz
        ) {
            view.setOnClickListener {

            }
        }
    }

    fun addData(data: List<QuizAvailableDto.Data.Quiz>) {
        val oldSize = list.size
        data.forEach {
            if (!list.contains(it)) {
                list.add(it)
            }
        }
        notifyItemRangeInserted(list.size, data.size)
    }

    fun clearAllData() {
        list.clear()
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: MyVH, position: Int) {
        val view = holder.view
        view.brandTheme = Prefs.getNonPrimitiveData<InitializeDto.Data.BrandTheme>(object :
            TypeToken<InitializeDto.Data.BrandTheme>() {}.type, "BRAND_THEME")
        view.brandThemeColors = Colors
        view.tvTitle.text = list[position].name
        if (list[position].partiallyFilled == 1) {
            view.tvStartResume.text = myContext.getString(R.string.resume)
        } else view.tvStartResume.text = myContext.getString(R.string.start_survey_quiz).format(
            myContext.getString(
                R.string.quiz
            )
        )

        val containsPoints = list[position].cohorts.filter { it.benefitType == 2 }
        val containsCoupons = list[position].cohorts.filter { it.benefitType == 1 }
        val containsMsg = list[position].cohorts.filter { it.benefitType == 3 }

        Timber.d("Check values: $containsCoupons, $containsPoints, $containsMsg")
        //all three
        if(containsPoints.isNotEmpty() && containsCoupons.isNotEmpty() && containsMsg.isNotEmpty()){
            view.tvBenefit.text = myContext.getString(R.string.benefit_point_survey).format(getFormattedPoints( containsPoints[0].benefitPoints.toString() )) + " " + Constants.init.pointsIdentifier.pointsLabelPlural + " & coupons"
        }
        //points and coupons
        else if(containsPoints.isNotEmpty() && containsCoupons.isNotEmpty() && containsMsg.isEmpty()){
            view.tvBenefit.text = myContext.getString(R.string.benefit_point_survey).format(getFormattedPoints(containsPoints[0].benefitPoints.toString())) + " " +Constants.init.pointsIdentifier.pointsLabelPlural+ " & coupons"
        }//only coupons
        else if(containsPoints.isEmpty() && containsCoupons.isNotEmpty() && containsMsg.isEmpty()){
            view.tvBenefit.text = myContext.getString(R.string.win_coupons)
        }//only points
        else if(containsPoints.isNotEmpty() && containsCoupons.isEmpty() && containsMsg.isEmpty()){
            view.tvBenefit.text = myContext.getString(R.string.benefit_point_survey).format(
                getFormattedPoints(containsPoints[0].benefitPoints.toString())) + " " + Constants.init.pointsIdentifier.pointsLabelPlural
        }//only msg
        else if(containsPoints.isEmpty() && containsCoupons.isEmpty() && containsMsg.isNotEmpty()){
            view.tvBenefit.text = myContext.getString(R.string.receive_a_surprise_message)
        }//coupon n msg
        else if(containsPoints.isEmpty() && containsCoupons.isNotEmpty() && containsMsg.isNotEmpty()){
            view.tvBenefit.text = myContext.getString(R.string.win_coupons) + " & much more"
        }//points n msg
        else if(containsPoints.isNotEmpty() && containsCoupons.isEmpty() && containsMsg.isNotEmpty()){
            view.tvBenefit.text = myContext.getString(R.string.benefit_point_survey).format(getFormattedPoints(containsPoints[0].benefitPoints.toString())) + " "+ Constants.init.pointsIdentifier.pointsLabelPlural + " & much more"
        }

        view.cvStartResume.setOnClickListener {
            listener.onQuizClicked(position, list[position].id, list[position].type, list[position])
        }
        holder.bind(view.root, listener, position, list[position].id, list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface QuizListener {
        fun onQuizClicked(position: Int, quizId: String, quizType: Int, quiz: QuizAvailableDto.Data.Quiz)
    }
}