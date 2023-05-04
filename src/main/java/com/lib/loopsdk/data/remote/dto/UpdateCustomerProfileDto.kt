package com.lib.loopsdk.data.remote.dto

data class UpdateCustomerProfileDto(
    val `data`: Data,
    val status: Status
) {
    data class Data(
        val address: String,
        val age: Int,
        val anniversary: String,
        val cityName: String,
        val countryCode: Int,
        val dob: String,
        val email: String,
        val firstName: String,
        val gender: String,
        val lastName: String,
        val mobileNumber: String,
        val pinCode: String,
        val primaryAddress: String,
        val secondaryAddress: String,
        val stateName: String,
        val userName: String
    )

    data class Status(
        val code: Int,
        val message: String
    )
}