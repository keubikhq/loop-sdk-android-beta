package com.lib.loopsdk.ui.feature_loyalty_intro.presentation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.gson.reflect.TypeToken
import com.lib.loopsdk.R
import com.lib.loopsdk.core.util.*
import com.lib.loopsdk.data.remote.dto.InitializeDto
import com.lib.loopsdk.data.remote.dto.SignUpDto
import com.lib.loopsdk.databinding.ActivityIntroductionBinding
import com.lib.loopsdk.ui.BaseActivity
import com.lib.loopsdk.ui.feature_landing_home.presentation.LandingHomeActivity
import kotlinx.coroutines.flow.onEach
import java.util.*

class IntroductionActivity : BaseActivity() {

    private lateinit var viewModel: IntroductionViewModel
    private lateinit var binding: ActivityIntroductionBinding

    private var introSliderAdapter: IntroSliderAdapter? = null
    var currentPage = 0
    var timer: Timer? = null
    private val DELAY_MS: Long = 1000 //delay in milliseconds before task is to be executed
    private val PERIOD_MS: Long = 3000

    override fun onResume() {
        super.onResume()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_introduction
        )
        binding.lifecycleOwner = this
        binding.brandTheme = Prefs.getNonPrimitiveData<InitializeDto.Data.BrandTheme>(object: TypeToken<InitializeDto.Data.BrandTheme>(){}.type, "BRAND_THEME")
        binding.brandThemeColors = Colors
        viewModel = ViewModelProvider(this)[IntroductionViewModel::class.java]
        setupIntroSliderAdapter()

        if (Constants.init.isReferalAdded == 0) binding.btnEnterReferralCode.hideView()
        else binding.btnEnterReferralCode.showView()

        binding.btnEnterReferralCode.setOnClickListener {
            with(ReferralBSFragment()){
                show(supportFragmentManager, "BrandFiltersBS")
            }
        }

        binding.btnJoinNow.setOnClickListener {
            Prefs.putBoolean("IS_JOIN_NOW_CLICKED", true)
            viewModel.joinNow()
        }

        binding.btnClose.setOnClickListener {
            onBackPressed()
        }

        viewModel.uiStateFlow.onEach {
            when (it) {
                is UIState.Loading -> {}
                is UIState.Success<*> -> {
                    when (it.data) {
                        is SignUpDto.Data -> {
                            Intent(this, LandingHomeActivity::class.java).apply {
                                startActivity(this)
                            }
                            this.finish()
                        }
                    }
                }
                is UIState.Error -> {}
                is UIState.ErrorWithData -> {}
            }
        }.observeInLifecycle(this)


    }

    private fun setupIntroSliderAdapter() {
        val slidesList = arrayListOf<IntroSlide>()
        slidesList.add(IntroSlide(R.drawable.ic_note, resources.getString(R.string.intro_slide_1_text)))
        slidesList.add(IntroSlide(R.drawable.ic_tier_badge_big, resources.getString(R.string.intro_slide_2_text)))
        slidesList.add(IntroSlide(R.drawable.ic_game_controller, resources.getString(R.string.intro_slide_3_text).format(
            Constants.init.pointsIdentifier.pointsLabelPlural
        )))

        introSliderAdapter = IntroSliderAdapter(
            slidesList,
            object: IntroSliderAdapter.Interaction {
            override fun onCoverImageItemClicked(
                pos: Int
            ) {

            }

            }
        )

        binding.vpStoreImages.adapter = introSliderAdapter
        binding.indicatorView.apply {
            setSliderGap(resources.getDimension(R.dimen.dp_16))
            setSliderWidth(resources.getDimension(R.dimen.dp_4))
            setupWithViewPager(binding.vpStoreImages)
        }
        /*After setting the adapter use the timer */
        val handler = Handler()
        val Update = Runnable {
            if (currentPage == 4 - 1) {
                currentPage = 0
            }
            binding.vpStoreImages.setCurrentItem(currentPage++, true)
        }
        timer = Timer() // This will create a new Thread

        timer!!.schedule(object : TimerTask() {
            // task to be scheduled
            override fun run() {
                handler.post(Update)
            }
        }, DELAY_MS, PERIOD_MS)

    }

}