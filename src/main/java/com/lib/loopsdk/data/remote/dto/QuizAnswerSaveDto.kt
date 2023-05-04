package com.lib.loopsdk.data.remote.dto


import com.google.gson.annotations.SerializedName

data class QuizAnswerSaveDto(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("status")
    val status: Status
) {
    data class Data(
        @SerializedName("answerId")
        val answerId: String,
        @SerializedName("correctOptionId")
        val correctOptionId: String,
        @SerializedName("isCorrect")
        val isCorrect: Int,
        @SerializedName("optionId")
        val optionId: String,
        @SerializedName("questionId")
        val questionId: String,
        @SerializedName("quizId")
        val quizId: String,
        @SerializedName("userId")
        val userId: String
    )

    data class Status(
        @SerializedName("code")
        val code: Int,
        @SerializedName("message")
        val message: String
    )
}