package com.android.pexels.network

import com.android.pexels.utilities.GET_HTTP_REQUEST_METHOD
import com.android.pexels.utilities.HTTP_HEADER_AUTHORIZATION
import com.android.pexels.utilities.HTTP_RESPONSE_CODE_SUCCESS
import com.android.pexels.utilities.PEXEL_API_KEY
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception
import java.net.URL
import java.net.UnknownHostException
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future
import javax.net.ssl.HttpsURLConnection

data class JsonRequest(var url: String, var headers: Map<String, String>)

/**
 * Callback for network request.
 */
interface Callback<T> {

    fun onSuccess(response: T)

    fun onError(errorMessage: String)
}

class JsonHttpsRequest(
    private var jsonRequest: JsonRequest,
    private var executor: ExecutorService,
    private var callback: Callback<String>
) : Callable<String> {

    fun execute(): Future<String> = executor.submit(this)

    override fun call(): String {
        var httpsConnection: HttpsURLConnection? = null
        try {
            httpsConnection = (URL(jsonRequest.url).openConnection() as HttpsURLConnection).also {
                it.addRequestProperty(HTTP_HEADER_AUTHORIZATION, PEXEL_API_KEY)
                it.requestMethod = GET_HTTP_REQUEST_METHOD
                it.connect()
                when (it.responseCode) {
                    HTTP_RESPONSE_CODE_SUCCESS -> {
                        callback.onSuccess(readInputStream(InputStreamReader(it.inputStream)))
                    }
                    else -> callback.onError("Sorry, We couldn't load your photos at the moment.")
                }
            }
        } catch (unknownHost: UnknownHostException) {
            callback.onError("Sorry, We couldn't load your photos at the moment.")
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            httpsConnection?.disconnect()
        }
        return ""
    }


    // Converts the input stream to string.
    private fun readInputStream(inputStreamReader: InputStreamReader): String {
        val sb = StringBuffer()
        var str: String?
        val reader = BufferedReader(inputStreamReader)
        while (reader.readLine().also { str = it } != null) {
            sb.append(str)
        }
        return sb.toString()
    }
}