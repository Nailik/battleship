package battleship.client.program

import battleship.server.data.DataPacket
import battleship.server.data.DataType
import battleship.server.program.jsonFormat
import io.ktor.websocket.*


/**
 * used when this machine is only a client
 */
class Client(val socket: DefaultWebSocketSession) {

    fun receive(frame: Frame) {

        (frame as? Frame.Text?)?.also { text ->

            val string = text.readText()
            val data = jsonFormat.decodeFromString<DataPacket>(string)

            //work with json
            when (data.type) {
                DataType.CREATE_LOBBY -> Logic.onCreatedLobby(jsonFormat.decodeFromString(data.data))
                DataType.READ_LOBBY_LIST -> Logic.onReceivedLobbyList(jsonFormat.decodeFromString(data.data))
                DataType.JOIN_LOBBY -> Logic.onJoinLobby(jsonFormat.decodeFromString(data.data))
                DataType.JOIN_LOBBY_FAILED -> Logic.onJoinLobbyFailed(jsonFormat.decodeFromString(data.data))
                DataType.LOBBY_UPDATE -> Logic.onLobbyUpdate(jsonFormat.decodeFromString(data.data))
                DataType.GAME_STARTED -> Logic.onStartGame(jsonFormat.decodeFromString(data.data))
                DataType.GAME_STATUS -> GameLogic.setGameStatus(jsonFormat.decodeFromString(data.data))
                DataType.SHIP_SELECTION -> {
                    //TODO result not validated
                    // GameLogic.onStartShipSelection(jsonFormat.decodeFromString(data.data))
                }
                DataType.SHOT_RESULT -> GameLogic.shootResult(jsonFormat.decodeFromString(data.data))
                DataType.SHOT_RECEIVED -> GameLogic.enemyShootResult(jsonFormat.decodeFromString(data.data))
                DataType.STATUS -> {
                }//TODO
                DataType.LOBBY_SETTINGS -> {
                }///TODO
                DataType.SHOT -> {
                } //TODO
                DataType.CHANGED_NAME -> {
                }///TODO
                DataType.GAME_SETTINGS -> Logic.onGameSettingsUpdate(jsonFormat.decodeFromString(data.data))
            }
        }
    }

}