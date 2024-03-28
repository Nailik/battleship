package battleship.server.data

import kotlinx.serialization.Serializable

@Serializable
data class GameSettings(var fieldWidth: Int, var fieldHeight: Int, var touching: Boolean, var allowedShipSelection: List<ShipSettings>)

@Serializable
data class ShipSettings(var length: Int, var num: Int)

fun getDefault(): GameSettings {
    return GameSettings(12, 12, false, listOf(ShipSettings(2, 4), ShipSettings(3, 3), ShipSettings(4, 2), ShipSettings(5, 1)))
}