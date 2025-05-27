package com.hellodoc.healthcaresystem.local

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hellodoc.healthcaresystem.local.dao.AppointmentDao
import com.hellodoc.healthcaresystem.viewmodel.AppointmentViewModel

class AppointmentViewModelFactory(
    private val sharedPreferences: SharedPreferences,
    private val dao: AppointmentDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AppointmentViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AppointmentViewModel(sharedPreferences, dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
