package battleship.server.webserver

import battleship.server.data.*
import battleship.server.webserver.ServerApi.errorHandlingScope
import kotlinx.coroutines.launch
import kotlin.random.Random

/**
 * has the lobby, players, ship selection, checks everything
 */
class Game(serverClientOne: ServerClient, serverClientTwo: ServerClient, var gameSettings: GameSettings) {

    private var playerOne = Player(serverClientOne, mutableListOf(), this)
    private var playerTwo = Player(serverClientTwo, mutableListOf(), this)

    init {
        playerOne.onGameStarted(this)
        playerTwo.onGameStarted(this)
    }


    /**
     * game should start
     */
    private fun onStartGame() {
        //TODO problem vermutlich, dass das hier aus receive heraus von Server Client im Selben context ist
        errorHandlingScope.launch {
            playerOne.onUpdateGameStatus(UserGameState.SELECT_SHIPS)
            playerTwo.onUpdateGameStatus(UserGameState.SELECT_SHIPS)
        }
    }

    fun onUserReady() {
        println("onUserReady ${playerOne.serverClient.status} ${playerTwo.serverClient.status} ")
        println("playerOne ${playerOne.serverClient.status}")
        println("playerTwo ${playerTwo.serverClient.status} ")
        //TODO check nullpointer playertwo server client??
        if (playerOne.serverClient.status == UserState.IN_GAME && playerTwo.serverClient.status == UserState.IN_GAME) {
            //game has loaded for this user
            onStartGame()
        }
    }

    fun onUserSelectedShips() {
        if (playerOne.userGameStatus == UserGameState.SELECT_SHIPS_READY && playerTwo.userGameStatus == UserGameState.SELECT_SHIPS_READY) {
            //game has loaded for this user
            //random player is starter player}
            errorHandlingScope.launch {
                if (Random.nextInt(0, 1) == 0) {
                    playerOne.onUpdateGameStatus(UserGameState.SHOOTING)
                    playerTwo.onUpdateGameStatus(UserGameState.SPECTATING)
                } else {
                    playerOne.onUpdateGameStatus(UserGameState.SPECTATING)
                    playerTwo.onUpdateGameStatus(UserGameState.SHOOTING)
                }
            }
        }
    }

    //returns null if invalid
    fun generateFieldMatrix(data: List<Ship>): Array<Array<Boolean>>? {
        val fieldMatrix = Array(gameSettings.fieldWidth) { Array(gameSettings.fieldHeight) { false } }
        val necessarySelection = gameSettings.allowedShipSelection.map { it.copy() }.toMutableList()

        data.forEach { ship ->
            necessarySelection.find { it.length == ship.shipType && it.num > 0 }?.also {
                //add to array
                when (ship.orientation) {
                    Orientation.HORIZONTAL -> {
                        for (x in (ship.centerPosition.x - ((ship.shipType - 1) / 2))..(ship.centerPosition.x + ship.shipType / 2)) {
                            if (x < 0 || x >= gameSettings.fieldWidth || fieldMatrix[x][ship.centerPosition.y]) {
                                return null
                            } else {
                                fieldMatrix[x][ship.centerPosition.y] = true
                            }
                        }
                    }

                    Orientation.VERTICAL -> {
                        for (y in (ship.centerPosition.y - ((ship.shipType - 1) / 2))..(ship.centerPosition.y + ship.shipType / 2)) {
                            if (y < 0 || y >= gameSettings.fieldHeight || fieldMatrix[ship.centerPosition.x][y]) {
                                return null
                            } else {
                                fieldMatrix[ship.centerPosition.x][y] = true
                            }
                        }
                    }
                }

                it.num -= 1
            } ?: kotlin.run {
                return null
            }
        }

        //check if all ships are set
        return necessarySelection.find { it.num != 0 }?.let {
            null
        } ?: run {
            fieldMatrix
        }
    }

    //null is invalid, true is hit, false is miss
    fun validateShot(position: Position, player: Player) {
        if (position.x >= 0 && position.x < gameSettings.fieldWidth && position.y >= 0 && position.y < gameSettings.fieldHeight) {
            //TODO check not yet shot there
            val enemyPlayer = if (player == playerOne) {
                playerTwo
            } else {
                playerOne
            }

            val result = enemyPlayer.shipFieldMatrix[position.x][position.y]
            player.shotFieldMatrix[position.x][position.y] = result

            //always check because it's possible to set no ship at all
            if (checkGameFinished(enemyPlayer, player)) {
                //player is winner
                //enemy is looser
                player.userGameStatus = UserGameState.END_SCREEN_WON
                enemyPlayer.userGameStatus = UserGameState.END_SCREEN_LOST
            } else {
                player.userGameStatus = if (result) {
                    UserGameState.SHOOTING
                } else {
                    UserGameState.SPECTATING
                }

                enemyPlayer.userGameStatus = if (result) {
                    UserGameState.SPECTATING
                } else {
                    UserGameState.SHOOTING
                }
            }

            errorHandlingScope.launch {
                player.onShotResult(ShootResponse(position, result, player.userGameStatus))
                enemyPlayer.onShotReceived(ShootResponse(position, result, enemyPlayer.userGameStatus))
            }
        }
    }

    private fun checkGameFinished(target: Player, player: Player): Boolean {
        target.shipFieldMatrix.forEachIndexed { indexX, arrayOfPairs ->
            arrayOfPairs.forEachIndexed { indexY, pair ->
                if (pair && player.shotFieldMatrix[indexX][indexY] == null) {
                    //one field where player did not shoot yet
                    return false
                }
            }
        }
        return true
    }
}