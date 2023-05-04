package com.lib.loopsdk.ui.feature_scratch_card

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lib.loopsdk.core.util.UIState
import com.lib.loopsdk.data.remote.ApiClient
import com.lib.loopsdk.data.remote.customnetworkadapter.NetworkResponse
import com.lib.loopsdk.data.remote.dto.ErrorDto
import com.lib.loopsdk.data.remote.dto.TierDetailsDto
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import retrofit2.http.Field

class ScratchCardLandingViewModel: ViewModel() {

    private val apiService = ApiClient.initRetrofit()

    private val uiStateChannel = Channel<UIState>(Channel.BUFFERED)
    val uiStateFlow = uiStateChannel.receiveAsFlow()

    private val fragmentToActivityEventChannel = Channel<Event>(Channel.BUFFERED)
    val fragmentToActivityEventFlow = fragmentToActivityEventChannel.receiveAsFlow()

    fun getSingleScratchCardDetails(scratchCardId: String, shouldShowLoading: Boolean = false){
        if (scratchCardId.isNullOrEmpty()) return
        viewModelScope.launch {
            viewModelScope.launch {
                if (shouldShowLoading) uiStateChannel.send(UIState.Loading)
            }
            val response = apiService.getSingleScratchCardDetails(scratchCardId)
            when (response) {
                is NetworkResponse.Success -> {
                    if (response.body.status.code != 200) {
                        uiStateChannel.send(UIState.ErrorWithData(
                            ErrorDto.Status(
                                code = response.body.status.code,
                                message = response.body.status.message
                            )))
                    } else {
                        uiStateChannel.send(UIState.Success(response.body.data))
                    }
                }
                is NetworkResponse.ServerError -> {
                    uiStateChannel.send(UIState.ErrorWithData(response.body?.status))
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

    fun unlockScratchCard(scratchCardId: String,shouldShowLoading: Boolean = false){
        if (scratchCardId.isNullOrEmpty()) return
        viewModelScope.launch {
            viewModelScope.launch {
                if (shouldShowLoading) uiStateChannel.send(UIState.Loading)
            }
            val response = apiService.unlockScratchCard(scratchCardId)
            when (response) {
                is NetworkResponse.Success -> {
                    if (response.body.status.code != 200) {
                        uiStateChannel.send(UIState.ErrorWithData(
                            ErrorDto.Status(
                                code = response.body.status.code,
                                message = response.body.status.message
                            )))
                    } else {
                        uiStateChannel.send(UIState.Success(response.body.data))
                    }
                }
                is NetworkResponse.ServerError -> {
                    uiStateChannel.send(UIState.ErrorWithData(response.body?.status))
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

    fun redeemScratchCard(scratchCardTransactionId: String, shouldShowLoading: Boolean = false){
        if (scratchCardTransactionId.isNullOrEmpty()) return
        viewModelScope.launch {
            viewModelScope.launch {
                if (shouldShowLoading) uiStateChannel.send(UIState.Loading)
            }
            val response = apiService.redeemScratchCard(scratchCardTransactionId)
            when (response) {
                is NetworkResponse.Success -> {
                    if (response.body.status.code != 200) {
                        uiStateChannel.send(UIState.ErrorWithData(
                            ErrorDto.Status(
                                code = response.body.status.code,
                                message = response.body.status.message
                            )))
                    } else {
                        uiStateChannel.send(UIState.Success(response.body.data))
                    }
                }
                is NetworkResponse.ServerError -> {
                    uiStateChannel.send(UIState.ErrorWithData(response.body?.status))
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