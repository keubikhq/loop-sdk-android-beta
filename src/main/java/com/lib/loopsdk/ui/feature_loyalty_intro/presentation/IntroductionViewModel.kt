package com.lib.loopsdk.ui.feature_loyalty_intro.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lib.loopsdk.core.util.Prefs
import com.lib.loopsdk.core.util.UIState
import com.lib.loopsdk.data.remote.ApiClient
import com.lib.loopsdk.data.remote.customnetworkadapter.NetworkResponse
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class IntroductionViewModel: ViewModel() {

    private val apiService = ApiClient.initRetrofit()

    private val referralBsStateChannel = Channel<UIState>(Channel.BUFFERED)
    val referralBsStateFlow = referralBsStateChannel.receiveAsFlow()

    private val uiStateChannel = Channel<UIState>(Channel.BUFFERED)
    val uiStateFlow = uiStateChannel.receiveAsFlow()


    fun joinNow(){
        viewModelScope.launch {
            viewModelScope.launch { uiStateChannel.send(UIState.Loading) }
            val response = apiService.joinNow()
            when (response) {
                is NetworkResponse.Success -> {
                    if (response.body.status.code != 200) {
                        uiStateChannel.send(UIState.ErrorWithData(response.body.status))
                    } else {
                        //TODO: remove the below override
                        Prefs.putInt("HAS_SIGNED_UP", 1)
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

    fun addReferral(
        referralCode: String,
        isLast: Int
    ){
        viewModelScope.launch {
            viewModelScope.launch { referralBsStateChannel.send(UIState.Loading) }
            val response = apiService.addReferral(referralCode, isLast)
            when (response) {
                is NetworkResponse.Success -> {
                    if (response.body.status.code != 200) {
                        referralBsStateChannel.send(UIState.ErrorWithData(response.body.status))
                    } else {
                        referralBsStateChannel.send(UIState.Success(response.body.status.message))
                    }
                }

                is NetworkResponse.ServerError -> {
                    referralBsStateChannel.send(UIState.ErrorWithData(response.body?.status))
                }

                is NetworkResponse.NetworkError -> {
                    referralBsStateChannel.send(UIState.Error("NetworkError"))
                }

                is NetworkResponse.UnknownError -> {
                    referralBsStateChannel.send(UIState.Error("UnknownError"))
                }

            }
        }

    }

}