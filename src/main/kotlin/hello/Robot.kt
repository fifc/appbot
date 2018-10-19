package hello

import com.google.protobuf.Message
import kotlinx.coroutines.delay
import java.sql.Timestamp

class Robot constructor(var uid: Long): MqttListener {
    val lngNW: Double = 113.910938
    val latNW: Double = 22.520323
    val lngSE: Double = 113.952137
    val latSE: Double = 22.484561

    var lng: Double = 0.0
    var lat: Double = 0.0

    var http = Http()
    var mqtt = Mqtt(uid, this)

    var status = 0
    var token = ""
    var gameId = 0L

    init {
        Companion.gps(this)
        //println("$uid -> ($lng $lat)")
    }

    private suspend fun init(): Int {
	return 0
    }

    suspend fun run(): Robot {
        status = init()
        if (status != 0) {
            stop()
            return this
        }

        //val timeStamp = Timestamp(System.currentTimeMillis())
        var stateTime = 0L
        var nearbyTime = 0L
        val start = System.currentTimeMillis()
        for (i in 1..180) {
            val time = System.currentTimeMillis()
            if (stateTime + 3000 <= time) {
                gps(this)
                stateTime = time
            }

            if ((i%10)==0) {
                val elapsed = System.currentTimeMillis() - start
                println("[$uid] i $i elapsed ${elapsed}ms")
            }

            val sleep = time + 1000 - System.currentTimeMillis()
            if (sleep > 0) {
                delay(sleep)
            } else {
                println("[$uid] no time ...")
            }
        }

        stop()

        return this
    }

    private fun stop() {
        mqtt.stop()
    }

    private fun stop(code: Int) {
        status = code
        stop()
    }

    companion object {
        private fun gps(robot: Robot) {
            var r = Math.random()
            robot.lng = robot.lngNW + (robot.lngSE - robot.lngNW) * r
            r = Math.random()
            robot.lat = robot.latNW + (robot.latSE - robot.latNW) * r
        }
    }
}
