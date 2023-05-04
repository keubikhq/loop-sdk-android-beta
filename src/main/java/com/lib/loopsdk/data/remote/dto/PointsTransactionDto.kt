package com.lib.loopsdk.data.remote.dto


import com.google.gson.annotations.SerializedName

data class PointsTransactionDto(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("status")
    val status: Status
) {
    data class Data(
        @SerializedName("pointTransactions")
        val pointTransactions: List<PointTransaction>,
        @SerializedName("totalEntries")
        val totalEntries: Int,
        @SerializedName("totalPages")
        val totalPages: Int,
        @SerializedName("totalPoints")
        val totalPoints: String
    ) {
        data class PointTransaction(
            @SerializedName("createdAt")
            val createdAt: String,
            @SerializedName("creditType")
            val creditType: Int,
            @SerializedName("debitType")
            val debitType: Int,
            @SerializedName("id")
            val id: String,
            @SerializedName("message")
            val message: String,
            @SerializedName("type")
            val type: Int,
            @SerializedName("value")
            val value: String
        )
    }

    data class Status(
        @SerializedName("code")
        val code: Int,
        @SerializedName("message")
        val message: String
    )
}