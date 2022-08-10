package org.laolittle.plugin.molly.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.*
import net.mamoe.mirai.utils.error
import net.mamoe.mirai.utils.info
import org.laolittle.plugin.molly.Molly
import org.laolittle.plugin.molly.MollyConfig
import org.laolittle.plugin.molly.MollyConfig.UnknownReply.*
import org.laolittle.plugin.molly.MollyData.customUnknownReply
import org.laolittle.plugin.molly.utils.KtorOkHttp.post
import java.security.cert.X509Certificate
import javax.net.ssl.*

object MollyApiService {
    val inActMember = mutableSetOf<Long>()

    @ExperimentalSerializationApi
    suspend fun request(
        message: String,
        userId: Long,
        userName: String,
        groupName: String?,
        groupId: Long?,
        inGroup: Boolean
    ): List<MollyReply> {
        val mollyUrl = "https://api.mlyai.com/reply"

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
        return runCatching {
            val mollyData: MollyData = Json.decodeFromJsonElement(json)
            val replyData = if (mollyData.plugin == null) {
                when (MollyConfig.unknownReplyBehavior) {
                    DEFAULT -> mollyData
                    LOCAL -> {
                        mollyData.copy(data = buildJsonArray {
                            addJsonObject {
                                val nullStr: String? = null
                                put("content", customUnknownReply.random())
                                put("typed", 1)
                                put("remark", nullStr)
                            }
                        })
                    }
                    OFF -> mollyData.copy(data = buildJsonArray { })
                }
            } else mollyData
            decode(replyData.data)
        }.onFailure {
            val mollyError: MollyError = Json.decodeFromJsonElement(json)
            hasError(mollyError)
            when (mollyError.code) {
                "C1001" -> return decode(buildJsonArray {
                    addJsonObject {
                        val nullStr: String? = null
                        put("content", mollyError.message)
                        put("typed", 5)
                        put("remark", nullStr)
                    }
                })
            }
        }.getOrElse { throw Exception("解析错误! $json") }
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
    private fun decode(msgData: JsonArray): List<MollyReply> {
        val mollyReply = mutableListOf<MollyReply>()
        for (json in msgData) {
            mollyReply.add(Json.decodeFromJsonElement(json))
        }
        return mollyReply
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