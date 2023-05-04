package com.lib.loopsdk.ui.feature_survey.presentation.dynamic_quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lib.loopsdk.core.util.UIState
import com.lib.loopsdk.data.remote.ApiClient
import com.lib.loopsdk.data.remote.customnetworkadapter.NetworkResponse
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class DynamicQuesAnsViewModel(): ViewModel() {

    private val apiService = ApiClient.initRetrofit()

    private val questionUiStateChannel = Channel<UIState>(Channel.BUFFERED)
    val questionUiStateFlow = questionUiStateChannel.receiveAsFlow()

    private val submitUiStateChannel = Channel<UIState>(Channel.BUFFERED)
    val submitUiStateFlow = submitUiStateChannel.receiveAsFlow()

    private val answerUiStateChannel = Channel<UIState>(Channel.BUFFERED)
    val answerUiStateFlow = answerUiStateChannel.receiveAsFlow()

    fun getQuizQuestions(id: String){
        viewModelScope.launch {
            viewModelScope.launch { questionUiStateChannel.send(UIState.Loading) }
            val response = apiService.getQuizQuestions(id)
            when (response) {
                is NetworkResponse.Success -> {
                    if (response.body.status.code != 200) {
                        questionUiStateChannel.send(UIState.Error(response.body.status.message ?: ""))
                    } else {
                        questionUiStateChannel.send(UIState.Success(response.body.data))
                        Timber.d("Survey questions: ${response.body.data.questions}")
                    }
                }

                is NetworkResponse.ServerError -> {
                    questionUiStateChannel.send(UIState.Error(response.body?.status?.code.toString()))
                    Timber.d("Survey questions: error: ${response.error}")
                }

                is NetworkResponse.NetworkError -> {
                    questionUiStateChannel.send(UIState.Error("NetworkError"))
                    Timber.d("Network Error")
                }

                is NetworkResponse.UnknownError -> {
                    questionUiStateChannel.send(UIState.Error("Unknown Error"))
                    Timber.d("Unknown Error: ${response.error.message}")
                }

            }
        }
    }

    fun submitQuiz(id: String){
        viewModelScope.launch {
            viewModelScope.launch { submitUiStateChannel.send(UIState.Loading) }
            val response = apiService.submitQuiz(id)
            when (response) {
                is NetworkResponse.Success -> {
                    if (response.body.status.code != 200) {
                        submitUiStateChannel.send(UIState.Error(response.body.status.message ?: ""))
                    } else {
                        submitUiStateChannel.send(UIState.Success(response.body.data))
                    }
                }

                is NetworkResponse.ServerError -> {
                    submitUiStateChannel.send(UIState.Error(response.body?.status?.code.toString()))
                    Timber.d("Survey questions: error: ${response.error}")
                }

                is NetworkResponse.NetworkError -> {
                    submitUiStateChannel.send(UIState.Error("NetworkError"))
                    Timber.d("Network Error")
                }

                is NetworkResponse.UnknownError -> {
                    submitUiStateChannel.send(UIState.Error("Unknown Error"))
                    Timber.d("Unknown Error")
                }

            }
        }
    }

    fun postAnswerQuiz(quizId: String, questionId :String, answer:String ) {
        viewModelScope.launch {
            viewModelScope.launch { answerUiStateChannel.send(UIState.Loading) }
            val response = apiService.postQuizAnswer(quizId, questionId, answer, 0)
            when (response) {
                is NetworkResponse.Success -> {
                    if (response.body.status.code != 200) {
                        answerUiStateChannel.send(UIState.Error(response.body.status.message ?: ""))
                        Timber.d("Error msg: ${response.body.status.message}")
                    } else {
                        answerUiStateChannel.send(UIState.Success(response.body.status))
                    }
                }

                is NetworkResponse.ServerError -> {
                    answerUiStateChannel.send(UIState.Error(response.body?.status?.message.toString()))
                    Timber.d("Quiz questions: error: ${response.error}")
                }

                is NetworkResponse.NetworkError -> {
                    answerUiStateChannel.send(UIState.Error("NetworkError"))
                    Timber.d("Network Error")
                }

                is NetworkResponse.UnknownError -> {
                    answerUiStateChannel.send(UIState.Error("Unknown Error"))
                    Timber.d("Unknown Error")
                }

            }
        }
    }
}