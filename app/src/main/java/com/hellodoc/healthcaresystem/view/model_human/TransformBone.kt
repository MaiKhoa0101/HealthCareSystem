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



data class Quaternion(
    val x: Float,
    val y: Float,
    val z: Float,
    val w: Float
)

fun eulerToQuaternion(xDeg: Float, yDeg: Float, zDeg: Float): Quaternion {
    val x = Math.toRadians(xDeg.toDouble()).toFloat()
    val y = Math.toRadians(yDeg.toDouble()).toFloat()
    val z = Math.toRadians(zDeg.toDouble()).toFloat()

    val cx = cos(x / 2)
    val sx = sin(x / 2)
    val cy = cos(y / 2)
    val sy = sin(y / 2)
    val cz = cos(z / 2)
    val sz = sin(z / 2)

    return Quaternion(
        x = sx * cy * cz + cx * sy * sz,
        y = cx * sy * cz - sx * cy * sz,
        z = cx * cy * sz + sx * sy * cz,
        w = cx * cy * cz - sx * sy * sz
    )
}

fun multiply(a: Quaternion, b: Quaternion): Quaternion {
    return Quaternion(
        w = a.w * b.w - a.x * b.x - a.y * b.y - a.z * b.z,
        x = a.w * b.x + a.x * b.w + a.y * b.z - a.z * b.y,
        y = a.w * b.y - a.x * b.z + a.y * b.w + a.z * b.x,
        z = a.w * b.z + a.x * b.y - a.y * b.x + a.z * b.w
    )
}
fun extractQuaternion(m: FloatArray): Quaternion {
    val trace = m[0] + m[5] + m[10]
    return if (trace > 0) {
        val s = sqrt(trace + 1.0f) * 2
        Quaternion(
            w = 0.25f * s,
            x = (m[9] - m[6]) / s,
            y = (m[2] - m[8]) / s,
            z = (m[4] - m[1]) / s
        )
    } else {
        Quaternion(0f, 0f, 0f, 1f)
    }
}
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