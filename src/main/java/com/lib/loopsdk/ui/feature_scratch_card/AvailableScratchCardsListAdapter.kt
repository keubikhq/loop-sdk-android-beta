package com.lib.loopsdk.ui.feature_scratch_card

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.gson.reflect.TypeToken
import com.lib.loopsdk.R
import com.lib.loopsdk.core.util.*
import com.lib.loopsdk.core.util.Constants.init
import com.lib.loopsdk.data.remote.dto.AllAvailableScratchCardsDto
import com.lib.loopsdk.data.remote.dto.InitializeDto
import com.lib.loopsdk.databinding.ListItemScratchCardListingBinding
import kotlin.collections.ArrayList

class AvailableScratchCardsListAdapter(
    private val list: ArrayList<AllAvailableScratchCardsDto.Data.ScratchCard> = arrayListOf(),
    private val interaction: Interaction? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val TAG = this::class.java.simpleName

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: ListItemScratchCardListingBinding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.list_item_scratch_card_listing,
            parent,
            false)
        binding.brandTheme = Prefs.getNonPrimitiveData<InitializeDto.Data.BrandTheme>(object: TypeToken<InitializeDto.Data.BrandTheme>(){}.type, "BRAND_THEME")
        binding.brandThemeColors = Colors
        binding.executePendingBindings()
        return ViewHolder(binding, interaction)
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun addData(data: List<AllAvailableScratchCardsDto.Data.ScratchCard>) {
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
        val binding: ListItemScratchCardListingBinding,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(binding.root){

        fun bind(item: AllAvailableScratchCardsDto.Data.ScratchCard) {

            val context = binding.root.context

            binding.tvScratchCardName.text = item.name

            if(!item.benefitBasicDetails.points.isNullOrEmpty() && item.benefitBasicDetails.coupons == 1){
                binding.tvPrizeText.text = "win upto\n${item.benefitBasicDetails.points} ${init.pointsIdentifier.pointsLabelPlural} & coupons"
            }else if (!item.benefitBasicDetails.points.isNullOrEmpty() && item.benefitBasicDetails.coupons == 0) {
                binding.tvPrizeText.text = "win upto\n${item.benefitBasicDetails.points} ${init.pointsIdentifier.pointsLabelPlural}"
            }else if (item.benefitBasicDetails.points.isNullOrEmpty() && item.benefitBasicDetails.coupons == 1) {
                binding.tvPrizeText.text = "win\ncoupons"
            }else if (item.benefitBasicDetails.points.isNullOrEmpty() && item.benefitBasicDetails.coupons == 0) {
                binding.tvPrizeText.text = "receive a\nsurprise message"
            }

            if(item.pointsToUnlock <= 0){
                binding.flPointsImageContainer.hideView()
                binding.tvUnlockPlayBtnText.text = "Unlock"
            }else {
                binding.flPointsImageContainer.showView()
                binding.tvUnlockPlayBtnText.text = "${item.pointsToUnlock}"
            }

            binding.btnUnlockPlay.setOnClickListener {
                interaction?.onUnlockButtonClicked(adapterPosition, item)
            }

        }

    }

    interface Interaction {
        fun onUnlockButtonClicked(position: Int, scratchCard: AllAvailableScratchCardsDto.Data.ScratchCard)
    }
}