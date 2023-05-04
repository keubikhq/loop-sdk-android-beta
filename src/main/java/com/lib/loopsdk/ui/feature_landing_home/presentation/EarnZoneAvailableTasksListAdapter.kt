package com.lib.loopsdk.ui.feature_landing_home.presentation

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout.LayoutParams
import androidx.core.content.ContextCompat
import androidx.core.view.marginTop
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.google.gson.reflect.TypeToken
import com.lib.loopsdk.R
import com.lib.loopsdk.core.util.*
import com.lib.loopsdk.data.remote.dto.GetHomeScreenDetailsDto
import com.lib.loopsdk.data.remote.dto.InitializeDto
import com.lib.loopsdk.data.remote.dto.TierDetailsDto
import com.lib.loopsdk.databinding.ListItemEarnZoneTaskHomeBinding
import com.lib.loopsdk.databinding.ListItemGameArenaHomeBinding
import com.lib.loopsdk.databinding.ListItemTierDetailBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class EarnZoneAvailableTasksListAdapter(
    private val list: ArrayList<GetHomeScreenDetailsDto.Data.EarnZoneTask> = arrayListOf(),
    private val interaction: Interaction? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val TAG = this::class.java.simpleName

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: ListItemEarnZoneTaskHomeBinding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.list_item_earn_zone_task_home,
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

    fun addData(data: List<GetHomeScreenDetailsDto.Data.EarnZoneTask>) {
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
        val binding: ListItemEarnZoneTaskHomeBinding,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(binding.root){

        fun bind(item: GetHomeScreenDetailsDto.Data.EarnZoneTask) {

            val context = binding.root.context

            when(item.type){

                Constants.EARN_ZONE_TYPE.BIRTHDAY.type-> {
                    binding.tvTaskTitle.text = context.getString(R.string.add_your_bday)
                    binding.ivTaskIcon.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.ic_birthday_cake
                        )
                    )
                    binding.tvTaskValueText.text ="${item.value} ${if (item.value > 1) Constants.init.pointsIdentifier.pointsLabelPlural else Constants.init.pointsIdentifier.pointsLabelSingular}"
                }

                Constants.EARN_ZONE_TYPE.ANNIVERSARY.type-> {
                    binding.tvTaskTitle.text = "Add your Anniversary"
                    binding.ivTaskIcon.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.ic_anniversary
                        )
                    )
                    binding.tvTaskValueText.text ="${item.value} ${if (item.value > 1) Constants.init.pointsIdentifier.pointsLabelPlural else Constants.init.pointsIdentifier.pointsLabelSingular}"
                }

                Constants.EARN_ZONE_TYPE.YOUTUBE.type-> {
                    binding.tvTaskTitle.text = "Like video on Youtube"
                    binding.ivTaskIcon.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.ic_youtube
                        )
                    )
                    binding.tvTaskValueText.text ="${item.value} ${if (item.value > 1) Constants.init.pointsIdentifier.pointsLabelPlural else Constants.init.pointsIdentifier.pointsLabelSingular}"
                }

                Constants.EARN_ZONE_TYPE.FACEBOOK.type-> {
                    binding.tvTaskTitle.text = "Follow page on Facebook"
                    binding.ivTaskIcon.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.ic_fb
                        )
                    )
                    binding.tvTaskValueText.text ="${item.value} ${if (item.value > 1) Constants.init.pointsIdentifier.pointsLabelPlural else Constants.init.pointsIdentifier.pointsLabelSingular}"
                }

                Constants.EARN_ZONE_TYPE.LINKEDIN.type-> {
                    binding.tvTaskTitle.text = "Like post on LinkedIn"
                    binding.ivTaskIcon.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.ic_linkedin
                        )
                    )
                    binding.tvTaskValueText.text ="${item.value} ${if (item.value > 1) Constants.init.pointsIdentifier.pointsLabelPlural else Constants.init.pointsIdentifier.pointsLabelSingular}"
                }

                Constants.EARN_ZONE_TYPE.TWITTER.type-> {
                    binding.tvTaskTitle.text = "Share post on Twitter"
                    binding.ivTaskIcon.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.ic_twitter
                        )
                    )
                    binding.tvTaskValueText.text ="${item.value} ${if (item.value > 1) Constants.init.pointsIdentifier.pointsLabelPlural else Constants.init.pointsIdentifier.pointsLabelSingular}"
                }

                Constants.EARN_ZONE_TYPE.INSTALIKE.type-> {
                    binding.tvTaskTitle.text = "Like post on Instagram"
                    binding.ivTaskIcon.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.ic_instagram
                        )
                    )
                    binding.tvTaskValueText.text ="${item.value} ${if (item.value > 1) Constants.init.pointsIdentifier.pointsLabelPlural else Constants.init.pointsIdentifier.pointsLabelSingular}"
                }

                Constants.EARN_ZONE_TYPE.INSTAFOLLOW.type-> {
                    binding.tvTaskTitle.text = "Follow page on Instagram"
                    binding.ivTaskIcon.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.ic_instagram
                        )
                    )
                    binding.tvTaskValueText.text ="${item.value} ${if (item.value > 1) Constants.init.pointsIdentifier.pointsLabelPlural else Constants.init.pointsIdentifier.pointsLabelSingular}"
                }

            }



            binding.root.setOnClickListener {
                interaction?.onEarnZoneTaskClicked(adapterPosition, item)
            }

        }

    }

    interface Interaction {
        fun onEarnZoneTaskClicked(position: Int, task: GetHomeScreenDetailsDto.Data.EarnZoneTask)
    }
}