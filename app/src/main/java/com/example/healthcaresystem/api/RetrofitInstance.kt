package com.example.healthcaresystem.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL = "http://192.168.1.9:3000/"

    private val client = OkHttpClient.Builder()
        .build()
    // Tạo instance Retrofit duy nhất
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client) // Sử dụng client có logging
            .addConverterFactory(GsonConverterFactory.create()) // Dùng gson để chuyển JSON thành obj
            .build()
    }

    // Các service API
    val api: AuthService by lazy { retrofit.create(AuthService::class.java) }
    val admin: AdminService by lazy { retrofit.create(AdminService::class.java) }
}
