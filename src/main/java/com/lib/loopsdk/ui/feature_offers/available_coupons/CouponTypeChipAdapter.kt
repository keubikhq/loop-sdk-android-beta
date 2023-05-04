package com.lib.loopsdk.ui.feature_offers.available_coupons

import android.graphics.Color
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.google.gson.reflect.TypeToken
import com.lib.loopsdk.R
import com.lib.loopsdk.core.util.Colors
import com.lib.loopsdk.core.util.Prefs
import com.lib.loopsdk.core.util.hideView
import com.lib.loopsdk.core.util.showView
import com.lib.loopsdk.data.remote.dto.CouponTypeData
import com.lib.loopsdk.data.remote.dto.InitializeDto
import com.lib.loopsdk.databinding.ListItemCouponChipFilterBinding

class CouponTypeChipAdapter(private val interaction: Interaction? = null) :
    ListAdapter<CouponTypeData, CouponTypeChipAdapter.ViewHolder>(
        CouponDC()
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: ListItemCouponChipFilterBinding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.list_item_coupon_chip_filter,
            parent,
            false)
        binding.brandTheme = Prefs.getNonPrimitiveData<InitializeDto.Data.BrandTheme>(object: TypeToken<InitializeDto.Data.BrandTheme>(){}.type, "BRAND_THEME")
        binding.brandThemeColors = Colors
        return ViewHolder(binding, interaction)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getItem(position),position)

    fun swapData(data: List<CouponTypeData>) {
        submitList(data.toMutableList())
    }

    inner class ViewHolder(
        val binding: ListItemCouponChipFilterBinding,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(binding.root){


        fun bind(item: CouponTypeData, position: Int){
            val themeData = Prefs.getNonPrimitiveData<InitializeDto.Data>(object: TypeToken<InitializeDto.Data>(){}.type, "INIT_DATA")

            val primaryColor: Int= Color.parseColor(binding.brandThemeColors!!.PRIMARY_BRAND_COLOR)

            if(item.CouponTypeTitle.contains("-")){
//                val img: Drawable = binding.root.getResources().getDrawable(R.drawable.ic_point_on_chip)
//                img.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(primaryColor,
//                    BlendModeCompat.SRC_IN)
                binding.ivPointIcon.showView()
                binding.tvFilterChip.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
                binding.tvFilterChip.text=item.CouponTypeTitle
            }else{
                binding.ivPointIcon.hideView()
                binding.tvFilterChip.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
                binding.tvFilterChip.text=item.CouponTypeTitle
            }

            binding.root.setOnClickListener {

              //  interaction?.onCouponClicked(adapterPosition, item)
            }
        }
    }

    interface Interaction {
        fun onCouponClicked(position: Int, item:CouponTypeData)
    }

    private class CouponDC : DiffUtil.ItemCallback<CouponTypeData>()
    {
        override fun areItemsTheSame(
            oldItem: CouponTypeData,
            newItem: CouponTypeData
        ): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(
            oldItem: CouponTypeData,
            newItem: CouponTypeData
        ): Boolean {
            return oldItem.CouponTypeId == newItem.CouponTypeId
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}