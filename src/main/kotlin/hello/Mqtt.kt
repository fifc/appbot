package hello

import java.net.*
import java.io.*
import java.util.*
import kotlin.experimental.and
import kotlin.concurrent.thread
//import org.eclipse.paho.client.mqttv3.*
//import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import org.eclipse.paho.mqttv5.client.*
import org.eclipse.paho.mqttv5.client.persist.MemoryPersistence
import java.sql.Timestamp

import com.google.protobuf.Message
import org.eclipse.paho.mqttv5.common.MqttException
import org.eclipse.paho.mqttv5.common.MqttMessage
import org.eclipse.paho.mqttv5.common.packet.MqttProperties
import java.net.InetAddress

interface MqttListener {
}

class Mqtt constructor(var uid: Long, private var listener: MqttListener): MqttCallback {
    override fun authPacketArrived(p0: Int, p1: MqttProperties?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun disconnected(p0: MqttDisconnectResponse?) {
        val time = Timestamp(System.currentTimeMillis())
        Thread.sleep(1000L)
        try {
            connect(broker_, clientId_, true, uid.toString(), password_)
            for (topic in topics_) {
                subscribe(topic, qos_)
            }
        } catch (e: MqttException) {
            println("reason " + e.reasonCode)
            println("msg " + e.message)
            println("loc " + e.localizedMessage)
            println("cause " + e.cause)
            println("excep " + e)
            e.printStackTrace()
        }
    }

    override fun mqttErrorOccurred(e: MqttException?) {
    }

    private var brokerUrl: String? = null
    private var client: MqttAsyncClient? = null

    override fun connectComplete(reconnect: Boolean, serverURI: String) {
        for (topic in topics_) {
            subscribe(topic, qos_)
        }
        println("[$uid] mqtt connected ...")
    }

    override fun messageArrived(topic: String?, msg: MqttMessage?) {
        val time = Timestamp(System.currentTimeMillis())

        if (msg?.payload == null || topic == null || msg.payload.size < 10) {
            return
        }
        try {
            var msgUid = 0L
            var channel = 0
            when {
                topic.startsWith("codein/live/") -> {
                    msgUid = topic.substring(12).toLong()
                }
                topic.startsWith("im/user/") -> {
                    channel = 1
                }
                topic.startsWith("im/group/") -> {
                    channel = 1
                };
                else -> return
            }

            val arr = msg.payload as ByteArray
            val type = (arr[0] and 0xff.toByte()).toInt() shl 8 or
                    (arr[1] and 0xff.toByte()).toInt()
            val msgId = (arr[0] and 0xff.toByte()).toLong() shl 56 or
                    ((arr[3] and 0xff.toByte()).toLong() shl 48) or
                    ((arr[4] and 0xff.toByte()).toLong() shl 40) or
                    ((arr[5] and 0xff.toByte()).toLong() shl 32) or
                    ((arr[6] and 0xff.toByte()).toLong() shl 24) or
                    ((arr[7] and 0xff.toByte()).toLong() shl 16) or
                    ((arr[8] and 0xff.toByte()).toLong() shl 8) or
                    (arr[9] and 0xff.toByte()).toLong()
/*
            when(channel) {
                0 -> when (LiveMessageType.forNumber(type)) {
                    LiveMessageType.LMT_GAME_CONTACT_INFO -> {
                        var mqttMsg = MessagingProto.GameContactInfo.parseFrom(arr.sliceArray(10 until arr.size));
                        listener.onGameContactInfo(mqttMsg, msgUid, msgId)
                    }
                    LiveMessageType.LMT_NEARBY_USER_UPDATE -> {
                        var mqttMsg = MessagingProto.NearbyUserUpdate.parseFrom(arr.sliceArray(10 until arr.size))
                        listener.onNearbyUserUpdate(mqttMsg, msgUid, msgId)
                    }
                    else -> {
                    }
                }
                1 -> when(InstantMessageType.forNumber(type)) {
                    InstantMessageType.IM_ADD_CONTACT -> {
                        var mqttMsg = MessagingProto.ImAddContact.parseFrom(arr.sliceArray(10 until arr.size))
                        listener.onImAddContact(mqttMsg, msgUid, msgId)
                    }
                    else -> {

                    }
                }
                else -> {

                }
            }
*/
        } catch (e: com.google.protobuf.InvalidProtocolBufferException) {
        } catch (e: Exception) {

        }

        //println("Msg Arrive Time: $time Topic: $topic Qos: ${msg?.qos}\nPayload: $str")
    }

    override fun deliveryComplete(token: IMqttDeliveryToken?) {
        //LOGGER.info("message delivery complete. token $token")
    }

    private fun connect(brokerUrl: String, clientId: String, cleanSession: Boolean, userName: String?, password: String?): Boolean {
        this.brokerUrl = brokerUrl
        val persistence = MemoryPersistence()
        var ok = false
        try {
            val connOpt = MqttConnectionOptions()
            connOpt.isAutomaticReconnect = true
            connOpt.isCleanSession = cleanSession
            if (userName != null) {
                connOpt.userName = userName;
                if (password != null)
                    connOpt.password = password.toByteArray()
            }

            val cl = MqttAsyncClient(brokerUrl, clientId, persistence)
            client = cl
            cl.setCallback(this)
            cl.connect(connOpt, object: MqttActionListener {
                override fun onFailure(p0: IMqttToken?, p1: Throwable?) {
                    println("connect failure")
                }

                override fun onSuccess(p0: IMqttToken?) {
                    println("connect success")
                }
            })


            ok = true
        } catch (e: MqttException) {
            e.printStackTrace()
            println("error connect to $brokerUrl $e")
        }

        return ok;
    }

    fun connected(): Boolean {
        return if (client != null) client!!.isConnected else false
    }

    private fun publish(topic: String, qos: Int, payload: ByteArray){
        //val time = Timestamp(System.currentTimeMillis())
        //println("topic: $topic qos: $qos payload: $payload")
        val msg = MqttMessage()
        msg.payload = payload
        msg.qos = qos
        client?.publish(topic, msg)
    }

    fun sendLiveMsg(msg: com.google.protobuf.Message, type: Int, id: Long = 0) {
        var body = msg.toByteArray()
        val payload = ByteArray(size = body.size + 10)
        payload[0] = ((type shr 8) and 0xff).toByte()
        payload[1] = (type and 0xff).toByte()
        payload[2] = (id shr 56).toByte()
        payload[3] = (id shr 48).toByte()
        payload[4] = (id shr 40).toByte()
        payload[5] = (id shr 32).toByte()
        payload[6] = (id shr 24).toByte()
        payload[7] = (id shr 16).toByte()
        payload[8] = (id shr 8).toByte()
        payload[9] = id.toByte()

        (0 until body.size).forEach {
            payload[it + 10] = body[it]
        }

        return publish("codein/live", 1, payload)
    }

    fun sendImMsg(msg: com.google.protobuf.Message, type: Int, id: Long = 0) {
        var body = msg.toByteArray()
        val payload = ByteArray(size = body.size + 10)
        payload[0] = ((type shr 8) and 0xff).toByte()
        payload[1] = (type and 0xff).toByte()
        payload[2] = (id shr 56).toByte()
        payload[3] = (id shr 48).toByte()
        payload[4] = (id shr 40).toByte()
        payload[5] = (id shr 32).toByte()
        payload[6] = (id shr 24).toByte()
        payload[7] = (id shr 16).toByte()
        payload[8] = (id shr 8).toByte()
        payload[9] = id.toByte()

        (0 until body.size).forEach {
            payload[it + 10] = body[it]
        }

        return publish("im/sys", 1, payload)
    }

    private fun subscribe(topic: String, qos: Int) {
        //println("subscribing to topic $topic qos $qos")
        client?.subscribe(topic, qos)
    }

    var qos_ = 1
    var clientId_: String
    var topics_: Array<String>
    var password_ = "password"
    var broker_ = "tcp://gw.codein.net:1883"
    //var broker_ = "tcp://[2001:2:0:1baa::276c:efaa]:1883"

    init {
        clientId_ = try {
            val addr = InetAddress.getLocalHost()
            val hostname = addr.hostName
            hostname + "_" + uid.toString()
        } catch (e: UnknownHostException) {
            "co_" + (Math.random() * 1e12).toLong().toString(16)
        }

        topics_ = arrayOf("codein/live/$uid", "im/user/$uid")
    }

    fun start(): Boolean {
        (0..2).map { i ->
            try {
                if (connect(broker_, clientId_, true, uid.toString(), password_)) {
                    return true
                }
            } catch (e: MqttException) {
                println("reason " + e.reasonCode)
                println("msg " + e.message)
                println("loc " + e.localizedMessage)
                println("cause " + e.cause)
                println("excep " + e)
                e.printStackTrace()
            }
        }

        return false
    }

    fun stop() {
        val b = client?.isConnected
        if (b!= null && b) {
            client?.disconnect()
        }
    }

    companion object {
        private fun testMqtt(uid: Long) {
            //(1230000 until 1231200L).forEach {
            (1230001 .. 1230002L).forEach {
                var mqtt = Mqtt(uid, object : MqttListener {})
                mqtt.start()
            }
        }

        @Throws(Exception::class)
        @JvmStatic
        fun main(args: Array<String>) {
        }
    }
}
