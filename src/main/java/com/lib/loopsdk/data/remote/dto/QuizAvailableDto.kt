package com.lib.loopsdk.data.remote.dto


import com.google.gson.annotations.SerializedName

data class QuizAvailableDto(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("status")
    val status: Status
) {
    data class Data(
        @SerializedName("quiz")
        val quiz: List<Quiz>,
        @SerializedName("totalEntries")
        val totalEntries: Int,
        @SerializedName("totalPages")
        val totalPages: Int
    ) {
        data class Quiz(
            @SerializedName("cohorts")
            val cohorts: List<Cohort>,
            @SerializedName("coverImage")
            val coverImage: String,
            @SerializedName("name")
            val name: String,
            @SerializedName("description")
            val description: String,
            @SerializedName("endDate")
            val endDate: String,
            @SerializedName("hasAnsweredAll")
            val hasAnsweredAll: Int,
            @SerializedName("id")
            val id: String,
            @SerializedName("partiallyFilled")
            val partiallyFilled: Int,
            @SerializedName("timed")
            val timed: Int,
            @SerializedName("totalQuestion")
            val totalQuestion: Int,
            @SerializedName("type")
            val type: Int
        ) {
            data class Cohort(
                @SerializedName("benefitCount")
                val benefitCount: Int,
                @SerializedName("benefitId")
                val benefitId: Int,
                @SerializedName("benefitMessage")
                val benefitMessage: String,
                @SerializedName("benefitPoints")
                val benefitPoints: Int,
                @SerializedName("benefitType")
                val benefitType: Int,
                @SerializedName("cohortName")
                val cohortName: String,
                @SerializedName("id")
                val id: String,
                @SerializedName("maxPoints")
                val maxPoints: Int,
                @SerializedName("minPoints")
                val minPoints: Int,
                @SerializedName("successMessage")
                val successMessage: String
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