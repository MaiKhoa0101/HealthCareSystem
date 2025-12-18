package com.hellodoc.healthcaresystem.model.dataclass.responsemodel
import kotlinx.serialization.Serializable


// --- DATA CLASSES (Khớp 100% với file JSON của bạn) ---
@Serializable
data class GestureFrame(
    val frame: Int,
    val timestamp: Float,
    val bones: Map<String, Map<String, String>> // Map<Nhóm, Map<TênXương, GiáTrịString>>
)



// Class nội bộ dùng để update xương trong engine
data class BoneRotation(
    val x: Float,
    val y: Float,
    val z: Float
)