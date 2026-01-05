package com.hellodoc.healthcaresystem.model.dataclass.responsemodel
import com.google.gson.annotations.SerializedName

data class SuggestLine(
    val content:String,
    val id: String
)

// 1. Class bao ngoài cùng (Vì JSON là mảng các object chứa key "p")
// Retrofit sẽ trả về: List<Neo4jResultItem>
data class WordResultResponse(
    val source: String,
    val suggestion: String,
    val score: Double,
    val label: List<String>
)

// 2. Dữ liệu bên trong key "p"
data class Neo4jPathData(
    @SerializedName("start") val startNode: Neo4jNode,
    @SerializedName("end") val endNode: Neo4jNode,
    @SerializedName("segments") val segments: List<Neo4jSegment>,
    @SerializedName("length") val length: Double
)

// 3. Segment (Một đoạn nối: Node -> Relation -> Node)
data class Neo4jSegment(
    @SerializedName("start") val startNode: Neo4jNode,
    @SerializedName("end") val endNode: Neo4jNode,
    @SerializedName("relationship") val relationship: Neo4jRelationship
)

// 4. Node (Đỉnh - start/end)
data class Neo4jNode(
    @SerializedName("identity") val identity: Long,
    @SerializedName("labels") val labels: List<String>,      // Ví dụ: ["P"], ["V"]
    @SerializedName("properties") val properties: NodeProps, // Chứa name
    @SerializedName("elementId") val elementId: String
)

// 5. Properties của Node
data class NodeProps(
    @SerializedName("name") val name: String // Ví dụ: "tôi", "ghét"
)

// 6. Relationship (Cạnh - relationship)
data class Neo4jRelationship(
    @SerializedName("identity") val identity: Long,
    @SerializedName("type") val type: String,               // Ví dụ: "Related_To"
    @SerializedName("properties") val properties: RelProps, // Chứa weight
    @SerializedName("elementId") val elementId: String,
    @SerializedName("startNodeElementId") val startNodeElementId: String,
    @SerializedName("endNodeElementId") val endNodeElementId: String
)

// 7. Properties của Relationship
data class RelProps(
    @SerializedName("weight") val weight: Double = 0.0      // Ví dụ: 2.066, 0.0
)