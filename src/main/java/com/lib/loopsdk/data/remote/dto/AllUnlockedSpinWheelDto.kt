package com.lib.loopsdk.data.remote.dto


import com.google.gson.annotations.SerializedName

data class AllUnlockedSpinWheelDto(
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
        val totalPages: Int
    ) {
        data class SpinWheel(
            @SerializedName("benefitBasicDetails")
            val benefitBasicDetails: BenefitBasicDetails,
            @SerializedName("benefits")
            val benefits: List<Benefit>,
            @SerializedName("description")
            val description: String,
            @SerializedName("name")
            val name: String,
            @SerializedName("spinWheelId")
            val spinWheelId: String,
            @SerializedName("spinWheelTransactionId")
            val spinWheelTransactionId: String,
            @SerializedName("termsAndConditions")
            val termsAndConditions: String,
            @SerializedName("transactionId")
            val transactionId: String
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