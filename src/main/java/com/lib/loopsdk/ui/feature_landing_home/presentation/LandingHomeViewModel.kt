package com.lib.loopsdk.ui.feature_landing_home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lib.loopsdk.core.util.Prefs
import com.lib.loopsdk.core.util.UIState
import com.lib.loopsdk.data.remote.ApiClient
import com.lib.loopsdk.data.remote.customnetworkadapter.NetworkResponse
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class LandingHomeViewModel: ViewModel() {

    private val apiService = ApiClient.initRetrofit()

    private val uiStateChannel = Channel<UIState>(Channel.BUFFERED)
    val uiStateFlow = uiStateChannel.receiveAsFlow()

    private val referEarnBsStateChannel = Channel<UIState>(Channel.BUFFERED)
    val referEarnBsStateFlow = referEarnBsStateChannel.receiveAsFlow()

    private val unlockingCouponUiStateChannel = Channel<UIState>(Channel.BUFFERED)
    val unlockingCouponUiStateFlow = unlockingCouponUiStateChannel.receiveAsFlow()


    private val earnZoneUiStateChannel = Channel<UIState>(Channel.BUFFERED)
    val earnZoneUiStateFlow = earnZoneUiStateChannel.receiveAsFlow()

    init {
        getCountryCodes()
    }
    fun getInviteEarnData(){
        viewModelScope.launch {
            viewModelScope.launch { referEarnBsStateChannel.send(UIState.Loading) }
            val response = apiService.getInviteAndEarnData()
            when (response) {
                is NetworkResponse.Success -> {
                    if (response.body.status.code != 200) {
                        referEarnBsStateChannel.send(UIState.Error(response.body.status.message ?: ""))
                    } else {
                        referEarnBsStateChannel.send(UIState.Success(response.body.data))
                    }
                }

                is NetworkResponse.ServerError -> {
                    referEarnBsStateChannel.send(UIState.Error(response.body?.status?.message ?: ""))
                }

                is NetworkResponse.NetworkError -> {
                    referEarnBsStateChannel.send(UIState.Error("NetworkError"))
                }

                is NetworkResponse.UnknownError -> {
                    referEarnBsStateChannel.send(UIState.Error("UnknownError"))
                }

            }
        }

    }

    fun getPointsAndTier(){
        viewModelScope.launch {
            viewModelScope.launch { uiStateChannel.send(UIState.Loading) }
            val response = apiService.getWalletPoints()
            when (response) {
                is NetworkResponse.Success -> {
                    if (response.body.status.code != 200) {
                        uiStateChannel.send(UIState.Error(response.body.status.message ?: ""))
                    } else {
                        uiStateChannel.send(UIState.Success(response.body.data))
                    }
                }

                is NetworkResponse.ServerError -> {
                    uiStateChannel.send(UIState.Error(response.body?.status?.message ?: ""))
                }

                is NetworkResponse.NetworkError -> {
                    uiStateChannel.send(UIState.Error("NetworkError"))
                }

                is NetworkResponse.UnknownError -> {
                    uiStateChannel.send(UIState.Error("UnknownError"))
                }

            }
        }

    }

    fun getHomeScreenData(){
        viewModelScope.launch {
            viewModelScope.launch { uiStateChannel.send(UIState.Loading) }
            val response = apiService.getHomeScreenDetails()
            when (response) {
                is NetworkResponse.Success -> {
                    if (response.body.status.code != 200) {
                        uiStateChannel.send(UIState.Error(response.body.status.message ?: ""))
                    } else {
                        uiStateChannel.send(UIState.Success(response.body.data))
                    }
                }

                is NetworkResponse.ServerError -> {
                    uiStateChannel.send(UIState.Error(response.body?.status?.message ?: ""))
                }

                is NetworkResponse.NetworkError -> {
                    uiStateChannel.send(UIState.Error("NetworkError"))
                }

                is NetworkResponse.UnknownError -> {
                    uiStateChannel.send(UIState.Error("UnknownError"))
                }

            }
        }

    }

    fun getCountryCodes(){
        viewModelScope.launch {
            unlockingCouponUiStateChannel.send(UIState.Loading)
            val response =apiService.getCountryCodes()
            when (response){
                is NetworkResponse.Success ->{
                    if(response.body.status.code != 200){
                        unlockingCouponUiStateChannel.send(UIState.Error(response.body.status.message))
                    }else{
                        Prefs.putNonPrimitiveData("COUNTRY_CODE_LIST", response.body.data.countryCodes)
                    }
                }
                is NetworkResponse.NetworkError -> {
                    unlockingCouponUiStateChannel.send(UIState.Error("NetworkError"))
                }
                is NetworkResponse.ServerError -> {
                    unlockingCouponUiStateChannel.send(UIState.Error("ServerError"))
                }
                is NetworkResponse.UnknownError -> {
                    unlockingCouponUiStateChannel.send(UIState.Error("UnknownError"))
                }
            }
        }
    }
    fun unlockCoupon(couponId:String) {
        viewModelScope.launch {
            viewModelScope.launch { unlockingCouponUiStateChannel.send(UIState.Loading) }
            val response = apiService.unlockCoupon(couponId)
            when (response) {
                is NetworkResponse.Success -> {
                    if (response.body.status.code != 200) {
                        unlockingCouponUiStateChannel.send(UIState.Error(response.body.status.message ?: ""))
                    } else {
                        unlockingCouponUiStateChannel.send(UIState.Success(response.body.data))
                    }
                }

                is NetworkResponse.ServerError -> {
                    unlockingCouponUiStateChannel.send(UIState.Error(response.body?.status?.message ?: ""))
                    Timber.d("Server Error")
                }

                is NetworkResponse.NetworkError -> {
                    unlockingCouponUiStateChannel.send(UIState.Error("NetworkError"))
                    Timber.d("Network Error")
                }

                is NetworkResponse.UnknownError -> {
                    unlockingCouponUiStateChannel.send(UIState.Error("Unknown Error"))
                    Timber.d("Unknown Error")
                }

            }
        }
    }

    fun redeemCoupon(couponTransactionId:String) {
        viewModelScope.launch {
            viewModelScope.launch { unlockingCouponUiStateChannel.send(UIState.Loading) }
            val response = apiService.redeemCoupon(couponTransactionId)
            when (response) {
                is NetworkResponse.Success -> {
                    if (response.body.status.code != 200) {
                        unlockingCouponUiStateChannel.send(UIState.Error(response.body.status.message ?: ""))
                    } else {
                        unlockingCouponUiStateChannel.send(UIState.Success(response.body.data))
                    }
                }

                is NetworkResponse.ServerError -> {
                    unlockingCouponUiStateChannel.send(UIState.Error(response.body?.status?.message ?: ""))
                    Timber.d("Server Error")
                }

                is NetworkResponse.NetworkError -> {
                    unlockingCouponUiStateChannel.send(UIState.Error("NetworkError"))
                    Timber.d("Network Error")
                }

                is NetworkResponse.UnknownError -> {
                    unlockingCouponUiStateChannel.send(UIState.Error("Unknown Error"))
                    Timber.d("Unknown Error")
                }

            }
        }
    }


    fun transferCouponEmail(couponTransactionId:String,email:String) {
        viewModelScope.launch {
            viewModelScope.launch { unlockingCouponUiStateChannel.send(UIState.Loading) }
            val response = apiService.transferCouponViaEmail(couponTransactionId,email)
            when (response) {
                is NetworkResponse.Success -> {
                    if (response.body.status.code != 200) {
                        unlockingCouponUiStateChannel.send(UIState.Error(response.body.status.message ?: ""))
                    } else {
                        unlockingCouponUiStateChannel.send(UIState.Success(response.body.data))
                    }
                }

                is NetworkResponse.ServerError -> {
                    unlockingCouponUiStateChannel.send(UIState.Error(response.body?.status?.message ?: ""))
                    Timber.d("Server Error")
                }

                is NetworkResponse.NetworkError -> {
                    unlockingCouponUiStateChannel.send(UIState.Error("NetworkError"))
                    Timber.d("Network Error")
                }

                is NetworkResponse.UnknownError -> {
                    unlockingCouponUiStateChannel.send(UIState.Error("Unknown Error"))
                    Timber.d("Unknown Error")
                }

            }
        }
    }

    fun transferCouponMobile(mobile:String,countryCode:String,couponTransactionId:String) {
        viewModelScope.launch {
            viewModelScope.launch { unlockingCouponUiStateChannel.send(UIState.Loading) }
            val response = apiService.transferCouponViaMobile(couponTransactionId,mobile,countryCode)
            when (response) {
                is NetworkResponse.Success -> {
                    if (response.body.status.code != 200) {
                        unlockingCouponUiStateChannel.send(UIState.Error(response.body.status.message ?: ""))
                    } else {
                        unlockingCouponUiStateChannel.send(UIState.Success(response.body.data))
                    }
                }

                is NetworkResponse.ServerError -> {
                    unlockingCouponUiStateChannel.send(UIState.Error(response.body?.status?.message ?: ""))
                    Timber.d("Server Error")
                }

                is NetworkResponse.NetworkError -> {
                    unlockingCouponUiStateChannel.send(UIState.Error("NetworkError"))
                    Timber.d("Network Error")
                }

                is NetworkResponse.UnknownError -> {
                    unlockingCouponUiStateChannel.send(UIState.Error("Unknown Error"))
                    Timber.d("Unknown Error")
                }

            }
        }
    }


    fun earnToRedeemPointViaSocial(type:Int){
        viewModelScope.launch {
            viewModelScope.launch { earnZoneUiStateChannel.send(UIState.Loading) }
            val response = apiService.earnZoneViaSocial(type)
            when (response) {
                is NetworkResponse.Success -> {
                    if (response.body.status.code != 200) {
                        earnZoneUiStateChannel.send(UIState.Error(response.body.status.message ?: ""))
                    } else {
                        earnZoneUiStateChannel.send(UIState.Success(response.body.data))
                    }
                }

                is NetworkResponse.ServerError -> {
                    earnZoneUiStateChannel.send(UIState.Error(response.body?.status?.message.toString()))
                }
                is NetworkResponse.NetworkError -> {
                    earnZoneUiStateChannel.send(UIState.Error("NetworkError"))
                }
                is NetworkResponse.UnknownError -> {
                    earnZoneUiStateChannel.send(UIState.Error("Unknown Error"))
                }

            }
        }
    }

    fun earnToRedeemPointViaBirthDay(type:Int,date:String){
        viewModelScope.launch {
            viewModelScope.launch { earnZoneUiStateChannel.send(UIState.Loading) }
            val response = apiService.earnZoneViaBirthDay(type,date)
            when (response) {
                is NetworkResponse.Success -> {
                    if (response.body.status.code != 200) {
                        earnZoneUiStateChannel.send(UIState.Error(response.body.status.message ?: ""))
                    } else {
                        earnZoneUiStateChannel.send(UIState.Success(response.body.data))
                    }
                }

                is NetworkResponse.ServerError -> {
                    earnZoneUiStateChannel.send(UIState.Error(response.body?.status?.message.toString()))
                }
                is NetworkResponse.NetworkError -> {
                    earnZoneUiStateChannel.send(UIState.Error("NetworkError"))
                }
                is NetworkResponse.UnknownError -> {
                    earnZoneUiStateChannel.send(UIState.Error("Unknown Error"))
                }

            }
        }
    }

    fun earnToRedeemPointViaAnni(type:Int,date:String){
        viewModelScope.launch {
            viewModelScope.launch { earnZoneUiStateChannel.send(UIState.Loading) }
            val response = apiService.earnZoneViaAnniversary(type,date)
            when (response) {
                is NetworkResponse.Success -> {
                    if (response.body.status.code != 200) {
                        earnZoneUiStateChannel.send(UIState.Error(response.body.status.message ?: ""))
                    } else {
                        earnZoneUiStateChannel.send(UIState.Success(response.body.data))
                    }
                }

                is NetworkResponse.ServerError -> {
                    earnZoneUiStateChannel.send(UIState.Error(response.body?.status?.message.toString()))
                }
                is NetworkResponse.NetworkError -> {
                    earnZoneUiStateChannel.send(UIState.Error("NetworkError"))
                }
                is NetworkResponse.UnknownError -> {
                    earnZoneUiStateChannel.send(UIState.Error("Unknown Error"))
                }

            }
        }
    }
}