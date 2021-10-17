package battleship.client.program

import battleship.client.interfaces.IScreen
import battleship.client.popups.IPopUp
import battleship.client.popups.SettingsPopUp
import battleship.client.resources.Sounds
import battleship.client.screens.EndScreen
import battleship.client.screens.GameScreen
import battleship.client.screens.LoadingScreen
import battleship.client.screens.SelectionScreen
import battleship.server.data.*

object GameLogic {

    var gameSettings: GameSettings = getDefault()
        set(value) {
            field = value
            shotMatrix = Array(gameSettings.fieldWidth) { Array(gameSettings.fieldHeight) { null } }
            fieldMatrix = Array(gameSettings.fieldWidth) { Array(gameSettings.fieldHeight) { false } }
            selectedShipsMatrix = Array(gameSettings.fieldWidth) { Array(gameSettings.fieldHeight) { false } }
            //reset ship Selection
            remainingShips = gameSettings.allowedShipSelection.toMutableList()
            shipSelection.clear()
        }

    var currentScreen: IScreen = LoadingScreen()
        private set(value) {
            if (field != value) {
                Sketch.oldScreen = field
                field = value
                value.open()
            }
        }

    var remainingShips = mutableListOf<ShipSettings>()
        private set

    val shipSelection = mutableListOf<Ship>()

    var playerRate = Pair(0, 0)
        private set

    var enemyPlayerRate = Pair(0, 0)
        private set

    var gameState = UserGameState.NOTHING
        private set(value) {
            if (field != value) {
                field = value
                ClientApi.sendData(DataPacket(DataType.GAME_STATUS, value))

                currentScreen = when (field) {
                    UserGameState.NOTHING -> LoadingScreen()
                    UserGameState.STARTING -> {
                        //reset ship selection and remaining ships by setting game settings
                        gameSettings = gameSettings
                        LoadingScreen()
                    }
                    UserGameState.SELECT_SHIPS -> SelectionScreen()
                    UserGameState.SELECT_SHIPS_READY -> currentScreen //just waiting
                    UserGameState.SHOOTING,
                    UserGameState.SPECTATING -> {
                        (currentScreen as? GameScreen)?.let {
                            it.updateState()
                            it
                        } ?: run {
                            GameScreen()
                        }
                    }
                    UserGameState.END_SCREEN_LOST,
                    UserGameState.END_SCREEN_WON -> EndScreen()
                    else -> currentScreen
                }
            }
        }

    fun setGameStatus(data: String) {
        gameState = java.lang.Enum.valueOf(UserGameState::class.java, data)
    }


    fun setGameStatusEnum(data: UserGameState) {
        gameState = data
    }

    fun addToShipSelection(ship: Ship) {
        if (gameState == UserGameState.SELECT_SHIPS) {
            //remove from remaining selection
            remainingShips.find { it.length == ship.shipType }?.also {
                if (it.num > 0) {
                    it.num -= 1
                }
                shipSelection.add(ship)

                ship.getListFields().forEach { position ->
                    selectedShipsMatrix[position.x][position.y] = true
                }

                //check if ship selection
                checkShipSelectionFinished()
            }
        }
    }

    fun removeFromShipSelection(ship: Ship) {
        if (gameState == UserGameState.SELECT_SHIPS) {
            //remove from remaining selection
            remainingShips.find { it.length == ship.shipType }?.also {
                ship.getListFields().forEach { position ->
                    selectedShipsMatrix[position.x][position.y] = false
                }
                shipSelection.remove(ship)
                if (it.num > 0) {
                    it.num += 1
                }

                //check if ship selection
                checkShipSelectionFinished()
            }
        }
    }

    var currentPopUp: IPopUp? = null
        private set(value) {
            if (field != value) {
                field = value
                currentScreen.setPopUp(value)
            }
        }

    fun showSettings() {
        if (currentPopUp == null) {
            currentPopUp = SettingsPopUp()
        }
    }

    fun closeCurrentPopUp() {
        currentPopUp = null
    }

    fun checkShipSelectionFinished() {
        if (remainingShips.find { it.num > 0 } == null) {
            //finished
            (currentScreen as? SelectionScreen)?.finishedSelection()
        }
    }

    //TODO better with nullable: null = nothing, true = hit, false = miss
    //contains where we have shot and hit
    //first: Shot there
    //second: hit there
    private var fieldMatrix = Array(gameSettings.fieldWidth) { Array(gameSettings.fieldHeight) { false } }

    //TODO better with nullable: null = nothing, true = hit, false = miss
    //contains where enemy has shot on our field
    private var shotMatrix = Array(gameSettings.fieldWidth) { Array<Boolean?>(gameSettings.fieldHeight) { null } }

