package com.lib.loopsdk.ui.feature_offers.coupon_detail

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
import com.lib.loopsdk.data.remote.dto.CountryCodesDto
import com.lib.loopsdk.data.remote.dto.InitializeDto
import com.lib.loopsdk.databinding.ListItemCountryCodeBinding
import timber.log.Timber


class CountryCodeAdapter(
    private val list: List<CountryCodesDto.Data.CountryCode>,
    private val listener: OnCountryCodeClickListener,
) : RecyclerView.Adapter<CountryCodeAdapter.CountryCodeViewHolder>() {

    class CountryCodeViewHolder(val view: ListItemCountryCodeBinding) : RecyclerView.ViewHolder(view.root) {
        fun bind(
            clCity: View,
            position: Int,
            list: List<CountryCodesDto.Data.CountryCode>,
            listener: OnCountryCodeClickListener
        ) {
            clCity.setOnClickListener {
                if (list[position].isSelected) {
                    list[position].isSelected = false
                } else {
                    list.forEach {
                        if (it.isSelected) {
                            it.isSelected = false
                        }
                    }
                    list[position].isSelected = true
                }
                listener.onCountryCodeItemClicked(list[position])

            }
        }
    }

    interface OnCountryCodeClickListener {
        fun onCountryCodeItemClicked(list: CountryCodesDto.Data.CountryCode)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountryCodeViewHolder {
        val view = ListItemCountryCodeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        view.brandTheme = Prefs.getNonPrimitiveData<InitializeDto.Data.BrandTheme>(object: TypeToken<InitializeDto.Data.BrandTheme>(){}.type, "BRAND_THEME")
        view.brandThemeColors = Colors
        return CountryCodeViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: CountryCodeViewHolder, position: Int) {
        Timber.d("tvCountryCode called"+ position)
        holder.view.tvCountryCode.text = list[position].name+"("+list[position].code+")"
        if(position==list.size-1) holder.view.divider.hideView()
        else  holder.view.divider.showView()
        val primaryColor=holder.view.brandTheme!!.themeColors.primary.hexCode
        if (list[position].isSelected) {
            holder.view.tvCountryCode.setTextColor(Color.parseColor(primaryColor))

//            holder.view.tvCountryCode.setTextColor(ContextCompat.getColor(holder.view.root.context,Color.parseColor(primaryColor)))
            holder.view.ivTick.showView()
        } else {
//            holder.view.tvCountryCode.setTextColor(R.color.text_black)

            holder.view.tvCountryCode.setTextColor(ContextCompat.getColor(holder.view.root.context,R.color.text_black))
            holder.view.ivTick.hideView()
        }
        holder.bind(holder.view.root, position, list, listener)
    }
}