package com.lib.loopsdk.data.remote.dto


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class QuizDetailDto(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("status")
    val status: Status
): Serializable {
    data class Data(
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
        @SerializedName("totalQuestion")
        val totalQuestion: Int,
        @SerializedName("termsAndConditions")
        val termsAndConditions: String,
        @SerializedName("benefitBasicDetails")
        val benefitBasicDetails: BenefitBasicDetails,
    ):Serializable {
        data class Cohort(
            @SerializedName("benefitCount")
            val benefitCount: Any,
            @SerializedName("benefitDetails")
            val benefitDetails: BenefitDetails,
            @SerializedName("benefitId")
            val benefitId: Any,
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
            val successMessage: Any
        ):Serializable {
            class BenefitDetails(
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
        data class BenefitBasicDetails(
            @SerializedName("points")
            val points: String,
            @SerializedName("coupons")
            val coupons: Int
        ):Serializable
    }

    data class Status(
        @SerializedName("code")
        val code: Int,
        @SerializedName("message")
        val message: String
    ):Serializable
}