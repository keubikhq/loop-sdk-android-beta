package com.lib.loopsdk.ui.feature_offers.available_coupons

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.reflect.TypeToken
import com.lib.loopsdk.R
import com.lib.loopsdk.core.util.Colors
import com.lib.loopsdk.core.util.Prefs
import com.lib.loopsdk.core.util.hideView
import com.lib.loopsdk.core.util.showView
import com.lib.loopsdk.data.remote.dto.CouponTypeData
import com.lib.loopsdk.data.remote.dto.InitializeDto
import com.lib.loopsdk.databinding.ListItemCouponChipFilterBinding
import com.lib.loopsdk.databinding.ListItemCouponTileFilterBinding

class CouponTypeListAdapter(private val interaction: Interaction? = null) :
    ListAdapter<CouponTypeData, RecyclerView.ViewHolder?>( CategoryDC()) {

    private var filterChipsCategoriesList = arrayListOf<CouponTypeData>()
    private var filterTilesCategoriesList = arrayListOf<CouponTypeData>()
    private var viewType: Int = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        when(viewType) {
            1 -> {
                val binding: ListItemCouponChipFilterBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.list_item_coupon_chip_filter,
                    parent,
                    false
                )
                return FilterChipViewHolder(binding, interaction)
            }
            2 ->{
                val binding
                : ListItemCouponTileFilterBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.list_item_coupon_tile_filter,
                parent,
                false
            )
            return FilterTileViewHolder(binding, interaction)

            }
            else -> {
                val binding: ListItemCouponChipFilterBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.list_item_coupon_chip_filter,
                    parent,
                    false
                )

                return FilterChipViewHolder(binding, interaction)
            }

        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (this.viewType) {
            1 -> 1
            2 -> 2
            else -> 1
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(this.viewType){
            1 -> {
                (holder as FilterChipViewHolder).bind(getItem(position))
            }
            2 -> {
                (holder as FilterTileViewHolder).bind(getItem(position))
            }
            else -> {
                (holder as FilterChipViewHolder).bind(getItem(position))
            }
        }

    }

    fun toggleCategoryTileSelection(position: Int, isSelected: Boolean){
        filterTilesCategoriesList[position].isSelected = isSelected
        notifyItemChanged(position)
    }

    fun toggleCategoryChipsSelection(selectedCategoriesIdList: List<String>){
        if(selectedCategoriesIdList.isEmpty()){
            filterChipsCategoriesList.forEach {
                it.isSelected = false
            }
        }else{
            selectedCategoriesIdList.forEach {
                filterChipsCategoriesList.forEachIndexed { index, mainCategory ->
                    if(mainCategory.CouponTypeId.equals(it)) {
                        filterChipsCategoriesList[index].isSelected = true
                    }
                }
            }
        }
        notifyDataSetChanged()
    }

    fun clearAllChipsSelection(){
        filterChipsCategoriesList.forEach {
            it.isSelected = false
        }

        notifyDataSetChanged()
    }

    fun clearAllTilesSelection(){
        filterTilesCategoriesList.forEach {
            it.isSelected = false
        }

        notifyDataSetChanged()
    }

    fun addFilterChips(data: List<CouponTypeData>) {
        filterChipsCategoriesList.addAll(data)
        submitList(data)
    }

    fun addFilterTiles(data: List<CouponTypeData>) {
        filterTilesCategoriesList.addAll(data)
        submitList(data)
    }

    fun getSelectedFilterChips() = filterChipsCategoriesList

    fun setViewType(viewType: Int){
        this.viewType = viewType
    }


    private fun customizeButton(
        binding: ListItemCouponChipFilterBinding,
        isSelected: Boolean
    ) {
        if (isSelected) {
//            binding.crdvFilterChip.isOutline = false
//            binding.crdvFilterChip.setColor()
//            binding.tvCategoryName.setTextColor(ContextCompat.getColor(binding.root.context, R.color.white))
            /*binding.ivCategoryImage.hideView()
            binding.tvCategoryName.setPadding(0,0, 0, 0)*/
            /*ImageViewCompat.setImageTintList(
                binding.ivCategoryImage,
                ColorStateList.valueOf(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.white))
            )*/
        } else {
//            binding.crdvFilterChip.isOutline = true
//            binding.crdvFilterChip.setColor()
//            binding.tvCategoryName.setTextColor(ContextCompat.getColor(binding.root.context, R.color.primary_color))
            /*binding.ivCategoryImage.showView()
            binding.tvCategoryName.setPadding(Utils.dpToPx(binding.root.context, 8),0, 0, 0)*/
            //tvName?.setColor(false)
            /*ImageViewCompat.setImageTintList(
                binding.ivCategoryImage,
                ColorStateList.valueOf(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.white))
            )*/
        }
    }


    private fun customizeTile(
        binding: ListItemCouponTileFilterBinding,
        isSelected: Boolean
    ) {
        if (isSelected) {
            binding.ivTypeUnCheck.hideView()
            binding.ivTypeCheck.showView()
        } else {
            binding.ivTypeUnCheck.showView()
            binding.ivTypeCheck.hideView()
        }
    }

//    fun getItemPositionByCategoryId(categoryId: Int): Int{
//        return currentList.indexOfFirst { it.id == categoryId }
//    }

    inner class FilterChipViewHolder(
        val binding: ListItemCouponChipFilterBinding,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CouponTypeData){
            binding.brandTheme = Prefs.getNonPrimitiveData<InitializeDto.Data.BrandTheme>(object: TypeToken<InitializeDto.Data.BrandTheme>(){}.type, "BRAND_THEME")
            binding.brandThemeColors = Colors
            binding.tvFilterChip.text = item.CouponTypeTitle
            //customizeButton(binding, item.isSelected)
            binding.root.setOnClickListener {
                filterChipsCategoriesList[adapterPosition].isSelected = !item.isSelected
                notifyItemChanged(adapterPosition)
                if(filterChipsCategoriesList.none { it.isSelected }){
                    interaction?.onClearAllFilterChips()
                }else{
                    interaction?.onCategoryFilterChipClicked(item, adapterPosition)
                }
            }
        }
    }

    inner class FilterTileViewHolder(
        val binding: ListItemCouponTileFilterBinding,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CouponTypeData){
            binding.brandTheme = Prefs.getNonPrimitiveData<InitializeDto.Data.BrandTheme>(object: TypeToken<InitializeDto.Data.BrandTheme>(){}.type, "BRAND_THEME")
            binding.brandThemeColors = Colors
            binding.tvType.text = item.CouponTypeTitle
            customizeTile(binding, item.isSelected)
            binding.root.setOnClickListener {
                filterTilesCategoriesList[adapterPosition].isSelected = !item.isSelected
                notifyItemChanged(adapterPosition)
                interaction?.onCategoryFilterTileClicked(item, adapterPosition)
            }
        }
    }

    interface Interaction {
        fun onClearAllFilterChips()
        fun onCategoryFilterChipClicked(item: CouponTypeData, position: Int)
        fun onCategoryFilterTileClicked(item: CouponTypeData, position: Int)
    }

    private class CategoryDC : DiffUtil.ItemCallback<CouponTypeData>() {
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
}