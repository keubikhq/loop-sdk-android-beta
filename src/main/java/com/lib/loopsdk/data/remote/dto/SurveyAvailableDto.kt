package com.lib.loopsdk.data.remote.dto


import com.google.gson.annotations.SerializedName

data class SurveyAvailableDto(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("status")
    val status: Status
) {
    data class Data(
        @SerializedName("survey")
        val survey: List<Survey>,
        @SerializedName("totalEntries")
        val totalEntries: Int,
        @SerializedName("totalPages")
        val totalPages: Int
    ) {
        data class Survey(
            @SerializedName("backgroundImageUrl")
            val backgroundImageUrl: String,
            @SerializedName("benefitDetails")
            val benefitDetails: BenefitDetails,
            @SerializedName("benefitId")
            val benefitId: String,
            @SerializedName("benefitType")
            val benefitType: Int,
            @SerializedName("benefitValue")
            val benefitValue: Any,
            @SerializedName("description")
            val description: String,
            @SerializedName("endDate")
            val endDate: String,
            @SerializedName("messageText")
            val messageText: Any,
            @SerializedName("name")
            val name: String,
            @SerializedName("partiallyFilled")
            val partiallyFilled: Boolean,
            @SerializedName("surveyId")
            val surveyId: String,
            @SerializedName("targeting")
            val targeting: Int,
            @SerializedName("totalQuestion")
            val totalQuestion: Int,
            @SerializedName("defaultType")
            val defaultType: Int
        ) {
            data class BenefitDetails(
                @SerializedName("couponLabel")
                val couponLabel: String,
                @SerializedName("couponName")
                val couponName: String,
                @SerializedName("description")
                val description: String,
                @SerializedName("featuredImage")
                val featuredImage: Any,
                @SerializedName("termsAndConditions")
                val termsAndConditions: String
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