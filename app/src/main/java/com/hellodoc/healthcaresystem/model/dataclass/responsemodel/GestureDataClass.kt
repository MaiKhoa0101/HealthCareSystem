package com.hellodoc.healthcaresystem.model.dataclass.responsemodel
import com.google.gson.annotations.SerializedName
import kotlin.math.abs
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sqrt

// =======================
// DATA CLASSES

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

data class WordCode(
    val wordCodes: String
)
data class urlMedia(
    val urlMedia :String
)

data class GestureCodeResponse(
    @SerializedName("word") val word: String,
    @SerializedName("gestureData") val gestureData: List<GestureFrame>,
    @SerializedName("cached") val cached: Boolean = false,
    @SerializedName("accuracy") val accuracy: Double = 0.0,
    @SerializedName("gross") val gross: String
)

@Serializable
data class GestureFrame(
    val frame: Int,
    val timestamp: Float,
    @SerializedName("original_image") val originalImage: String? = null,
    @SerializedName("skeleton_image") val skeletonImage: String? = null,
    val gestures: BoneData
)

@Serializable
data class BoneData(
    // --- Spine ---
    @SerializedName("spine_01") val spine01: String = "",
    @SerializedName("spine_02") val spine02: String = "",
    @SerializedName("spine_03") val spine03: String = "",

    // --- Head & Neck ---
    val neck: String = "",
    val head: String = "",

    // --- Facial (Single) ---
    val jaw: String = "",
    @SerializedName("eyelid_l") val eyelidL: String = "",
    @SerializedName("eyelid_r") val eyelidR: String = "",
    @SerializedName("mouth_l") val mouthL: String = "",
    @SerializedName("mouth_r") val mouthR: String = "",

    // --- Facial (Multiple/Combined) ---
    // Chứa "eye_l(...); eye_r(...)"
    val eyes: String = "",
    // Chứa "eyebrow_l(...); eyebrow_r(...)"
    val eyebrows: String = "",

    // --- Left Arm ---
    @SerializedName("shoulder_l") val shoulderL: String = "",
    @SerializedName("upperarm_l") val upperarmL: String = "",
    @SerializedName("lowerarm_l") val lowerarmL: String = "",
    @SerializedName("hand_l") val handL: String = "",

    // --- Left Fingers (Multiple) ---
    @SerializedName("thumb_l") val thumbL: String = "",
    @SerializedName("index_l") val indexL: String = "",
    @SerializedName("middle_l") val middleL: String = "",
    @SerializedName("ring_l") val ringL: String = "",
    @SerializedName("pinky_l") val pinkyL: String = "",

    // --- Right Arm ---
    @SerializedName("shoulder_r") val shoulderR: String = "",
    @SerializedName("upperarm_r") val upperarmR: String = "",
    @SerializedName("lowerarm_r") val lowerarmR: String = "",
    @SerializedName("hand_r") val handR: String = "",

    // --- Right Fingers (Multiple) ---
    @SerializedName("thumb_r") val thumbR: String = "",
    @SerializedName("index_r") val indexR: String = "",
    @SerializedName("middle_r") val middleR: String = "",
    @SerializedName("ring_r") val ringR: String = "",
    @SerializedName("pinky_r") val pinkyR: String = ""
)

// =======================
// ROTATION PARSER
// =======================
data class Rotation(
    val x: Float = 0f,
    val y: Float = 0f,
    val z: Float = 0f
) {
    companion object {
        /**
         * Parse chuỗi dạng: "bone_name(x=10, y=-5.5, z=0)"
         * Hoặc dạng thiếu: "spine(z=90)" -> x, y tự động = 0
         * Chấp nhận cả khoảng trắng lộn xộn: "arm( x = 10 , y= 5 )"
         */
        fun fromString(data: String): Rotation {
            if (data.isBlank()) return Rotation(0f, 0f, 0f)

            // Dùng Regex để tìm giá trị sau x=, y=, z=
            // Giải thích Regex: [xX] tìm chữ x hoặc X, \s* là khoảng trắng tùy ý, = là dấu bằng
            // ([-+]?[0-9]*\.?[0-9]+) là cụm bắt số (có thể có dấu âm, số thập phân)
            return Rotation(
                x = parseAxisValue(data, "x"),
                y = parseAxisValue(data, "y"),
                z = parseAxisValue(data, "z")
            )
        }

        private fun parseAxisValue(text: String, axis: String): Float {
            // Regex tìm: axis + dấu bằng + số (float/int)
            // Ví dụ tìm "y": khớp với "y=8.5" hoặc "y = -90"
            val regex = Regex("$axis\\s*=\\s*([-+]?[0-9]*\\.?[0-9]+)", RegexOption.IGNORE_CASE)

            val match = regex.find(text)
            // groupValues[1] là giá trị con số tìm được. Nếu ko thấy trả về 0f
            return match?.groupValues?.get(1)?.toFloatOrNull() ?: 0f
        }

        /**
         * Xử lý chuỗi nhiều xương cách nhau bởi dấu chấm phẩy
         * Input: "thumb(x=0...); index(x=0...)"
         */
        fun parseMultiple(data: String): Map<String, Rotation> {
            if (data.isBlank()) return emptyMap()

            val result = mutableMapOf<String, Rotation>()

            // 1. Tách các cụm xương bằng dấu chấm phẩy ;
            val parts = data.split(";")

            for (part in parts) {
                val trimmed = part.trim()
                if (trimmed.isEmpty()) continue

                // 2. Tách tên xương ra khỏi dữ liệu xoay
                // "thumb_01_l(x=...)" -> Lấy phần trước dấu ngoặc đơn '('
                val openParenIndex = trimmed.indexOf('(')

                if (openParenIndex > 0) {
                    val boneName = trimmed.take(openParenIndex).trim()
                    // Phần còn lại chính là data để parse rotation
                    val rotationData = trimmed.substring(openParenIndex)

                    result[boneName] = fromString(rotationData)
                }
            }
            return result
        }

        // Hàm tiện ích chuyển sang Matrix/Quaternion nếu cần (Optional)
        fun fromMatrix(mat: FloatArray): Rotation {
            // Logic lấy Euler từ Matrix (nếu bạn đang dùng logic này để lấy currentRotation)
            // Đây là ví dụ đơn giản, thực tế cần thuật toán Euler extraction chuẩn
            // Tạm thời trả về 0 để tránh lỗi biên dịch nếu bạn chưa có logic này
            return Rotation(0f, 0f, 0f)
        }
    }
}