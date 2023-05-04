package com.lib.loopsdk.ui.feature_survey.presentation.survey_questions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lib.loopsdk.core.util.UIState
import com.lib.loopsdk.data.remote.ApiClient
import com.lib.loopsdk.data.remote.ApiService
import com.lib.loopsdk.data.remote.customnetworkadapter.NetworkResponse
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import timber.log.Timber


class SurveyQuesAnsViewModel(): ViewModel(){

    private val apiService = ApiClient.initRetrofit()

    private val questionUiStateChannel = Channel<UIState>(Channel.BUFFERED)
    val questionUiStateFlow = questionUiStateChannel.receiveAsFlow()

    private val submitUiStateChannel = Channel<UIState>(Channel.BUFFERED)
    val submitUiStateFlow = submitUiStateChannel.receiveAsFlow()

    private val answerStringUiStateChannel = Channel<UIState>(Channel.BUFFERED)
    val answerStringUiStateFlow = answerStringUiStateChannel.receiveAsFlow()

    private val answerIntUiStateChannel = Channel<UIState>(Channel.BUFFERED)
    val answerIntUiStateFlow = answerIntUiStateChannel.receiveAsFlow()

    private val answerArrayUiStateChannel = Channel<UIState>(Channel.BUFFERED)
    val answerArrayUiStateFlow = answerArrayUiStateChannel.receiveAsFlow()


    fun getSurveyQuestions(id: String){
        viewModelScope.launch {
            viewModelScope.launch { questionUiStateChannel.send(UIState.Loading) }
            val response = apiService.getQuestions(id)
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

    fun submitSurvey(id: String){
        viewModelScope.launch {
            viewModelScope.launch { submitUiStateChannel.send(UIState.Loading) }
            val response = apiService.submitSurvey(id)
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

    fun postAnswerStringTypeSurvey(surveyId :String,questionId :String, answer:String, other :String ="" ) {
        viewModelScope.launch {
            viewModelScope.launch { answerStringUiStateChannel.send(UIState.Loading) }
            val response = apiService.postAnswerStringTypeSurvey(surveyId, questionId, answer, other)
            when (response) {
                is NetworkResponse.Success -> {
                    if (response.body.status.code != 200) {
                        answerStringUiStateChannel.send(UIState.Error(response.body.status.message ?: ""))
                        Timber.d("Error msg: ${response.body.status.message}")
                    } else {
                        answerStringUiStateChannel.send(UIState.Success(response.body.status))
                    }
                }

                is NetworkResponse.ServerError -> {
                    answerStringUiStateChannel.send(UIState.Error(response.body?.status?.message.toString()))
                    Timber.d("Survey questions: error: ${response.error}")
                }

                is NetworkResponse.NetworkError -> {
                    answerStringUiStateChannel.send(UIState.Error("NetworkError"))
                    Timber.d("Network Error")
                }

                is NetworkResponse.UnknownError -> {
                    answerStringUiStateChannel.send(UIState.Error("Unknown Error"))
                    Timber.d("Unknown Error")
                }

            }
        }
    }

    fun postAnswerIntTypeSurvey(surveyId :String,questionId :String, answer:String, other :String ="" ) {
        viewModelScope.launch {
            viewModelScope.launch { answerIntUiStateChannel.send(UIState.Loading) }
            val response = apiService.postAnswerIntTypeSurvey(surveyId, questionId, answer, other)
            when (response) {
                is NetworkResponse.Success -> {
                    if (response.body.status.code != 200) {
                        answerIntUiStateChannel.send(UIState.Error(response.body.status.message ?: ""))
                    } else {
                        Timber.d("Survey answer sucess ${response.body.status}")
                        answerIntUiStateChannel.send(UIState.Success(response.body.status))
                    }
                }

                is NetworkResponse.ServerError -> {
                    answerIntUiStateChannel.send(UIState.Error(response.body?.status?.message.toString()))
                    Timber.d("Survey questions: error: ${response.error}")
                }

                is NetworkResponse.NetworkError -> {
                    answerIntUiStateChannel.send(UIState.Error("NetworkError"))
                    Timber.d("Network Error")
                }

                is NetworkResponse.UnknownError -> {
                    answerIntUiStateChannel.send(UIState.Error("Unknown Error"))
                    Timber.d("Unknown Error")
                }

            }
        }

    }
    fun postAnswerArrayTypeSurvey(surveyId :String,questionId :String,answer: String, other :String ="" ) {
        viewModelScope.launch {
            viewModelScope.launch { answerArrayUiStateChannel.send(UIState.Loading) }
            val response = apiService.postAnswerArrayTypeSurvey(surveyId, questionId, answer, other)
            when (response) {
                is NetworkResponse.Success -> {
                    if (response.body.status.code != 200) {
                        answerArrayUiStateChannel.send(UIState.Error(response.body.status.message ?: ""))
                    } else {
                        answerArrayUiStateChannel.send(UIState.Success(response.body.status))
                    }
                }

                is NetworkResponse.ServerError -> {
                    answerArrayUiStateChannel.send(UIState.Error(response.body?.status?.message.toString()))
                    Timber.d("Survey questions: error: ${response.error}")
                }

                is NetworkResponse.NetworkError -> {
                    answerArrayUiStateChannel.send(UIState.Error("NetworkError"))
                    Timber.d("Network Error")
                }

                is NetworkResponse.UnknownError -> {
                    answerArrayUiStateChannel.send(UIState.Error("Unknown Error"))
                    Timber.d("Unknown Error")
                }

            }
        }
    }
}