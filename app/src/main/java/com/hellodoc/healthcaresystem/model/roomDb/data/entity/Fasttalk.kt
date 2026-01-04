package com.hellodoc.healthcaresystem.model.roomDb.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "quick_responses")
data class QuickResponseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val question: String,
    val response: String,
    val patientId: String = ""
)

// Bảng 1: Lưu các từ (Nodes)
// Dữ liệu lấy từ: start.properties.name, start.labels
@Entity(tableName = "words")
data class WordEntity(
    @PrimaryKey
    val word: String, // Ví dụ: "tôi", "ghét", "ăn" (Dùng làm ID luôn nếu là duy nhất)
    val label: String // Ví dụ: "P", "V"
)

// Bảng 2: Lưu mối quan hệ và trọng số (Edges)
// Dữ liệu lấy từ: relationship segments
@Entity(
    tableName = "word_edges",
    // Khóa ngoại để đảm bảo toàn vẹn dữ liệu: Xóa từ -> Xóa luôn quan hệ
    foreignKeys = [
        ForeignKey(
            entity = WordEntity::class,
            parentColumns = ["word"],
            childColumns = ["fromWord"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = WordEntity::class,
            parentColumns = ["word"],
            childColumns = ["toWord"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    // Index giúp truy vấn "Từ này liên kết đến từ nào?" cực nhanh
    indices = [Index("fromWord"), Index("toWord")]
)
data class WordEdgeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fromWord: String, // Node bắt đầu (start)
    val toWord: String,   // Node kết thúc (end)
    val relateType: String, // Ví dụ: "Related_To"
    val weight: Double    // Quan trọng: lấy từ properties.weight (ví dụ: 2.066)
)

data class WordPrediction(
    val nextWord: String,
    val weight: Double,
    val label: String
)

// 1. Class đại diện cho một phần tử trong mảng JSON gốc
data class Neo4jPath(
    @SerializedName("segments") val segments: List<Neo4jSegment>,
    @SerializedName("length") val length: Int
    // start và end ở cấp này có thể bỏ qua nếu chỉ quan tâm đến segments
)

// 2. Class Segment (cái bạn hỏi)
data class Neo4jSegment(
    @SerializedName("start") val startNode: Neo4jNode,
    @SerializedName("end") val endNode: Neo4jNode,
    @SerializedName("relationship") val relationship: Neo4jRelationship
)

// 3. Class đại diện cho Node (Đỉnh: P, V...)
data class Neo4jNode(
    @SerializedName("labels") val labels: List<String>,      // Ví dụ: ["P"]
    @SerializedName("properties") val properties: NodeProps, // Chứa name
    @SerializedName("elementId") val elementId: String
)

data class NodeProps(
    @SerializedName("name") val name: String // Ví dụ: "tôi", "ghét"
)

// 4. Class đại diện cho Relationship (Cạnh: Related_To)
data class Neo4jRelationship(
    @SerializedName("type") val type: String,               // Ví dụ: "Related_To"
    @SerializedName("properties") val properties: RelProps  // Chứa weight
)

data class RelProps(
    @SerializedName("weight") val weight: Double = 0.0      // Ví dụ: 2.066
)