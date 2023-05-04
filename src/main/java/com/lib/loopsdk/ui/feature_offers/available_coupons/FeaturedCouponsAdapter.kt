package com.lib.loopsdk.ui.feature_offers.available_coupons

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.Glide
import com.google.gson.reflect.TypeToken
import com.lib.loopsdk.R
import com.lib.loopsdk.core.util.Colors
import com.lib.loopsdk.core.util.Prefs
import com.lib.loopsdk.core.util.Utils
import com.lib.loopsdk.core.util.hideView
import com.lib.loopsdk.core.util.showView
import com.lib.loopsdk.data.remote.dto.AvailableCouponDto
import com.lib.loopsdk.data.remote.dto.InitializeDto
import com.lib.loopsdk.databinding.ListItemFeaturedCouponsBinding

class FeaturedCouponsAdapter(private val interaction: Interaction? = null) :
    ListAdapter<AvailableCouponDto.Data.CouponsData, FeaturedCouponsAdapter.ViewHolder>(
        CouponDC()
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: ListItemFeaturedCouponsBinding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.list_item_featured_coupons,
            parent,
            false)
        binding.brandTheme = Prefs.getNonPrimitiveData<InitializeDto.Data.BrandTheme>(object: TypeToken<InitializeDto.Data.BrandTheme>(){}.type, "BRAND_THEME")
        binding.brandThemeColors = Colors
        return ViewHolder(binding, interaction)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getItem(position),position)

    fun swapData(data: List<AvailableCouponDto.Data.CouponsData>) {
        submitList(data.toMutableList())
    }
    fun clearAllData(){
        currentList.clear()
        notifyDataSetChanged()
    }

    inner class ViewHolder(
        val binding: ListItemFeaturedCouponsBinding,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(binding.root){


        fun bind(item: AvailableCouponDto.Data.CouponsData, position: Int){
            val themeData = Prefs.getNonPrimitiveData<InitializeDto.Data>(object: TypeToken<InitializeDto.Data>(){}.type, "INIT_DATA")

            if(item.pointsToUnlock!=0) {
                binding.tvPoints.text=item.pointsToUnlock.toString()
            }else{
                binding.ivPointIcon.hideView()
                binding.tvPoints.text ="Free"
            }

            if(position==0){
                binding.root.setPadding(Utils.dpToPx(binding.root.context, 20), 0, Utils.dpToPx(binding.root.context, 16), 0)
            }else if(position==currentList.size-1){
                binding.root.setPadding(0,0,Utils.dpToPx(binding.root.context, 20),0)

            }else{
                binding.root.setPadding(0,0, Utils.dpToPx(binding.root.context, 16),0)
            }
            if(!item.featuredImage.isNullOrEmpty()){
                Glide.with(binding.root.context)
                    .load(item.featuredImage)
                    .into(binding.ivCouponPic)
            }

            when(item.couponClassification){
                1,2->{
                    binding.tvCouponTag.text=themeData.defaultCurrency.symbol+ Utils.digitCountUpdate(item.maxDiscountValue)
                    binding.tvCouponTag.showView()
                    binding.ivCouponTag.hideView()
                }
                3,4->{
                    binding.tvCouponTag.text=item.discountPercent.toString()+"%"
                    binding.tvCouponTag.showView()
                    binding.ivCouponTag.hideView()
                }
                5->{
                    //free shipping
                    binding.ivCouponTag.setImageDrawable(
                        ContextCompat.getDrawable(binding.root.context,
                        R.drawable.ic_vehicle_truck))
                    binding.ivCouponTag.showView()
                    binding.tvCouponTag.hideView()
                }
                6->{
                    //Other
                    binding.ivCouponTag.setImageDrawable(
                        ContextCompat.getDrawable(binding.root.context,
                        R.drawable.ic_gift))
                    binding.ivCouponTag.showView()
                    binding.tvCouponTag.hideView()
                }
            }
            if(item.isExpiringSoon == 1){
                binding.cvExpired.showView()
            }else{
                binding.cvExpired.hideView()
            }

            binding.tvCouponDesc.text=item.displayName

            binding.root.setOnClickListener {

                interaction?.onCouponClicked(adapterPosition, item)
            }


        }
    }

    interface Interaction {
        fun onCouponClicked(position: Int, item:AvailableCouponDto.Data.CouponsData)
    }

    private class CouponDC : DiffUtil.ItemCallback<AvailableCouponDto.Data.CouponsData>()
    {
        override fun areItemsTheSame(
            oldItem: AvailableCouponDto.Data.CouponsData,
            newItem: AvailableCouponDto.Data.CouponsData
        ): Boolean {

            return oldItem === newItem
        }

        override fun areContentsTheSame(
            oldItem: AvailableCouponDto.Data.CouponsData,
            newItem: AvailableCouponDto.Data.CouponsData
        ): Boolean {
            return oldItem.id == newItem.id
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}