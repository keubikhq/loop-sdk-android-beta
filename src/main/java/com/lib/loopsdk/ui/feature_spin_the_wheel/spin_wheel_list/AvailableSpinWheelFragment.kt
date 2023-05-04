package com.lib.loopsdk.ui.feature_spin_the_wheel.spin_wheel_list

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
import com.google.gson.reflect.TypeToken
import com.lib.loopsdk.R
import com.lib.loopsdk.core.customViews.LoadingDialog
import com.lib.loopsdk.core.util.*
import com.lib.loopsdk.data.remote.dto.AllAvailableSpinWheelDto
import com.lib.loopsdk.data.remote.dto.InitializeDto
import com.lib.loopsdk.databinding.FragmentAvailableSpinWheelBinding
import com.lib.loopsdk.ui.feature_landing_home.presentation.LandingHomeActivity
import com.lib.loopsdk.ui.feature_spin_the_wheel.spin_wheel_detail.SpinWheelDetailActivity
import kotlinx.coroutines.flow.onEach

class AvailableSpinWheelFragment : Fragment(), AvailableSpinWheelListAdapter.Interaction {

    private var currentPage = 1
    private var totalPages = 1
    private var isScrollDataLoading = false
    private lateinit var availableSpinWheelListAdapter: AvailableSpinWheelListAdapter
    private val viewModel: SpinWheelListViewModel by activityViewModels()
    private lateinit var binding: FragmentAvailableSpinWheelBinding
    private val loadingDialog: LoadingDialog by lazy { LoadingDialog(
        requireContext()
    ) }


    override fun onResume() {
        binding.root.alpha = 0f
        binding.root.translationY = Utils.dpToPx(requireContext(), 10).toFloat()
        resetListParams()
        viewModel.getAllAvailableSpinWheelList(currentPage)
        super.onResume()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_available_spin_wheel,
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

        viewModel.availableSpinWheelListUiStateFlow.onEach { uiState ->
            when (uiState){
                is UIState.Loading -> {
                    //loadingDialog.show()
                }
                is UIState.Success<*> -> {
                    //loadingDialog.cancel()
                    when (uiState.data){
                        is AllAvailableSpinWheelDto.Data -> {
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
                            if(!uiState.data.spinWheels.isNullOrEmpty()){
                                binding.nothingFoundContainer.hideView()
                                //binding.somethingWentWrongContainer.root.hideView()
                                availableSpinWheelListAdapter.addData(uiState.data.spinWheels)
                            }else{
                                binding.nothingFoundContainer.showView()
                                //binding.somethingWentWrongContainer.root.hideView()
                            }

                        }
                    }
                }
                is UIState.Error -> {
                    //loadingDialog.cancel()
                    if (uiState.message == "NetworkError") {
                        FullScreenNetworkErrorActivity.startActivity(this, requireContext())
                    }

                }
                else -> {}
            }
        }.observeInLifecycle(viewLifecycleOwner)

        binding.btnExploreGames.setOnClickListener {
            LandingHomeActivity.startActivity(requireContext(), "gameArena")
            activity?.finish()
        }

    }

    private fun resetListParams(){
        currentPage = 1
        totalPages = 1
        isScrollDataLoading = false
        availableSpinWheelListAdapter.clearAllData()
    }

    private fun setupAdapters(){
        availableSpinWheelListAdapter = AvailableSpinWheelListAdapter(interaction = this)
        binding.rvAvailableCards.layoutManager = GridLayoutManager(
            requireContext(),
            2,
            LinearLayoutManager.VERTICAL,
            false
        )
        binding.rvAvailableCards.adapter = availableSpinWheelListAdapter
    }

    private fun registerScrollListener() {
        binding.rvAvailableCards.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!binding.rvAvailableCards.canScrollVertically(1) &&
                    newState == RecyclerView.SCROLL_STATE_IDLE &&
                    !isScrollDataLoading &&
                    currentPage < totalPages
                ) {
                    currentPage++
                    isScrollDataLoading = true
                    viewModel.getAllAvailableSpinWheelList(currentPage)
                }
            }
        })
    }

    override fun onUnlockButtonClicked(position: Int, spinWheel: AllAvailableSpinWheelDto.Data.SpinWheel) {
        Intent(requireContext(), SpinWheelDetailActivity::class.java).apply{
            this.putExtra("spinWheelId", spinWheel.id)
            startActivity(this)
        }
    }
}

