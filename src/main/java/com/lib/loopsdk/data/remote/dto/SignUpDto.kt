package com.lib.loopsdk.data.remote.dto

data class SignUpDto(
    val `data`: Data,
    val status: ErrorDto.Status
) {
    data class Data(
        val customerId: String,
        val hasSignedUp: Int
    )

}