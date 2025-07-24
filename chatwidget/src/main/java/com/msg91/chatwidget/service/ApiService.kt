package com.msg91.chatwidget.service

import com.msg91.chatwidget.config.ApiUrls
import com.msg91.chatwidget.utils.LogUtil
import kotlinx.coroutines.*
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

object ApiService {

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
                    LogUtil.log("Error in generateUUID: HTTP $responseCode")
                    null
                }
            } catch (e: Exception) {
                LogUtil.log("Error in generateUUID", e.message)
                null
            }
        }
    }
}