package com.hellodoc.healthcaresystem.requestmodel

data class EmailRequest(val email: String)
data class OtpVerifyRequest(val email: String, val otp: String)
data class ResetPasswordRequest(val email: String, val newPassword: String)
