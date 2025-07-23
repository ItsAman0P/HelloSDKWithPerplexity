package com.msg91.chatwidget.service

import com.msg91.chatwidget.config.ApiUrls
import kotlinx.coroutines.*
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

object ApiService {
    private const val IS_DEBUG = false

    fun log(vararg args: Any) {
        if (IS_DEBUG) {
            println("[MSG91 HELLO SDK]: ${args.joinToString(" ")}")
        }
    }

    suspend fun generateUUID(token: String, body: Map<String, Any>): String? {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL(ApiUrls.GENERATE_UUID)
                val connection = url.openConnection() as HttpURLConnection

                connection.apply {
                    requestMethod = "POST"
                    setRequestProperty("Accept", "application/json")
                    setRequestProperty("Content-Type", "application/json")
                    setRequestProperty("Authorization", token)
                    doOutput = true
                }

                val jsonBody = JSONObject(body).toString()
                connection.outputStream.use { os ->
                    os.write(jsonBody.toByteArray())
                }

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val response = connection.inputStream.bufferedReader().use { it.readText() }
                    val jsonResponse = JSONObject(response)
                    jsonResponse.optString("uuid", null)
                } else {
                    log("Error in generateUUID: HTTP $responseCode")
                    null
                }
            } catch (e: Exception) {
                log("Error in generateUUID", e.message)
                null
            }
        }
    }
}