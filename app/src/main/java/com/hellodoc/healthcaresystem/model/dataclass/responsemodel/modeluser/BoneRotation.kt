package com.hellodoc.healthcaresystem.model.dataclass.responsemodel.modeluser

data class BoneRotation(
    val boneName: String, // Tên khớp xương (VD: "head", "upperarm_l")
    val x: Float, // Góc xoay trục X (độ)
    val y: Float, // Góc xoay trục Y (độ)
    val z: Float  // Góc xoay trục Z (độ)
)
