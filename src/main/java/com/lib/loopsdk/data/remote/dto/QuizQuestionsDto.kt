package com.lib.loopsdk.data.remote.dto


import com.google.gson.annotations.SerializedName

data class QuizQuestionsDto(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("status")
    val status: Status
) {
    data class Data(
        @SerializedName("questions")
        val questions: List<Question>
    ) {
        data class Question(
            @SerializedName("answeredOptionId")
            val answeredOptionId: String,
            @SerializedName("description")
            val description: String,
            @SerializedName("id")
            val id: String,
            @SerializedName("isRandom")
            val isRandom: Int,
            @SerializedName("options")
            val options: List<Option>,
            @SerializedName("qOrder")
            val qOrder: Int,
            @SerializedName("question")
            val question: String,
            @SerializedName("timer")
            val timer: Int,
            @SerializedName("updatedAnsString")
            var updatedAnsString: String,
            @SerializedName("progress")
            var progress: Int = 0
        ) {
            data class Option(
                @SerializedName("id")
                val id: String,
                @SerializedName("text")
                val text: String,
                @SerializedName("isSelected")
                var isSelected: Boolean = false,
                @SerializedName("setCorrect")
                var setCorrect: Boolean = false,
                @SerializedName("isWrong")
                var isWrong: Boolean = false
            )
        }
    }

    data class Status(
        @SerializedName("code")
        val code: Int,
        @SerializedName("message")
        val message: String
    )
}