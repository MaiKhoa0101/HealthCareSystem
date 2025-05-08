package com.hellodoc.healthcaresystem.responsemodel

import android.net.Uri
import com.google.gson.annotations.SerializedName

data class ServiceInput(
    var specialtyId: String,
    var specialtyName: String,
    var imageService: List<Uri>,
    var minprice: String,
    var maxprice: String,
    var description: String,
)

data class ServiceOutput(
    val specialtyId : String,
    val specialtyName : String,
    val description: String,
    val imageService: List<String>,
    @SerializedName("minprice") val minprice: String,
    @SerializedName("maxprice")val maxprice: String,
)

