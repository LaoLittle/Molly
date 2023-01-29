package org.laolittle.plugin.molly.utils

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.serialization.json.JsonElement
import org.laolittle.plugin.molly.MollyConfig.api_key
import org.laolittle.plugin.molly.MollyConfig.api_secret
import org.laolittle.plugin.molly.model.Json
import java.io.InputStream

object KtorOkHttp {
    private val client = HttpClient(OkHttp)

    suspend fun getFile(url: String): InputStream {
        return client.get(url).body()
    }

    suspend fun String.post(url: String): JsonElement {
        val responseData = client.post(url) client@{
            setBody(this@post)
            header("Content-Type", "application/json;charset=utf-8")
            header("Api-Key", api_key)
            header("Api-Secret", api_secret)
        }
        return Json.parseToJsonElement(responseData.bodyAsText())
    }
}