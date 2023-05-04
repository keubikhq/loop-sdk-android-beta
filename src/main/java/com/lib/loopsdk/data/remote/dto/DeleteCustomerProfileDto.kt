package com.lib.loopsdk.data.remote.dto

data class DeleteCustomerProfileDto(
    val `data`: Data,
    val status: Status
) {
    data class Data(
        val deletionRequestId: String,
        val deletionScheduledAt: String
    )

    data class Status(
        val code: Int,
        val message: String
    )
}