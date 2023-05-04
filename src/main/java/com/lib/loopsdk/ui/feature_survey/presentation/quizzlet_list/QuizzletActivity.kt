package com.lib.loopsdk.ui.feature_survey.presentation.quizzlet_list

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.google.android.material.tabs.TabLayout
import com.google.gson.reflect.TypeToken
import com.lib.loopsdk.R
import com.lib.loopsdk.core.customViews.LoadingDialog
import com.lib.loopsdk.core.util.*
import com.lib.loopsdk.data.remote.dto.GetWalletPointsDto
import com.lib.loopsdk.data.remote.dto.InitializeDto
import com.lib.loopsdk.databinding.ActivityQuizzletBinding
import com.lib.loopsdk.ui.BaseActivity
import com.lib.loopsdk.ui.feature_tier_details_points_wallet.presentation.TierDetailsPointsWalletActivity
import kotlinx.coroutines.flow.onEach

class QuizzletActivity : BaseActivity() {
    private lateinit var binding: ActivityQuizzletBinding
    private var tabPosition: Int = 0
    private val loadingDialog: LoadingDialog by lazy { LoadingDialog(this) }
    private val viewModel: QuizzletListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_quizzlet)
        binding.lifecycleOwner = this
        binding.viewModel = this.viewModel
        binding.brandTheme = Prefs.getNonPrimitiveData<InitializeDto.Data.BrandTheme>(object: TypeToken<InitializeDto.Data.BrandTheme>(){}.type, "BRAND_THEME")
        binding.brandThemeColors = Colors
        tabPosition = intent.getIntExtra("tabPosition", 0)
        setUpListener()
        setupViewPager()

        viewModel.userUiStateFlow.onEach {
            when (it) {
                is UIState.Loading -> {
                    loadingDialog.show()
                }
                is UIState.Success<*> -> {
                    loadingDialog.cancel()
                    when (it.data) {
                        is GetWalletPointsDto.Data -> {
                            binding.llHeader.animate()
                                .translationY(0f)
                                .alpha(1f)
                                .setDuration(750)

                            Handler().postDelayed({
                                binding.tlQuizzlet.animate()
                                    .translationY(0f)
                                    .alpha(1f)
                                    .setDuration(750)
                            },50)
                            binding.tvPoints.text = getFormattedPoints(it.data.userDetails.pointsBalance)
                        }
                    }
                }
                is UIState.Error -> {
                    loadingDialog.cancel()
                    if (it.message == "NetworkError") {
                        FullScreenNetworkErrorActivity.startActivity(this)
                    }
                    try {
//                        val errorCode = it.message.toInt()
//                        when (errorCode) {
//                            401 -> {
//                                CommonDialogUtils.showSingleButtonLogoutDialog(requireActivity(), "Session Expired!", null)
//                            }
//                            422 -> {
//                                binding.clServerError.showView()
//                                binding.rvAvailableTask.hideView()
//                            }
//                        }
                    } catch (e: java.lang.Exception) {
//                        if (it.message == "NetworkError") {
//                            FullScreenNetworkErrorActivity.startActivity(this)
//                        }
                    }
                }
                else -> {}
            }
        }.observeInLifecycle(this)
    }

    override fun onResume() {
        binding.llHeader.alpha = 0f
        binding.llHeader.translationY = Utils.dpToPx(this, 10).toFloat()
        binding.tlQuizzlet.alpha = 0f
        binding.tlQuizzlet.translationY = Utils.dpToPx(this, 10).toFloat()

        super.onResume()
        viewModel.getPointsAndTier()
    }
    companion object {
        fun startActivity(context: Context, selectedTabPosition: Int) {
            val intent = Intent(context, QuizzletActivity::class.java)
            intent.putExtra("tabPosition", selectedTabPosition)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }
    private fun setupViewPager() {
        binding.vpQuizzlet.adapter = QuizzletStateAdapter(this, supportFragmentManager, 2)
        binding.vpQuizzlet.addOnPageChangeListener(
            object : TabLayout.TabLayoutOnPageChangeListener(
                binding.tlQuizzlet
            ) {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)

                }
            }
        )
        binding.tlQuizzlet.setupWithViewPager(binding.vpQuizzlet)
        binding.vpQuizzlet.currentItem = tabPosition
    }
    private fun setUpListener(){
        binding.ivBack.setOnClickListener{
            finish()
        }
        binding.cvPoints.setOnClickListener {

        TierDetailsPointsWalletActivity.startActivity(this,2)
        }
    }
}