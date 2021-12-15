package org.laolittle.plugin.molly.utils

import kotlinx.serialization.json.JsonElement
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.laolittle.plugin.molly.MollyConfig.api_key
import org.laolittle.plugin.molly.MollyConfig.api_secret
import org.laolittle.plugin.molly.model.Json
import java.io.InputStream
import java.time.Duration

object OkHttp {


    private var client: OkHttpClient = OkHttpClient().newBuilder().connectTimeout(Duration.ofMillis(5_000)).build()

    fun getFile(url: String): InputStream {
        val request = Request.Builder().url(url)
            .get().build()
        val responseBody = client.newCall(request).execute().body
        if (responseBody != null){
            return responseBody.byteStream()
        } else {
            throw Exception("响应为空")
        }
    }

    fun String.post(url: String): JsonElement {
        val media = "application/json;charset=utf-8"
        val request = Request.Builder().url(url)
            .header("Content-Type", media)
            .header("Api-Key", api_key)
            .header("Api-Secret", api_secret)
            .post(this.toRequestBody(media.toMediaTypeOrNull())).build()
        val responseBody = client.newCall(request).execute().body
        if (responseBody != null){
            return responseBody.use { Json.parseToJsonElement(it.string()) }
        } else {
            throw Exception("响应为空")
        }
    }

}