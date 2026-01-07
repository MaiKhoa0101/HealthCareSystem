package com.hellodoc.healthcaresystem.model.dataclass.responsemodel
import com.google.gson.annotations.SerializedName

data class SuggestLine(
    val content:String,
    val id: String
)
data class WordResultResponse(
    val startNode: String?,      // Có thể null
    val startLabel: String?,     // Có thể null
    val endNode: String?,        // Có thể null
    val endLabel: String?,       // Có thể null
    val weight: Double?,         // Có thể null
    val relType: String?         // Có thể null
)

data class Neo4jResultItem(
    @SerializedName("p") val pathData: Neo4jPathData
)

/**
 * 2. Dữ liệu bên trong key "p" (Path)
 */
data class Neo4jPathData(
    @SerializedName("start") val startNode: Neo4jNode,
    @SerializedName("end") val endNode: Neo4jNode,
    @SerializedName("segments") val segments: List<Neo4jSegment>,
    @SerializedName("length") val length: Double
)

/**
 * 3. Segment: Một đoạn nối (Node -> Relationship -> Node)
 */
data class Neo4jSegment(
    @SerializedName("start") val startNode: Neo4jNode,
    @SerializedName("end") val endNode: Neo4jNode,
    @SerializedName("relationship") val relationship: Neo4jRelationship
)

/**
 * 4. Node: Đại diện cho đỉnh (P:tôi, V:ghét...)
 */
data class Neo4jNode(
    @SerializedName("identity") val identity: Long,
    @SerializedName("labels") val labels: List<String>,      // Ví dụ: ["P"], ["V"]
    @SerializedName("properties") val properties: NodeProps, // Chứa name
    @SerializedName("elementId") val elementId: String
)

/**
 * 5. Properties của Node
 */
data class NodeProps(
    @SerializedName("name") val name: String = "" // Mặc định rỗng để tránh null
)

/**
 * 6. Relationship: Đại diện cho cạnh nối
 */
data class Neo4jRelationship(
    @SerializedName("identity") val identity: Long,
    @SerializedName("type") val type: String,               // Ví dụ: "Related_To"
    @SerializedName("properties") val properties: RelProps, // Chứa weight
    @SerializedName("elementId") val elementId: String,
    @SerializedName("startNodeElementId") val startNodeElementId: String,
    @SerializedName("endNodeElementId") val endNodeElementId: String
)

/**
 * 7. Properties của Relationship
 */
data class RelProps(
    @SerializedName("weight") val weight: Double = 0.0      // Mặc định 0.0 để tránh null
)