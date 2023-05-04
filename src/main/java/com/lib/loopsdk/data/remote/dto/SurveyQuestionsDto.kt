package com.lib.loopsdk.data.remote.dto


import com.google.gson.annotations.SerializedName

data class SurveyQuestionsDto(
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
            @SerializedName("answer")
            val answer: Any?,
            @SerializedName("description")
            val description: String,
            @SerializedName("id")
            val id: String,
            @SerializedName("isOther")
            val isOther: Boolean,
            @SerializedName("isRequired")
            val isRequired: Boolean,
            @SerializedName("optionType")
            val optionType: Int,
            @SerializedName("options")
            val options: List<Option>,
            @SerializedName("other")
            val other: String,
            @SerializedName("qOrder")
            val qOrder: Int,
            @SerializedName("question")
            val question: String,
            @SerializedName("surveyId")
            val surveyId: String,
            @SerializedName("savedRadioAns")
            var savedRadioAns: String,
            @SerializedName("savedAnsArray")
            var savedAnsArray: ArrayList<String>,
            @SerializedName("updatedAnswerString")
            var updatedAnswerString: String,
            @SerializedName("updatedAnswerInt")
            var updatedAnswerInt: String,
            @SerializedName("updatedAnswerIntArray")
            var updatedAnswerIntArray: ArrayList<String>,
            @SerializedName("updatedOther")
            var updatedOther: String,
            @SerializedName("progress")
            var progress: Int = 0
        ) {
            data class Option(
                @SerializedName("id")
                val id: String,
                @SerializedName("lablel")
                val lablel: String,
                @SerializedName("max")
                val max: String,
                @SerializedName("min")
                val min: String,
                @SerializedName("oOrder")
                val oOrder: Int,
                @SerializedName("value")
                val value: String,
                @SerializedName("isSelected")
                var isSelected: Boolean = false
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