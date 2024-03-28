package battleship.client.program

import battleship.client.interfaces.IScreen
import battleship.client.popups.*
import battleship.client.resources.Images
import battleship.client.resources.Music
import battleship.client.resources.Sounds
import battleship.client.screens.*
import battleship.server.data.*
import battleship.server.program.DefaultGamePort
import battleship.server.webserver.ServerApi
import battleship.server.webserver.ServerSettings
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import mu.KotlinLogging
import java.io.File
import java.util.*
import kotlin.random.Random

/**
 * handles navigation between pages etc
 */
private val logger = KotlinLogging.logger {}

object Logic {

    private val errorHandler = CoroutineExceptionHandler { _, _ -> }
    private val errorHandlingScope = CoroutineScope(errorHandler)

    private val jsonFormat = Json { isLenient = true }

    private var host = ""
    private var port = DefaultGamePort
    var secure = true

    var currentLobby: LobbyData? = null
    var hasCreatedLobby = false

    var currentScreen: IScreen = LoadingScreen("Loading Game")
        private set(value) {
            if (field != value && field.javaClass != value.javaClass) {
                val currentOffset = field.position
                Sketch.oldScreen = field
                value.position = currentOffset
                field = value
                value.open()
            }
        }

    var currentPopUp: IPopUp? = null
        private set(value) {
            if (field != value) {
                field = value
                currentScreen.setPopUp(value)
            }
        }

    var userState = UserState.STARTING
        set(value) {
            if (field != value) {
                field = value
                ClientApi.sendData(DataPacket(DataType.STATUS, value.name, userSettings.uuid))
                //save offset
                currentScreen = when (field) {
                    UserState.STARTING -> LoadingScreen("Loading Game")
                    UserState.CHOOSING_NAME -> NameScreen()
                    UserState.CONNECTING -> LoadingScreen("Connecting")
                    /**
                     * client successfully connected to server
                     */
                    UserState.START_SCREEN -> {
                        if (!Music.Background_Menu.isPlaying) {
                            Music.Background_Menu.play()
                            Music.Background_Game.stop()
                        }
                        HomeScreen()
                    }

                    UserState.CREATE_GAME -> CreateGameScreen()
                    UserState.LOBBYLIST -> LobbyListScreen()
                    UserState.SERVER_SETTINGS -> ServerScreen()
                    UserState.LOBBY_JOINED -> {
                        hasCreatedLobby = false
                        LobbyScreen()
                    }

                    UserState.LOBBY_CREATED -> {
                        hasCreatedLobby = true
                        LobbyScreen()
                    }

                    UserState.QUEUE -> QueueScreen()
                    //nothing waiting for other player
                    UserState.LOBBY_READY -> currentScreen
                    UserState.IN_GAME -> {
                        Music.Background_Game.play()
                        Music.Background_Menu.stop()
                        GameLogic.currentScreen
                    }
                }
            }
        }

    var userSettings = UserSettings(UUID.randomUUID().toString())
        private set

    init {
        try {
            logger.debug { "ok" }
            currentScreen.open()

            errorHandlingScope.launch {
                loadSettings()
                //if (!isDebug) {
                Music.Background_Menu.play()
                // }
                Sounds.loadAll()
                Images.loadAll()
                userState = if (userSettings.userName.isEmpty()) {
                    UserState.CHOOSING_NAME
                } else {
                    UserState.SERVER_SETTINGS
                }
            }
        } catch (e: Exception) {
            println("Exception $e")
        } catch (e: Error) {
            println("Error $e")
        } catch (e: Throwable) {
            println("Throwable $e")
        }
    }

    /**
     * user chose name and wants to connect to server
     */
    fun onChooseName(name: String) {
        //Save name
        userSettings.userName = name
        saveSettings()

        userState = UserState.SERVER_SETTINGS
    }

    fun connectFromSettings(host: String, port: Int, secure: Boolean) {
        this.host = host
        this.port = port
        this.secure = secure
        userState = UserState.CONNECTING
        tryConnect()
    }

    fun tryConnect() {
        currentPopUp?.close()
        //kilianpc.local
        ClientApi.connectToServer(host, port, secure)
    }

    fun showSettings() {
        if (currentPopUp == null) {
            currentPopUp = SettingsPopUp()
        }
    }

    //start and connect to local server
    fun startLocalServer(port: String) {
        userState = UserState.STARTING
        errorHandlingScope.launch {
            if (ServerApi.startServer(ServerSettings(-1, -1, true, Integer.valueOf(port)))) {
                connectFromSettings("127.0.0.1", Integer.valueOf(port), false)
            } else {
                currentPopUp = InfoPopUp(
                    "Unable to create server",
                    "Check if port is already in use\ntry to select different port."
                )
                userState = UserState.SERVER_SETTINGS
            }
        }
    }


    fun stopLocalServer() {
        //connect to default
        connectFromSettings("", DefaultGamePort, true)
        ServerApi.stopServer()
    }

    fun onDisconnected(e: ClosedReceiveChannelException?) {
        currentPopUp = if (userState == UserState.STARTING) {
            //initial connection did not work
            ConnectionPopUp()
        } else {
            //player maybe disconnected while playing
            ReconnectPopUp()
        }
    }

