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
import com.google.gson.reflect.TypeToken
import com.lib.loopsdk.R
import com.lib.loopsdk.core.customViews.LoadingDialog
import com.lib.loopsdk.core.util.*
import com.lib.loopsdk.data.remote.dto.InitializeDto
import com.lib.loopsdk.data.remote.dto.TierDetailsDto
import com.lib.loopsdk.databinding.FragmentTierDetailsBinding
import kotlinx.coroutines.flow.onEach
import java.lang.Exception

class TierDetailsFragment : Fragment() {

    private var tierHistoryAdapter: TierHistoryAdapter? = null
    private val viewModel: TierDetailsPointsWalletViewModel by activityViewModels()
    private lateinit var binding: FragmentTierDetailsBinding
    private var userData: TierDetailsDto.Data.UserDetails? = null
    private val loadingDialog: LoadingDialog by lazy { LoadingDialog(requireContext()) }

    override fun onResume() {
        binding.root.alpha = 0f
        binding.root.translationY = Utils.dpToPx(requireContext(), 10).toFloat()
        tierHistoryAdapter?.clearAllData()
        tierHistoryAdapter = null
        setupAdapters()
        viewModel.getTierDetails()
        super.onResume()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_tier_details,
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
        setupAdapters()

        viewModel.tierUiStateFlow.onEach { uiState ->
            when (uiState){
                is UIState.Loading -> {
                    loadingDialog.show()
                }
                is UIState.Success<*> -> {

                    when (uiState.data){
                        is TierDetailsDto.Data -> {
                            Handler().postDelayed({
                                loadingDialog.cancel()
                            }, 1000)
                            Handler().postDelayed({
                                binding.root.animate()
                                    .translationY(0f)
                                    .alpha(1f)
                                    .setDuration(750)
                            },1300)
                            onTierDetailsDataReceived(uiState.data)
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

    private fun onTierDetailsDataReceived(response: TierDetailsDto.Data) {
        userData = response.userDetails
        tierHistoryAdapter?.setTotalPointsEarned(userData!!.totalPointsEarned)
        if (!response.tierData.isNullOrEmpty()) {
            //binding.nothingFoundContainer.hideView()
            //binding.somethingWentWrongContainer.root.hideView()
            val tierList = response.tierData.sortedBy { it.tierOrder }
            val currentTierItemIndex = tierList.indexOfLast { it.remaingPoints == 0 }
            /*if (currentTierItemIndex != -1){
                tierList[currentTierItemIndex].isCurrentTier = true
                tierHistoryAdapter?.setCurrentTierOrder(tierList[currentTierItemIndex].tierOrder)
                tierList[currentTierItemIndex].isExpanded = true
                tierList[currentTierItemIndex].tierUnlockDate = userData!!.tierDetails.tierUnlockDate
            }*/
            tierList.forEachIndexed { index, tierData ->
                if (tierData.tierID == userData!!.tierDetails.tierID) {
                    tierList[index].isCurrentTier = true
                    tierHistoryAdapter?.setCurrentTierOrder(tierData.tierOrder)
                    tierList[index].isExpanded = true
                    //tierList[index].tierUnlockDate = userData!!.tierDetails.tierUnlockDate
                    return@forEachIndexed
                }
            }
            tierHistoryAdapter?.addData(tierList)
        } else {
            //binding.nothingFoundContainer.showView()
            //binding.somethingWentWrongContainer.root.hideView()
        }
    }

    private fun setupAdapters(){
        tierHistoryAdapter = TierHistoryAdapter()
        binding.rvTierDetails.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )
        binding.rvTierDetails.adapter = tierHistoryAdapter
    }

}