import com.google.android.filament.Engine
import com.google.android.filament.RenderableManager
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.GestureFrame
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.Rotation
import io.github.sceneview.model.ModelInstance
import io.github.sceneview.math.Position
import io.github.sceneview.math.Scale
import io.github.sceneview.math.Transform
// Lưu ý: Chúng ta sẽ dùng Quaternion từ thư viện toán học gốc của SceneView
import dev.romainguy.kotlin.math.Quaternion
import dev.romainguy.kotlin.math.Float3
import io.github.sceneview.math.toColumnsFloatArray
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

fun updateBoneRotation(
    engine: Engine,
    modelInstance: ModelInstance?,
    boneName: String,
    x: Float, y: Float, z: Float // Góc xoay tuyệt đối (Độ)
) {
    if (modelInstance == null) return

    println("transform dược gọi")
    val asset = modelInstance.asset
    val boneEntity = asset.getFirstEntityByName(boneName)
    if (boneEntity == 0) return

    val tcm = engine.transformManager
    val instance = tcm.getInstance(boneEntity)
    if (instance == 0) return

    // 1. Lấy Transform hiện tại (Local transform relative to parent)
    val currentMat = FloatArray(16)
    tcm.getTransform(instance, currentMat)

    // 2. Trích xuất Position (Giữ nguyên vị trí xương so với cha)
    val currentPos = Position(currentMat[12], currentMat[13], currentMat[14])

    // 3. Trích xuất Scale (Giữ nguyên tỷ lệ)
    // Công thức tính độ dài vector cột
    val scaleX = sqrt((currentMat[0] * currentMat[0] + currentMat[1] * currentMat[1] + currentMat[2] * currentMat[2]).toDouble()).toFloat()
    val scaleY = sqrt((currentMat[4] * currentMat[4] + currentMat[5] * currentMat[5] + currentMat[6] * currentMat[6]).toDouble()).toFloat()
    val scaleZ = sqrt((currentMat[8] * currentMat[8] + currentMat[9] * currentMat[9] + currentMat[10] * currentMat[10]).toDouble()).toFloat()
    val currentScale = Scale(scaleX, scaleY, scaleZ)

    // 4. TẠO ROTATION MỚI (QUAN TRỌNG)
    // Chúng ta set trực tiếp góc mới, thay thế góc cũ hoàn toàn.
    val newRotation = Float3(x, y, z)

    // 5. Tạo Transform mới và cập nhật
    val newTransform = Transform(
        position = currentPos,
        rotation = newRotation, // SceneView sẽ tự đổi Euler sang Quaternion nội bộ
        scale = currentScale
    ).toColumnsFloatArray()

    tcm.setTransform(instance, newTransform)
}


/**
 * Reset bone về rotation mặc định
 */
fun resetBoneRotation(
    engine: Engine,
    modelInstance: ModelInstance,
    boneName: String
) {
    updateBoneRotation(engine, modelInstance, boneName, 0f, 0f, 0f)
}


