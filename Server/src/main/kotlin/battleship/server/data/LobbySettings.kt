package battleship.server.data

import kotlinx.serialization.Serializable
import battleship.server.webserver.ServerClient

//stored on server, used to create lobby initially
@Serializable
data class LobbySettings(var name: String, var password: String?)

@Serializable
data class LobbyJoinRequest(var id: String, var password: String?)

@Serializable
data class LobbyJoinResponse(var data: LobbyData?, var lobbyStatus: LobbyStatus)

//send to client for information
@Serializable
data class LobbyData(var id: String, var name: String, var createdPlayer: String, var joinedPlayer: String? = null, var passwordEnabled: Boolean, var gameSettings: GameSettings)

//used on server
data class Lobby(var settings: LobbySettings, var data: LobbyData, var createdPlayerUUID: ServerClient, var joinedPlayerUUID: ServerClient? = null)

enum class LobbyStatus{
    FAIL_PASSWORD_REQUIRED,
    FAIL_WRONG_PASSWORD,
    FAIL_LOBBY_FULL,
    FAIL_LOBBY_CLOSED
}