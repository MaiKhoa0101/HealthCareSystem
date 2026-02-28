package com.hellodoc.healthcaresystem.model.dataclass.responsemodel

import com.google.gson.annotations.SerializedName

data class DetectionResponse(
    @SerializedName("status") val status: String,
    @SerializedName("data") val data: DetectionData
)

data class DetectionData(
    @SerializedName("eyebrows") val eyebrows: String = "",
    @SerializedName("eyelid_l") val eyelidL: String = "",
    @SerializedName("eyelid_r") val eyelidR: String = "",
    @SerializedName("eyes") val eyes: String = "",
    @SerializedName("hand_l") val handL: String = "",
    @SerializedName("hand_r") val handR: String = "",
    @SerializedName("head") val head: String = "",
    @SerializedName("index_l") val indexL: String = "",
    @SerializedName("index_r") val indexR: String = "",
    @SerializedName("jaw") val jaw: String = "",
    @SerializedName("lowerarm_l") val lowerarmL: String = "",
    @SerializedName("lowerarm_r") val lowerarmR: String = "",
    @SerializedName("middle_l") val middleL: String = "",
    @SerializedName("middle_r") val middleR: String = "",
    @SerializedName("mouth_l") val mouthL: String = "",
    @SerializedName("mouth_r") val mouthR: String = "",
    @SerializedName("neck") val neck: String = "",
    @SerializedName("pinky_l") val pinkyL: String = "",
    @SerializedName("pinky_r") val pinky_r: String = "",
    @SerializedName("ring_l") val ringL: String = "",
    @SerializedName("ring_r") val ringR: String = "",
    @SerializedName("shoulder_l") val shoulderL: String = "",
    @SerializedName("shoulder_r") val shoulderR: String = "",
    @SerializedName("spine_01") val spine01: String = "",
    @SerializedName("spine_02") val spine02: String = "",
    @SerializedName("spine_03") val spine03: String = "",
    @SerializedName("thumb_l") val thumbL: String = "",
    @SerializedName("thumb_r") val thumbR: String = "",
    @SerializedName("upperarm_l") val upperarmL: String = "",
    @SerializedName("upperarm_r") val upperarmR: String = ""
)
