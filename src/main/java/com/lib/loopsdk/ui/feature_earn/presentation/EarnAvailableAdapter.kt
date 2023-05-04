package com.lib.loopsdk.ui.feature_earn.presentation

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.reflect.TypeToken
import com.lib.loopsdk.R
import com.lib.loopsdk.core.util.Colors
import com.lib.loopsdk.core.util.Constants
import com.lib.loopsdk.core.util.Constants.EARN_ZONE_TYPE
import com.lib.loopsdk.core.util.Prefs
import com.lib.loopsdk.data.remote.dto.EarnZoneDto
import com.lib.loopsdk.data.remote.dto.InitializeDto
import com.lib.loopsdk.databinding.ListItemEarnAvailableBinding
import timber.log.Timber
import java.lang.invoke.ConstantCallSite

class EarnAvailableAdapter(
    private val myContext: Context,
    private val list: List<EarnZoneDto.Data.Available>,
   private val listener: EarnAvailableListener
): RecyclerView.Adapter<EarnAvailableAdapter.MyVH>() {

    class MyVH(val binding: ListItemEarnAvailableBinding) : RecyclerView.ViewHolder(binding.root) {

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyVH {
        val binding = ListItemEarnAvailableBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.brandTheme = Prefs.getNonPrimitiveData<InitializeDto.Data.BrandTheme>(object: TypeToken<InitializeDto.Data.BrandTheme>(){}.type, "BRAND_THEME")
        binding.brandThemeColors = Colors
        binding.executePendingBindings()
        return MyVH(binding)
    }

    override fun getItemCount(): Int {
       return list.size
    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyVH, position: Int) {
        val binding = holder.binding
        val earnData=list[position]
        binding.root.setOnClickListener {
            listener.onEarnAvailableClicked(position,earnData)
        }
        when(earnData.type){

            EARN_ZONE_TYPE.ANNIVERSARY.type -> {
                binding.tvEventType.text ="Add your Anniversary"
                binding.ivEventType.setImageDrawable(
                    ContextCompat.getDrawable(
                        myContext,
                        R.drawable.ic_anniversary
                    )
                )
                binding.textNoCoins.text ="${earnData.value} points"


            }
            EARN_ZONE_TYPE.YOUTUBE.type -> {
                binding.tvEventType.text ="Like video on Youtube"
                binding.ivEventType.setImageDrawable(
                    ContextCompat.getDrawable(
                        myContext,
                        R.drawable.ic_youtube
                    )
                )
                binding.textNoCoins.text ="${earnData.value} points"

            }
            EARN_ZONE_TYPE.BIRTHDAY.type -> {

                binding.tvEventType.text = myContext.getString(R.string.add_your_bday)
                binding.ivEventType.setImageDrawable(
                    ContextCompat.getDrawable(
                        myContext,
                        R.drawable.ic_birthday_cake
                    )
                )
                binding.textNoCoins.text ="${earnData.value} points"


            }

            EARN_ZONE_TYPE.FACEBOOK.type -> {
                binding.tvEventType.text ="Follow page on Facebook"
                binding.ivEventType.setImageDrawable(
                    ContextCompat.getDrawable(
                        myContext,
                        R.drawable.ic_fb
                    )
                )
                binding.textNoCoins.text ="${earnData.value} points"

            }
            EARN_ZONE_TYPE.LINKEDIN.type -> {
                binding.tvEventType.text ="Like post on LinkedIn"
                binding.ivEventType.setImageDrawable(
                    ContextCompat.getDrawable(
                        myContext,
                        R.drawable.ic_linkedin
                    )
                )
                binding.textNoCoins.text ="${earnData.value} points"

            }
            EARN_ZONE_TYPE.TWITTER.type -> {
                binding.tvEventType.text ="Share post on Twitter"
                binding.ivEventType.setImageDrawable(
                    ContextCompat.getDrawable(
                        myContext,
                        R.drawable.ic_twitter
                    )
                )
                binding.textNoCoins.text ="${earnData.value} points"

            }

            EARN_ZONE_TYPE.INSTALIKE.type -> {
                binding.tvEventType.text ="Like post on Instagram"
                binding.ivEventType.setImageDrawable(
                    ContextCompat.getDrawable(
                        myContext,
                        R.drawable.ic_instagram
                    )
                )
                binding.textNoCoins.text ="${earnData.value} points"


            }
            EARN_ZONE_TYPE.INSTAFOLLOW.type -> {
                binding.tvEventType.text ="Like page on Instagram"
                binding.ivEventType.setImageDrawable(
                    ContextCompat.getDrawable(
                        myContext,
                        R.drawable.ic_instagram
                    )
                )
                binding.textNoCoins.text ="${earnData.value} points"


            }




        }
    }
    interface EarnAvailableListener{
        fun onEarnAvailableClicked(position: Int, earnAvailable: EarnZoneDto.Data.Available)
    }


}