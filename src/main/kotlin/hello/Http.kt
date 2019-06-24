package hello

import com.google.protobuf.Message
import java.io.IOException
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.RequestBody
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull

class Http {
    interface GwCallback {
        fun onError()
        fun onReply(reply: com.google.protobuf.Message)
    }

    private val client = OkHttpClient()

    var name = "My Ng ClientTest"
    var gwUrl = "https://gw.codein.net/api/"

    @Throws(Exception::class)
    fun get(url: String = "http://publicobject.com/helloworld.txt") {
        val request = Request.Builder()
                .url(url)
                //.tag(this)
                .build()

        val call = client.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                //val http = call.request().tag() as Http
                //println("tag: ${http.name}")
                response.body!!.use { responseBody ->
                    if (!response.isSuccessful) throw IOException("Unexpected code " + response)

                    val responseHeaders = response.headers
                    var i = 0
                    val size = responseHeaders.size
                    while (i < size) {
                        println(responseHeaders.name(i) + ": " + responseHeaders.value(i))
                        i++
                    }

                    println(responseBody.string())
                }
            }
        })
    }

    fun post(url: String, json: String) {
        val JSON = "application/json; charset=utf-8".toMediaTypeOrNull()
        val body = RequestBody.create(JSON, json)
        val request = Request.Builder()
                .url(url)
                .post(body)
                .tag(this)
                .build()

        val call = client.newCall(request)
        call.enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                val http = call.request().tag() as Http
                println("tag: ${http.name}")
                response.body!!.use { responseBody ->
                    if (!response.isSuccessful) throw IOException("Unexpected code " + response)

                    val responseHeaders = response.headers
                    var i = 0
                    val size = responseHeaders.size
                    while (i < size) {
                        println(responseHeaders.name(i) + ": " + responseHeaders.value(i))
                        i++
                    }

                    println(responseBody.string())
                }
            }
        })

    }

    fun gwCall(api: String, req: com.google.protobuf.Message, builder: com.google.protobuf.Message.Builder, callback: GwCallback?) {
        val mtype = "application/octet-stream".toMediaTypeOrNull()
        val body = RequestBody.create(mtype, req.toByteArray())
        val request = Request.Builder()
                .url(gwUrl + api)
                .post(body)
                .build()

        val call = client.newCall(request)
        call.enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                callback?.onError()
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                response.body!!.use { responseBody ->
                    if (!response.isSuccessful) throw IOException("Unexpected code " + response)

                    var reply = builder.mergeFrom(responseBody.byteStream()).build()
                    callback?.onReply(reply)
                }
            }
        })
    }
/*
    fun post(req: com.google.protobuf.Message, rep: com.google.protobuf.Message.Builder) {
        val reqStr = req.toString()
        println("request: $reqStr")
        var b = CodeInProto.LoginReply.newBuilder()
        b.liveTopic = "topic/live"
        b.userInfoBuilder.uid = 2017002L
        b.userInfoBuilder.name = "青云"
        var msg = b.build()

        var ba = msg.toByteArray()
        rep.mergeFrom(ba)
    }
*/
    companion object {

        private fun testGw(testId: Int) {
        }

        @Throws(Exception::class)
        @JvmStatic
        fun main(args: Array<String>) {
            (0 until 100).forEach {
                testGw(it)
            }
        }
    }
}
