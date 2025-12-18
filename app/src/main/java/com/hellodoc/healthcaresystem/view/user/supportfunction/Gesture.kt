package com.hellodoc.healthcaresystem.view.user.supportfunction

import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.BoneRotation
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.GestureFrame

object GestureUtils {
    // Regex này bắt pattern: tencuaxuong(x=so, y=so, z=so)
    // Nó hoạt động tốt cho cả xương lẻ và chuỗi xương ngăn cách bởi dấu chấm phẩy
    private val boneParserRegex = Regex("""([a-zA-Z0-9_]+)\(x=([-0-9.]+),\s*y=([-0-9.]+),\s*z=([-0-9.]+)\)""")

    fun parseFrame(frame: GestureFrame): Map<String, BoneRotation> {
        val flatMap = mutableMapOf<String, BoneRotation>()

        // Duyệt qua tất cả các nhóm (spine, right_arm, fingers...)
        frame.bones.values.forEach { groupData ->
            // Duyệt qua từng value string trong nhóm
            groupData.values.forEach { valueString ->
                // Tìm tất cả các khớp xương trong chuỗi string đó
                // Ví dụ chuỗi: "thumb_01(x=...); thumb_02(x=...)" sẽ tìm thấy 2 kết quả
                boneParserRegex.findAll(valueString).forEach { matchResult ->
                    val (boneName, x, y, z) = matchResult.destructured
                    flatMap[boneName] = BoneRotation(
                        x = x.toFloatOrNull() ?: 0f,
                        y = y.toFloatOrNull() ?: 0f,
                        z = z.toFloatOrNull() ?: 0f
                    )
                }
            }
        }
        return flatMap
    }
}