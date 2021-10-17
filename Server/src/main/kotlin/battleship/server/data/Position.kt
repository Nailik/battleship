package battleship.server.data

import kotlinx.serialization.Serializable

@Serializable
data class Position(var x: Int, var y: Int)