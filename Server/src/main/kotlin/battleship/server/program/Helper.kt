package battleship.server.program

import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import battleship.server.data.DataPacket
import battleship.server.data.DataType
import java.io.IOException
import java.net.InetSocketAddress
import java.net.ServerSocket

const val OpenForConnections = "openForConnections"

val isDebug = System.getProperty("debug", "false").toString().toBoolean()

const val BroadcastPort = 8000
const val DefaultGamePort = 5000
 val jsonFormat = Json { isLenient = true }

fun searchFreePort(): Int{
    for (port in 8001 until 8080) {
        try {
            aSocket(ActorSelectorManager(Dispatchers.IO)).udp()
                .bind(InetSocketAddress(port)).close()

            val serverSocket = ServerSocket(port)
            serverSocket.close()
            return port
        } catch (ex: IOException) {
            continue  // try next port
        }
    }
    return 0
}

inline fun <reified T>DataPacket(type: DataType, data: T) : DataPacket {
    return DataPacket(type, Json.encodeToJsonElement(data).toString())
}