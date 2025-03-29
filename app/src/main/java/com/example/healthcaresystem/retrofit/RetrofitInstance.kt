package com.example.healthcaresystem.retrofit

import com.example.healthcaresystem.api.AdminService
import com.example.healthcaresystem.api.AppointmentService
import com.example.healthcaresystem.api.AuthService
import com.example.healthcaresystem.api.DoctorService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    //private const val BASE_URL = "http://192.168.0.104:4000"
    private const val BASE_URL = "http://192.168.117.22:4000"

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
    val doctor: DoctorService by lazy { retrofit.create(DoctorService::class.java) }
    val appointment: AppointmentService by lazy { retrofit.create(AppointmentService::class.java) }
}
