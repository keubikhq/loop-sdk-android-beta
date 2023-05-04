package com.lib.loopsdk.ui.feature_scratch_card

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.gson.reflect.TypeToken
import com.lib.loopsdk.R
import com.lib.loopsdk.core.customViews.LoadingDialog
import com.lib.loopsdk.core.util.*
import com.lib.loopsdk.data.remote.dto.AllUnlockedScratchCardsDto
import com.lib.loopsdk.data.remote.dto.InitializeDto
import com.lib.loopsdk.databinding.FragmentUnlockedScratchCardsListBinding
import com.lib.loopsdk.ui.feature_landing_home.presentation.LandingHomeActivity
import kotlinx.coroutines.flow.onEach
import java.lang.Exception

class UnlockedScratchCardsListFragment :
    Fragment(), UnlockedScratchCardsListAdapter.Interaction {

    private var currentPage = 1
    private var totalPages = 1
    private var isScrollDataLoading = false
    private lateinit var unlockScratchCardsListAdapter: UnlockedScratchCardsListAdapter
    private val viewModel: ScratchCardsListHomeViewModel by activityViewModels()
    private lateinit var binding: FragmentUnlockedScratchCardsListBinding
    private val loadingDialog: LoadingDialog by lazy { LoadingDialog(requireContext()) }

    override fun onResume() {
        binding.root.alpha = 0f
        binding.root.translationY = Utils.dpToPx(requireContext(), 10).toFloat()
        binding.nothingFoundContainer.hideView()
        resetListParams()
        viewModel.getAllUnlockedScratchCardsList(currentPage)
        super.onResume()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_unlocked_scratch_cards_list,
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
        registerScrollListener()
        binding.btnExploreGames.setOnClickListener {
            LandingHomeActivity.startActivity(requireContext(), "gameArena")
            activity?.finish()
        }

        viewModel.unlockedScratchCardsListUiStateFlow.onEach { uiState ->
            when (uiState){
                is UIState.Loading -> {
                    //loadingDialog.show()
                }
                is UIState.Success<*> -> {

                    when (uiState.data){
                        is AllUnlockedScratchCardsDto.Data -> {
                            if(currentPage == 1){
                                Handler().postDelayed({
                                    binding.root.animate()
                                        .translationY(0f)
                                        .alpha(1f)
                                        .setDuration(750)
                                },200)
                            }

                            isScrollDataLoading = false
                            totalPages = uiState.data.totalPages
                            if(!uiState.data.scratchCards.isNullOrEmpty()){
                                binding.nothingFoundContainer.hideView()
                                //binding.somethingWentWrongContainer.root.hideView()
                                unlockScratchCardsListAdapter.addData(uiState.data.scratchCards)
                                loadingDialog.cancel()
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

    private fun resetListParams(){
        currentPage = 1
        totalPages = 1
        isScrollDataLoading = false
        unlockScratchCardsListAdapter.clearAllData()
    }

    private fun setupAdapters(){
        unlockScratchCardsListAdapter = UnlockedScratchCardsListAdapter(interaction = this)
        binding.rvUnlockedCards.layoutManager = GridLayoutManager(
            requireContext(),
            2,
            LinearLayoutManager.VERTICAL,
            false
        )
        binding.rvUnlockedCards.adapter = unlockScratchCardsListAdapter
        (binding.rvUnlockedCards?.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
    }

    private fun registerScrollListener() {
        binding.rvUnlockedCards.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!binding.rvUnlockedCards.canScrollVertically(1) &&
                    newState == RecyclerView.SCROLL_STATE_IDLE &&
                    !isScrollDataLoading &&
                    currentPage < totalPages
                ) {
                    currentPage++
                    isScrollDataLoading = true
                    viewModel.getAllUnlockedScratchCardsList(currentPage)
                }
            }
        })
    }

    override fun onPlayNowButtonClicked(position: Int, scratchCard: AllUnlockedScratchCardsDto.Data.ScratchCard) {
        Intent(requireContext(), ScratchCardLandingActivity::class.java).apply{
            this.putExtra("scratchCardId", scratchCard.scratchCardId)
            this.putExtra("scratchCardTransactionId", scratchCard.scratchCardTransactionId)
            startActivity(this)
        }
    }

}