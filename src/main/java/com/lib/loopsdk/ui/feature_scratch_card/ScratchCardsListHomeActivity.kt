package com.lib.loopsdk.ui.feature_scratch_card

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.gson.reflect.TypeToken
import com.lib.loopsdk.R
import com.lib.loopsdk.core.util.*
import com.lib.loopsdk.data.remote.dto.InitializeDto
import com.lib.loopsdk.databinding.ActivityScratchCardsListHomeBinding
import com.lib.loopsdk.ui.BaseActivity
import com.lib.loopsdk.ui.feature_tier_details_points_wallet.presentation.TierDetailsPointsWalletActivity
import kotlinx.coroutines.flow.onEach

class ScratchCardsListHomeActivity : BaseActivity() {

    private lateinit var binding: ActivityScratchCardsListHomeBinding
    private lateinit var viewModel: ScratchCardsListHomeViewModel

    override fun onResume() {
        binding.llHeader.alpha = 0f
        binding.llHeader.translationY = Utils.dpToPx(this, 10).toFloat()
        binding.llTlContainer.alpha = 0f
        binding.llTlContainer.translationY = Utils.dpToPx(this, 10).toFloat()
        binding.vpMain.currentItem = 0
        super.onResume()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_scratch_cards_list_home
        )
        binding.lifecycleOwner = this
        binding.brandTheme = Prefs.getNonPrimitiveData<InitializeDto.Data.BrandTheme>(object: TypeToken<InitializeDto.Data.BrandTheme>(){}.type, "BRAND_THEME")
        binding.brandThemeColors = Colors
        viewModel = ViewModelProvider(this)[ScratchCardsListHomeViewModel::class.java]
        setupViewPager()
        binding.btnBack.setOnClickListener{
            onBackPressed()
        }

        viewModel.fragmentToActivityEventFlow.onEach {
            when(it){
                is ScratchCardsListHomeViewModel.Event.OnPointsReceived -> {
                    binding.llHeader.animate()
                        .translationY(0f)
                        .alpha(1f)
                        .setDuration(750)

                    Handler().postDelayed({
                        binding.llTlContainer.animate()
                            .translationY(0f)
                            .alpha(1f)
                            .setDuration(750)
                    },100)
                    if(it.points.isNullOrEmpty()){
                        binding.btnPointsWallet.hideView()
                    }else{
                        binding.btnPointsWallet.showView()
                        goToTierDetailPointWalletActivity()
                        binding.tvTotalPoints.text = getFormattedPoints( it.points)
                    }
                }

            }
        }.observeInLifecycle(this)
    }

    private fun goToTierDetailPointWalletActivity() {
        binding.btnPointsWallet.setOnClickListener {
            TierDetailsPointsWalletActivity.startActivity(this,2)
        }

    }

    private fun setupViewPager() {
        val tabsTitles = arrayOf("Available", "Unlocked")
        val tabFragmentsList = ArrayList<Fragment>()
        tabFragmentsList.add(AvailableScratchCardsListFragment())
        tabFragmentsList.add(UnlockedScratchCardsListFragment())
        binding.vpMain.offscreenPageLimit = 2
        binding.vpMain.adapter = TabSetup(
            tabFragmentsList,
            supportFragmentManager,
            tabsTitles
        )
        binding.tlMain.setupWithViewPager(binding.vpMain)
        binding.vpMain.currentItem = 0

    }
}