package com.hellodoc.healthcaresystem.retrofit

import com.hellodoc.healthcaresystem.api.AdminService
import com.hellodoc.healthcaresystem.api.AppointmentService
import com.hellodoc.healthcaresystem.api.AuthService
import com.hellodoc.healthcaresystem.api.DoctorService
import com.hellodoc.healthcaresystem.api.FAQItemService
import com.hellodoc.healthcaresystem.api.GeminiService
import com.hellodoc.healthcaresystem.api.MedicalOptionService
import com.hellodoc.healthcaresystem.api.RemoteMedicalOptionService
import com.hellodoc.healthcaresystem.api.SpecialtyService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    //private const val BASE_URL = "http://192.168.0.104:4000"
    private const val BASE_URL = "http://192.168.5.100:3000"

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
    val specialtyService: SpecialtyService by lazy { retrofit.create(SpecialtyService::class.java) }
    val medicalOptionService: MedicalOptionService by lazy { retrofit.create(MedicalOptionService::class.java) }
    val remoteMedicalOptionService: RemoteMedicalOptionService by lazy { retrofit.create(RemoteMedicalOptionService::class.java) }
    val faqItemService: FAQItemService by lazy { retrofit.create(FAQItemService::class.java) }

    val geminiService: GeminiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://generativelanguage.googleapis.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GeminiService::class.java)
    }
}
