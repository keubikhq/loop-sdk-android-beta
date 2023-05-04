package com.lib.loopsdk.ui.feature_spin_the_wheel.spin_wheel_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lib.loopsdk.core.util.UIState
import com.lib.loopsdk.data.remote.ApiClient
import com.lib.loopsdk.data.remote.customnetworkadapter.NetworkResponse
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class SpinWheelDetailViewModel: ViewModel() {

    private val apiService = ApiClient.initRetrofit()

    private val uiStateChannel = Channel<UIState>(Channel.BUFFERED)
    val uiStateFlow = uiStateChannel.receiveAsFlow()

    private val fragmentToActivityEventChannel = Channel<Event>(Channel.BUFFERED)
    val fragmentToActivityEventFlow = fragmentToActivityEventChannel.receiveAsFlow()

    fun getSingleSpinWheelDetails(SpinWheelId: String){
        if (SpinWheelId.isNullOrEmpty()) return
        viewModelScope.launch {
            viewModelScope.launch { uiStateChannel.send(UIState.Loading) }
            val response = apiService.getSingleSpinWheel(SpinWheelId)
            when (response) {
                is NetworkResponse.Success -> {
                    if (response.body.status.code != 200) {
                        uiStateChannel.send(UIState.Error(response.body.status.message ?: ""))
                    } else {
                        uiStateChannel.send(UIState.Success(response.body.data))
                    }
                }
                is NetworkResponse.ServerError -> {
                    uiStateChannel.send(UIState.Error(response.body!!.status.message ?: ""))
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

    fun unlockSpinWheel(SpinWheelId: String){
        if (SpinWheelId.isNullOrEmpty()) return
        viewModelScope.launch {
            viewModelScope.launch { uiStateChannel.send(UIState.Loading) }
            val response = apiService.unlockSpinWheel(SpinWheelId)
            when (response) {
                is NetworkResponse.Success -> {
                    if (response.body.status.code != 200) {
                        uiStateChannel.send(UIState.Error(response.body.status.message ?: ""))
                    } else {
                        uiStateChannel.send(UIState.Success(response.body.data))
                    }
                }
                is NetworkResponse.ServerError -> {
                    uiStateChannel.send(UIState.Error(response.body!!.status.message ?: ""))
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

    fun redeemSpinWheel(SpinWheelTransactionId: String){
        if (SpinWheelTransactionId.isNullOrEmpty()) return
        viewModelScope.launch {
            viewModelScope.launch { uiStateChannel.send(UIState.Loading) }
            val response = apiService.redeemSpinWheel(SpinWheelTransactionId)
            when (response) {
                is NetworkResponse.Success -> {
                    if (response.body.status.code != 200) {
                        uiStateChannel.send(UIState.Error(response.body.status.message ?: ""))
                    } else {
                        uiStateChannel.send(UIState.Success(response.body.data))
                    }
                }
                is NetworkResponse.ServerError -> {
                    uiStateChannel.send(UIState.Error(response.body!!.status.message ?: ""))
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

    fun onClickCloseDialog(){
        viewModelScope.launch {
            fragmentToActivityEventChannel.send(Event.OnBenefitDialogCloseClicked)
        }
    }

    fun onClickTryAgain(){
        viewModelScope.launch {
            fragmentToActivityEventChannel.send(Event.OnTryAgainButtonClicked)
        }
    }

    sealed class Event {
        object OnBenefitDialogCloseClicked: Event()
        object OnTryAgainButtonClicked: Event()
    }

}