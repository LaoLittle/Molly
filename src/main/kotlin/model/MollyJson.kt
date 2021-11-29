package org.laolittle.plugin.molly.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray

/**
 * Molly机器人返回的正常数据
 * @param plugin 使用的Molly插件
 * @param data Molly机器人返回的消息[JsonArray]
 * */
@Serializable
data class MollyData(
    @SerialName("plugin") val plugin: String?,
    @SerialName("data") val data: JsonArray
)

/**
 * Molly机器人返回的[Json]数据内的[JsonArray]，其为回复消息的主要数据
 * @param content Molly机器人返回的消息字符串
 * @param typed Molly机器人返回的消息类型 1：文本，2：图片，3：文档，4：音频，9：其它文件
 * @param remark 表示附件上传时的原文件名
 * */
@Serializable
data class MollyReply(
    @SerialName("content") val content: String,
    @SerialName("typed") val typed: Int,
    @SerialName("remark") val remark: String?
)

/**
 * 当尝试接收消息失败时Molly机器人返回的错误信息
 * @param code Molly机器人返回的错误代码
 * @param message Molly机器人返回的错误信息
 * */

@Serializable
data class MollyError(
    @SerialName("code") val code: String,
    @SerialName("message") val message: String
)

internal val Json = Json {
    prettyPrint = true
    ignoreUnknownKeys = true
    isLenient = true
    allowStructuredMapKeys = true
}