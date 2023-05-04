package com.lib.loopsdk.ui.feature_survey.presentation.trivia_quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lib.loopsdk.core.util.UIState
import com.lib.loopsdk.data.remote.ApiClient
import com.lib.loopsdk.data.remote.customnetworkadapter.NetworkResponse
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class TriviaQuizViewModel(): ViewModel() {

    private val apiService = ApiClient.initRetrofit()

    private val questionUiStateChannel = Channel<UIState>(Channel.BUFFERED)
    val questionUiStateFlow = questionUiStateChannel.receiveAsFlow()

    private val submitQuizUiStateChannel = Channel<UIState>(Channel.BUFFERED)
    val submitQuizUiStateFlow = submitQuizUiStateChannel.receiveAsFlow()

    private val answerUiStateChannel = Channel<UIState>(Channel.BUFFERED)
    val answerUiStateFlow = answerUiStateChannel.receiveAsFlow()

    private val leaveUiStateChannel = Channel<UIState>(Channel.BUFFERED)
    val leaveUiStateFlow = leaveUiStateChannel.receiveAsFlow()

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
            viewModelScope.launch { submitQuizUiStateChannel.send(UIState.Loading) }
            val response = apiService.submitQuiz(id)
            when (response) {
                is NetworkResponse.Success -> {
                    if (response.body.status.code != 200) {
                        submitQuizUiStateChannel.send(UIState.Error(response.body.status.message ?: ""))
                    } else {
                        submitQuizUiStateChannel.send(UIState.Success(response.body.data))
                        Timber.d("Submit quiz data: ${response.body.data}")
                    }
                }

                is NetworkResponse.ServerError -> {
                    submitQuizUiStateChannel.send(UIState.Error(response.body?.status?.code.toString()))
                    Timber.d("Survey questions: error: ${response.error}")
                }

                is NetworkResponse.NetworkError -> {
                    submitQuizUiStateChannel.send(UIState.Error("NetworkError"))
                    Timber.d("Network Error")
                }

                is NetworkResponse.UnknownError -> {
                    submitQuizUiStateChannel.send(UIState.Error("Unknown Error"))
                    Timber.d("Unknown Error")
                }

            }
        }
    }

    fun postAnswerQuiz(quizId: String, questionId :String, answer:String, isTimeUp: Int ) {
        viewModelScope.launch {
            viewModelScope.launch { answerUiStateChannel.send(UIState.Loading) }
            val response = apiService.postQuizAnswer(quizId, questionId, answer, isTimeUp)
            when (response) {
                is NetworkResponse.Success -> {
                    if (response.body.status.code != 200) {
                        answerUiStateChannel.send(UIState.Error(response.body.status.message ?: ""))
                        Timber.d("Error msg: ${response.body.status.message}")
                    } else {
                        answerUiStateChannel.send(UIState.Success(response.body.data))
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
    fun postLeaveQuiz(quizId: String) {
        viewModelScope.launch {
            viewModelScope.launch { answerUiStateChannel.send(UIState.Loading) }
            val response = apiService.leaveQuiz(quizId)
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