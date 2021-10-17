package battleship.server.data

import kotlinx.serialization.Serializable

@Serializable
data class DataPacket(val type: DataType, val data: String, val uuid: String? = null)