fun applyFrameRotations(
    engine: Engine,
    modelInstance: ModelInstance,
    frame: GestureFrame
) {
    val bones = frame.bones

    // Spine
    applyBoneRotation(engine, modelInstance, "spine_01", bones.spine.spine01)
    applyBoneRotation(engine, modelInstance, "spine_02", bones.spine.spine02)
    applyBoneRotation(engine, modelInstance, "spine_03", bones.spine.spine03)

    // Neck & Head
    applyBoneRotation(engine, modelInstance, "neck", bones.neckHead.neck)
    applyBoneRotation(engine, modelInstance, "head", bones.neckHead.head)

    // Facial (nếu có)
    if (bones.facial.jaw.isNotEmpty()) {
        applyBoneRotation(engine, modelInstance, "jaw", bones.facial.jaw)
    }
    if (bones.facial.eyeL.isNotEmpty()) {
        applyBoneRotation(engine, modelInstance, "eye_l", bones.facial.eyeL)
    }
    if (bones.facial.eyeR.isNotEmpty()) {
        applyBoneRotation(engine, modelInstance, "eye_r", bones.facial.eyeR)
    }
    if (bones.facial.eyelidL.isNotEmpty()) {
        applyBoneRotation(engine, modelInstance, "eyelid_l", bones.facial.eyelidL)
    }
    if (bones.facial.eyelidR.isNotEmpty()) {
        applyBoneRotation(engine, modelInstance, "eyelid_r", bones.facial.eyelidR)
    }
    if (bones.facial.eyebrowL.isNotEmpty()) {
        applyBoneRotation(engine, modelInstance, "eyebrow_l", bones.facial.eyebrowL)
    }
    if (bones.facial.eyebrowR.isNotEmpty()) {
        applyBoneRotation(engine, modelInstance, "eyebrow_r", bones.facial.eyebrowR)
    }
    if (bones.facial.mouthL.isNotEmpty()) {
        applyBoneRotation(engine, modelInstance, "mouth_l", bones.facial.mouthL)
    }
    if (bones.facial.mouthR.isNotEmpty()) {
        applyBoneRotation(engine, modelInstance, "mouth_r", bones.facial.mouthR)
    }

    // Left Arm
    if (bones.leftArm.shoulderL.isNotEmpty()) {
        applyBoneRotation(engine, modelInstance, "shoulder_l", bones.leftArm.shoulderL)
    }
    if (bones.leftArm.upperarmL.isNotEmpty()) {
        applyBoneRotation(engine, modelInstance, "upperarm_l", bones.leftArm.upperarmL)
    }
    if (bones.leftArm.lowerarmL.isNotEmpty()) {
        applyBoneRotation(engine, modelInstance, "lowerarm_l", bones.leftArm.lowerarmL)
    }
    if (bones.leftArm.handL.isNotEmpty()) {
        applyBoneRotation(engine, modelInstance, "hand_l", bones.leftArm.handL)
    }

    // Left Fingers
    applyMultipleBoneRotations(engine, modelInstance, bones.leftArm.fingers.thumb)
    applyMultipleBoneRotations(engine, modelInstance, bones.leftArm.fingers.index)
    applyMultipleBoneRotations(engine, modelInstance, bones.leftArm.fingers.middle)
    applyMultipleBoneRotations(engine, modelInstance, bones.leftArm.fingers.ring)
    applyMultipleBoneRotations(engine, modelInstance, bones.leftArm.fingers.pinky)

    // Right Arm
    if (bones.rightArm.shoulderR.isNotEmpty()) {
        applyBoneRotation(engine, modelInstance, "shoulder_r", bones.rightArm.shoulderR)
    }
    if (bones.rightArm.upperarmR.isNotEmpty()) {
        applyBoneRotation(engine, modelInstance, "upperarm_r", bones.rightArm.upperarmR)
    }
    if (bones.rightArm.lowerarmR.isNotEmpty()) {
        applyBoneRotation(engine, modelInstance, "lowerarm_r", bones.rightArm.lowerarmR)
    }
    if (bones.rightArm.handR.isNotEmpty()) {
        applyBoneRotation(engine, modelInstance, "hand_r", bones.rightArm.handR)
    }

    // Right Fingers
    applyMultipleBoneRotations(engine, modelInstance, bones.rightArm.fingers.thumb)
    applyMultipleBoneRotations(engine, modelInstance, bones.rightArm.fingers.index)
    applyMultipleBoneRotations(engine, modelInstance, bones.rightArm.fingers.middle)
    applyMultipleBoneRotations(engine, modelInstance, bones.rightArm.fingers.ring)
    applyMultipleBoneRotations(engine, modelInstance, bones.rightArm.fingers.pinky)
}

/**
 * Apply rotation cho 1 bone từ string
 */
fun applyBoneRotation(
    engine: Engine,
    modelInstance: ModelInstance,
    boneName: String,
    rotationStr: String
) {
    if (rotationStr.isEmpty()) return

    val rotation = Rotation.fromString(rotationStr)
    updateBoneRotation(engine, modelInstance, boneName, rotation.x, rotation.y, rotation.z)
}

/**
 * Apply rotations cho nhiều bones từ string có nhiều bones
 */
fun applyMultipleBoneRotations(
    engine: Engine,
    modelInstance: ModelInstance,
    rotationsStr: String
) {
    if (rotationsStr.isEmpty()) return

    val rotations = Rotation.parseMultiple(rotationsStr)
    rotations.forEach { (boneName, rotation) ->
        updateBoneRotation(engine, modelInstance, boneName, rotation.x, rotation.y, rotation.z)
    }
}


/**
 * Reset tất cả bones về rotation mặc định
 */
fun resetAllBones(
    engine: Engine,
    modelInstance: ModelInstance,
    boneNames: List<String>
) {
    boneNames.forEach { boneName ->
        resetBoneRotation(engine, modelInstance, boneName)
    }
}