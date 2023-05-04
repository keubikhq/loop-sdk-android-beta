package com.lib.loopsdk.ui.feature_offers

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayout
import com.google.gson.reflect.TypeToken
import com.lib.loopsdk.R
import com.lib.loopsdk.core.util.Colors
import com.lib.loopsdk.core.util.Constants
import com.lib.loopsdk.core.util.Prefs
import com.lib.loopsdk.core.util.Utils
import com.lib.loopsdk.core.util.getFormattedPoints
import com.lib.loopsdk.core.util.observeInLifecycle
import com.lib.loopsdk.core.util.showView
import com.lib.loopsdk.data.remote.dto.InitializeDto
import com.lib.loopsdk.databinding.ActivityOffersBinding
import com.lib.loopsdk.ui.BaseActivity
import com.lib.loopsdk.ui.feature_offers.available_coupons.CouponsViewModel
import com.lib.loopsdk.ui.feature_tier_details_points_wallet.presentation.TierDetailsPointsWalletActivity
import kotlinx.coroutines.flow.onEach

class OffersActivity : BaseActivity(), TabLayout.OnTabSelectedListener  {

    private lateinit var binding:ActivityOffersBinding
    private var selectedTab = 0
    private lateinit var viewModel: CouponsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_offers
        )
        initializeVariables()
        setUpViewPager()
        binding.ivClose.setOnClickListener{
            onBackPressed()
        }
        if(viewModel.userBalance==0){
            binding.cvPoints.showView()
            val userBalance=Prefs.getString(Constants.USER_BALANCE)
            binding.tvPoints.text =getFormattedPoints(userBalance!!)
            viewModel.userBalance=userBalance.toDouble().toInt()
        }
        binding.cvPoints.setOnClickListener {
            TierDetailsPointsWalletActivity.startActivity(this,2)
        }
    }
    private fun initializeVariables(){
        selectedTab = intent.getIntExtra(SELECTED_TAB, 0)
        binding.lifecycleOwner = this
        binding.brandTheme = Prefs.getNonPrimitiveData<InitializeDto.Data.BrandTheme>(object: TypeToken<InitializeDto.Data.BrandTheme>(){}.type, "BRAND_THEME")
        binding.brandThemeColors = Colors
        viewModel = ViewModelProvider(this)[CouponsViewModel::class.java]

        viewModel.fragmentToActivityEventFlow.onEach {
            when(it){
                is CouponsViewModel.Event.OnPointsReceived -> {
                    binding.llHeader.animate()
                        .translationY(0f)
                        .alpha(1f)
                        .setDuration(750)

                    binding.tlOffer.animate()
                        .translationY(0f)
                        .alpha(1f)
                        .setDuration(750)

//                    Handler().postDelayed({
//
//                    },50)
                    if(it.pointsResponse != null){
                        binding.cvPoints.showView()
                        binding.tvPoints.text =getFormattedPoints(it.pointsResponse.userDetails.pointsBalance)
                        viewModel.userBalance=it.pointsResponse.userDetails.pointsBalance.toDouble().toInt()
                    }
                }
                else -> {}
            }
        }.observeInLifecycle(this)

    }
    private fun setUpViewPager(){
        binding.viewPagerOffer.adapter = OffersTabAdapter(this,supportFragmentManager,2)
        binding.viewPagerOffer.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(binding.tlOffer))
        binding.tlOffer.setupWithViewPager(binding.viewPagerOffer)
        Handler().postDelayed({
            binding.viewPagerOffer.currentItem = selectedTab
        },500)
        binding.tlOffer.addOnTabSelectedListener(this)
    }

    companion object{
        const val SELECTED_TAB = "SELECTED_TAB"
        fun startActivity(context: Context, selectedTab: Int = 0){
            val intent = Intent(context, OffersActivity::class.java)
            intent.putExtra(SELECTED_TAB, selectedTab)
            context.startActivity(intent)
        }
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {

    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {
    }

    override fun onTabReselected(tab: TabLayout.Tab?) {
    }

    override fun onResume() {
        binding.llHeader.alpha = 0f
        binding.llHeader.translationY = Utils.dpToPx(this, 10).toFloat()
        binding.tlOffer.alpha = 0f
        binding.tlOffer.translationY = Utils.dpToPx(this, 10).toFloat()
        super.onResume()

    }



}