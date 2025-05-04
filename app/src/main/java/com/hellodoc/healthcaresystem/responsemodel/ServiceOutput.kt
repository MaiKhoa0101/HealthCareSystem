package com.hellodoc.healthcaresystem.responsemodel

import android.net.Uri
import com.google.gson.annotations.SerializedName

data class ServiceInput(
    var specializationName: String,
    var imageUri: Uri,
    var priceFrom: String,
    var priceTo: String,
    var description: String
)

data class ServiceOutput(
    val specialtyID : String,
    val specialtyName : String,
    val imageService: String,
    @SerializedName("minprice") val minPrice: String,
    @SerializedName("maxprice")val maxPrice: String,
    val description: String
)