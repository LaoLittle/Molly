package org.laolittle.plugin.molly.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray

@Serializable
data class MollyData(
    @SerialName("code") val code: String,
    @SerialName("message") val message: String,
    @SerialName("plugin") val plugin: String?,
    @SerialName("data") val data: JsonArray?
)

@Serializable
data class MollyReply(
    @SerialName("content") val content: String,
    @SerialName("typed") val typed: Int,
    @SerialName("remark") val remark: String?
)

internal val Json = Json {
    prettyPrint = true
    ignoreUnknownKeys = true
    isLenient = true
    allowStructuredMapKeys = true
}