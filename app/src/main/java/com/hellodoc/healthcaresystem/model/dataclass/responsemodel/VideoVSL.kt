package com.hellodoc.healthcaresystem.model.dataclass.responsemodel


data class ResponseVSLVideo(
    val playlist:List<VSL>,
    val reorder_debug: ReorderDebug,
)

data class ReorderDebug(
    val before: String,
    val after: String
)

data class VSL(
    val gross: String,
    val url: String
)