package com.hellodoc.healthcaresystem.view.user.supportfunction

import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.BoneRotation
import kotlin.math.abs

object MathUtils {
    // Nội suy tuyến tính (Linear Interpolation)
    fun lerp(start: Float, end: Float, fraction: Float): Float {
        return start + (end - start) * fraction
    }

    // Nội suy góc độ (xử lý vụ nhảy từ 180 -> -180)
    fun lerpAngle(start: Float, end: Float, fraction: Float): Float {
        var diff = end - start
        if (diff > 180) diff -= 360
        if (diff < -180) diff += 360
        return start + diff * fraction
    }

    // Nội suy cả object BoneRotation
    fun lerpBoneRotation(start: BoneRotation, end: BoneRotation, fraction: Float): BoneRotation {
        return BoneRotation(
            x = lerpAngle(start.x, end.x, fraction),
            y = lerpAngle(start.y, end.y, fraction),
            z = lerpAngle(start.z, end.z, fraction)
        )
    }
}