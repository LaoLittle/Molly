package org.laolittle.plugin.molly.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.put
import net.mamoe.mirai.utils.error
import org.laolittle.plugin.molly.Molly
import org.laolittle.plugin.molly.MollyConfig.api_key
import org.laolittle.plugin.molly.MollyConfig.api_secret
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

var mollyReply: Map<Int, MollyReply> = linkedMapOf()
var inActMember = mutableListOf<Long>()

private fun request(
    message: String,
    userId: Long,
    userName: String,
    groupName: String?,
    groupId: Long?,
    inGroup: Boolean
): String {
    val mollyUrl = "https://i.mly.app/reply"
    val connection = URL(mollyUrl).openConnection() as HttpURLConnection
    connection.requestMethod = "POST"
    connection.connectTimeout = 3000
    connection.doOutput = true
    connection.doInput = true
    connection.useCaches = false
    connection.instanceFollowRedirects = true

    connection.setRequestProperty("Api-Key", api_key)
    connection.setRequestProperty("Api-Secret", api_secret)
    connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8")
    connection.connect()

    val out = DataOutputStream(connection.outputStream)
    val json = if (inGroup)
        buildJsonObject {
            put("content", message)
            put("type", 2)
            put("from", userId)
            put("fromName", userName)
            put("to", groupId)
            put("toName", groupName)
        }
    else buildJsonObject {
        put("content", message)
        put("type", 1)
        put("from", userId)
        put("fromName", userName)
    }
    out.use {
        it.writeChars(json.toString())
        it.flush()
    }
    connection.disconnect() // 断开连接

    val input = connection.inputStream
    return if (input != null) {
        val writer: Writer = StringWriter()
        val buffer = CharArray(1024)
        input.use {
            val reader: Reader = BufferedReader(
                InputStreamReader(input, "UTF-8")
            )
            var n: Int
            while (reader.read(buffer).also { n = it } != -1) {
                writer.write(buffer, 0, n)
            }
        }
        writer.toString()
    } else {
        ""
    }
}

@ExperimentalSerializationApi
private fun hasError(mollyError: MollyError) {
    Molly.logger.error {
        """
            回复发生错误! 
            错误代码: ${mollyError.code}
            错误信息: ${mollyError.message}
        """.trimIndent()
    }
}

@ExperimentalSerializationApi
fun request(message: String, userId: Long, userName: String, groupName: String, groupId: Long) {
    val mollyJsonString = request(
        message,
        userId,
        userName,
        groupName,
        groupId,
        true
    )
    try{
        val mollyData: MollyData = Json.decodeFromString(mollyJsonString)
        decode(mollyData.data)
    } catch (e: Exception){
        val mollyError: MollyError = Json.decodeFromString(mollyJsonString)
        hasError(mollyError)
    }

}

@ExperimentalSerializationApi
fun request(message: String, userId: Long, userName: String) {
    val mollyJsonString = request(
        message,
        userId,
        userName,
        null,
        null,
        false
    )
    try {
        val mollyData: MollyData = Json.decodeFromString(mollyJsonString)
        decode(mollyData.data)
    } catch (e: Exception) {
        val mollyError: MollyError = Json.decodeFromString(mollyJsonString)
        hasError(mollyError)
    }
}

@ExperimentalSerializationApi
private fun decode(msgData: JsonArray) {
    for ((i, json) in msgData.withIndex()) {
        mollyReply = mollyReply.plus(i to Json.decodeFromJsonElement(json))
    }
}

fun mollyFile(url: String): InputStream {
    val connection: HttpURLConnection = URL(url).openConnection() as HttpURLConnection
    connection.connect()
    connection.disconnect()
    return connection.inputStream
}