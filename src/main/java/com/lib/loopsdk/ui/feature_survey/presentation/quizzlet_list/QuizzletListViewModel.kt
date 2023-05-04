package com.lib.loopsdk.ui.feature_survey.presentation.quizzlet_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lib.loopsdk.core.util.UIState
import com.lib.loopsdk.data.remote.ApiClient
import com.lib.loopsdk.data.remote.customnetworkadapter.NetworkResponse
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class QuizzletListViewModel(): ViewModel(){

    private val apiService = ApiClient.initRetrofit()

    private val userUiStateChannel = Channel<UIState>(Channel.BUFFERED)
    val userUiStateFlow = userUiStateChannel.receiveAsFlow()

    private val surveyUiStateChannel = Channel<UIState>(Channel.BUFFERED)
    val surveyUiStateFlow = surveyUiStateChannel.receiveAsFlow()

    private val quizUiStateChannel = Channel<UIState>(Channel.BUFFERED)
    val quizUiStateFlow = quizUiStateChannel.receiveAsFlow()

    fun getAvailableSurveyList(){
        viewModelScope.launch {
            viewModelScope.launch { surveyUiStateChannel.send(UIState.Loading) }
            val response = apiService.getAvailableSurvey()
            when (response) {
                is NetworkResponse.Success -> {
                    if (response.body.status.code != 200) {
                        surveyUiStateChannel.send(UIState.Error(response.body.status.message ?: ""))
                    } else {
                        surveyUiStateChannel.send(UIState.Success(response.body.data))
                        Timber.d("Survey list: ${response.body.data.survey}")
                    }
                }

                is NetworkResponse.ServerError -> {
                    surveyUiStateChannel.send(UIState.Error(response.body?.status?.code.toString()))
                    Timber.d("Survey task error: ${response.error}")
                }

                is NetworkResponse.NetworkError -> {
                    surveyUiStateChannel.send(UIState.Error("NetworkError"))
                    Timber.d("Network Error")
                }

                is NetworkResponse.UnknownError -> {
                    surveyUiStateChannel.send(UIState.Error("Unknown Error"))
                    Timber.d("Unknown Error")
                }

            }
        }
    }

    fun getAvailableQuizList(){
        viewModelScope.launch {
            viewModelScope.launch { quizUiStateChannel.send(UIState.Loading) }
            val response = apiService.getAvailableQuiz()
            when (response) {
                is NetworkResponse.Success -> {
                    if (response.body.status.code != 200) {
                        quizUiStateChannel.send(UIState.Error(response.body.status.message ?: ""))
                    } else {
                        quizUiStateChannel.send(UIState.Success(response.body.data))
                        Timber.d("Quiz list: ${response.body.data.quiz}")
                    }
                }

                is NetworkResponse.ServerError -> {
                    quizUiStateChannel.send(UIState.Error(response.body?.status?.code.toString()))
                    Timber.d("Quiz task error: ${response.error}")
                }

                is NetworkResponse.NetworkError -> {
                    quizUiStateChannel.send(UIState.Error("NetworkError"))
                    Timber.d("Network Error")
                }

                is NetworkResponse.UnknownError -> {
                    quizUiStateChannel.send(UIState.Error("Unknown Error"))
                    Timber.d("Unknown Error")
                }

            }
        }
    }

    fun getPointsAndTier(){
        viewModelScope.launch {
            viewModelScope.launch { userUiStateChannel.send(UIState.Loading) }
            val response = apiService.getWalletPoints()
            when (response) {
                is NetworkResponse.Success -> {
                    if (response.body.status.code != 200) {
                        userUiStateChannel.send(UIState.Error(response.body.status.message ?: ""))
                    } else {
                        Timber.d("Points: ${response.body.data.userDetails.pointsBalance}")
                        userUiStateChannel.send(UIState.Success(response.body.data))
                    }
                }

                is NetworkResponse.ServerError -> {
                    userUiStateChannel.send(UIState.Error(response.body?.status?.message ?: ""))
                    Timber.d("User task error: ${response.error}")
                }

                is NetworkResponse.NetworkError -> {
                    userUiStateChannel.send(UIState.Error("NetworkError"))
                    Timber.d("Network Error")
                }

                is NetworkResponse.UnknownError -> {
                    userUiStateChannel.send(UIState.Error("UnknownError"))
                    Timber.d("Unknown Error: ${response.error.message}")
                }

            }
        }

    }
}