package com.hellodoc.healthcaresystem.responsemodel

//dữ liệu quản lí tài khoản
data class Account2(
    val name: String,
    val email: String,
    val phone: String,
    val createdDate: String
)

//dữ liệu quản lí xác thực tài khoản
data class Account(
    val email: String,
    val phone: String,
    val licenseId: String,
    val verified: Boolean
)
