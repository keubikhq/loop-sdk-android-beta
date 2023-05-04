package com.lib.loopsdk.data.remote.dto

import java.io.Serializable
import com.google.gson.annotations.SerializedName

data class CountryCodesDto(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("status")
    val status: Status
): Serializable {
    data class Data(
        @SerializedName("countryCodes")
        val countryCodes: List<CountryCode>
    ): Serializable {
        data class CountryCode(
            @SerializedName("code")
            val code: String,
            @SerializedName("id")
            val id: Int,
            @SerializedName("length")
            val length: Int,
            @SerializedName("name")
            val name: String,

            var isSelected: Boolean = false
        ): Serializable
    }

    data class Status(
        @SerializedName("code")
        val code: Int,
        @SerializedName("message")
        val message: String
    ): Serializable
}