import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.BoneRotation
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.GestureFrame
import com.hellodoc.healthcaresystem.view.user.supportfunction.GestureUtils
import com.hellodoc.healthcaresystem.view.user.supportfunction.MathUtils
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class SignLanguageViewModel : ViewModel() {

    // State chứa danh sách xương và góc quay hiện tại
    private val _currentBoneState = MutableStateFlow<Map<String, BoneRotation>>(emptyMap())
    val currentBoneState = _currentBoneState.asStateFlow()

    private var animationJob: Job? = null
    private var gestureFrames: List<GestureFrame> = emptyList()

    // Cấu hình: Mỗi bước trong JSON sẽ diễn ra trong 400ms
    private val DURATION_PER_FRAME_MS = 400L
    private val FPS_DELAY = 16L // ~60 FPS update rate

    fun loadData(jsonString: String) {
        try {
            val json = Json { ignoreUnknownKeys = true }
            gestureFrames = json.decodeFromString<List<GestureFrame>>(jsonString)
                .sortedBy { it.frame }
            println("ViewModel: Đã load ${gestureFrames.size} frame dữ liệu.")
        } catch (e: Exception) {
            println("ViewModel Error: Lỗi parse JSON: ${e.message}")
        }
    }

    fun playAnimation() {
        if (gestureFrames.isEmpty()) {
            println("ViewModel: List frame rỗng, không chạy animation.")
            return
        }
        stopAnimation()

        animationJob = viewModelScope.launch {
            println("ViewModel: Bắt đầu chạy Animation...")
            while (true) { // Lặp vô tận

                // Duyệt qua từng Frame trong JSON
                for (i in 0 until gestureFrames.size - 1) {
                    // Lấy dữ liệu Frame hiện tại (Start) và Frame kế tiếp (End)
                    val startMap = GestureUtils.parseFrame(gestureFrames[i])
                    val endMap = GestureUtils.parseFrame(gestureFrames[i + 1])

                    val startTime = System.currentTimeMillis()

                    // Vòng lặp nhỏ này chạy trong 0.4s để chuyển từ Start -> End
                    while (System.currentTimeMillis() - startTime < DURATION_PER_FRAME_MS) {
                        val elapsed = System.currentTimeMillis() - startTime
                        val fraction = (elapsed.toFloat() / DURATION_PER_FRAME_MS).coerceIn(0f, 1f)

                        // Tính toán trạng thái trung gian (Interpolation)
                        val interpolatedMap = mutableMapOf<String, BoneRotation>()
                        val allKeys = startMap.keys + endMap.keys // Gộp key của cả 2 frame

                        for (key in allKeys) {
                            val startRot = startMap[key] ?: BoneRotation(0f,0f,0f)
                            val endRot = endMap[key] ?: startRot

                            // Hàm lerpBoneRotation nằm trong MathUtils (xem bên dưới)
                            interpolatedMap[key] = MathUtils.lerpBoneRotation(startRot, endRot, fraction)
                        }

                        // CẬP NHẬT UI
                        _currentBoneState.value = interpolatedMap

                        // Nghỉ 16ms để UI kịp vẽ (60fps)
                        delay(FPS_DELAY)
                    }
                }

                // Hết 1 vòng JSON, nghỉ 1s rồi lặp lại
                delay(1000)
            }
        }
    }

    fun stopAnimation() {
        animationJob?.cancel()
        animationJob = null
    }
}