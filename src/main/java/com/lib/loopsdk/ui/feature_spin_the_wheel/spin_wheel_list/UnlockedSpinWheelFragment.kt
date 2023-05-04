package com.lib.loopsdk.ui.feature_spin_the_wheel.spin_wheel_list

import android.content.Context
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
import com.lib.loopsdk.data.remote.dto.AllUnlockedSpinWheelDto
import com.lib.loopsdk.data.remote.dto.InitializeDto
import com.lib.loopsdk.databinding.FragmentUnlockedSpinWheelBinding
import com.lib.loopsdk.ui.feature_landing_home.presentation.LandingHomeActivity
import com.lib.loopsdk.ui.feature_scratch_card.ScratchCardLandingActivity
import com.lib.loopsdk.ui.feature_spin_the_wheel.spin_wheel_detail.SpinWheelDetailActivity
import kotlinx.coroutines.flow.onEach

class UnlockedSpinWheelFragment : Fragment(), UnlockedSpinWheelListAdapter.Interaction {

        private var currentPage = 1
        private var totalPages = 1
        private var isScrollDataLoading = false
        private lateinit var unlockSpinWheelListAdapter: UnlockedSpinWheelListAdapter
        private val viewModel: SpinWheelListViewModel by activityViewModels()
        private lateinit var binding: FragmentUnlockedSpinWheelBinding
        private lateinit var myContext: Context
        private val loadingDialog: LoadingDialog by lazy { LoadingDialog(requireContext()) }

        override fun onAttach(context: Context) {
        super.onAttach(context)
        myContext = context
        }
        override fun onResume() {
            binding.root.alpha = 0f
            binding.root.translationY = Utils.dpToPx(requireContext(), 10).toFloat()
            resetListParams()
            viewModel.getAllUnlockedSpinWheelList(currentPage)
            super.onResume()
        }

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_unlocked_spin_wheel,
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

            viewModel.unlockedSpinWheelListUiStateFlow.onEach { uiState ->
                when (uiState){
                    is UIState.Loading -> {
                        //loadingDialog.show()
                    }
                    is UIState.Success<*> -> {
                        //loadingDialog.cancel()

                        when (uiState.data){
                            is AllUnlockedSpinWheelDto.Data -> {
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
                                    unlockSpinWheelListAdapter.addData(uiState.data.spinWheels)
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

            binding.btnEarnPoints.setOnClickListener {
                LandingHomeActivity.startActivity(requireContext(), "gameArena")
                activity?.finish()
            }

        }

        private fun resetListParams(){
            currentPage = 1
            totalPages = 1
            isScrollDataLoading = false
            unlockSpinWheelListAdapter.clearAllData()
        }

        private fun setupAdapters(){
            unlockSpinWheelListAdapter = UnlockedSpinWheelListAdapter(interaction = this)
            binding.rvUnlockedCards.layoutManager = GridLayoutManager(
                requireContext(),
                2,
                LinearLayoutManager.VERTICAL,
                false
            )
            binding.rvUnlockedCards.adapter = unlockSpinWheelListAdapter
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
                        viewModel.getAllUnlockedSpinWheelList(currentPage)
                    }
                }
            })
        }

        override fun onPlayNowButtonClicked(position: Int, spinWheel: AllUnlockedSpinWheelDto.Data.SpinWheel) {
            Intent(requireContext(), SpinWheelDetailActivity::class.java).apply{
                this.putExtra("spinWheelId", spinWheel.spinWheelId)
                this.putExtra("spinWheelTransactionId", spinWheel.spinWheelTransactionId)
                startActivity(this)
            }
        }

    }