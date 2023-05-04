package com.lib.loopsdk.ui.feature_earn.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lib.loopsdk.core.util.UIState
import com.lib.loopsdk.data.remote.ApiClient
import com.lib.loopsdk.data.remote.customnetworkadapter.NetworkResponse
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class EarnListViewModel() : ViewModel() {

    private val apiService = ApiClient.initRetrofit()

    private val earnUiStateChannel = Channel<UIState>(Channel.BUFFERED)
    val earnUiStateFlow = earnUiStateChannel.receiveAsFlow()

    private val fragmentToActivityEventChannel = Channel<Event>(Channel.BUFFERED)
    val fragmentToActivityEventFlow = fragmentToActivityEventChannel.receiveAsFlow()

    fun getEarnDataList(){
        viewModelScope.launch {
            viewModelScope.launch { earnUiStateChannel.send(UIState.Loading) }
            val response = apiService.getEarnZone()
            when (response) {
                is NetworkResponse.Success -> {
                    if (response.body.status.code != 200) {
                        earnUiStateChannel.send(UIState.Error(response.body.status.message ?: ""))
                    } else {
                        earnUiStateChannel.send(UIState.Success(response.body.data))
                        fragmentToActivityEventChannel.send(Event.OnPointsReceived(response.body.data.totalPoints))
                        Timber.d("earn list: ${response.body.data}")
                    }
                }

                is NetworkResponse.ServerError -> {
                    earnUiStateChannel.send(UIState.Error(response.body?.status?.code.toString()))
                    Timber.d("earn task error: ${response.error}")
                }

                is NetworkResponse.NetworkError -> {
                    earnUiStateChannel.send(UIState.Error("NetworkError"))
                    Timber.d("Network Error")
                }

                is NetworkResponse.UnknownError -> {
                    earnUiStateChannel.send(UIState.Error("Unknown Error"))
                    Timber.d("Unknown Error")
                }

            }
        }
    }
    fun earnToRedeemPointViaSocial(type:Int){
        viewModelScope.launch {
            viewModelScope.launch { earnUiStateChannel.send(UIState.Loading) }
            val response = apiService.earnZoneViaSocial(type)
            when (response) {
                is NetworkResponse.Success -> {
                    if (response.body.status.code != 200) {
                        earnUiStateChannel.send(UIState.Error(response.body.status.message ?: ""))
                    } else {
                        earnUiStateChannel.send(UIState.Success(response.body.data))
                    }
                }

                is NetworkResponse.ServerError -> {
                    earnUiStateChannel.send(UIState.Error(response.body?.status?.message.toString()))
                }
                is NetworkResponse.NetworkError -> {
                    earnUiStateChannel.send(UIState.Error("NetworkError"))
                }
                is NetworkResponse.UnknownError -> {
                    earnUiStateChannel.send(UIState.Error("Unknown Error"))
                }

            }
        }
    }

    fun earnToRedeemPointViaBirthDay(type:Int,date:String){
        viewModelScope.launch {
            viewModelScope.launch { earnUiStateChannel.send(UIState.Loading) }
            val response = apiService.earnZoneViaBirthDay(type,date)
            when (response) {
                is NetworkResponse.Success -> {
                    if (response.body.status.code != 200) {
                        earnUiStateChannel.send(UIState.Error(response.body.status.message ?: ""))
                    } else {
                        earnUiStateChannel.send(UIState.Success(response.body.data))
                    }
                }

                is NetworkResponse.ServerError -> {
                    earnUiStateChannel.send(UIState.Error(response.body?.status?.message.toString()))
                }
                is NetworkResponse.NetworkError -> {
                    earnUiStateChannel.send(UIState.Error("NetworkError"))
                }
                is NetworkResponse.UnknownError -> {
                    earnUiStateChannel.send(UIState.Error("Unknown Error"))
                }

            }
        }
    }

    fun earnToRedeemPointViaAnni(type:Int,date:String){
        viewModelScope.launch {
            viewModelScope.launch { earnUiStateChannel.send(UIState.Loading) }
            val response = apiService.earnZoneViaAnniversary(type,date)
            when (response) {
                is NetworkResponse.Success -> {
                    if (response.body.status.code != 200) {
                        earnUiStateChannel.send(UIState.Error(response.body.status.message ?: ""))
                    } else {
                        earnUiStateChannel.send(UIState.Success(response.body.data))
                    }
                }

                is NetworkResponse.ServerError -> {
                    earnUiStateChannel.send(UIState.Error(response.body?.status?.message.toString()))
                }
                is NetworkResponse.NetworkError -> {
                    earnUiStateChannel.send(UIState.Error("NetworkError"))
                }
                is NetworkResponse.UnknownError -> {
                    earnUiStateChannel.send(UIState.Error("Unknown Error"))
                }

            }
        }
    }

    sealed class Event {
        data class OnPointsReceived(
            val points: Float
        ): Event()

    }

}