package com.lib.loopsdk.data.remote.dto


import com.google.gson.annotations.SerializedName

data class ProfileReferralBenefitsDto(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("status")
    val status: Status
) {
    data class Data(
        @SerializedName("link")
        val link: String,
        @SerializedName("referralData")
        val referralData: ReferralData
    ) {
        data class ReferralData(
            @SerializedName("benefitTypeReferee")
            val benefitTypeReferee: Int,
            @SerializedName("benefitTypeReferrer")
            val benefitTypeReferrer: Int,
            @SerializedName("benefitValueReferee")
            val benefitValueReferee: Int,
            @SerializedName("benefitValueReferrer")
            val benefitValueReferrer: Int,
            @SerializedName("code")
            val code: String,
            @SerializedName("fallbackBenefit")
            val fallbackBenefit: Int
        )
    }

    data class Status(
        @SerializedName("code")
        val code: Int,
        @SerializedName("message")
        val message: String
    )
}