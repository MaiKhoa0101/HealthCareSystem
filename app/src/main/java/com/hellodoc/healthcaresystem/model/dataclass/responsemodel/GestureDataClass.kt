package com.hellodoc.healthcaresystem.model.dataclass.responsemodel
import com.google.gson.annotations.SerializedName
import kotlin.math.abs
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sqrt

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// =======================
// DATA CLASSES
// =======================

@Serializable
data class GestureFrame(
    val frame: Int,
    val timestamp: Float,
    val bones: BoneData
)

@Serializable
data class BoneData(
    val spine: SpineData = SpineData(),
    @SerialName("neck_head")
    val neckHead: NeckHeadData = NeckHeadData(),
    val facial: FacialData = FacialData(),
    @SerialName("left_arm")
    val leftArm: ArmData = ArmData(),
    @SerialName("right_arm")
    val rightArm: ArmData = ArmData()
)

@Serializable
data class SpineData(
    @SerialName("spine_01")
    val spine01: String = "",
    @SerialName("spine_02")
    val spine02: String = "",
    @SerialName("spine_03")
    val spine03: String = ""
)

@Serializable
data class NeckHeadData(
    val neck: String = "",
    val head: String = ""
)

@Serializable
data class FacialData(
    val jaw: String = "",
    @SerialName("eye_l")
    val eyeL: String = "",
    @SerialName("eye_r")
    val eyeR: String = "",
    @SerialName("eyelid_l")
    val eyelidL: String = "",
    @SerialName("eyelid_r")
    val eyelidR: String = "",
    @SerialName("eyebrow_l")
    val eyebrowL: String = "",
    @SerialName("eyebrow_r")
    val eyebrowR: String = "",
    @SerialName("mouth_l")
    val mouthL: String = "",
    @SerialName("mouth_r")
    val mouthR: String = ""
)

@Serializable
data class ArmData(
    @SerialName("shoulder_l")
    val shoulderL: String = "",
    @SerialName("shoulder_r")
    val shoulderR: String = "",
    @SerialName("upperarm_l")
    val upperarmL: String = "",
    @SerialName("upperarm_r")
    val upperarmR: String = "",
    @SerialName("lowerarm_l")
    val lowerarmL: String = "",
    @SerialName("lowerarm_r")
    val lowerarmR: String = "",
    @SerialName("hand_l")
    val handL: String = "",
    @SerialName("hand_r")
    val handR: String = "",
    val fingers: FingerData = FingerData()
)

@Serializable
data class FingerData(
    val thumb: String = "",
    val index: String = "",
    val middle: String = "",
    val ring: String = "",
    val pinky: String = ""
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
         * Parse rotation từ string: "bone_name(x=10, y=20, z=30)"
         */
        fun fromString(rotationStr: String): Rotation {
            if (rotationStr.isEmpty()) return Rotation()

            try {
                val pattern = """x=(-?\d+),\s*y=(-?\d+),\s*z=(-?\d+)""".toRegex()
                val matchResult = pattern.find(rotationStr) ?: return Rotation()

                val (x, y, z) = matchResult.destructured
                return Rotation(
                    x = x.toFloat(),
                    y = y.toFloat(),
                    z = z.toFloat()
                )
            } catch (e: Exception) {
                return Rotation()
            }
        }

        /**
         * Parse multiple bones từ string: "thumb_01_l(x=10, y=20, z=30); thumb_02_l(...)"
         */
        fun parseMultiple(rotationsStr: String): Map<String, Rotation> {
            if (rotationsStr.isEmpty()) return emptyMap()

            val result = mutableMapOf<String, Rotation>()

            try {
                val parts = rotationsStr.split(";")

                for (part in parts) {
                    val trimmed = part.trim()
                    if (trimmed.isEmpty()) continue

                    val boneNameMatch = """^(\w+)\(""".toRegex().find(trimmed)
                    if (boneNameMatch != null) {
                        val boneName = boneNameMatch.groupValues[1]
                        val rotation = fromString(trimmed)
                        result[boneName] = rotation
                    }
                }
            } catch (e: Exception) {
                // Return empty map on error
            }

            return result
        }

        fun fromMatrix(m: FloatArray): Rotation {
            // --- Tách scale ---
            val scaleX = sqrt(m[0]*m[0] + m[1]*m[1] + m[2]*m[2])
            val scaleY = sqrt(m[4]*m[4] + m[5]*m[5] + m[6]*m[6])
            val scaleZ = sqrt(m[8]*m[8] + m[9]*m[9] + m[10]*m[10])

            if (scaleX == 0f || scaleY == 0f || scaleZ == 0f) {
                return Rotation()
            }

            // --- Normalize rotation matrix ---
            val r00 = m[0] / scaleX
            val r01 = m[1] / scaleX
            val r02 = m[2] / scaleX

            val r10 = m[4] / scaleY
            val r11 = m[5] / scaleY
            val r12 = m[6] / scaleY

            val r20 = m[8] / scaleZ
            val r21 = m[9] / scaleZ
            val r22 = m[10] / scaleZ

            // --- Euler XYZ extraction ---
            val y: Double = asin(-r20.toDouble())
            val cosY = cos(y)

            val x: Double
            val z: Double

            if (abs(cosY) > 1e-6) {
                x = atan2(r21 / cosY, r22 / cosY)
                z = atan2(r10 / cosY, r00 / cosY)
            } else {
                // Gimbal lock
                x = 0.0
                z = atan2(-r01.toDouble(), r11.toDouble())
            }

            return Rotation(
                Math.toDegrees(x).toFloat(),
                Math.toDegrees(y).toFloat(),
                Math.toDegrees(z).toFloat()
            )
        }
    }
}
