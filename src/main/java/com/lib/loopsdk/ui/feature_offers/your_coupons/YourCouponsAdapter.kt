package com.lib.loopsdk.ui.feature_offers.your_coupons

import android.graphics.PorterDuff
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
import com.lib.loopsdk.core.util.Constants
import com.lib.loopsdk.core.util.Prefs
import com.lib.loopsdk.core.util.Utils.digitCountUpdate
import com.lib.loopsdk.core.util.hideView
import com.lib.loopsdk.core.util.showView
import com.lib.loopsdk.data.remote.dto.CouponHistoryDto
import com.lib.loopsdk.data.remote.dto.InitializeDto
import com.lib.loopsdk.databinding.ListItemYourCouponsBinding


class YourCouponsAdapter(private val interaction: Interaction? = null) :
    ListAdapter<CouponHistoryDto.Data.CouponsData, YourCouponsAdapter.ViewHolder>(
        CouponDC()
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: ListItemYourCouponsBinding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.list_item_your_coupons,
            parent,
            false)
        binding.brandTheme = Prefs.getNonPrimitiveData<InitializeDto.Data.BrandTheme>(object: TypeToken<InitializeDto.Data.BrandTheme>(){}.type, "BRAND_THEME")
        binding.brandThemeColors = Colors
        return ViewHolder(binding, interaction)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getItem(position),position)

    fun swapData(data: List<CouponHistoryDto.Data.CouponsData>) {
        submitList(data.toMutableList())
    }

    inner class ViewHolder(
        val binding: ListItemYourCouponsBinding,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(binding.root){


        fun bind(item: CouponHistoryDto.Data.CouponsData, position: Int){
            val themeData = Prefs.getNonPrimitiveData<InitializeDto.Data>(object: TypeToken<InitializeDto.Data>(){}.type, "INIT_DATA")

                when(item.couponClassification){
                    1,2->{
                        binding.tvClassificationTitle.text=themeData.defaultCurrency.symbol+ digitCountUpdate(item.maxDiscountValue)
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
//            if(!item.isExpired.isNullOrBlank()&&item.isExpiringSoon == 1 && item.isExpired.toInt()!=1){
//                binding.ivExpired.showView()
//            }else{
//                binding.ivExpired.hideView()
//            }
            if(item.isExpiringSoon == 1 ){
                if(!item.isExpired.isNullOrBlank() && item.isExpired.toInt()==1)
                    binding.ivExpired.hideView()
                else
                    binding.ivExpired.showView()
            }else{
                binding.ivExpired.hideView()
            }

            binding.tvCouponDesc.text=item.displayName
            //is  Expired
            if(!item.isExpired.isNullOrBlank() &&item.isExpired.toInt()==1 && item.expiredOn.isNotEmpty())
            {
                //show expired state
                binding.ivCouponStatus.setImageDrawable(ContextCompat.getDrawable(binding.root.context,
                    R.drawable.ic_error_circle))
                binding.ivCouponStatus.setColorFilter(ContextCompat.getColor(binding.root.context,R.color.error_red), PorterDuff.Mode.SRC_IN)
                binding.tvCouponStatus.setTextColor(ContextCompat.getColor(binding.root.context,R.color.error_red))
                binding.tvCouponStatus.text="Expired"

            }else {
                when (item.couponStatus) {
                    1 -> {
                        // Unlocked
                        binding.ivCouponStatus.setColorFilter(
                            ContextCompat.getColor(
                                binding.root.context,
                                R.color.text_medium_gray
                            ), PorterDuff.Mode.SRC_IN
                        )
                        binding.tvCouponStatus.setTextColor(
                            ContextCompat.getColor(
                                binding.root.context,
                                R.color.text_medium_gray
                            )
                        )
                        if (item.unlockOrigin == Constants.CouponUnlockType.UNLOCKED.type) {
                            binding.ivCouponStatus.setImageDrawable(
                                ContextCompat.getDrawable(
                                    binding.root.context,
                                    R.drawable.ic_unlock
                                )
                            )
                            binding.tvCouponStatus.text = "unlocked on " + item.unlockedOn
                        } else {
                            //3: REWARDS
                            binding.ivCouponStatus.setImageDrawable(
                                ContextCompat.getDrawable(
                                    binding.root.context,
                                    R.drawable.ic_gift
                                )
                            )

                            when (item.unlockOrigin) {
                                Constants.CouponUnlockType.REFERRAL.type -> {
                                    binding.tvCouponStatus.text =
                                        Constants.CouponUnlockType.REFERRAL.title + " reward"
                                }

                                Constants.CouponUnlockType.GRATIFICATION.type -> {
                                    binding.tvCouponStatus.text =
                                        Constants.CouponUnlockType.GRATIFICATION.title + " reward"
                                }

                                Constants.CouponUnlockType.SCRATCH_CARD.type -> {
                                    binding.tvCouponStatus.text =
                                        Constants.CouponUnlockType.SCRATCH_CARD.title + " reward"
                                }

                                Constants.CouponUnlockType.WHEEL_OF_FORTUNE.type -> {
                                    binding.tvCouponStatus.text =
                                        Constants.CouponUnlockType.WHEEL_OF_FORTUNE.title + " reward"
                                }

                                Constants.CouponUnlockType.SURVEY.type -> {
                                    binding.tvCouponStatus.text =
                                        Constants.CouponUnlockType.SURVEY.title + " reward"
                                }

                                Constants.CouponUnlockType.GIFT_FROM_FRIEND.type -> {
                                    binding.tvCouponStatus.text =
                                        Constants.CouponUnlockType.GIFT_FROM_FRIEND.title
                                }

                                Constants.CouponUnlockType.QUIZ.type -> {
                                    binding.tvCouponStatus.text =
                                        Constants.CouponUnlockType.QUIZ.title + " reward"
                                }

                                else -> {}
                            }
                        }
                    }

                    2 -> {
                        // Used
                        binding.ivCouponStatus.setImageDrawable(
                            ContextCompat.getDrawable(
                                binding.root.context,
                                R.drawable.ic_checkmark_starburst
                            )
                        )
                        binding.ivCouponStatus.setColorFilter(
                            ContextCompat.getColor(
                                binding.root.context,
                                R.color.success_green
                            ), PorterDuff.Mode.SRC_IN
                        )
                        binding.tvCouponStatus.setTextColor(
                            ContextCompat.getColor(
                                binding.root.context,
                                R.color.success_green
                            )
                        )
                        binding.tvCouponStatus.text = "redeemed on " + item.usedOn
                    }

                    3, 4 -> {
                        //3: GIFTED
                        binding.ivCouponStatus.setImageDrawable(
                            ContextCompat.getDrawable(
                                binding.root.context,
                                R.drawable.ic_gift
                            )
                        )

                        binding.ivCouponStatus.setColorFilter(
                            ContextCompat.getColor(
                                binding.root.context,
                                R.color.success_green
                            ), PorterDuff.Mode.SRC_IN
                        )
                        binding.tvCouponStatus.setTextColor(
                            ContextCompat.getColor(
                                binding.root.context,
                                R.color.success_green
                            )
                        )
                        binding.tvCouponStatus.text = "gifted on " + item.giftedOn
                    }

                    5 -> {
                        // Code Viewed
                        binding.ivCouponStatus.setColorFilter(
                            ContextCompat.getColor(
                                binding.root.context,
                                R.color.info_blue
                            ), PorterDuff.Mode.SRC_IN
                        )
                        binding.tvCouponStatus.setTextColor(
                            ContextCompat.getColor(
                                binding.root.context,
                                R.color.info_blue
                            )
                        )
                        binding.ivCouponStatus.setImageDrawable(
                            ContextCompat.getDrawable(
                                binding.root.context,
                                R.drawable.ic_eye
                            )
                        )
                        binding.tvCouponStatus.text = "code viewed " + item.unlockedOn
                    }
                }
            }
            binding.root.setOnClickListener {

                interaction?.onCouponClicked(adapterPosition, item)
            }
        }
    }

    interface Interaction {
        fun onCouponClicked(position: Int, item:CouponHistoryDto.Data.CouponsData)
    }

    private class CouponDC : DiffUtil.ItemCallback<CouponHistoryDto.Data.CouponsData>()
    {
        override fun areItemsTheSame(
            oldItem: CouponHistoryDto.Data.CouponsData,
            newItem: CouponHistoryDto.Data.CouponsData
        ): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(
            oldItem: CouponHistoryDto.Data.CouponsData,
            newItem: CouponHistoryDto.Data.CouponsData
        ): Boolean {
            return oldItem.transactionId == newItem.transactionId
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

}