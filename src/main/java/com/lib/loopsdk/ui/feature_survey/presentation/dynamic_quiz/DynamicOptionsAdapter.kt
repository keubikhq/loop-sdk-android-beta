package com.lib.loopsdk.ui.feature_survey.presentation.dynamic_quiz

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.reflect.TypeToken
import com.lib.loopsdk.core.util.Colors
import com.lib.loopsdk.core.util.Prefs
import com.lib.loopsdk.core.util.hideView
import com.lib.loopsdk.core.util.showView
import com.lib.loopsdk.data.remote.dto.InitializeDto
import com.lib.loopsdk.data.remote.dto.QuizQuestionsDto
import com.lib.loopsdk.data.remote.dto.SurveyQuestionsDto
import com.lib.loopsdk.databinding.ListItemSurveySinglechoiceBinding
import com.lib.loopsdk.ui.feature_survey.presentation.survey_questions.SurveySCOptionsAdapter
import timber.log.Timber

class DynamicOptionsAdapter(private val options: List<QuizQuestionsDto.Data.Question.Option>,
                             private val currentQuestionPostion: Int,
                             private val listener: SCSelectorListener
) : RecyclerView.Adapter<DynamicOptionsAdapter.MyVH>() {

    inner class MyVH(val view: ListItemSurveySinglechoiceBinding) :
        RecyclerView.ViewHolder(view.root) {
        fun bind(
            view: View,
            options: List<QuizQuestionsDto.Data.Question.Option>,
            position: Int,
            id: String
        ) {
            view.setOnClickListener {

                val option = options[adapterPosition]
                option.isSelected = !option.isSelected
                options.forEach {
                    if (options[position].isSelected) {
                        if (it.id != options[position].id) {
                            it.isSelected = false
                        }
                    } else {
                        it.isSelected = false
                    }
                }

                listener.onSCSelected(view, currentQuestionPostion, id)
                notifyDataSetChanged()

            }
        }
    }

    companion object {
        var selectedOption: String = ""

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyVH {
        val view =
            ListItemSurveySinglechoiceBinding.inflate(
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
        view.tvChoice.text = options[position].text
        if (options[position].isSelected) {
            selectedOption = options[position].id
            view.cvChoiceSelected.showView()
            view.cvChoice.hideView()
        } else {
            view.cvChoice.showView()
            view.cvChoiceSelected.hideView()
        }
        Timber.d("Selected option: $selectedOption")
        holder.bind(view.root, options, position, options[position].id)
    }

    override fun getItemCount(): Int {
        return options.size
    }

    interface SCSelectorListener {
        fun onSCSelected(view: View, position: Int, id: String)
    }
}