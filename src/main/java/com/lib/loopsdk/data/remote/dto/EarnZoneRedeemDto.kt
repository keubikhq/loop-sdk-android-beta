package com.lib.loopsdk.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.lib.loopsdk.ui.feature_earn.data.Data
import com.lib.loopsdk.ui.feature_earn.data.Status

class EarnZoneRedeemDto(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("status")
    val status: Status
) {
    data class Data(
        @SerializedName("closingBalance")
        val closingBalance: String,
        @SerializedName("createdAt")
        val createdAt: String,
        @SerializedName("createdById")
        val createdById: Int,
        @SerializedName("creditType")
        val creditType: Int,
        @SerializedName("customerId")
        val customerId: Int,
        @SerializedName("debitType")
        val debitType: Any,
        @SerializedName("debitTypeId")
        val debitTypeId: Int,
        @SerializedName("id")
        val id: Int,
        @SerializedName("instanceEnv")
        val instanceEnv: Int,
        @SerializedName("instanceId")
        val instanceId: Int,
        @SerializedName("instanceIdentifier")
        val instanceIdentifier: String,
        @SerializedName("isActive")
        val isActive: Int,
        @SerializedName("isDeleted")
        val isDeleted: Int,
        @SerializedName("message")
        val message: String,
        @SerializedName("openingBalance")
        val openingBalance: Int,
        @SerializedName("transcationDatetime")
        val transcationDatetime: String,
        @SerializedName("transcationId")
        val transcationId: String,
        @SerializedName("type")
        val type: Int,
        @SerializedName("updatedAt")
        val updatedAt: String,
        @SerializedName("value")
        val value: Int
    )
    data class Status(
        @SerializedName("code")
        val code: Int,
        @SerializedName("message")
        val message: String
    )

}