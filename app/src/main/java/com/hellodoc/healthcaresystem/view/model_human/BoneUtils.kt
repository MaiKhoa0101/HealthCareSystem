//import android.content.Context
//import java.io.BufferedReader
//import java.io.InputStreamReader
//
//data class BoneData(
//    val code: String,      // Cột Mã (VD: 1A)
//    val boneName: String,  // Cột Tên bộ phận (VD: thumb_01_l)
//    val rotX: Float,
//    val rotY: Float,
//    val rotZ: Float
//)
//
//fun loadBoneData(context: Context, fileName: String): List<BoneData> {
//    val list = mutableListOf<BoneData>()
//    try {
//        val inputStream = context.assets.open(fileName)
//        val reader = BufferedReader(InputStreamReader(inputStream))
//
//        // Bỏ qua dòng tiêu đề
//        reader.readLine()
//
//        var line = reader.readLine()
//        while (line != null) {
//            if (line.isNotBlank()) {
//                val tokens = line.split(",")
//                // Đảm bảo dòng có đủ dữ liệu (ít nhất 7 cột theo file template tôi gửi)
//                if (tokens.size >= 7) {
//                    list.add(BoneData(
//                        // Cột 2 là Mã, Cột 1 là Tên Xương (Theo template CSV)
//                        code = tokens[2].trim(),
//                        boneName = tokens[1].trim(),
//                        // Cột 4,5,6 là X, Y, Z
//                        rotX = tokens[4].trim().toFloatOrNull() ?: 0f,
//                        rotY = tokens[5].trim().toFloatOrNull() ?: 0f,
//                        rotZ = tokens[6].trim().toFloatOrNull() ?: 0f
//                    ))
//                }
//            }
//            line = reader.readLine()
//        }
//    } catch (e: Exception) {
//        e.printStackTrace()
//    }
//    return list
//}