package com.lib.loopsdk.data.remote.dto


import com.google.gson.annotations.SerializedName

data class QuizSubmitDto(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("status")
    val status: Status
) {
    data class Data(
        @SerializedName("benefitDetails")
        val benefitDetails: BenefitDetails,
        @SerializedName("benefitId")
        val benefitId: Any,
        @SerializedName("benefitType")
        val benefitType: Int,
        @SerializedName("benefitValue")
        val benefitValue: Any,
        @SerializedName("messageText")
        val messageText: String,
        @SerializedName("quizId")
        val quizId: String,
        @SerializedName("transactionId")
        val transactionId: String,
        @SerializedName("description")
        val description: String,
        @SerializedName("defaultSuccessMessage")
        val defaultSuccessMessage: String,
    ) {
        data class BenefitDetails(
            @SerializedName("couponClassification")
            val couponClassification: Int,
            @SerializedName("couponLabel")
            val couponLabel: String,
            @SerializedName("couponName")
            val couponName: String,
            @SerializedName("description")
            val description: Any,
            @SerializedName("discountPercent")
            val discountPercent: Int,
            @SerializedName("featuredImage")
            val featuredImage: String,
            @SerializedName("maxDiscountValue")
            val maxDiscountValue: Int,
            @SerializedName("pointsToUnlock")
            val pointsToUnlock: Int,
            @SerializedName("termsAndConditions")
            val termsAndConditions: Any,
            @SerializedName("threshold")
            val threshold: Any
        )
    }

    data class Status(
        @SerializedName("code")
        val code: Int,
        @SerializedName("message")
        val message: String
    )
}