package com.lib.loopsdk.ui.feature_landing_home.presentation

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.google.gson.reflect.TypeToken
import com.lib.loopsdk.R
import com.lib.loopsdk.core.util.*
import com.lib.loopsdk.core.util.Constants.init
import com.lib.loopsdk.data.remote.dto.GetHomeScreenDetailsDto
import com.lib.loopsdk.data.remote.dto.InitializeDto
import com.lib.loopsdk.databinding.ListItemCouponHomeBinding
import kotlin.collections.ArrayList

class FeaturedCouponsListAdapter(
    private val list: ArrayList<GetHomeScreenDetailsDto.Data.Coupon> = arrayListOf(),
    private val interaction: Interaction? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val TAG = this::class.java.simpleName

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: ListItemCouponHomeBinding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.list_item_coupon_home,
            parent,
            false)
        return ViewHolder(binding, interaction)
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun addData(data: List<GetHomeScreenDetailsDto.Data.Coupon>) {
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

    inner class ViewHolder(
        val binding: ListItemCouponHomeBinding,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(binding.root){

        fun bind(item: GetHomeScreenDetailsDto.Data.Coupon) {
            binding.brandTheme = Prefs.getNonPrimitiveData<InitializeDto.Data.BrandTheme>(object: TypeToken<InitializeDto.Data.BrandTheme>(){}.type, "BRAND_THEME")
            binding.brandThemeColors = Colors
            binding.executePendingBindings()

            val context = binding.root.context

            binding.tvCouponDescription.text = item.displayName

            if(item.pointsToUnlock == 0){
                binding.ivPointsIcon.hideView()
                binding.tvCouponValue.text = "Free"
            }else{
                binding.ivPointsIcon.showView()
                binding.tvCouponValue.text = item.pointsToUnlock.toString()
            }

            if(!item.featuredImage.isNullOrEmpty()){
                Glide.with(context)
                    .load(item.featuredImage)
                    .into(binding.ivCouponImage)
            }

            if(item.isExpiringSoon == 1){
                binding.matCvExpiringSoon.showView()
            }else{
                binding.matCvExpiringSoon.hideView()
            }

            when (item.couponClassification) {
                1, 2 -> {
                    binding.matCvDiscount.showView()
                    binding.tvCouponDiscount.text = "${init.defaultCurrency.symbol}${Utils.digitCountUpdate(item.maxDiscountValue)}"
                }
                3, 4 -> {
                    binding.matCvDiscount.showView()
                    binding.tvCouponDiscount.text = "${item.discountPercent}%"
                }
                5 -> {
                    binding.matCvDiscount.showView()
                    binding.tvCouponDiscount.hideView()
                    binding.ivClassIcon.showView()
                    binding.ivClassIcon.setImageResource(R.drawable.ic_vehicle_truck)
                }
                6-> {
                    binding.matCvDiscount.showView()
                    binding.tvCouponDiscount.hideView()
                    binding.ivClassIcon.showView()
                    binding.ivClassIcon.setImageResource(R.drawable.ic_gift)
                }
                else -> {
                    binding.matCvDiscount.hideView()
                }
            }

            binding.root.setOnClickListener {
                interaction?.onFeaturedCouponClicked(adapterPosition, item)
            }

        }

    }

    interface Interaction {
        fun onFeaturedCouponClicked(position: Int, coupon: GetHomeScreenDetailsDto.Data.Coupon)
    }
}