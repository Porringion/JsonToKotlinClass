package wu.seal.jsontokotlin.statistics

import java.net.HttpURLConnection
import java.net.URL

/**
 * NetWork relative
 * Created by Seal.Wu on 2017/9/25.
 */

const val actionInfoUrl = "http://localhost:8080/sendActionInfo"
const val exceptionLogUrl = "http://localhost:8080/sendExceptionInfo"

fun sendExceptionLog(log: String) {
    try {
        val connection = URL(exceptionLogUrl).openConnection() as HttpURLConnection
        connection.doOutput = true
        connection.doInput=true
        connection.addRequestProperty("Content-Type","application/text")
        val outputStream = connection.outputStream
        val writer = outputStream.writer()
        writer.write(log)
        writer.flush()
        if (connection.responseCode != 200) {
            println(connection.responseMessage+"\n"+connection.errorStream.reader().readText())
        }
    } catch(e: Exception) {
        e.printStackTrace()
        PersistCache.saveExceptionInfo(log)
    }
}


fun sendActionInfo(actionInfo: String) {
    try {
        val connection = URL(actionInfoUrl).openConnection() as HttpURLConnection
        connection.doOutput = true
        connection.requestMethod = "POST"
        connection.addRequestProperty("Content-Type","application/json;charset=UTF-8")

        val outputStream = connection.getOutputStream()
        val writer = outputStream.writer()
        writer.write(actionInfo)
        writer.flush()
        if (connection.responseCode != 200) {
            println(connection.responseMessage+"\n"+connection.errorStream.reader().readText())
        }
    } catch(e: Exception) {
        e.printStackTrace()
        PersistCache.saveActionInfo(actionInfo)
    }
}


fun sendHistoryExceptionInfo() {
    Thread() {
        PersistCache.readAllCachedExceptionInfo().split("#").filter { it.isNotBlank() }.forEach {
            sendExceptionLog(it)
        }
    }
    PersistCache.deleteAllExceptionInfoCache()
}


fun sendHistoryActionInfo() {
    Thread() {
        PersistCache.readAllCachedActionInfo().split("#").filter { it.isNotBlank() }.forEach {
            sendActionInfo(it)
        }
    }.start()
    PersistCache.deleteAllActionInfo()
}


fun main(args: Array<String>) {
    val demeoActionInfo ="""{"id":12,"uuid":"fdsafds","actionType":"start","time":"1234231434124324"}"""

    sendExceptionLog("hello,I am exception")
    sendActionInfo(demeoActionInfo)
}