package battleship.client.program

import kotlinx.serialization.Serializable

@Serializable
data class UserSettings(
    val uuid: String,
    var userName: String = "",
    var soundOn: Boolean = true,
    var soundLevel: Float = 0.5f,
    var musicOn: Boolean = true,
    var musicLevel: Float = 0.5f
)