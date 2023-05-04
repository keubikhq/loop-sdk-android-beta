package com.lib.loopsdk.ui.feature_survey.presentation.trivia_quiz

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
import com.lib.loopsdk.databinding.ListItemSurveySinglechoiceBinding
import com.lib.loopsdk.databinding.ListItemTriviaOptionsBinding
import timber.log.Timber


class TriviaOptionsAdapter(private val options: List<QuizQuestionsDto.Data.Question.Option>,
                            private val currentQuestionPostion: Int,
                            private val listener: SCSelectorListener
) : RecyclerView.Adapter<TriviaOptionsAdapter.MyVH>() {

    inner class MyVH(val view: ListItemTriviaOptionsBinding) :
        RecyclerView.ViewHolder(view.root) {
        fun bind(
            view: View,
            options: List<QuizQuestionsDto.Data.Question.Option>,
            position: Int,
            answerId: String
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

                listener.onSCSelected(view,position, currentQuestionPostion, answerId)
                notifyDataSetChanged()

            }
        }
    }

    companion object {
        var selectedOption: String = ""
        var isCorrect: Boolean = false
        var correctAnswerId: String = ""
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyVH {
        val view =
            ListItemTriviaOptionsBinding.inflate(
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
            selectedOption = ""
            view.cvChoice.showView()
            view.cvChoiceSelected.hideView()
        }
//        if(!isCorrect){
//            view.cvChoiceWrong.showView()
//            view.cvChoice.hideView()
//        }
        if(options[position].setCorrect){
            view.cvChoiceCorrect.showView()
            view.cvChoice.hideView()
            view.cvChoiceWrong.hideView()
        }
        if(options[position].isWrong){
            view.cvChoiceSelected.hideView()
            view.cvChoiceWrong.showView()
            view.cvChoice.hideView()
        }
        Timber.d("Selected option: $selectedOption")
        holder.bind(view.root, options, position, options[position].id)
    }

    override fun getItemCount(): Int {
        return options.size
    }

    interface SCSelectorListener {
        fun onSCSelected(view: View, position: Int, currentQuestionPostion: Int, answerId: String)
    }
}