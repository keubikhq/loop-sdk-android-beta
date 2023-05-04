package com.lib.loopsdk.ui.feature_landing_home.presentation

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
import com.lib.loopsdk.data.remote.dto.InitializeDto
import com.lib.loopsdk.data.remote.dto.TierDetailsDto
import com.lib.loopsdk.databinding.ListItemGameArenaHomeBinding
import com.lib.loopsdk.databinding.ListItemTierDetailBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class GamesListAdapter(
    private val list: ArrayList<Game> = arrayListOf(),
    private val interaction: Interaction? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val TAG = this::class.java.simpleName

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: ListItemGameArenaHomeBinding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.list_item_game_arena_home,
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

    fun addData(data: List<Game>) {
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
        val binding: ListItemGameArenaHomeBinding,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(binding.root){

        fun bind(item: Game) {

            val context = binding.root.context

            binding.tvGameName.text = item.gameName

            if (item.gameName.contains("quiz", true)) {
                binding.ivPointsIcon.hideView()
                if(item.isEnabled == 0){
                    binding.valuePill.hideView()
                    binding.tvStartsAt.text = "game unavailable"
                }else{
                    if(item.resume == 1) {
                        binding.valuePill.hideView()
                        binding.tvStartsAt.text = "resume your\nquizlets"
                        binding.tvBtnExploreText.text = "View"
                    } else {
                        binding.tvStartsAt.text = "unlock for"
                        binding.valuePill.showView()
                        binding.tvGameStartsAtValue.text = "FREE"
                        binding.tvBtnExploreText.text = "Explore"
                    }
                }
            }else {
                if (item.pointsStartAt == 0) {
                    binding.ivPointsIcon.hideView()
                    if(item.isEnabled == 0 && item.isUnlockGameEnabled == 1){
                        binding.valuePill.hideView()
                        binding.tvStartsAt.text = "check your\nunlocked games"
                        binding.tvBtnExploreText.text = "View"
                    }else {
                        binding.valuePill.showView()
                        binding.tvStartsAt.text = "unlock for"
                        binding.tvGameStartsAtValue.text = "FREE"
                        binding.tvBtnExploreText.text = "Explore"
                    }
                }else{
                    binding.ivPointsIcon.showView()
                    if(item.isEnabled == 0 && item.isUnlockGameEnabled == 1){
                        binding.valuePill.hideView()
                        binding.tvStartsAt.text = "check your\nunlocked games"
                        binding.tvBtnExploreText.text = "View"
                    }else {
                        binding.valuePill.showView()
                        binding.tvBtnExploreText.text = "Explore"
                        binding.tvStartsAt.text = "starts at"
                        binding.tvGameStartsAtValue.text = item.pointsStartAt.toString()
                    }
                }
            }

            when {
                item.gameName.contains("wheel", true) -> {
                    binding.ivGameImage.setImageResource(R.drawable.spin_wheel_circular)
                }
                item.gameName.contains("scratch", true) -> {
                    binding.ivGameImage.setImageResource(R.drawable.scratch_card_circular)
                }
                item.gameName.contains("quiz", true) -> {
                    binding.ivGameImage.setImageResource(R.drawable.ic_surveylist)
                }
            }

            binding.btnExplore.setOnClickListener {
                interaction?.onExploreGameClicked(adapterPosition, item)
            }

        }

    }

    interface Interaction {
        fun onExploreGameClicked(position: Int, game: Game)
    }
}