package com.android.pexels.network

import com.android.pexels.utilities.GET_HTTP_REQUEST_METHOD
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future
import javax.net.ssl.HttpsURLConnection

data class JsonRequest(var url: String, var headers: Map<String, String>)

interface Callback<T> {

    fun onSuccess(response: T)

    fun onError(errorMessage: String)
}

class JsonHttpsRequest(
    var jsonRequest: JsonRequest,
    var executor: ExecutorService,
    var callback: Callback<String>
) : Callable<String> {

    fun execute(): Future<String> = executor.submit(this)

    override fun call(): String {
        val connection: HttpsURLConnection =
            URL(jsonRequest.url).openConnection() as HttpsURLConnection
        for (headerKey in jsonRequest.headers.keys) {
            connection.addRequestProperty(headerKey, jsonRequest.headers[headerKey])
        }
        connection.requestMethod = GET_HTTP_REQUEST_METHOD
        try {
            connection.connect()
            val response: String = readInputStream(InputStreamReader(connection.inputStream))
            callback.onSuccess(response)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    private fun readInputStream(bufferedInputStream: InputStreamReader): String {
        val sb = StringBuffer()
        var str: String?
        val reader = BufferedReader(bufferedInputStream)
        while (reader.readLine().also { str = it } != null) {
            sb.append(str)
        }
        return sb.toString()
    }
}