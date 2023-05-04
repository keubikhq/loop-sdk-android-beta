package com.lib.loopsdk.ui.feature_survey.presentation.survey_quiz_detail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.reflect.TypeToken
import com.lib.loopsdk.R
import com.lib.loopsdk.core.util.Colors
import com.lib.loopsdk.core.util.Constants
import com.lib.loopsdk.core.util.Prefs
import com.lib.loopsdk.core.util.hideView
import com.lib.loopsdk.data.remote.dto.InitializeDto
import com.lib.loopsdk.data.remote.dto.QuizDetailDto
import com.lib.loopsdk.databinding.ListItemMulticouponBenefitBinding

class MultiCouponAdapter(private val coupons: List<QuizDetailDto.Data.Cohort>): RecyclerView.Adapter<MultiCouponAdapter.MyVH>() {

    inner class MyVH(val view: ListItemMulticouponBenefitBinding): RecyclerView.ViewHolder(view.root){
        fun bind(
            view: View,
            coupons: List<QuizDetailDto.Data.Cohort>
        ) {
            view.setOnClickListener {

            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MultiCouponAdapter.MyVH {
        val view =
            ListItemMulticouponBenefitBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return MyVH(view)
    }
    override fun onBindViewHolder(holder: MultiCouponAdapter.MyVH, position: Int) {
        val view = holder.view
        view.brandTheme = Prefs.getNonPrimitiveData<InitializeDto.Data.BrandTheme>(object :
            TypeToken<InitializeDto.Data.BrandTheme>() {}.type, "BRAND_THEME")
        view.brandThemeColors = Colors

        view.tvBenefitDes.text=coupons[position].benefitDetails.couponName
        when(coupons[position].benefitDetails.couponClassification){
            1,2->{
                view.tvCouponBenefit.text =
                    Constants.init.defaultCurrency.symbol + coupons[position].benefitDetails.maxDiscountValue.toString() + " Voucher"
            }
            3,4->{
                view.tvCouponBenefit.text =
                    coupons[position].benefitDetails.discountPercent.toString() + "%" + " Discount"
            }
            5->{
                //free shipping
                view.tvCouponBenefit.text = "Free Delivery"
            }
            6->{
                //Other

            }
        }
        holder.bind(view.root, coupons)
    }
    override fun getItemCount(): Int {
        return coupons.size
    }
}