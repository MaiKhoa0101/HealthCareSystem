package com.hellodoc.healthcaresystem.responsemodel


//dữ liệu quản lí xác thực tài khoản
data class Account(
    val email: String,
    val phone: String,
    val licenseId: String,
    val verified: Boolean
)
