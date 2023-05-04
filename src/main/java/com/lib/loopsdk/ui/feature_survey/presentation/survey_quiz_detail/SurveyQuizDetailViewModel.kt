package com.lib.loopsdk.ui.feature_survey.presentation.survey_quiz_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lib.loopsdk.core.util.UIState
import com.lib.loopsdk.data.remote.ApiClient
import com.lib.loopsdk.data.remote.customnetworkadapter.NetworkResponse
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class SurveyQuizDetailViewModel():ViewModel(){

    private val apiService = ApiClient.initRetrofit()

    private val surveyDetailUiStateChannel = Channel<UIState>(Channel.BUFFERED)
    val surveyDetailUiStateFlow = surveyDetailUiStateChannel.receiveAsFlow()

    fun getSurveyDetail(id: String){
        viewModelScope.launch {
            viewModelScope.launch { surveyDetailUiStateChannel.send(UIState.Loading) }
            val response = apiService.getSurveyById(id)
            when (response) {
                is NetworkResponse.Success -> {
                    if (response.body.status.code != 200) {
                        surveyDetailUiStateChannel.send(UIState.Error(response.body.status.message ?: ""))
                    } else {
                        surveyDetailUiStateChannel.send(UIState.Success(response.body.data))
                        Timber.d("Survey detail: ${response.body.data}")
                    }
                }

                is NetworkResponse.ServerError -> {
                    surveyDetailUiStateChannel.send(UIState.Error(response.body?.status?.code.toString()))
                    Timber.d("Survey detail: error: ${response.error}")
                }

                is NetworkResponse.NetworkError -> {
                    surveyDetailUiStateChannel.send(UIState.Error("NetworkError"))
                    Timber.d("Network Error")
                }

                is NetworkResponse.UnknownError -> {
                    surveyDetailUiStateChannel.send(UIState.Error("Unknown Error"))
                    Timber.d("Unknown Error")
                }

            }
        }
    }

    fun getQuizDetail(id: String){
        viewModelScope.launch {
            viewModelScope.launch { surveyDetailUiStateChannel.send(UIState.Loading) }
            val response = apiService.getQuizById(id)
            when (response) {
                is NetworkResponse.Success -> {
                    if (response.body.status.code != 200) {
                        surveyDetailUiStateChannel.send(UIState.Error(response.body.status.message ?: ""))
                    } else {
                        surveyDetailUiStateChannel.send(UIState.Success(response.body.data))
                        Timber.d("Quiz detail: ${response.body.data}")
                    }
                }

                is NetworkResponse.ServerError -> {
                    surveyDetailUiStateChannel.send(UIState.Error(response.body?.status?.code.toString()))
                    Timber.d("Quiz detail: error: ${response.error}")
                }

                is NetworkResponse.NetworkError -> {
                    surveyDetailUiStateChannel.send(UIState.Error("NetworkError"))
                    Timber.d("Network Error")
                }

                is NetworkResponse.UnknownError -> {
                    surveyDetailUiStateChannel.send(UIState.Error("Unknown Error"))
                    Timber.d("Unknown Error ${response.error.message}")
                }

            }
        }
    }

}