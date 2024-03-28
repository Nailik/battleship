package battleship.server.program

import battleship.server.data.DataPacket
import battleship.server.data.DataType
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement

const val OpenForConnections = "openForConnections"

const val BroadcastPort = 8000
const val DefaultGamePort = 5000
val jsonFormat = Json { isLenient = true }

inline fun <reified T> DataPacket(type: DataType, data: T): DataPacket {
    return DataPacket(type, Json.encodeToJsonElement(data).toString())
}