package com.lib.loopsdk.data.remote.dto


import com.google.gson.annotations.SerializedName

data class GetWalletPointsDto(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("status")
    val status: Status
) {
    data class Data(
        @SerializedName("userDetails")
        val userDetails: UserDetails
    ) {
        data class UserDetails(
            @SerializedName("countryCode")
            val countryCode: String,
            @SerializedName("email")
            val email: String,
            @SerializedName("firstName")
            val firstName: String,
            @SerializedName("id")
            val id: String,
            @SerializedName("lastName")
            val lastName: String,
            @SerializedName("loopUserIdentity")
            val loopUserIdentity: String,
            @SerializedName("mobileNumber")
            val mobileNumber: Long,
            @SerializedName("pointsBalance")
            val pointsBalance: String,
            @SerializedName("tier")
            val tier: Tier,
            @SerializedName("totalPointsBurned")
            val totalPointsBurned: String,
            @SerializedName("totalPointsEarned")
            val totalPointsEarned: String,
            @SerializedName("userName")
            val userName: String
        ) {
            data class Tier(
                @SerializedName("id")
                val id: String,
                @SerializedName("tierImage")
                val tierImage: String,
                @SerializedName("tierName")
                val tierName: String,
                @SerializedName("tierUnlockDate")
                val tierUnlockDate: String
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