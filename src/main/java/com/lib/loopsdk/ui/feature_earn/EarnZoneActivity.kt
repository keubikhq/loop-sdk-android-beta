package com.lib.loopsdk.ui.feature_earn

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.google.android.material.tabs.TabLayout
import com.google.gson.reflect.TypeToken
import com.lib.loopsdk.R
import com.lib.loopsdk.core.customViews.LoadingDialog
import com.lib.loopsdk.core.util.*
import com.lib.loopsdk.data.remote.dto.InitializeDto
import com.lib.loopsdk.databinding.ActivityEarnZoneBinding
import com.lib.loopsdk.ui.BaseActivity
import com.lib.loopsdk.ui.feature_earn.presentation.EarnListViewModel
import com.lib.loopsdk.ui.feature_scratch_card.ScratchCardsListHomeViewModel
import com.lib.loopsdk.ui.feature_tier_details_points_wallet.presentation.TierDetailsPointsWalletActivity
import kotlinx.coroutines.flow.onEach

class EarnZoneActivity : BaseActivity() {
    private lateinit var binding: ActivityEarnZoneBinding
    private var tabPosition: Int = 0
    private val viewModel: EarnListViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_earn_zone)
        initializeEarn()
        setUpListener()
        setupViewPager()
    }

    private fun initializeEarn() {
        binding.lifecycleOwner = this
        binding.viewModel = this.viewModel
        binding.brandTheme = Prefs.getNonPrimitiveData<InitializeDto.Data.BrandTheme>(object :
            TypeToken<InitializeDto.Data.BrandTheme>() {}.type, "BRAND_THEME")
        binding.brandThemeColors = Colors

        tabPosition = intent.getIntExtra("tabPosition", 0)

    }


    override fun onResume() {

//        binding.llHeader.alpha = 0f
//        binding.llHeader.translationY = Utils.dpToPx(this, 10).toFloat()
//        binding.tlEarnZone.alpha = 0f
//        binding.tlEarnZone.translationY = Utils.dpToPx(this, 10).toFloat()
        super.onResume()
    }

    private fun setUpListener() {
        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
        viewModel.fragmentToActivityEventFlow.onEach {
            when(it){
                is EarnListViewModel.Event.OnPointsReceived -> {
//                    binding.llHeader.animate()
//                        .translationY(0f)
//                        .alpha(1f)
//                        .setDuration(750)

//                    Handler().postDelayed({
//                        binding.llTlContainer.animate()
//                            .translationY(0f)
//                            .alpha(1f)
//                            .setDuration(750)
//                    },100)

                    binding.tvPoints.text = getFormattedPoints( it.points.toString())
                }

            }
        }.observeInLifecycle(this)

        binding.cvPoints.setOnClickListener {

            TierDetailsPointsWalletActivity.startActivity(this,2)
        }
    }

    private fun setupViewPager() {
        binding.vpEarnZone.adapter = EarnZoneStateAdapter(this, supportFragmentManager, 2)
        binding.vpEarnZone.addOnPageChangeListener(
            object : TabLayout.TabLayoutOnPageChangeListener(
                binding.tlEarnZone
            ) {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    viewModel.fragmentToActivityEventFlow.onEach {
                        when(it){
                            is EarnListViewModel.Event.OnPointsReceived -> {
//                    binding.llHeader.animate()
//                        .translationY(0f)
//                        .alpha(1f)
//                        .setDuration(750)

//                    Handler().postDelayed({
//                        binding.llTlContainer.animate()
//                            .translationY(0f)
//                            .alpha(1f)
//                            .setDuration(750)
//                    },100)

                                binding.tvPoints.text = getFormattedPoints( it.points.toString())
                            }

                        }
                    }.observeInLifecycle(this@EarnZoneActivity)
                }
            }
        )
        binding.tlEarnZone.setupWithViewPager(binding.vpEarnZone)
        binding.vpEarnZone.currentItem = tabPosition
    }

    companion object {
        fun startActivity(context: Context, selectedTabPosition: Int) {
            val intent = Intent(context, EarnZoneActivity::class.java)
            intent.putExtra("tabPosition", selectedTabPosition)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }
}