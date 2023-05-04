package com.lib.loopsdk.ui.feature_survey.presentation.survey_questions

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.reflect.TypeToken
import com.lib.loopsdk.R
import com.lib.loopsdk.core.util.Colors
import com.lib.loopsdk.core.util.Prefs
import com.lib.loopsdk.core.util.hideView
import com.lib.loopsdk.core.util.showView
import com.lib.loopsdk.data.remote.dto.InitializeDto
import com.lib.loopsdk.data.remote.dto.SurveyQuestionsDto
import com.lib.loopsdk.databinding.ListItemSurveyDropdownBottomsheetBinding
import timber.log.Timber

class SurveyDDOptionsAdapter(private val myContext: Context,
                             private val options: List<SurveyQuestionsDto.Data.Question.Option>,
                             private val listener: DDSelectedListener

) : RecyclerView.Adapter<SurveyDDOptionsAdapter.MyVH>(){

    inner class MyVH(val view: ListItemSurveyDropdownBottomsheetBinding) : RecyclerView.ViewHolder(view.root) {
        fun bind(
            view: View,
            options: List<SurveyQuestionsDto.Data.Question.Option>,
            position: Int,
            id: String
        ) {
            view.setOnClickListener {

                options[position].isSelected = !options[position].isSelected
                options.forEach {
                    if (options[position].isSelected) {
                        selectedOptions = options[position].value
                        if (it.id != options[position].id) {
                            it.isSelected = false
                        }
                    } else {
                        it.isSelected = false
                    }
                }
                listener.onDDClicked(id)
                notifyDataSetChanged()

            }
        }
    }
    companion object{
        var selectedOptions: String = ""
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyVH {
        val view =
            ListItemSurveyDropdownBottomsheetBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyVH(view)
    }

    override fun onBindViewHolder(holder: MyVH, position: Int) {
        val view = holder.view
        view.brandTheme = Prefs.getNonPrimitiveData<InitializeDto.Data.BrandTheme>(object: TypeToken<InitializeDto.Data.BrandTheme>(){}.type, "BRAND_THEME")
        view.brandThemeColors = Colors
        val brandTheme = Prefs.getNonPrimitiveData<InitializeDto.Data.BrandTheme>(object: TypeToken<InitializeDto.Data.BrandTheme>(){}.type, "BRAND_THEME")
        view.tvChoice.text = options[position].value
        if(options[position].isSelected){
            selectedOptions = options[position].value
            view.ivIsSelected.showView()
            view.tvChoice.setTextColor(Color.parseColor(Colors.PRIMARY_BRAND_COLOR))
        } else {
            view.ivIsSelected.hideView()
            view.tvChoice.setTextColor(ContextCompat.getColor(myContext, R.color.text_black))
        }
        Timber.d("Selected option: $selectedOptions")
        holder.bind(view.root, options, position, options[position].id)
    }

    override fun getItemCount(): Int {
        return options.size
    }

    interface DDSelectedListener {
        fun onDDClicked(id: String)
    }

}