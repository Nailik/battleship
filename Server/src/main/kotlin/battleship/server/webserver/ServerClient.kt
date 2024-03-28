package battleship.server.webserver

import battleship.server.data.*
import battleship.server.program.*
import io.ktor.websocket.*
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement

class ServerClient(private val socket: DefaultWebSocketSession, val uuid: String, var name: String) {

    var status: UserState = UserState.STARTING
    private set

    private var lobby: Lobby? = null
    var player: Player? = null

    suspend fun receive(frame: Frame) {
        try {

            (frame as? Frame.Text?)?.also { text ->

                val string = text.readText()
                val data = jsonFormat.decodeFromString<DataPacket>(string)

                if (data.uuid != uuid) {
                    println("invalid id")
                } else {
                    //work with json
                    when (data.type) {
                        DataType.CREATE_LOBBY -> onCreateLobby(jsonFormat.decodeFromString(data.data))
                        DataType.READ_LOBBY_LIST -> sendLobbyList()
                        DataType.STATUS -> setStatus(data.data)
                        DataType.JOIN_LOBBY -> onJoinLobby(jsonFormat.decodeFromString(data.data))
                        DataType.SHIP_SELECTION -> player?.onSaveShipSelection(jsonFormat.decodeFromString(data.data))
                        DataType.SHOT -> player?.onShot(jsonFormat.decodeFromString(data.data))
                        DataType.LOBBY_UPDATE -> {
                        } ////TODO
                        DataType.LOBBY_SETTINGS -> {
                        }////TODO
                        DataType.GAME_STATUS -> {
                        }////TODO
                        DataType.GAME_STARTED -> {
                        }// //TODO
                        DataType.SHOT_RESULT -> {
                        }////TODO
                        DataType.SHOT_RECEIVED -> {
                        }////TODO
                        DataType.JOIN_LOBBY_FAILED -> {
                        }////TODO
                        DataType.CHANGED_NAME -> name = jsonFormat.decodeFromString(data.data)
                        DataType.GAME_SETTINGS -> onGameSettingsChanged(jsonFormat.decodeFromString(data.data))
                    }
                }
            }
        }catch (e: Exception){
            println(e)
        }
    }

    private suspend fun onJoinLobby(lobbyJoinRequest: LobbyJoinRequest) {
        ServerApi.onJoinLobby(lobbyJoinRequest.id)?.also{
            if(lobbyJoinRequest.password == null && it.settings.password != null){
                //password missing
                send(DataPacket(DataType.JOIN_LOBBY_FAILED, LobbyJoinResponse(it.data, LobbyStatus.FAIL_PASSWORD_REQUIRED)))
                return
            }else if(it.settings.password != null && lobbyJoinRequest.password != it.settings.password){
                //wrong password
                send(DataPacket(DataType.JOIN_LOBBY_FAILED, LobbyJoinResponse(it.data, LobbyStatus.FAIL_WRONG_PASSWORD)))
                return
            }

            if(it.joinedPlayerUUID != null){
                //already two players in lobby
                send(DataPacket(DataType.JOIN_LOBBY_FAILED, LobbyJoinResponse(it.data, LobbyStatus.FAIL_LOBBY_FULL)))
                return
            }

            lobby = it
            it.data.joinedPlayer = this.name
            it.joinedPlayerUUID = this

            ServerApi.errorHandlingScope.launch {
                it.createdPlayerUUID.lobbyUpdate(it)
                it.joinedPlayerUUID?.joinedLobby(it)
            }
        }?: run{
            //lobby doesn't exist (anymore)
            send(DataPacket(DataType.JOIN_LOBBY_FAILED, LobbyJoinResponse(null, LobbyStatus.FAIL_LOBBY_CLOSED)))
            return
        }
    }

    suspend fun lobbyUpdate(data: Lobby) {
        lobby = data
        send(DataPacket(DataType.LOBBY_UPDATE, data.data))
    }

    private fun setStatus(data: String) {
        status = java.lang.Enum.valueOf(UserState::class.java, data)
        when (status) {
            UserState.START_SCREEN -> {
                ServerApi.removeFromQueue(this)

                //reset player and game
                player = null
            }
            UserState.QUEUE -> {
                ServerApi.onJoinQueue(this)
            }
            UserState.LOBBY_CREATED -> {
                //TODO not ready
            }
            UserState.LOBBY_JOINED -> {
                //TODO not ready
            }
            UserState.LOBBY_READY -> {
                lobby?.also { ServerApi.onLobbyReady(it) }
            }
            UserState.IN_GAME -> {
                player?.onReady()
            }
            else -> {}
        }
    }

    suspend fun send(data: DataPacket) {
        socket.send(Frame.Text(Json.encodeToJsonElement(data).toString()))
    }

    /**
     * send List of lobbies to user
     */
    suspend fun sendLobbyList(){
        send(DataPacket(DataType.READ_LOBBY_LIST, ServerApi.getLobbyListJson()))
    }

    /**
     * user created a lobby
     */
    private suspend fun onCreateLobby(lobbySettings: LobbySettings) {
        ServerApi.onCreateLobby(lobbySettings, this).also { created ->
            lobby = created
            send(DataPacket(DataType.CREATE_LOBBY, created.data))
        }
    }

    /**
     * user created a lobby
     */
    suspend fun onCreatedLobby(created: Lobby) {
        lobby = created
        send(DataPacket(DataType.CREATE_LOBBY, created.data))
    }

    /**
     * user joined a lobby
     */
    suspend fun joinedLobby(joinedLobby: Lobby) {
        lobby = joinedLobby
        send(DataPacket(DataType.JOIN_LOBBY, joinedLobby.data))
    }

    /**
     * both players ready, game will start soon
     */
    suspend fun onStartGame(gameSettings: GameSettings){
        lobby = null
        send(DataPacket(DataType.GAME_STARTED, gameSettings))
    }

    //save changed settings and send to other user
    private suspend fun onGameSettingsChanged(gameSettings: GameSettings) {
        lobby?.also {
            if(it.createdPlayerUUID == this){ //only player who created lobby is allowed to update game settings
                it.data.gameSettings = gameSettings
                it.joinedPlayerUUID?.status = UserState.LOBBY_JOINED
                it.joinedPlayerUUID?.onGameSettingsUpdate(gameSettings)
            }
        }
    }

    private suspend fun onGameSettingsUpdate(gameSettings: GameSettings) {
        send(DataPacket(DataType.GAME_SETTINGS, gameSettings))
    }
}