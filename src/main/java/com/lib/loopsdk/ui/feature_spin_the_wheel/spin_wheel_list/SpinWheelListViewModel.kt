package com.lib.loopsdk.ui.feature_spin_the_wheel.spin_wheel_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lib.loopsdk.core.util.UIState
import com.lib.loopsdk.data.remote.ApiClient
import com.lib.loopsdk.data.remote.customnetworkadapter.NetworkResponse
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class SpinWheelListViewModel():ViewModel() {

    private val apiService = ApiClient.initRetrofit()

    private val unlockedSpinWheelListUiStateChannel = Channel<UIState>(Channel.BUFFERED)
    val unlockedSpinWheelListUiStateFlow = unlockedSpinWheelListUiStateChannel.receiveAsFlow()

    private val availableSpinWheelListUiStateChannel = Channel<UIState>(Channel.BUFFERED)
    val availableSpinWheelListUiStateFlow = availableSpinWheelListUiStateChannel.receiveAsFlow()

    private val fragmentToActivityEventChannel = Channel<Event>(Channel.BUFFERED)
    val fragmentToActivityEventFlow = fragmentToActivityEventChannel.receiveAsFlow()


    fun getAllAvailableSpinWheelList(pageNo: Int){
        viewModelScope.launch {
            viewModelScope.launch { availableSpinWheelListUiStateChannel.send(UIState.Loading) }
            val response = apiService.getAllAvailableSpinWheel(pageNo.toString())
            when (response) {
                is NetworkResponse.Success -> {
                    if (response.body.status.code != 200) {
                        availableSpinWheelListUiStateChannel.send(UIState.Error(response.body.status.message ?: ""))
                    } else {
                        availableSpinWheelListUiStateChannel.send(UIState.Success(response.body.data))
                        fragmentToActivityEventChannel.send(Event.OnPointsReceived(response.body.data.totalPoints))
                    }
                }
                is NetworkResponse.ServerError -> {
                    availableSpinWheelListUiStateChannel.send(UIState.ErrorWithData(response.body?.status))
                }

                is NetworkResponse.NetworkError -> {
                    availableSpinWheelListUiStateChannel.send(UIState.Error("NetworkError"))
                }

                is NetworkResponse.UnknownError -> {
                    availableSpinWheelListUiStateChannel.send(UIState.Error("UnknownError"))
                }

            }
        }
    }

    fun getAllUnlockedSpinWheelList(pageNo: Int){
        viewModelScope.launch {
            viewModelScope.launch { unlockedSpinWheelListUiStateChannel.send(UIState.Loading) }
            val response = apiService.getAllUnlockedSpinWheel(pageNo = pageNo.toString())
            when (response) {
                is NetworkResponse.Success -> {
                    if (response.body.status.code != 200) {
                        unlockedSpinWheelListUiStateChannel.send(UIState.Error(response.body.status.message ?: ""))
                    } else {
                        unlockedSpinWheelListUiStateChannel.send(UIState.Success(response.body.data))
                    }
                }
                is NetworkResponse.ServerError -> {
                    unlockedSpinWheelListUiStateChannel.send(UIState.ErrorWithData(response.body?.status))
                }

                is NetworkResponse.NetworkError -> {
                    unlockedSpinWheelListUiStateChannel.send(UIState.Error("NetworkError"))
                }

                is NetworkResponse.UnknownError -> {
                    unlockedSpinWheelListUiStateChannel.send(UIState.Error("UnknownError"))
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