package com.lib.loopsdk.ui.feature_survey.presentation.survey_questions

import android.content.Context
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
import com.lib.loopsdk.data.remote.dto.SurveyQuestionsDto
import com.lib.loopsdk.databinding.ListItemSurveyOptionsCbBinding
import timber.log.Timber
import java.util.ArrayList

class SurveyCBOptionsAdapter(private val myContext: Context,
                             private val options: List<SurveyQuestionsDto.Data.Question.Option>,
                             private val listener: CBSelectorListener
) : RecyclerView.Adapter<SurveyCBOptionsAdapter.MyVH>(){

    inner class MyVH(val view: ListItemSurveyOptionsCbBinding) : RecyclerView.ViewHolder(view.root) {
        fun bind(
            view: View,
            options: List<SurveyQuestionsDto.Data.Question.Option>,
            position: Int,
            id: String
        ) {
            view.setOnClickListener {

                val option = options[adapterPosition]
                option.isSelected = !option.isSelected
                notifyItemChanged(adapterPosition)
                listener.onCBSelected(position, id)
                notifyDataSetChanged()
                }
            }
        }
    companion object{
        var selectedOptions: ArrayList<String>? = ArrayList()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyVH {
        val view =
            ListItemSurveyOptionsCbBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyVH(view)
    }

    override fun onBindViewHolder(holder: MyVH, position: Int) {
        val view = holder.view
        view.brandTheme = Prefs.getNonPrimitiveData<InitializeDto.Data.BrandTheme>(object: TypeToken<InitializeDto.Data.BrandTheme>(){}.type, "BRAND_THEME")
        view.brandThemeColors = Colors
        view.tvChoice.text = options[position].value
        if(options[position].isSelected){
            if(selectedOptions?.contains(options[position].id) == false) {
                selectedOptions?.add(options[position].id)
            }
            view.ivCheck.showView()
    //        view.cbChoice.hideView()
            view.cbChoiceSelected.showView()
            view.cvChoiceSelected.showView()
            view.cvChoice.hideView()
        } else {
            if(selectedOptions?.contains(options[position].id) == true) {
                selectedOptions?.remove(options[position].id)
            }
            view.ivCheck.hideView()
   //         view.cbChoice.showView()
            view.cbChoiceSelected.hideView()
            view.cvChoiceSelected.hideView()
            view.cvChoice.showView()
        }
        Timber.d("Selected optionin cbadapter: $selectedOptions")
        holder.bind(view.root, options, position, options[position].id)
    }

    override fun getItemCount(): Int {
        return options.size
    }

    interface CBSelectorListener{
        fun onCBSelected(position: Int, id: String)
    }
}