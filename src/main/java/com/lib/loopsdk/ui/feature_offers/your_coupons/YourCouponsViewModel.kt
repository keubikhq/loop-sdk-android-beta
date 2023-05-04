package com.lib.loopsdk.ui.feature_offers.your_coupons

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lib.loopsdk.core.util.Constants
import com.lib.loopsdk.core.util.Prefs
import com.lib.loopsdk.core.util.UIState
import com.lib.loopsdk.data.remote.ApiClient
import com.lib.loopsdk.data.remote.customnetworkadapter.NetworkResponse
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class YourCouponsViewModel: ViewModel() {

    private val apiService = ApiClient.initRetrofit()

    private val unlockingInHistoryUiStateChannel = Channel<UIState>(Channel.BUFFERED)
    val unlockingInHistoryUiStateFlow = unlockingInHistoryUiStateChannel.receiveAsFlow()


    private val historyUiStateChannel = Channel<UIState>(Channel.BUFFERED)
    val historyUiStateFlow = historyUiStateChannel.receiveAsFlow()


    fun redeemCoupon(couponTransactionId:String) {
        viewModelScope.launch {
            viewModelScope.launch { unlockingInHistoryUiStateChannel.send(UIState.Loading) }
            val response = apiService.redeemCoupon(couponTransactionId)
            when (response) {
                is NetworkResponse.Success -> {
                    if (response.body.status.code != 200) {
                        unlockingInHistoryUiStateChannel.send(UIState.Error(response.body.status.message ?: ""))
                    } else {
                        unlockingInHistoryUiStateChannel.send(UIState.Success(response.body.data))
                    }
                }

                is NetworkResponse.ServerError -> {
                    unlockingInHistoryUiStateChannel.send(UIState.Error(response.body?.status?.message ?: ""))
                    Timber.d("Server Error")
                }

                is NetworkResponse.NetworkError -> {
                    unlockingInHistoryUiStateChannel.send(UIState.Error("NetworkError"))
                    Timber.d("Network Error")
                }

                is NetworkResponse.UnknownError -> {
                    unlockingInHistoryUiStateChannel.send(UIState.Error("Unknown Error"))
                    Timber.d("Unknown Error")
                }

            }
        }
    }


    fun transferCouponEmail(couponTransactionId:String,email:String) {

    viewModelScope.launch {
            viewModelScope.launch { unlockingInHistoryUiStateChannel.send(UIState.Loading) }
            val response = apiService.transferCouponViaEmail(couponTransactionId,email)
            when (response) {
                is NetworkResponse.Success -> {
                    if (response.body.status.code != 200) {
                        unlockingInHistoryUiStateChannel.send(UIState.Error(response.body.status.message ?: ""))
                    } else {
                        unlockingInHistoryUiStateChannel.send(UIState.Success(response.body.data))
                    }
                }

                is NetworkResponse.ServerError -> {
                    unlockingInHistoryUiStateChannel.send(UIState.Error(response.body?.status?.message ?: ""))
                    Timber.d("Server Error")
                }

                is NetworkResponse.NetworkError -> {
                    unlockingInHistoryUiStateChannel.send(UIState.Error("NetworkError"))
                    Timber.d("Network Error")
                }

                is NetworkResponse.UnknownError -> {
                    unlockingInHistoryUiStateChannel.send(UIState.Error("Unknown Error"))
                    Timber.d("Unknown Error")
                }

            }
        }
    }

        fun transferCouponMobile(mobile:String,countryCode:String,couponTransactionId:String) {
        viewModelScope.launch {
            viewModelScope.launch { unlockingInHistoryUiStateChannel.send(UIState.Loading) }
            val response = apiService.transferCouponViaMobile(couponTransactionId,mobile,countryCode)
            when (response) {
                is NetworkResponse.Success -> {
                    if (response.body.status.code != 200) {
                        unlockingInHistoryUiStateChannel.send(UIState.Error(response.body.status.message ?: ""))
                    } else {
                        unlockingInHistoryUiStateChannel.send(UIState.Success(response.body.data))
                    }
                }

                is NetworkResponse.ServerError -> {
                    unlockingInHistoryUiStateChannel.send(UIState.Error(response.body?.status?.message ?: ""))
                    Timber.d("Server Error")
                }

                is NetworkResponse.NetworkError -> {
                    unlockingInHistoryUiStateChannel.send(UIState.Error("NetworkError"))
                    Timber.d("Network Error")
                }

                is NetworkResponse.UnknownError -> {
                    unlockingInHistoryUiStateChannel.send(UIState.Error("Unknown Error"))
                    Timber.d("Unknown Error")
                }

            }
        }
    }


    fun getCouponUnlockedList(
        pageNo: Int,
        searchParam:String
    ) {
        val hashMap = HashMap<String, String>()
        hashMap["pageNo"] = pageNo.toString()
        hashMap["pageSize"] = Constants.IDEAL_PAGE_SIZE.toString()
        val sortType=Prefs.getString(Constants.COUPON_SORT_TYPE)?:""
        val tabSelected=Prefs.getString(Constants.COUPON_TYPE_SELECTED)?:""
        if(sortType.isNotEmpty()){
            if(sortType.equals(Constants.COUPONSORTType.EXPIRED.title)){
                hashMap["isExpired"] = "1"
            }else if(sortType.equals(Constants.COUPONSORTType.EXPIRING_SOON.title)){
                hashMap["isExpiringSoon"] = "1"
            }else if(sortType.equals(Constants.COUPONSORTType.CODE_VIEWED.title)){
                hashMap["isViewed"] = "1"
            }else if(sortType.equals(tabSelected)){
                hashMap["isUnlocked"] = "1"
            }
        }
        if(!searchParam.isNullOrEmpty()){
            hashMap["searchParams"] = searchParam
        }
        viewModelScope.launch {
            viewModelScope.launch { historyUiStateChannel.send(UIState.Loading) }
            val response = apiService.getUnlockedList(hashMap)
            when (response) {
                is NetworkResponse.Success -> {
                    if (response.body.status.code != 200) {
                        historyUiStateChannel.send(UIState.Error(response.body.status.message ?: ""))
                    } else {
                        historyUiStateChannel.send(UIState.Success(response.body.data))
                    }
                }

                is NetworkResponse.ServerError -> {
                    historyUiStateChannel.send(UIState.Error(response.body?.status?.message ?: ""))
                    Timber.d("Server Error")
                }

                is NetworkResponse.NetworkError -> {
                    historyUiStateChannel.send(UIState.Error("NetworkError"))
                    Timber.d("Network Error")
                }

                is NetworkResponse.UnknownError -> {
                    historyUiStateChannel.send(UIState.Error("Unknown Error"))
                    Timber.d("Unknown Error")
                }

            }
        }
    }

    fun getCouponRedeemedList(
        pageNo: Int,
        searchParam: String
    ) {
        val hashMap = HashMap<String, String>()
        hashMap["pageNo"] = pageNo.toString()
        hashMap["pageSize"] =Constants.IDEAL_PAGE_SIZE.toString()
        val sortType=Prefs.getString(Constants.COUPON_SORT_TYPE)?:""
        val tabSelected=Prefs.getString(Constants.COUPON_TYPE_SELECTED)?:""
        if(sortType.isNotEmpty()){
            if(sortType.equals(Constants.COUPONSORTType.EXPIRED.title)){
                hashMap["isExpired"] = "1"
            }else if(sortType.equals(Constants.COUPONSORTType.EXPIRING_SOON.title)){
                hashMap["isExpiringSoon"] = "1"
            }else  if(sortType.equals(tabSelected)){
                hashMap["sort"] = "asc"
            }
        }
        if(!searchParam.isNullOrEmpty()){
            hashMap["searchParams"] = searchParam
        }

        viewModelScope.launch {
            viewModelScope.launch { historyUiStateChannel.send(UIState.Loading) }
            val response = apiService.getRedeemedList(hashMap)
            when (response) {
                is NetworkResponse.Success -> {
                    if (response.body.status.code != 200) {
                        historyUiStateChannel.send(UIState.Error(response.body.status.message ?: ""))
                    } else {
                        historyUiStateChannel.send(UIState.Success(response.body.data))
                    }
                }

                is NetworkResponse.ServerError -> {
                    historyUiStateChannel.send(UIState.Error(response.body?.status?.message ?: ""))
                    Timber.d("Server Error")
                }

                is NetworkResponse.NetworkError -> {
                    historyUiStateChannel.send(UIState.Error("NetworkError"))
                    Timber.d("Network Error")
                }

                is NetworkResponse.UnknownError -> {
                    historyUiStateChannel.send(UIState.Error("Unknown Error"))
                    Timber.d("Unknown Error")
                }

            }
        }
    }

    fun getCouponGiftedList(
        pageNo: Int,
        searchParam: String
    ) {
        val hashMap = HashMap<String, String>()
        hashMap["pageNo"] = pageNo.toString()
        hashMap["pageSize"] =Constants.IDEAL_PAGE_SIZE.toString()
        val sortType=Prefs.getString(Constants.COUPON_SORT_TYPE)?:""
        val tabSelected=Prefs.getString(Constants.COUPON_TYPE_SELECTED)?:""
        if(sortType.isNotEmpty()){
            if(sortType.equals(Constants.COUPONSORTType.EXPIRED.title)){
                hashMap["isExpired"] = "1"
            }else if(sortType.equals(Constants.COUPONSORTType.EXPIRING_SOON.title)){
                hashMap["isExpiringSoon"] = "1"
            }else  if(sortType.equals(tabSelected)){
                hashMap["sort"] = "asc"
            }
        }
        if(!searchParam.isNullOrEmpty()){
            hashMap["searchParams"] = searchParam
        }
        viewModelScope.launch {
            viewModelScope.launch { historyUiStateChannel.send(UIState.Loading) }
            val response = apiService.getGiftedList(hashMap)
            when (response) {
                is NetworkResponse.Success -> {
                    if (response.body.status.code != 200) {
                        historyUiStateChannel.send(UIState.Error(response.body.status.message ?: ""))
                    } else {
                        historyUiStateChannel.send(UIState.Success(response.body.data))
                    }
                }

                is NetworkResponse.ServerError -> {
                    historyUiStateChannel.send(UIState.Error(response.body?.status?.message ?: ""))
                    Timber.d("Server Error")
                }

                is NetworkResponse.NetworkError -> {
                    historyUiStateChannel.send(UIState.Error("NetworkError"))
                    Timber.d("Network Error")
                }

                is NetworkResponse.UnknownError -> {
                    historyUiStateChannel.send(UIState.Error("Unknown Error"))
                    Timber.d("Unknown Error")
                }

            }
        }
    }


}