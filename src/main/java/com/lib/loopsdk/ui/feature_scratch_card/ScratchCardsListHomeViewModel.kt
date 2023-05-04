package com.lib.loopsdk.ui.feature_scratch_card

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lib.loopsdk.core.util.UIState
import com.lib.loopsdk.data.remote.ApiClient
import com.lib.loopsdk.data.remote.customnetworkadapter.NetworkResponse
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class ScratchCardsListHomeViewModel: ViewModel() {

    private val apiService = ApiClient.initRetrofit()

    private val unlockedScratchCardsListUiStateChannel = Channel<UIState>(Channel.BUFFERED)
    val unlockedScratchCardsListUiStateFlow = unlockedScratchCardsListUiStateChannel.receiveAsFlow()

    private val availableScratchCardsListUiStateChannel = Channel<UIState>(Channel.BUFFERED)
    val availableScratchCardsListUiStateFlow = availableScratchCardsListUiStateChannel.receiveAsFlow()

    private val fragmentToActivityEventChannel = Channel<Event>(Channel.BUFFERED)
    val fragmentToActivityEventFlow = fragmentToActivityEventChannel.receiveAsFlow()


    fun getAllAvailableScratchCardsList(pageNo: Int){
        viewModelScope.launch {
            viewModelScope.launch { availableScratchCardsListUiStateChannel.send(UIState.Loading) }
            val response = apiService.getAllAvailableScratchCards(pageNo.toString())
            when (response) {
                is NetworkResponse.Success -> {
                    if (response.body.status.code != 200) {
                        availableScratchCardsListUiStateChannel.send(UIState.Error(response.body.status.message ?: ""))
                    } else {
                        availableScratchCardsListUiStateChannel.send(UIState.Success(response.body.data))
                        fragmentToActivityEventChannel.send(Event.OnPointsReceived(response.body.data.totalPoints))
                    }
                }
                is NetworkResponse.ServerError -> {
                    availableScratchCardsListUiStateChannel.send(UIState.ErrorWithData(response.body?.status))
                }

                is NetworkResponse.NetworkError -> {
                    availableScratchCardsListUiStateChannel.send(UIState.Error("NetworkError"))
                }

                is NetworkResponse.UnknownError -> {
                    availableScratchCardsListUiStateChannel.send(UIState.Error("UnknownError"))
                }

            }
        }
    }

    fun getAllUnlockedScratchCardsList(pageNo: Int){
        viewModelScope.launch {
            viewModelScope.launch { unlockedScratchCardsListUiStateChannel.send(UIState.Loading) }
            val response = apiService.getAllUnlockedScratchCards(pageNo.toString())
            when (response) {
                is NetworkResponse.Success -> {
                    if (response.body.status.code != 200) {
                        unlockedScratchCardsListUiStateChannel.send(UIState.Error(response.body.status.message ?: ""))
                    } else {
                        unlockedScratchCardsListUiStateChannel.send(UIState.Success(response.body.data))
                    }
                }
                is NetworkResponse.ServerError -> {
                    unlockedScratchCardsListUiStateChannel.send(UIState.ErrorWithData(response.body?.status))
                }

                is NetworkResponse.NetworkError -> {
                    unlockedScratchCardsListUiStateChannel.send(UIState.Error("NetworkError"))
                }

                is NetworkResponse.UnknownError -> {
                    unlockedScratchCardsListUiStateChannel.send(UIState.Error("UnknownError"))
                }

            }
        }
    }

    sealed class Event {
        data class OnPointsReceived(
            val points: String
        ): Event()

    }

}