package com.lib.loopsdk.ui.feature_tier_details_points_wallet.presentation

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout.LayoutParams
import androidx.core.view.marginTop
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.google.gson.reflect.TypeToken
import com.lib.loopsdk.R
import com.lib.loopsdk.core.util.*
import com.lib.loopsdk.core.util.Constants.init
import com.lib.loopsdk.data.remote.dto.InitializeDto
import com.lib.loopsdk.data.remote.dto.TierDetailsDto
import com.lib.loopsdk.databinding.ListItemTierDetailBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class TierHistoryAdapter(
    private val list: ArrayList<TierDetailsDto.Data.TierData> = arrayListOf(),
    private val interaction: Interaction? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val TAG = this::class.java.simpleName

    private var currentTierOrder = -1
    private var mTotalPointsEarned: Float = 0F

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: ListItemTierDetailBinding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.list_item_tier_detail,
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

    fun setTotalPointsEarned(totalEarnedPoints: String){
        mTotalPointsEarned = totalEarnedPoints.toFloat()
    }

    fun setCurrentTierOrder(currentTier: Int){
        currentTierOrder = currentTier
    }

    fun addData(data: List<TierDetailsDto.Data.TierData>) {
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
        currentTierOrder = -1
        notifyDataSetChanged()
    }

    inner class ViewHolder(
        val binding: ListItemTierDetailBinding,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(binding.root){

        fun bind(item: TierDetailsDto.Data.TierData) {
            val context = binding.root.context
            binding.brandTheme = Prefs.getNonPrimitiveData<InitializeDto.Data.BrandTheme>(object: TypeToken<InitializeDto.Data.BrandTheme>(){}.type, "BRAND_THEME")
            binding.brandThemeColors = Colors
            binding.executePendingBindings()

            binding.tvTierName.text = item.tierName
            binding.tvTierNameExpanded.text = item.tierName

            binding.viewDisableTierIcon.hideView()

            if(item.isExpanded) {
                binding.llExpanded.showView()
            } else {
                binding.llExpanded.hideView()
            }

            if(item.tierScratchCards <= 0) {
                binding.llTierPerkScratchCard.hideView()
            }else {
                binding.llTierPerkScratchCard.showView()
                if(item.tierScratchCards == 1) binding.tvScratchCardText.text = "${item.tierScratchCards}\nscratch card"
                else binding.tvScratchCardText.text = "${item.tierScratchCards}\nscratch cards"
            }

            if(item.tierSpinWheel <= 0) {
                binding.llTierPerkSpinWheel.hideView()
            }else {
                binding.llTierPerkSpinWheel.showView()
                if(item.tierSpinWheel == 1) binding.tvSpinWheelText.text = "${item.tierSpinWheel}\nwheel of fortune"
                else binding.tvSpinWheelText.text = "${item.tierSpinWheel}\nwheels of fortune"
            }

            if(item.tierCoupons <= 0) {
                binding.llTierPerkCoupon.hideView()
            }else {
                binding.llTierPerkCoupon.showView()
                if(item.tierCoupons == 1) binding.tvCouponText.text = "${item.tierCoupons}\ncoupon"
                else binding.tvCouponText.text = "${item.tierCoupons}\ncoupons"
            }

            if(!item.badgeImage.isNullOrEmpty()){
                Glide.with(binding.root.context)
                    .load(item.badgeImage)
                    .into(binding.ivTierIcon)
                Glide.with(binding.root.context)
                    .load(item.badgeImage)
                    .into(binding.ivTierIconExpanded)
            }

            if(item.isCurrentTier){
                val mLayoutParams = LayoutParams(0, LayoutParams.WRAP_CONTENT)
                mLayoutParams.weight = Utils.dpToPx(context, 1).toFloat()
                mLayoutParams.marginStart = Utils.dpToPx(context, 8).toFloat().toInt()
                mLayoutParams.topMargin = Utils.dpToPx(context, 8).toFloat().toInt()
                mLayoutParams.bottomMargin = Utils.dpToPx(context, 8).toFloat().toInt()
                binding.llContent.layoutParams = mLayoutParams
                binding.llContentWithProgressbar.layoutParams = mLayoutParams
                binding.progressTierExpanded.hideView()
                binding.viewDisableTierIconExpanded.hideView()
                binding.viewDisableTierPerksExpanded.hideView()
                if(!item.tierUnlockDate.isNullOrEmpty()){
                    binding.tvUnlockedOnDate.text = "Unlocked on ${item.tierUnlockDate}"
                    binding.tvUnlockedOnDateExpanded.text = "Unlocked on ${item.tierUnlockDate}"
                }
            }else{
                if (item.tierOrder < currentTierOrder) {
                    val mLayoutParams = LayoutParams(0, LayoutParams.WRAP_CONTENT)
                    mLayoutParams.weight = Utils.dpToPx(context, 1).toFloat()
                    mLayoutParams.marginStart = Utils.dpToPx(context, 8).toFloat().toInt()
                    mLayoutParams.topMargin = Utils.dpToPx(context, 8).toFloat().toInt()
                    mLayoutParams.bottomMargin = Utils.dpToPx(context, 8).toFloat().toInt()
                    binding.llContent.layoutParams = mLayoutParams
                    binding.llContentWithProgressbar.layoutParams = mLayoutParams
                    binding.progressTierExpanded.hideView()
                    binding.viewDisableTierIcon.hideView()
                    binding.viewDisableTierIconExpanded.hideView()
                    binding.viewDisableTierPerksExpanded.hideView()
                    if(!item.tierUnlockDate.isNullOrEmpty()){
                        binding.tvUnlockedOnDate.text = "Unlocked on ${item.tierUnlockDate}"
                        binding.tvUnlockedOnDateExpanded.text = "Unlocked on ${item.tierUnlockDate}"
                    }
                } else if(item.tierOrder > currentTierOrder){
                    if(item.tierOrder == currentTierOrder + 1) {
                        val mLayoutParams = LayoutParams(0, LayoutParams.WRAP_CONTENT)
                        mLayoutParams.weight = Utils.dpToPx(context, 1).toFloat()
                        mLayoutParams.marginStart = Utils.dpToPx(context, 8).toFloat().toInt()
                        mLayoutParams.topMargin = Utils.dpToPx(context, 2).toFloat().toInt()
                        mLayoutParams.bottomMargin = Utils.dpToPx(context, 2).toFloat().toInt()
                        binding.llContent.layoutParams = mLayoutParams
                        binding.llContentWithProgressbar.layoutParams = mLayoutParams
                        binding.flExpandBtn.showView()
                        binding.viewDisableTierIcon.showView()
                        binding.viewDisableTierIconExpanded.showView()
                        binding.viewDisableTierPerksExpanded.showView()
                        binding.progressTierExpanded.progress = ((mTotalPointsEarned/item.cutOnValue.toInt().toFloat()) * 100).toInt()
                        binding.progressTierExpanded.showView()
                        if(item.remaingPoints > 1){
                            binding.tvUnlockedOnDate.text = "${getFormattedPoints(item.remaingPoints.toString())} ${init.pointsIdentifier.pointsLabelPlural} to go"
                            binding.tvUnlockedOnDateExpanded.text = "${getFormattedPoints(item.remaingPoints.toString())} ${init.pointsIdentifier.pointsLabelPlural} to go"
                        }else{
                            binding.tvUnlockedOnDate.text = "${getFormattedPoints(item.remaingPoints.toString())} ${init.pointsIdentifier.pointsLabelSingular} to go"
                            binding.tvUnlockedOnDateExpanded.text = "${getFormattedPoints(item.remaingPoints.toString())} ${init.pointsIdentifier.pointsLabelSingular} to go"
                        }

                    } else {
                        val mLayoutParams = LayoutParams(0, LayoutParams.WRAP_CONTENT)
                        mLayoutParams.weight = Utils.dpToPx(context, 1).toFloat()
                        mLayoutParams.marginStart = Utils.dpToPx(context, 8).toFloat().toInt()
                        mLayoutParams.topMargin = Utils.dpToPx(context, 8).toFloat().toInt()
                        mLayoutParams.bottomMargin = Utils.dpToPx(context, 8).toFloat().toInt()
                        binding.llContent.layoutParams = mLayoutParams
                        binding.llContentWithProgressbar.layoutParams = mLayoutParams
                        binding.flExpandBtn.hideView()
                        binding.progressTierExpanded.hideView()
                        binding.viewDisableTierIcon.hideView()
                        binding.viewDisableTierIconExpanded.hideView()
                        binding.viewDisableTierPerksExpanded.hideView()
                        binding.tvUnlockedOnDate.text = "Unlocked at ${getFormattedPoints(item.remaingPoints.toString())} " +
                                if (item.remaingPoints > 1) init.pointsIdentifier.pointsLabelPlural else init.pointsIdentifier.pointsLabelSingular
                    }
                }
            }


            binding.flExpandBtn.setOnClickListener {
                list.forEachIndexed { index, tierData ->
                    list[index].isExpanded = index == adapterPosition
                }
                notifyDataSetChanged()
            }

            binding.flCollapseBtn.setOnClickListener {
                list[adapterPosition].isExpanded = false
                notifyItemChanged(adapterPosition)
            }

        }

        private fun getDateFormat(inputDate: String):String {
            val dateFormat = SimpleDateFormat("dd-MMMM-yyyy", Locale.ENGLISH)
            val outFormat = SimpleDateFormat("dd MMMM, yyyy ", Locale.ENGLISH)
            var date: Date? = null
            try {
                date = dateFormat.parse(inputDate)

            } catch (e: Exception) {
                e.printStackTrace()
            }
            if (date != null) {
                val myDate: String = outFormat.format(date)
                return  myDate
            }
            return ""
        }
    }

    interface Interaction {

    }
}