package battleship.server.data

import kotlinx.serialization.Serializable

@Serializable
data class ShootResponse(val position: Position, val hit: Boolean, val userGameState: UserGameState)