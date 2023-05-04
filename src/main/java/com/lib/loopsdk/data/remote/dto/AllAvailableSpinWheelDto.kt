package com.lib.loopsdk.data.remote.dto


import com.google.gson.annotations.SerializedName

data class AllAvailableSpinWheelDto(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("status")
    val status: Status
) {
    data class Data(
        @SerializedName("spinWheels")
        val spinWheels: List<SpinWheel>,
        @SerializedName("totalEntries")
        val totalEntries: Int,
        @SerializedName("totalPages")
        val totalPages: Int,
        @SerializedName("totalPoints")
        val totalPoints: String
    ) {
        data class SpinWheel(
            @SerializedName("benefitBasicDetails")
            val benefitBasicDetails: BenefitBasicDetails,
            @SerializedName("benefits")
            val benefits: List<Benefit>,
            @SerializedName("description")
            val description: String,
            @SerializedName("id")
            val id: String,
            @SerializedName("name")
            val name: String,
            @SerializedName("pointsToUnlock")
            val pointsToUnlock: Int,
            @SerializedName("termsAndConditions")
            val termsAndConditions: String
        ) {
            data class BenefitBasicDetails(
                @SerializedName("coupons")
                val coupons: Int,
                @SerializedName("points")
                val points: String
            )

            data class Benefit(
                @SerializedName("amount")
                val amount: Int,
                @SerializedName("benefitType")
                val benefitType: Int,
                @SerializedName("id")
                val id: String,
                @SerializedName("messageText")
                val messageText: String,
                @SerializedName("typeId")
                val typeId: String
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