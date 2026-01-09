package com.hellodoc.healthcaresystem.model.dataclass.responsemodel

import java.time.ZonedDateTime
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import java.util.Date

@Serializable
data class ApiResponse(
    val success: Boolean,
    val fromCache: Boolean,
    val question: Question,
    val answer: Answer,
    val neo4j: Neo4jData,
    val qdrant: QdrantData,
    val timestamp: String,
    val metadata: JsonObject = JsonObject(emptyMap())
)

@Serializable
data class Question(
    val text: String,
    val hasEmbedding: Boolean,
    val cached: Boolean,
    val similarity: Double
)

@Serializable
data class Answer(
    val text: String,
    val tokens: List<String>,
    val posTags: List<String>,
    val tokenCount: Int
)

@Serializable
data class Neo4jData(
    val nodes: List<Node>,
    val relations: List<Relation>,
    val answerNodeId: String
)

@Serializable
data class Node(
    val id: String? = null,
    val labels: List<String>? = null,
    val label: String? = null,
    val name: String,
    val properties: NodeProperties? = null,
    val existed: Boolean
)

@Serializable
data class NodeProperties(
    val createdAt: String,
    val name: String? = null,
    val fullText: String? = null,
    val tokenCount: Int? = null,
    val type: String? = null,
    val tokens: String? = null,
    val posTags: String? = null,
    val originalToken: String? = null,
    val position: Int? = null,
    val inAnswer: String? = null
)

@Serializable
sealed class RelationTarget {
    data class QuestionTarget(
        val createdAt: ZonedDateTime,
        val name: String,
        val fullText: String,
        val tokenCount: Int,
        val type: String
    ) : RelationTarget()

    data class AnswerTarget(
        val createdAt: ZonedDateTime,
        val name: String,
        val fullText: String,
        val tokenCount: Int,
        val type: String,
        val tokens: String,
        val posTags: String
    ) : RelationTarget()

    data class TokenTarget(
        val name: String,
        val originalToken: String? = null,
        val position: Int? = null,
        val inAnswer: String? = null
    ) : RelationTarget()
}

@Serializable
data class Relation(
    val from: JsonObject,
    val relation: String,
    val weight: Double,
    val to: JsonObject,
    val existed: Boolean,
    val action: String
)

@Serializable
data class QdrantData(
    val questionId: String,
    val collection: String,
    val vectorSize: Int
)

// Enum cho các loại quan hệ nếu cần
enum class RelationType {
    HAS_ANSWER,
    CONTAINS_TOKEN,
    RELATED_TO,
    ADVERB_VERB,
    VERB_NOUN,
    NOUN_VERB,
    CO_OCCURS_WITH
}

// Enum cho POS tags
enum class PosTag {
    P,  // Pronoun
    R,  // Adverb
    V,  // Verb
    N   // Noun
}