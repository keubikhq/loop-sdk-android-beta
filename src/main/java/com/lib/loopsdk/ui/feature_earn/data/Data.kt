package com.lib.loopsdk.ui.feature_earn.data

data class Data(
    val closingBalance: String,
    val createdAt: String,
    val createdById: Int,
    val creditType: Int,
    val customerId: Int,
    val debitType: Any,
    val debitTypeId: Int,
    val id: Int,
    val instanceEnv: Int,
    val instanceId: Int,
    val instanceIdentifier: String,
    val isActive: Int,
    val isDeleted: Int,
    val message: String,
    val openingBalance: Int,
    val transcationDatetime: String,
    val transcationId: String,
    val type: Int,
    val updatedAt: String,
    val value: Int
)