package com.lib.loopsdk.data.remote.dto


import com.google.gson.annotations.SerializedName

data class SurveySubmitDto(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("status")
    val status: Status
) {
    data class Data(
        @SerializedName("benefitDetails")
        val benefitDetails: BenefitDetails,
        @SerializedName("benefitId")
        val benefitId: String,
        @SerializedName("benefitType")
        val benefitType: Int,
        @SerializedName("benefitValue")
        val benefitValue: Any,
        @SerializedName("messageText")
        val messageText: String,
        @SerializedName("name")
        val name: String,
        @SerializedName("surveyId")
        val surveyId: String,
        @SerializedName("targeting")
        val targeting: Int,
        @SerializedName("transactionId")
        val transactionId: String,
        @SerializedName("description")
        val description: String,
    ) {
        data class BenefitDetails(
            @SerializedName("couponClassification")
            val couponClassification: Int,
            @SerializedName("couponLabel")
            val couponLabel: String,
            @SerializedName("couponName")
            val couponName: String,
            @SerializedName("description")
            val description: String,
            @SerializedName("discountPercent")
            val discountPercent: Int,
            @SerializedName("featuredImage")
            val featuredImage: String,
            @SerializedName("maxDiscountValue")
            val maxDiscountValue: Int,
            @SerializedName("pointsToUnlock")
            val pointsToUnlock: Int,
            @SerializedName("termsAndConditions")
            val termsAndConditions: String,
            @SerializedName("threshold")
            val threshold: Int
        )
    }

    data class Status(
        @SerializedName("code")
        val code: Int,
        @SerializedName("message")
        val message: String
    )
}