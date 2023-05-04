package com.lib.loopsdk.ui.feature_loyalty_intro.presentation

import android.os.Bundle
import android.os.Handler
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.google.gson.reflect.TypeToken
import com.lib.loopsdk.R
import com.lib.loopsdk.core.util.Colors
import com.lib.loopsdk.core.util.Constants
import com.lib.loopsdk.core.util.Prefs
import com.lib.loopsdk.core.util.RxBusOnboardingRelay
import com.lib.loopsdk.data.remote.dto.InitializeDto
import com.lib.loopsdk.databinding.ActivitySignUpIntroBinding
import com.lib.loopsdk.ui.BaseActivity
import java.util.*

class SignUpIntroActivity : BaseActivity() {

    private val viewModel: IntroductionViewModel by viewModels()
    private lateinit var binding: ActivitySignUpIntroBinding

    private var introSliderAdapter: IntroSliderLongAdapter? = null
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
            R.layout.activity_sign_up_intro
        )
        binding.lifecycleOwner = this
        binding.brandTheme = Prefs.getNonPrimitiveData<InitializeDto.Data.BrandTheme>(object: TypeToken<InitializeDto.Data.BrandTheme>(){}.type, "BRAND_THEME")
        binding.brandThemeColors = Colors

        setupIntroSliderAdapter()

        binding.btnSignup.setOnClickListener {
            RxBusOnboardingRelay.accept(Constants.init.primaryUserId)
            this.finish()
        }

        binding.btnClose.setOnClickListener {
            onBackPressed()
        }


    }

    private fun setupIntroSliderAdapter() {
        val slidesList = arrayListOf<IntroSlide>()
        slidesList.add(IntroSlide(R.drawable.ic_note, resources.getString(R.string.intro_slide_1_text)))
        slidesList.add(IntroSlide(R.drawable.ic_tier_badge_big, resources.getString(R.string.intro_slide_2_text)))
        slidesList.add(IntroSlide(R.drawable.ic_game_controller, resources.getString(R.string.intro_slide_3_text).format(
            Constants.init.pointsIdentifier.pointsLabelPlural
        )))

        introSliderAdapter = IntroSliderLongAdapter(
            slidesList,
            object: IntroSliderLongAdapter.Interaction {
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