package org.laolittle.plugin.molly.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.put
import net.mamoe.mirai.utils.error
import net.mamoe.mirai.utils.info
import org.laolittle.plugin.molly.Molly
import org.laolittle.plugin.molly.MollyConfig
import org.laolittle.plugin.molly.utils.KtorHttpUtil.post
import java.security.cert.X509Certificate
import javax.net.ssl.*

object MollyApiService {

    var mollyReply: MutableMap<Int, MollyReply> = mutableMapOf()
    var inActMember = mutableSetOf<Long>()

    @ExperimentalSerializationApi
    suspend fun request(
        message: String,
        userId: Long,
        userName: String,
        groupName: String?,
        groupId: Long?,
        inGroup: Boolean
    ) {
        val mollyUrl = "https://i.mly.app/reply"

        val jsonRequest = buildJsonObject {
            put("content", message)
            put("type", if (!inGroup) 1 else 2)
            put("from", userId)
            put("fromName", userName)
            put("to", groupId)
            put("toName", groupName)
        }

        useInsecureSSL() // 忽略SSL证书
        val json = jsonRequest.toString().post(mollyUrl)
        if (MollyConfig.doPrintResultsOnConsole)
            Molly.logger.info { "服务器返回数据: $json" }
        try {
            val mollyData: MollyData = Json.decodeFromJsonElement(json)
            decode(mollyData.data)
        } catch (e: Exception) {
            val mollyError: MollyError = Json.decodeFromJsonElement(json)
            hasError(mollyError)
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
    private fun decode(msgData: JsonArray) {
        for ((i, json) in msgData.withIndex()) {
            mollyReply[i] = Json.decodeFromJsonElement(json)
        }
    }

    private fun useInsecureSSL() {
        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            override fun getAcceptedIssuers(): Array<X509Certificate>? = null
            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) = Unit
            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) = Unit
        })

        val sc = SSLContext.getInstance("SSL")
        sc.init(null, trustAllCerts, java.security.SecureRandom())
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.socketFactory)

        val allHostsValid = HostnameVerifier { _, _ -> true }

        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid)
    }
}