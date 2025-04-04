package com.hellodoc.core.common.utils

import android.content.Context
import android.content.Intent
import android.net.Uri

object PhoneCallUtils {

    fun startCall(context: Context, phoneNumber: String) {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$phoneNumber")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
}