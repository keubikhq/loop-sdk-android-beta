package com.lib.loopsdk.ui.feature_tier_details_points_wallet.presentation

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.gson.reflect.TypeToken
import com.lib.loopsdk.R
import com.lib.loopsdk.core.util.Colors
import com.lib.loopsdk.core.util.Constants
import com.lib.loopsdk.core.util.Prefs
import com.lib.loopsdk.core.util.getFormattedPoints
import com.lib.loopsdk.data.remote.dto.InitializeDto
import com.lib.loopsdk.data.remote.dto.PointsTransactionDto
import com.lib.loopsdk.databinding.ListItemPointsWalletBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class WalletHistoryAdapter(
    private val list: ArrayList<PointsTransactionDto.Data.PointTransaction> = arrayListOf(),
    private val interaction: Interaction? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val TAG = this::class.java.simpleName

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: ListItemPointsWalletBinding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.list_item_points_wallet,
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


    fun addData(data: List<PointsTransactionDto.Data.PointTransaction>) {
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
        val binding: ListItemPointsWalletBinding,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(binding.root){

        fun bind(item: PointsTransactionDto.Data.PointTransaction) {
            val context = binding.root.context
            binding.brandTheme = Prefs.getNonPrimitiveData<InitializeDto.Data.BrandTheme>(object: TypeToken<InitializeDto.Data.BrandTheme>(){}.type, "BRAND_THEME")
            binding.brandThemeColors = Colors
            binding.executePendingBindings()

            binding.tvDate.text = item.createdAt

            if (item.type == Constants.WalletRewardType.DEBIT.type) {
                binding.tvEntry.setTextColor(ContextCompat.getColor(binding.root.context, R.color.error_red))
                binding.tvEntry.text = "-${getFormattedPoints(item.value)}"
                when (item.debitType) {
                    Constants.WalletPointsDebitType.COUPON_UNLOCK.type -> {
                        binding.tvReason.text = Constants.WalletPointsDebitType.COUPON_UNLOCK.title
                        val drawable = ContextCompat.getDrawable(context, R.drawable.discount_tag_ni)
                        binding.ivTransactionIcon.setImageDrawable(drawable)

                    }
                    Constants.WalletPointsDebitType.SPIN_WHEEL_UNLOCK.type -> {
                        binding.tvReason.text = Constants.WalletPointsDebitType.SPIN_WHEEL_UNLOCK.title
                        val drawable = ContextCompat.getDrawable(context, R.drawable.spin_wheel_ni)
                        binding.ivTransactionIcon.setImageDrawable(drawable)
                    }

                    Constants.WalletPointsDebitType.GRATIFICATION.type -> {
                        binding.tvReason.text = Constants.WalletPointsDebitType.GRATIFICATION.title
                        val drawable = ContextCompat.getDrawable(context, R.drawable.giftbox_ni)
                        binding.ivTransactionIcon.setImageDrawable(drawable)
                    }

                    Constants.WalletPointsDebitType.SCRATCH_CARD_UNLOCKED.type -> {
                        binding.tvReason.text = Constants.WalletPointsDebitType.SCRATCH_CARD_UNLOCKED.title
                        val drawable = ContextCompat.getDrawable(context, R.drawable.scratch_card_ni)
                        binding.ivTransactionIcon.setImageDrawable(drawable)
                    }

                }
            }
            else {
                binding.tvEntry.setTextColor(ContextCompat.getColor(binding.root.context, R.color.success_green))
                binding.tvEntry.text = "+${getFormattedPoints(item.value)}"
                when (item.creditType) {
                    Constants.WalletPointsCreditType.TIER_ENTRY.type -> {
                        binding.tvReason.text = Constants.WalletPointsCreditType.TIER_ENTRY.title
                        /*val drawable = ContextCompat.getDrawable(context, R.drawable.ic_game_controller)?.mutate()
                        binding.ivTransactionIcon.setImageDrawable(drawable)*/
                    }
                    Constants.WalletPointsCreditType.SPIN_WHEEL_BENEFIT.type -> {
                        binding.tvReason.text = Constants.WalletPointsCreditType.SPIN_WHEEL_BENEFIT.title
                        val drawable = ContextCompat.getDrawable(context, R.drawable.spin_wheel_ni)?.mutate()
                        binding.ivTransactionIcon.setImageDrawable(drawable)
                    }
                    Constants.WalletPointsCreditType.SURVEY_BENEFIT.type -> {
                        binding.tvReason.text = Constants.WalletPointsCreditType.SURVEY_BENEFIT.title
                        val drawable = ContextCompat.getDrawable(context, R.drawable.quizzlet_ni)?.mutate()
                        binding.ivTransactionIcon.setImageDrawable(drawable)
                    }
                    Constants.WalletPointsCreditType.GRATIFICATION.type -> {
                        binding.tvReason.text = Constants.WalletPointsCreditType.GRATIFICATION.title
                        val drawable = ContextCompat.getDrawable(context, R.drawable.giftbox_ni)?.mutate()
                        binding.ivTransactionIcon.setImageDrawable(drawable)
                    }
                    Constants.WalletPointsCreditType.BENEFIT_TO_REFERRER.type -> {
                        binding.tvReason.text = Constants.WalletPointsCreditType.BENEFIT_TO_REFERRER.title
                        val drawable = ContextCompat.getDrawable(context, R.drawable.refer_earn)?.mutate()
                        binding.ivTransactionIcon.setImageDrawable(drawable)
                    }
                    Constants.WalletPointsCreditType.BENEFIT_TO_REFEREE.type -> {
                        binding.tvReason.text = Constants.WalletPointsCreditType.BENEFIT_TO_REFEREE.title
                        val drawable = ContextCompat.getDrawable(context, R.drawable.refer_earn)?.mutate()
                        binding.ivTransactionIcon.setImageDrawable(drawable)

                    }
                    Constants.WalletPointsCreditType.SCRATCH_CARD_BENEFIT.type -> {
                        binding.tvReason.text = Constants.WalletPointsCreditType.SCRATCH_CARD_BENEFIT.title
                        val drawable = ContextCompat.getDrawable(context, R.drawable.scratch_card_ni)?.mutate()
                        binding.ivTransactionIcon.setImageDrawable(drawable)

                    }
                    Constants.WalletPointsCreditType.QUIZ.type -> {
                        binding.tvReason.text = Constants.WalletPointsCreditType.QUIZ.title
                        val drawable = ContextCompat.getDrawable(context, R.drawable.quizzlet_ni)?.mutate()
                        binding.ivTransactionIcon.setImageDrawable(drawable)
                    }
                    Constants.WalletPointsCreditType.BIRTHDAY_TASK_EARNZONE.type -> {
                        binding.tvReason.text = item.message
                        val drawable = ContextCompat.getDrawable(context, R.drawable.ic_birthday_cake)?.mutate()
                        binding.ivTransactionIcon.setImageDrawable(drawable)
                    }
                    Constants.WalletPointsCreditType.ANNIVERSARY_TASK_EARNZONE.type -> {
                        binding.tvReason.text = item.message
                        val drawable = ContextCompat.getDrawable(context, R.drawable.ic_anniversary)?.mutate()
                        binding.ivTransactionIcon.setImageDrawable(drawable)
                    }
                    Constants.WalletPointsCreditType.YOUTUBE_TASK_EARNZONE.type -> {
                        binding.tvReason.text = item.message
                        val drawable = ContextCompat.getDrawable(context, R.drawable.ic_youtube)?.mutate()
                        binding.ivTransactionIcon.setImageDrawable(drawable)
                    }
                    Constants.WalletPointsCreditType.FACEBOOK_TASK_EARNZONE.type -> {
                        binding.tvReason.text = item.message
                        val drawable = ContextCompat.getDrawable(context, R.drawable.ic_fb)?.mutate()
                        binding.ivTransactionIcon.setImageDrawable(drawable)
                    }
                    Constants.WalletPointsCreditType.LINKEDIN_TASK_EARNZONE.type -> {
                        binding.tvReason.text = item.message
                        val drawable = ContextCompat.getDrawable(context, R.drawable.ic_linkedin)?.mutate()
                        binding.ivTransactionIcon.setImageDrawable(drawable)
                    }
                    Constants.WalletPointsCreditType.TWITTER_TASK_EARNZONE.type -> {
                        binding.tvReason.text = item.message
                        val drawable = ContextCompat.getDrawable(context, R.drawable.ic_twitter)?.mutate()
                        binding.ivTransactionIcon.setImageDrawable(drawable)
                    }
                    Constants.WalletPointsCreditType.INSTALIKE_TASK_EARNZONE.type -> {
                        binding.tvReason.text = item.message
                        val drawable = ContextCompat.getDrawable(context, R.drawable.ic_instagram)?.mutate()
                        binding.ivTransactionIcon.setImageDrawable(drawable)
                    }
                    Constants.WalletPointsCreditType.INSTAFOLLOW_TASK_EARNZONE.type -> {
                        binding.tvReason.text = item.message
                        val drawable = ContextCompat.getDrawable(context, R.drawable.ic_instagram)?.mutate()
                        binding.ivTransactionIcon.setImageDrawable(drawable)
                    }
                }
            }

            binding.root.setOnClickListener {

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