    fun onConnectionError(e: Error?) {
        currentPopUp = if (userState == UserState.STARTING) {
            //initial connection did not work
            ConnectionPopUp()
        } else {
            //player maybe disconnected while playing TODO
            ConnectionPopUp()
        }
    }

    fun onStartLobby(lobbySettings: LobbySettings) {
        ClientApi.sendData(DataPacket(DataType.CREATE_LOBBY, lobbySettings))
    }

    @OptIn(ExperimentalSerializationApi::class)
    private fun loadSettings() {
        val file = File("Settings.json")
        if (!file.exists()) {
            file.createNewFile()
        } else {
            val text = file.readText()
            userSettings = try {
                //TODO fix invalid file
                jsonFormat.decodeFromString(text)
            } catch (e: Exception) {
                UserSettings(UUID.randomUUID().toString(), "Player_${Random.nextInt(0, 1000)}")
            }
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    fun saveSettings() {
        val file = File("Settings.json")
        if (!file.exists()) {
            file.createNewFile()
        }
        file.writeText(jsonFormat.encodeToString(userSettings))
    }

    fun onReadLobbyList() {
        ClientApi.sendData(DataPacket(DataType.READ_LOBBY_LIST, ""))
    }

    fun onReceivedLobbyList(lobbyData: List<LobbyData>) {
        (currentScreen as? LobbyListScreen?)?.also {
            it.updateLobbyList(lobbyData)
        }
    }

    fun onCreatedLobby(lobbyData: LobbyData) {
        //Eventually not created but received from queue
        currentLobby = lobbyData
        userState = UserState.LOBBY_CREATED
    }

    fun onJoinLobby(lobbyData: LobbyData) {
        //Eventually not by click from user but from queue
        currentLobby = lobbyData
        userState = UserState.LOBBY_JOINED
    }

    fun onLobbyUpdate(lobbyData: LobbyData) {
        currentLobby = lobbyData
        (currentScreen as? LobbyScreen?)?.also {
            it.updateData(lobbyData)
        }
        //TODO only player in lobby
    }

    fun onRequestLobby(lobbyData: LobbyData) {
        if (lobbyData.passwordEnabled) {
            //show popup
            currentPopUp = TextInputPopUp("Password Required", "Input Password").apply {
                submit = { input ->
                    ClientApi.sendData(DataPacket(DataType.JOIN_LOBBY, LobbyJoinRequest(lobbyData.id, input)))
                }
            }
        } else {
            ClientApi.sendData(DataPacket(DataType.JOIN_LOBBY, LobbyJoinRequest(lobbyData.id, null)))
        }
    }

    fun onStartGame(gameSettings: GameSettings) {
        GameLogic.gameSettings = gameSettings
        GameLogic.setGameStatusEnum(UserGameState.STARTING)
        userState = UserState.IN_GAME
    }

    fun onJoinLobbyFailed(lobbyJoinResponse: LobbyJoinResponse) {
        currentPopUp = when (lobbyJoinResponse.lobbyStatus) {
            LobbyStatus.FAIL_PASSWORD_REQUIRED -> TextInputPopUp("Password Required", "Input Password").apply {
                submit = { input ->
                    ClientApi.sendData(
                        DataPacket(
                            DataType.JOIN_LOBBY,
                            LobbyJoinRequest(lobbyJoinResponse.data!!.id, input)
                        )
                    )
                }
            }

            LobbyStatus.FAIL_WRONG_PASSWORD -> TextInputPopUp("Password Wrong", "Input Password").apply {
                submit = { input ->
                    ClientApi.sendData(
                        DataPacket(
                            DataType.JOIN_LOBBY,
                            LobbyJoinRequest(lobbyJoinResponse.data!!.id, input)
                        )
                    )
                }
            }

            LobbyStatus.FAIL_LOBBY_FULL -> InfoPopUp("Failed", "Lobby already full")
            LobbyStatus.FAIL_LOBBY_CLOSED -> InfoPopUp("Failed", "Lobby not found.")
        }
    }

    fun closeCurrentPopUp() {
        currentPopUp = null
    }

    fun updateGameSettings() {
        //send to server
        ClientApi.sendData(DataPacket(DataType.GAME_SETTINGS, currentLobby!!.gameSettings))
    }

    fun toggleReady() {
        userState = if (userState == UserState.LOBBY_READY) {
            if (hasCreatedLobby) {
                UserState.LOBBY_CREATED
            } else {
                UserState.LOBBY_JOINED
            }
        } else {
            ayeSound()
            UserState.LOBBY_READY
        }

    }

    fun onGameSettingsUpdate(gameSettings: GameSettings) {
        //make player unready
        if (userState == UserState.LOBBY_READY) {
            userState = if (hasCreatedLobby) {
                UserState.LOBBY_CREATED
            } else {
                UserState.LOBBY_JOINED
            }
        }
        currentLobby?.gameSettings = gameSettings
        (currentScreen as? LobbyScreen)?.updateGameSettings()
    }


    fun ayeSound() {
        if (hasCreatedLobby) {
            Sounds.Aye_Captain.play()
        } else {
            Sounds.Aye_Pirate.play()
        }
    }
}