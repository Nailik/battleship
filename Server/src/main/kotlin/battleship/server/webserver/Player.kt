package battleship.server.webserver

import battleship.server.data.*
import battleship.server.program.DataPacket

class Player(val serverClient: ServerClient, private var shipSelection: List<Ship>, var game: Game) {

    init {
        serverClient.player = this
    }

    var userGameStatus: UserGameState = UserGameState.NOTHING

    //set/unset
    var shipFieldMatrix = Array(game.gameSettings.fieldWidth) { Array(game.gameSettings.fieldHeight) { false } }
        private set

    //shot/hit/nothing
    var shotFieldMatrix = Array(game.gameSettings.fieldWidth) { Array<Boolean?>(game.gameSettings.fieldHeight) { null } }
        private set

    /**
     * game has started
     */
    fun onGameStarted(startedGame: Game) {
        game = startedGame
    }

    /**
     * player saves ship selection
     */
    fun onSaveShipSelection(data: List<Ship>) {
        //TODO approve ship selection
        game.generateFieldMatrix(data)?.also {
            shipFieldMatrix = it
            shipSelection = data
            userGameStatus = UserGameState.SELECT_SHIPS_READY
            game.onUserSelectedShips()
        } ?: run {
            TODO("invalid")
        }
    }

    /**
     * for ship selection, shooting and spectating
     */
    suspend fun onUpdateGameStatus(userGameStatus: UserGameState) {
        this.userGameStatus = userGameStatus
        serverClient.send(DataPacket(DataType.GAME_STATUS, userGameStatus))
    }

    /**
     * this player has loaded the game
     */
    fun onReady() {
        game.onUserReady()
    }

    fun onShot(position: Position) {
        game.validateShot(position, this)
    }

    suspend fun onShotResult(shootResponse: ShootResponse) {
        serverClient.send(DataPacket(DataType.SHOT_RESULT, shootResponse))
    }

    suspend fun onShotReceived(shootResponse: ShootResponse) {
        serverClient.send(DataPacket(DataType.SHOT_RECEIVED, shootResponse))
    }
}