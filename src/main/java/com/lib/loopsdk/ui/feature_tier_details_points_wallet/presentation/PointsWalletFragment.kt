package com.lib.loopsdk.ui.feature_tier_details_points_wallet.presentation

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.reflect.TypeToken
import com.lib.loopsdk.R
import com.lib.loopsdk.core.customViews.LoadingDialog
import com.lib.loopsdk.core.util.*
import com.lib.loopsdk.data.remote.dto.InitializeDto
import com.lib.loopsdk.data.remote.dto.PointsTransactionDto
import com.lib.loopsdk.databinding.FragmentPointsWalletBinding
import com.lib.loopsdk.ui.feature_landing_home.presentation.LandingHomeActivity
import kotlinx.coroutines.flow.onEach
import java.lang.Exception

class PointsWalletFragment : Fragment() {

    private var currentPage = 1
    private var totalPages = 1
    private var isScrollDataLoading = false
    private lateinit var wallHistoryAdapter: WalletHistoryAdapter
    private val viewModel: TierDetailsPointsWalletViewModel by activityViewModels()
    private lateinit var binding: FragmentPointsWalletBinding
    private val loadingDialog: LoadingDialog by lazy { LoadingDialog(requireContext()) }

    override fun onResume() {
        binding.root.alpha = 0f
        binding.root.translationY = Utils.dpToPx(requireContext(), 10).toFloat()
        binding.nothingFoundContainer.hideView()
        resetListParams()
        viewModel.getPointsHistory(currentPage)
        super.onResume()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_points_wallet,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.brandTheme = Prefs.getNonPrimitiveData<InitializeDto.Data.BrandTheme>(object: TypeToken<InitializeDto.Data.BrandTheme>(){}.type, "BRAND_THEME")
        binding.brandThemeColors = Colors
        binding.init = Constants.init
        setPointsCreditDurationText()
        setupAdapters()
        registerScrollListener()
        binding.btnEarnPoints.setOnClickListener {
            LandingHomeActivity.startActivity(requireContext(), "gameArena")
            activity?.finish()
        }

        viewModel.pointsUiStateFlow.onEach { uiState ->
            when (uiState){
                is UIState.Loading -> {
                    loadingDialog.show()
                }
                is UIState.Success<*> -> {

                    when (uiState.data){
                        is PointsTransactionDto.Data -> {
                            isScrollDataLoading = false
                            totalPages = uiState.data.totalPages
                            if(!uiState.data.pointTransactions.isNullOrEmpty()){
                                binding.nothingFoundContainer.hideView()
                                //binding.somethingWentWrongContainer.root.hideView()
                                Handler().postDelayed({
                                    loadingDialog.cancel()
                                },1000)
                                if(currentPage == 1){
                                    Handler().postDelayed({
                                        binding.root.animate()
                                            .translationY(0f)
                                            .alpha(1f)
                                            .setDuration(750)
                                    },1300)
                                }
                                wallHistoryAdapter.addData(uiState.data.pointTransactions)
                            }else{
                                loadingDialog.cancel()
                                binding.nothingFoundContainer.showView()
                                //binding.somethingWentWrongContainer.root.hideView()
                            }
                        }
                    }
                }
                is UIState.Error -> {
                    loadingDialog.cancel()
                    try {
                        val errorCode = uiState.message.toInt()
                        when (errorCode) {
                            401 -> {
                                // CommonDialogUtils.showSingleButtonLogoutDialog(requireActivity(), "Session Expired!", null)
                            }
                            422 -> {

                            }
                        }
                    }catch (e: Exception){
                        if(uiState.message == "NetworkError"){
                            FullScreenNetworkErrorActivity.startActivity(this, requireContext())
                        }
                    }
                }
                else -> {
                    loadingDialog.cancel()
                }
            }
        }.observeInLifecycle(viewLifecycleOwner)

    }

    private fun setPointsCreditDurationText() {
        if (Constants.init.pointsCreditTime != null) {
            binding.crdvNote.showView()
            if (Constants.init.pointsCreditTime == 0){
                binding.tvNoteText.text = "It might take some time for your points to be credited."
            }else {
                binding.tvNoteText.text = "Note: It will take up to ${Constants.init.pointsCreditTime} ${if(Constants.init.pointsCreditTime > 1)"days" else "day"} for your ${Constants.init.pointsIdentifier.pointsLabelPlural} to be credited in your wallet post you make a purchase."
            }
        }else{
            binding.crdvNote.hideView()
        }
    }

    private fun resetListParams(){
        currentPage = 1
        totalPages = 1
        isScrollDataLoading = false
        wallHistoryAdapter.clearAllData()
    }

    private fun setupAdapters(){
        wallHistoryAdapter = WalletHistoryAdapter()
        binding.rvPointsWallet.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )
        binding.rvPointsWallet.adapter = wallHistoryAdapter
    }

    private fun registerScrollListener() {
        binding.rvPointsWallet.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!binding.rvPointsWallet.canScrollVertically(1) &&
                    newState == RecyclerView.SCROLL_STATE_IDLE &&
                    !isScrollDataLoading &&
                    currentPage < totalPages
                ) {
                    currentPage++
                    isScrollDataLoading = true
                    viewModel.getPointsHistory(currentPage)
                }
            }
        })
    }

}