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
import io.github.sceneview.math.Rotation as SceneRotation

fun updateBoneRotation(
    engine: Engine,
    modelInstance: ModelInstance?,
    boneName: String,
    x: Float, y: Float, z: Float
) {
    if (modelInstance == null) return

    // ⭐ THÊM KIỂM TRA NÀY
    // Nếu góc xoay là 0, bỏ qua (giữ nguyên trạng thái hiện tại)
    if (x == 0f && y == 0f && z == 0f) {
        return
    }
    println("vào được updatebone với x y z là "+x+" "+y+" "+z +" boneName là "+boneName)

    val asset = modelInstance.asset
    val boneEntity = asset.getFirstEntityByName(boneName)
    if (boneEntity == 0) return

    val tcm = engine.transformManager
    val instance = tcm.getInstance(boneEntity)
    if (instance == 0) return

    // Lấy Transform hiện tại
    val currentMat = FloatArray(16)
    tcm.getTransform(instance, currentMat)

    val currentPos = Position(currentMat[12], currentMat[13], currentMat[14])

    val scaleX = sqrt(
        currentMat[0] * currentMat[0] +
                currentMat[1] * currentMat[1] +
                currentMat[2] * currentMat[2]
    )
    val scaleY = sqrt(
        currentMat[4] * currentMat[4] +
                currentMat[5] * currentMat[5] +
                currentMat[6] * currentMat[6]
    )
    val scaleZ = sqrt(
        currentMat[8] * currentMat[8] +
                currentMat[9] * currentMat[9] +
                currentMat[10] * currentMat[10]
    )
    val currentScale = Scale(scaleX, scaleY, scaleZ)

    val newRotation = SceneRotation(x,y,z)
    println("NewRotation for "+boneName+" is $x $y $z")
    val newTransform = Transform(
        position = currentPos,
        rotation = newRotation,
        scale = currentScale
    ).toColumnsFloatArray()

    tcm.setTransform(instance, newTransform)
}