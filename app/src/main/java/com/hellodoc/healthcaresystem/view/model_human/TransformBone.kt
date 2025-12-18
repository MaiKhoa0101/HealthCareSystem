import com.google.android.filament.Engine
import com.google.android.filament.RenderableManager
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