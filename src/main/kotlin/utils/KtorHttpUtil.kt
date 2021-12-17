package org.laolittle.plugin.molly.utils

import io.ktor.client.*
import io.ktor.client.request.*
import kotlinx.serialization.json.JsonElement
import org.laolittle.plugin.molly.MollyConfig.api_key
import org.laolittle.plugin.molly.MollyConfig.api_secret
import org.laolittle.plugin.molly.model.Json
import java.io.InputStream

object KtorHttpUtil {
    private val client = HttpClient()

    suspend fun getFile(url: String): InputStream {
        return client.get(url)
    }

    suspend fun String.post(url: String): JsonElement {
        val responseData = client.post<String> {
            url(url)
            body = this@post
            header("Content-Type", "application/json;charset=utf-8")
            header("Api-Key", api_key)
            header("Api-Secret", api_secret)
        }
        return Json.parseToJsonElement(responseData)
    }
}