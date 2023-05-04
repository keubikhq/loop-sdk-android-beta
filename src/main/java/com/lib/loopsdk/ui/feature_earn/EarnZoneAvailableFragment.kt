package com.lib.loopsdk.ui.feature_earn

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.gson.reflect.TypeToken
import com.lib.loopsdk.R
import com.lib.loopsdk.core.customViews.LoadingDialog
import com.lib.loopsdk.core.util.*
import com.lib.loopsdk.data.remote.dto.EarnZoneDto
import com.lib.loopsdk.data.remote.dto.EarnZoneRedeemDto
import com.lib.loopsdk.data.remote.dto.EarnZoneTaskData
import com.lib.loopsdk.data.remote.dto.InitializeDto
import com.lib.loopsdk.databinding.FragmentEarnZoneAvailableBinding
import com.lib.loopsdk.ui.EarnZoneTypeBottmSheetFragment
import com.lib.loopsdk.ui.feature_earn.presentation.EarnAvailableAdapter
import com.lib.loopsdk.ui.feature_earn.presentation.EarnListViewModel
import kotlinx.coroutines.flow.onEach
import timber.log.Timber

class EarnZoneAvailableFragment : Fragment(),EarnAvailableAdapter.EarnAvailableListener ,
    EarnZoneTypeBottmSheetFragment.TaskCompleteListener{
    private lateinit var binding: FragmentEarnZoneAvailableBinding
    private val viewModel: EarnListViewModel by activityViewModels()
    private val loadingDialog: LoadingDialog by lazy { LoadingDialog(requireContext()) }
    private lateinit var list:ArrayList<EarnZoneDto.Data.Available>
    private lateinit var earnAdapter: EarnAvailableAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_earn_zone_available, container,false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        val brandTheme = Prefs.getNonPrimitiveData<InitializeDto.Data.BrandTheme>(object: TypeToken<InitializeDto.Data.BrandTheme>(){}.type, "BRAND_THEME")
        binding.brandTheme = brandTheme
        binding.brandThemeColors = Colors
        list= arrayListOf()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }


    private fun getViewModelData() {
        viewModel.earnUiStateFlow.onEach {
            when(it){
                is UIState.Loading -> {
                  //  loadingDialog.show()
                }
                is UIState.Success<*> ->{
                    when(it.data){

                        is EarnZoneDto.Data ->{
                            loadingDialog.cancel()
//                            Handler().postDelayed({
//                                binding.root.animate()
//                                    .translationY(0f)
//                                    .alpha(1f)
//                                    .setDuration(750)
//                            },200)
                            if(it.data.available.isEmpty()){
                                binding.nothingFoundContainer.showView()
                                binding.rvEarnAvailable.hideView()
                            }
                            else
                            {
                                binding.nothingFoundContainer.hideView()
                                binding.rvEarnAvailable.showView()
//                                earnAdapter.clearAllData()
//                                earnAdapter.addData(it.data.available)
//                                list= it.data.available
                                list= it.data.available as ArrayList<EarnZoneDto.Data.Available>
                                setupEarnAvailableAdapter(it.data.available)

                            }
                        }
                        is EarnZoneRedeemDto.Data->{
                            Handler().postDelayed({
                                val fragment = EarnZoneRedeemDialogFragment(it.data.value)
                                fragment.show(childFragmentManager, "TAG")
                            }, 500)
                        }
                    }
                }
                is UIState.Error ->{
                    loadingDialog.cancel()
                    if (!it.message.isNullOrEmpty()){
                        if (it.message.contains("token", true) ||
                            it.message.contains("unauthorized", true) ||
                            it.message.contains("details not found", true)) {
                            CommonDialogUtils.showSingleButtonLogoutDialog(binding.root.context, it.message, null)
                        } else if(it.message == "NetworkError"){
                            FullScreenNetworkErrorActivity.startActivity(
                                this,requireContext()
                            )
                        }
                        else {
                            CommonDialogUtils.showSingleButtonAlertDialog(binding.root.context, it.message)
                        }
                    }
                }
                else -> {}
            }
        }.observeInLifecycle(viewLifecycleOwner)

    }

    private fun setupEarnAvailableAdapter(available: List<EarnZoneDto.Data.Available>) {
       earnAdapter=EarnAvailableAdapter(requireContext(),available,this)
        Timber.d(" earn list data ${available}")
        binding.rvEarnAvailable.apply {
            setHasFixedSize(false)
            adapter = earnAdapter
            isNestedScrollingEnabled = false
        }
    }

    override fun onResume() {

//        binding.root.alpha = 0f
//        binding.root.translationY = Utils.dpToPx(requireContext(), 10).toFloat()
        super.onResume()
        viewModel.getEarnDataList()
        getViewModelData()
    }

    override fun onStop() {
        super.onStop()
        loadingDialog.cancel()
    }

    override fun onTaskCompleteClicked(task: EarnZoneTaskData, date: String?) {
        //call api
        when(task.type){
            Constants.EARN_ZONE_TYPE.BIRTHDAY.type->{
                viewModel.earnToRedeemPointViaBirthDay(task.type,date!!)
            }
            Constants.EARN_ZONE_TYPE.ANNIVERSARY.type->{
                    viewModel.earnToRedeemPointViaAnni(task.type,date!!)
            }
            else->{
                viewModel.earnToRedeemPointViaSocial(task.type)

               when(task.type){
                   Constants.EARN_ZONE_TYPE.YOUTUBE.type -> {

                       task.url.let { Utils.playYouTubeVideo(requireContext(), it) }
                   }

                   Constants.EARN_ZONE_TYPE.FACEBOOK.type -> {
                       task.url.let { Utils.shareTextOnFacebookWeb(requireContext(), it) }

                   }
                   Constants.EARN_ZONE_TYPE.LINKEDIN.type -> {
                       task.url.let { Utils.shareTextOnLinkedinWeb(requireContext(), it) }
                   }
                   Constants.EARN_ZONE_TYPE.TWITTER.type -> {
                       task.url.let { Utils.shareTextOnTwitterWeb(requireContext(), it) }

                   }

                   Constants.EARN_ZONE_TYPE.INSTALIKE.type -> {
                       task.url.let { Utils.shareTextOnInstagramWeb(requireContext(), it) }


                   }
                   Constants.EARN_ZONE_TYPE.INSTAFOLLOW.type -> {
                       task.url.let { Utils.shareTextOnInstagramWeb(requireContext(), it) }

                   }
               }
            }
        }
    }

    override fun onEarnAvailableClicked(position: Int, task: EarnZoneDto.Data.Available) {
        val earnZoneTaskData= EarnZoneTaskData()
        earnZoneTaskData.type=task.type
        if (task.url!=null){
            earnZoneTaskData.url=task.url
        }
        earnZoneTaskData.value=task.value
        val  earnZoneTypeBSFragment =  EarnZoneTypeBottmSheetFragment(earnZoneTaskData,this)
        if(!earnZoneTypeBSFragment.isVisible)
            earnZoneTypeBSFragment.show(childFragmentManager.beginTransaction(), earnZoneTypeBSFragment.getTag())
    }


}