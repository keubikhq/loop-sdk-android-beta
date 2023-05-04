package com.lib.loopsdk.ui.feature_earn

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import com.google.gson.reflect.TypeToken
import com.lib.loopsdk.R
import com.lib.loopsdk.core.customViews.LoadingDialog
import com.lib.loopsdk.core.util.*
import com.lib.loopsdk.data.remote.dto.EarnZoneDto
import com.lib.loopsdk.data.remote.dto.InitializeDto
import com.lib.loopsdk.databinding.FragmentEarnZoneAvailableBinding
import com.lib.loopsdk.databinding.FragmentEarnZoneCompletedBinding
import com.lib.loopsdk.ui.feature_earn.presentation.EarnAvailableAdapter
import com.lib.loopsdk.ui.feature_earn.presentation.EarnListViewModel
import kotlinx.coroutines.flow.onEach
import timber.log.Timber


class EarnZoneCompletedFragment : Fragment() ,EarnZoneCompletedAdapter.EarnCompletedListener{
    private lateinit var binding: FragmentEarnZoneCompletedBinding
   // private val loadingDialog: LoadingDialog by lazy { LoadingDialog(requireContext()) }
    private lateinit var earnComletedAdapter: EarnZoneCompletedAdapter
    private val viewModel: EarnListViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_earn_zone_completed, container,false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        val brandTheme = Prefs.getNonPrimitiveData<InitializeDto.Data.BrandTheme>(object: TypeToken<InitializeDto.Data.BrandTheme>(){}.type, "BRAND_THEME")
        binding.brandTheme = brandTheme
        binding.brandThemeColors = Colors
        viewModel.getEarnDataList()
        getDataViewModel()
        return binding.root
    }

    private fun getDataViewModel() {
        viewModel.earnUiStateFlow.onEach {
            Timber.d("earn list data $it")
            when(it){
                is UIState.Loading -> {
                   // loadingDialog.show()
                    Timber.d("Loading state")
                }
                is UIState.Success<*> ->{
                   // loadingDialog.cancel()
                    when(it.data){
                        is EarnZoneDto.Data ->{
//                            Handler().postDelayed({
//                                binding.root.animate()
//                                    .translationY(0f)
//                                    .alpha(1f)
//                                    .setDuration(750)
//                            },200)
                            if(it.data.completed.isEmpty()){
                                binding.nothingFoundContainer.showView()
                                binding.rvEarnCompleted.hideView()
                            }
                            else{
                                binding.nothingFoundContainer.hideView()
                                binding.rvEarnCompleted.showView()
//                                earnAdapter.clearAllData()
//                                earnAdapter.addData(it.data.available)
//                                list= it.data.available
                                Timber.d(" earn list data ${it.data.available}")
                                setupEarnAvailableAdapter(it.data.completed)

                            }
                        }
                    }
                }
                is UIState.Error ->{
                 //  loadingDialog.cancel()
                    if (it.message == "NetworkError") {
                        FullScreenNetworkErrorActivity.startActivity(this, requireContext())
                    }

                }
                else -> {}
            }
        }.observeInLifecycle(viewLifecycleOwner)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

    private fun setupEarnAvailableAdapter(completed: List<EarnZoneDto.Data.Completed>) {
        earnComletedAdapter=EarnZoneCompletedAdapter(requireContext(),completed,this)

        binding.rvEarnCompleted.apply {
            setHasFixedSize(false)
            adapter = earnComletedAdapter
            isNestedScrollingEnabled = false
        }
    }


    override fun onResume() {
        super.onResume()
//        binding.root.alpha = 0f
//        binding.root.translationY = Utils.dpToPx(requireContext(), 10).toFloat()
        viewModel.getEarnDataList()
        getDataViewModel()

    }

    override fun onStop() {
        super.onStop()
       // loadingDialog.cancel()
    }

    override fun onEarnCompletedClicked(
        position: Int,
        url: String,
        earnComplete: EarnZoneDto.Data.Completed
    ) {

    }
}