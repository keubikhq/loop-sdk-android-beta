package com.lib.loopsdk.ui.feature_survey.presentation.quizzlet_list

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.reflect.TypeToken
import com.lib.loopsdk.R
import com.lib.loopsdk.core.util.Colors
import com.lib.loopsdk.core.util.Constants
import com.lib.loopsdk.core.util.Prefs
import com.lib.loopsdk.core.util.getFormattedPoints
import com.lib.loopsdk.data.remote.dto.AllAvailableScratchCardsDto
import com.lib.loopsdk.data.remote.dto.InitializeDto
import com.lib.loopsdk.data.remote.dto.SurveyAvailableDto
import com.lib.loopsdk.databinding.ListItemSurveyBinding

class SurveyAvailableAdapter(private val myContext: Context,
                             private val list: ArrayList<SurveyAvailableDto.Data.Survey> = arrayListOf(),
                             private val listener: SurveyListener
) : RecyclerView.Adapter<SurveyAvailableAdapter.MyVH>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyVH {
        val view = ListItemSurveyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyVH(view)
    }

    class MyVH(val view: ListItemSurveyBinding) : RecyclerView.ViewHolder(view.root) {
        fun bind(
            view: View,
            listener: SurveyListener,
            position: Int,
            surveyId: String,
            survey: SurveyAvailableDto.Data.Survey
        ) {
            view.setOnClickListener {
     //           listener.onSurveyClicked(position, surveyId, survey)
            }
        }
    }
    fun addData(data: List<SurveyAvailableDto.Data.Survey>) {
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
        view.brandTheme = Prefs.getNonPrimitiveData<InitializeDto.Data.BrandTheme>(object: TypeToken<InitializeDto.Data.BrandTheme>(){}.type, "BRAND_THEME")
        view.brandThemeColors = Colors
        view.tvTitle.text = list[position].name
        if(list[position].partiallyFilled){
            view.tvStartResume.text = myContext.getString(R.string.resume)
        }else view.tvStartResume.text = myContext.getString(R.string.start_survey_quiz).format(myContext.getString(R.string.survey))
        when(list[position].benefitType){
            1 -> {
                view.tvBenefit.text = myContext.getString(R.string.win_coupons)
            }
            2-> {
                view.tvBenefit.text = myContext.getString(R.string.benefit_point_survey).format(getFormattedPoints(list[position].benefitValue.toString())) + " " + Constants.init.pointsIdentifier.pointsLabelPlural
            }
            3 -> {
                view.tvBenefit.text = myContext.getString(R.string.receive_a_surprise_message)
            }
        }
        view.cvStartResume.setOnClickListener {
            listener.onSurveyClicked(position, list[position].surveyId, list[position])
        }
        holder.bind(view.root, listener, position, list[position].surveyId, list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface SurveyListener{
        fun onSurveyClicked(position: Int, surveyId: String, survey: SurveyAvailableDto.Data.Survey)
    }

}