    private var selectedShipsMatrix = Array(gameSettings.fieldWidth) { Array(gameSettings.fieldHeight) { false } }

    fun shootResult(shootResponse: ShootResponse) {
        playerRate = playerRate.copy(
            if (shootResponse.hit) {
                playerRate.first + 1
            } else playerRate.first, playerRate.second + 1
        )

        shotMatrix[shootResponse.position.x][shootResponse.position.y] = shootResponse.hit
        playHitSound(shootResponse.hit)
        (currentScreen as? GameScreen)?.also {
            it.updateEnemyField(shootResponse)
        }
        gameState = shootResponse.userGameState
    }

    fun enemyShootResult(shootResponse: ShootResponse) {
        enemyPlayerRate = enemyPlayerRate.copy(
            if (shootResponse.hit) {
                enemyPlayerRate.first + 1
            } else enemyPlayerRate.first, enemyPlayerRate.second + 1
        )

        fieldMatrix[shootResponse.position.x][shootResponse.position.y] = shootResponse.hit
        playHitSound(shootResponse.hit)
        (currentScreen as? GameScreen)?.also {
            it.updateOwnField(shootResponse)
        }
        gameState = shootResponse.userGameState
    }

    private fun playHitSound(hit: Boolean) {
        if (hit) {
            Sounds.Shot_Hit.play()
        } else {
            Sounds.Shot_Miss.play()
        }
    }

    fun sendShipSelection() {
        Logic.ayeSound()
        ClientApi.sendData(DataPacket(DataType.SHIP_SELECTION, shipSelection))
    }

    fun shoot(position: Position) {
        println("shoot $position")
        if (gameState == UserGameState.SHOOTING && shotMatrix[position.x][position.y] == null) {
            println("send shoot $position")
            gameState = UserGameState.WAITING_RESULT
            ClientApi.sendData(DataPacket(DataType.SHOT, position))
        }
    }

    fun finish() {
        gameState = UserGameState.NOTHING
        Logic.userState = UserState.START_SCREEN
    }

    private fun isFreePlaceForShip(position: Position, ship: Ship): Boolean {
        val shipFields = ship.copy(centerPosition = position).getListFields()

        shipFields.forEach { fieldPos ->
            if (selectedShipsMatrix[fieldPos.x][fieldPos.y]) {
                return false //dieses feld wird schon von einem Schiff belegt
            }
            //schiffe dürfen sich nirgends berühren
            if (!gameSettings.touching) {
                if (checkSelectedFieldSet(fieldPos.copy(x = fieldPos.x - 1)) || //left
                    checkSelectedFieldSet(fieldPos.copy(x = fieldPos.x - 1, y = fieldPos.y - 1)) || //upper left
                    checkSelectedFieldSet(fieldPos.copy(y = fieldPos.y - 1)) || //above
                    checkSelectedFieldSet(fieldPos.copy(x = fieldPos.x + 1, y = fieldPos.y - 1)) || //above right
                    checkSelectedFieldSet(fieldPos.copy(x = fieldPos.x + 1)) || //right
                    checkSelectedFieldSet(fieldPos.copy(x = fieldPos.x + 1, y = fieldPos.y + 1)) || //right below
                    checkSelectedFieldSet(fieldPos.copy(y = fieldPos.y + 1)) || //below
                    checkSelectedFieldSet(fieldPos.copy(x = fieldPos.x - 1, y = fieldPos.y + 1))
                ) {  //left below
                    return false //2 schiffe berühren sich
                }
            }
        }
        return true
    }

    private fun checkSelectedFieldSet(position: Position): Boolean {
        return if (position.x >= 0 && position.x < gameSettings.fieldWidth && position.y >= 0 && position.y < gameSettings.fieldHeight) {
            selectedShipsMatrix[position.x][position.y]
        } else {
            false //field doesn't exist it's free
        }
    }

    fun isShipAllowed(position: Position, ship: Ship): Boolean {
        val add = if (ship.shipType.mod(2) == 0) 1 else 0
        return when (ship.orientation) {
            Orientation.HORIZONTAL -> {
                position.x + (ship.shipType / 2) < gameSettings.fieldWidth && position.x - (ship.shipType / 2) + add >= 0 && isFreePlaceForShip(
                    position,
                    ship
                )
            }
            Orientation.VERTICAL -> {
                position.y + (ship.shipType / 2) < gameSettings.fieldHeight && position.y - (ship.shipType / 2) + add >= 0 && isFreePlaceForShip(
                    position,
                    ship
                )
            }
        }
    }

}