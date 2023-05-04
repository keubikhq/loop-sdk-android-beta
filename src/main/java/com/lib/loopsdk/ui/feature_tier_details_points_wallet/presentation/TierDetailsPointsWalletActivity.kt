package com.lib.loopsdk.ui.feature_tier_details_points_wallet.presentation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.gson.reflect.TypeToken
import com.lib.loopsdk.R
import com.lib.loopsdk.core.util.*
import com.lib.loopsdk.data.remote.dto.InitializeDto
import com.lib.loopsdk.databinding.ActivityTierDetailsPointsWalletBinding
import com.lib.loopsdk.ui.BaseActivity
import com.lib.loopsdk.ui.feature_offers.OffersActivity
import kotlinx.coroutines.flow.onEach

class TierDetailsPointsWalletActivity : BaseActivity() {

    private var selectedTab = 0
    private lateinit var binding: ActivityTierDetailsPointsWalletBinding
    private lateinit var viewModel: TierDetailsPointsWalletViewModel

    override fun onResume() {
        binding.llHeader.alpha = 0f
        binding.llHeader.translationY = Utils.dpToPx(this, 10).toFloat()
        binding.llTlContainer.alpha = 0f
        binding.llTlContainer.translationY = Utils.dpToPx(this, 10).toFloat()
        super.onResume()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_tier_details_points_wallet
        )
        binding.lifecycleOwner = this
        binding.brandTheme = Prefs.getNonPrimitiveData<InitializeDto.Data.BrandTheme>(object: TypeToken<InitializeDto.Data.BrandTheme>(){}.type, "BRAND_THEME")
        binding.brandThemeColors = Colors
        viewModel = ViewModelProvider(this)[TierDetailsPointsWalletViewModel::class.java]

        selectedTab = intent.getIntExtra(OffersActivity.SELECTED_TAB, 0)

        setupViewPager()
        binding.btnBack.setOnClickListener{
            onBackPressed()
        }

        viewModel.fragmentToActivityEventFlow.onEach {
            when(it){
                is TierDetailsPointsWalletViewModel.Event.OnCurrentBalanceReceived -> {
                    Handler().postDelayed({
                    binding.llHeader.animate()
                        .translationY(0f)
                        .alpha(1f)
                        .setDuration(750)
                    },1100)

                    Handler().postDelayed({
                        binding.llTlContainer.animate()
                            .translationY(0f)
                            .alpha(1f)
                            .setDuration(750)
                    },1200)
                    if(it.currentBalance.isNullOrEmpty()){
                        binding.btnPointsWallet.hideView()
                    }else{
                        binding.btnPointsWallet.showView()
                        binding.tvTotalPoints.text = getFormattedPoints(it.currentBalance)
                    }
                }

            }
        }.observeInLifecycle(this)
    }

    private fun setupViewPager() {
        val tabsTitles = arrayOf("Tier Details", "Wallet")
        val tabFragmentsList = ArrayList<Fragment>()
        tabFragmentsList.add(TierDetailsFragment())
        tabFragmentsList.add(PointsWalletFragment())
        binding.vpMain.offscreenPageLimit = 2
        binding.vpMain.adapter = TabSetup(
            tabFragmentsList,
            supportFragmentManager,
            tabsTitles
        )
        binding.tlMain.setupWithViewPager(binding.vpMain)
        binding.vpMain.currentItem = selectedTab

    }

    companion object{
        const val SELECTED_TAB = "SELECTED_TAB"
        fun startActivity(context: Context, selectedTab: Int = 0){
            val intent = Intent(context, TierDetailsPointsWalletActivity::class.java)
            intent.putExtra(SELECTED_TAB, selectedTab)
            context.startActivity(intent)
        }
    }
}