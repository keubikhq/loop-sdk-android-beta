package com.lib.loopsdk.ui.feature_tier_details_points_wallet.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lib.loopsdk.core.util.UIState
import com.lib.loopsdk.data.remote.ApiClient
import com.lib.loopsdk.data.remote.customnetworkadapter.NetworkResponse
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class TierDetailsPointsWalletViewModel: ViewModel() {

    private val apiService = ApiClient.initRetrofit()

    private val tierUiStateChannel = Channel<UIState>(Channel.BUFFERED)
    val tierUiStateFlow = tierUiStateChannel.receiveAsFlow()

    private val pointsUiStateChannel = Channel<UIState>(Channel.BUFFERED)
    val pointsUiStateFlow = pointsUiStateChannel.receiveAsFlow()

    private val fragmentToActivityEventChannel = Channel<Event>(Channel.BUFFERED)
    val fragmentToActivityEventFlow = fragmentToActivityEventChannel.receiveAsFlow()

    fun getTierDetails(){
        viewModelScope.launch {
            viewModelScope.launch { tierUiStateChannel.send(UIState.Loading) }
            val response = apiService.getTierDetails()
            when (response) {
                is NetworkResponse.Success -> {
                    if (response.body.status.code != 200) {
                        tierUiStateChannel.send(UIState.Error(response.body.status.message ?: ""))
                        fragmentToActivityEventChannel.send(Event.OnCurrentBalanceReceived(""))
                    } else {
                        tierUiStateChannel.send(UIState.Success(response.body.data))
                        fragmentToActivityEventChannel.send(Event.OnCurrentBalanceReceived(response.body.data.userDetails.pointsBalance))
                    }
                }
                is NetworkResponse.ServerError -> {
                    tierUiStateChannel.send(UIState.ErrorWithData(response.body?.status))
                    fragmentToActivityEventChannel.send(Event.OnCurrentBalanceReceived(""))
                }

                is NetworkResponse.NetworkError -> {
                    tierUiStateChannel.send(UIState.Error("NetworkError"))
                    fragmentToActivityEventChannel.send(Event.OnCurrentBalanceReceived(""))
                }

                is NetworkResponse.UnknownError -> {
                    tierUiStateChannel.send(UIState.Error("UnknownError"))
                    fragmentToActivityEventChannel.send(Event.OnCurrentBalanceReceived(""))
                }

            }
        }
    }

    fun getPointsHistory(pageNo: Int){
        viewModelScope.launch {
            viewModelScope.launch { pointsUiStateChannel.send(UIState.Loading) }
            val response = apiService.getPointsHistory(pageNo.toString())
            when (response) {
                is NetworkResponse.Success -> {
                    if (response.body.status.code != 200) {
                        pointsUiStateChannel.send(UIState.Error(response.body.status.message ?: ""))
                        fragmentToActivityEventChannel.send(Event.OnCurrentBalanceReceived(""))
                    } else {
                        pointsUiStateChannel.send(UIState.Success(response.body.data))
                        fragmentToActivityEventChannel.send(Event.OnCurrentBalanceReceived(response.body.data.totalPoints))
                    }
                }
                is NetworkResponse.ServerError -> {
                    pointsUiStateChannel.send(UIState.ErrorWithData(response.body?.status))
                    fragmentToActivityEventChannel.send(Event.OnCurrentBalanceReceived(""))
                }

                is NetworkResponse.NetworkError -> {
                    pointsUiStateChannel.send(UIState.Error("NetworkError"))
                    fragmentToActivityEventChannel.send(Event.OnCurrentBalanceReceived(""))
                }

                is NetworkResponse.UnknownError -> {
                    pointsUiStateChannel.send(UIState.Error("UnknownError"))
                    fragmentToActivityEventChannel.send(Event.OnCurrentBalanceReceived(""))
                }

            }
        }
    }

    sealed class Event {
        data class OnCurrentBalanceReceived(
            val currentBalance: String
        ): Event()

    }

}