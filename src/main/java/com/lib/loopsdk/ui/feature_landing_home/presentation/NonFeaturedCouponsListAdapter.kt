package com.lib.loopsdk.ui.feature_landing_home.presentation

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.google.gson.reflect.TypeToken
import com.lib.loopsdk.R
import com.lib.loopsdk.core.util.*
import com.lib.loopsdk.core.util.Constants.init
import com.lib.loopsdk.data.remote.dto.GetHomeScreenDetailsDto
import com.lib.loopsdk.data.remote.dto.InitializeDto
import com.lib.loopsdk.databinding.ListItemNonFeaturedCouponsBinding

class NonFeaturedCouponsListAdapter(private val interaction: Interaction? = null) :
    ListAdapter<GetHomeScreenDetailsDto.Data.Coupon, NonFeaturedCouponsListAdapter.ViewHolder>(
        CouponDC()
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: ListItemNonFeaturedCouponsBinding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.list_item_non_featured_coupons,
            parent,
            false)
        binding.brandTheme = Prefs.getNonPrimitiveData<InitializeDto.Data.BrandTheme>(object: TypeToken<InitializeDto.Data.BrandTheme>(){}.type, "BRAND_THEME")
        binding.brandThemeColors = Colors
        return ViewHolder(binding, interaction)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position),position)

    fun swapData(data: List<GetHomeScreenDetailsDto.Data.Coupon>) {
        submitList(data.toMutableList())
    }

    inner class ViewHolder(
        val binding: ListItemNonFeaturedCouponsBinding,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(binding.root){

        fun bind(item: GetHomeScreenDetailsDto.Data.Coupon, position: Int){
            val themeData = Prefs.getNonPrimitiveData<InitializeDto.Data>(object: TypeToken<InitializeDto.Data>(){}.type, "INIT_DATA")

            binding.tvPoints.text=item.pointsToUnlock.toString()

            when(item.couponClassification){
                1,2->{
                    binding.tvClassificationTitle.text=themeData.defaultCurrency.symbol+ Utils.digitCountUpdate(
                        item.maxDiscountValue
                    )
                    binding.tvClassificationDesc.text="Voucher"
                    binding.tvClassificationTitle.showView()
                    binding.tvClassificationDesc.showView()
                    binding.ivClassification.hideView()
                }
                3,4->{
                    binding.tvClassificationTitle.text=item.discountPercent.toString()+"%"
                    binding.tvClassificationDesc.text="Discount"
                    binding.tvClassificationTitle.showView()
                    binding.tvClassificationDesc.showView()
                    binding.ivClassification.hideView()
                }
                5->{
                    //free shipping
                    binding.tvClassificationDesc.text="Free Delivery"
                    binding.ivClassification.setImageDrawable(ContextCompat.getDrawable(binding.root.context,
                        R.drawable.ic_vehicle_truck))
                    binding.ivClassification.showView()
                    binding.tvClassificationDesc.showView()
                    binding.tvClassificationTitle.hideView()
                }
                6->{
                    //Other
                    binding.ivClassification.setImageDrawable(ContextCompat.getDrawable(binding.root.context,
                        R.drawable.ic_gift))
                    binding.ivClassification.showView()
                    binding.tvClassificationDesc.hideView()
                    binding.tvClassificationTitle.hideView()
                }
            }

            binding.tvCouponDesc.text=item.displayName

            if(item.isExpiringSoon == 1){
                binding.ivExpired.showView()
            }else{
                binding.ivExpired.hideView()
            }
            binding.root.setOnClickListener {

                interaction?.onNonFeaturedCouponClicked(adapterPosition, item)
            }
        }
    }

    interface Interaction {
        fun onNonFeaturedCouponClicked(position: Int, coupon: GetHomeScreenDetailsDto.Data.Coupon)
    }

    private class CouponDC : DiffUtil.ItemCallback<GetHomeScreenDetailsDto.Data.Coupon>()
    {
        override fun areItemsTheSame(
            oldItem: GetHomeScreenDetailsDto.Data.Coupon,
            newItem: GetHomeScreenDetailsDto.Data.Coupon
        ): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(
            oldItem: GetHomeScreenDetailsDto.Data.Coupon,
            newItem: GetHomeScreenDetailsDto.Data.Coupon
        ): Boolean {
            return oldItem.id == newItem.id
        }
    }
}