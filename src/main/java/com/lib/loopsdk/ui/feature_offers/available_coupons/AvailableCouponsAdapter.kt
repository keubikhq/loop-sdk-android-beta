package com.lib.loopsdk.ui.feature_offers.available_coupons

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.google.gson.reflect.TypeToken
import com.lib.loopsdk.R
import com.lib.loopsdk.core.util.Colors
import com.lib.loopsdk.core.util.Prefs
import com.lib.loopsdk.core.util.Utils
import com.lib.loopsdk.core.util.hideView
import com.lib.loopsdk.core.util.showView
import com.lib.loopsdk.data.remote.dto.AvailableCouponDto
import com.lib.loopsdk.data.remote.dto.InitializeDto
import com.lib.loopsdk.databinding.ListItemAvailableCouponsBinding

class AvailableCouponsAdapter(private val interaction: Interaction? = null) :
    ListAdapter<AvailableCouponDto.Data.CouponsData, AvailableCouponsAdapter.ViewHolder>(
        CouponDC()
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: ListItemAvailableCouponsBinding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.list_item_available_coupons,
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

    inner class ViewHolder(
        val binding: ListItemAvailableCouponsBinding,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(binding.root){


        fun bind(item: AvailableCouponDto.Data.CouponsData, position: Int){
            val themeData = Prefs.getNonPrimitiveData<InitializeDto.Data>(object: TypeToken<InitializeDto.Data>(){}.type, "INIT_DATA")

            if(item.remaningPoints!=0){
                binding.tvUnlockFor.text = "Earn"
                binding.tvPoints.text = item.remaningPoints.toString()+" more to unlock"
            }else if(item.pointsToUnlock!=0) {
                binding.tvUnlockFor.text = "Unlock for"
                binding.tvPoints.text=item.pointsToUnlock.toString()
            }else{
                binding.ivPointIcon.hideView()
                binding.tvPoints.text ="Free"
            }


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