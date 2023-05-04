package com.lib.loopsdk.data.remote.dto


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class SurveyDetailDto(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("status")
    val status: Status
): Serializable {
    data class Data(
        @SerializedName("backgroundImageUrl")
        val backgroundImageUrl: String,
        @SerializedName("benefitDetails")
        val benefitDetails: BenefitDetails,
        @SerializedName("benefitId")
        val benefitId: String,
        @SerializedName("benefitType")
        val benefitType: Int,
        @SerializedName("benefitValue")
        val benefitValue: Int,
        @SerializedName("description")
        val description: String,
        @SerializedName("endDate")
        val endDate: String,
        @SerializedName("hasAnswered")
        val hasAnswered: Int,
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
        @SerializedName("termsAndCondition")
        val termsAndCondition: String,
        @SerializedName("totalQuestion")
        val totalQuestion: Int
    ):Serializable {
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
        ):Serializable
    }

    data class Status(
        @SerializedName("code")
        val code: Int,
        @SerializedName("message")
        val message: String
    ):Serializable
}