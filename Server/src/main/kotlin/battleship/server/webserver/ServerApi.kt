package battleship.server.webserver

import battleship.server.data.*
import battleship.server.program.*
import io.ktor.http.*
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.utils.io.core.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.serialization.json.encodeToJsonElement
import mu.KotlinLogging
import java.util.*
import kotlin.random.Random

/**
 * handles game slots etc
 */
private val logger = KotlinLogging.logger {}

object ServerApi {
    private val errorHandler = CoroutineExceptionHandler { _, error -> println("error occured in ServerApi $error") }
    internal val errorHandlingScope = CoroutineScope(errorHandler)

    var serverSettings = ServerSettings(-1, -1, false, DefaultGamePort)
        private set

    private val clients = Collections.synchronizedSet<ServerClient>(LinkedHashSet())
    private val queue = Collections.synchronizedList<ServerClient>(LinkedList())

    private val lobbyList = Collections.synchronizedList<Lobby>(mutableListOf())
    private val gameList = Collections.synchronizedList<Game>(mutableListOf())

    private var broadcastActivated = false
    private var server: NettyApplicationEngine? = null

    /**
     *   read server Settings from config file
     *   when in config file no port -> search for port
     *   broadcast should only be used for local server
     */
    fun startServer(serverSettings: ServerSettings): Boolean {
        this.serverSettings = serverSettings

        try {
            if (server == null) {
                server = embeddedServer(Netty, serverSettings.port) {
                    configureServer(this@ServerApi)
                }.start(wait = false)

                if (serverSettings.isLocalServer) {
                    startBroadcast()
                }
                logger.info { "Server started:\n port: ${serverSettings.port} \n broadcastPort: $BroadcastPort" }
                return true
            }
        } catch (e: Exception) {
            logger.error { "Server dit not start $e" }
        }
        return false
    }

    fun stopServer() {
        server?.stop(100, 100)
    }

    fun isServerRunning(): Boolean {
        return server != null
    }

    /**
     * send out pings, open for players
     */
    private fun startBroadcast() {
        broadcastActivated = true

        try {
            val udpSocket = aSocket(ActorSelectorManager(Dispatchers.IO)).udp()
                .connect(InetSocketAddress("192.168.178.255", BroadcastPort)) {
                    broadcast = true
                }

            errorHandlingScope.launch {
                while (broadcastActivated) {
                    val data = Datagram(BytePacketBuilder().apply {
                        writeText("{status: \"$OpenForConnections\", port: ${serverSettings.port}}")
                    }.build(), InetSocketAddress("192.168.178.255", BroadcastPort))

                    udpSocket.outgoing.send(data)
                    delay(500)
                }
            }
        } catch (e: Exception) {
            //maybe not working due to already used port
            broadcastActivated = false
        }
    }

    /**
     * spieler verbindet sich mit server
     */
    fun connect(socket: DefaultWebSocketSession, clientUUid: String?, clientName: String?): ServerClient? {
        if (serverSettings.maxClientCount != -1 && clients.size >= serverSettings.maxClientCount) {
            return null
        }
        return clientUUid?.let { uuid ->
            return clientName?.let { name ->
                val client = ServerClient(socket, uuid, name)

                println("new user ${clients.size} $clientName")

                clients.add(client)
                client
                //send user the lobby list
            } ?: run {
                null
            }
        } ?: run {
            null
        }
    }

    /**
     * spieler schließt verbindung zum server
     */
    fun disconnect(clientUUid: String?): Response {
        return clientUUid?.let { uuid ->
            clients.firstOrNull { it.uuid == uuid }?.let {
                removeClient(it)

                //bye
                Response(HttpStatusCode.Accepted, "Bye")
            } ?: run {
                Response(HttpStatusCode.Unauthorized, "Client UUID not found")
            }

        } ?: run {
            Response(HttpStatusCode.BadRequest, "Disconnect Failed, client UUID Missing")
        }
    }

    /**
     * user created a lobby
     */
    fun onCreateLobby(lobbySettings: LobbySettings, serverClient: ServerClient): Lobby {
        //TODO only if this client is in no lobby
        val lobbyData = LobbyData(
            UUID.randomUUID().toString(),
            lobbySettings.name,
            serverClient.name,
            null,
            lobbySettings.password != null,
            getDefault()
        )
        val lobby = Lobby(lobbySettings, lobbyData, serverClient, null)

        logger.info("Lobby \"${lobbySettings.name}\" created ${if (lobbySettings.password != null) "with password" else ""}")

        try {
            lobbyList.add(lobby)
            return lobby
        } finally {
            //tell other clients that a new lobby was created
            clients.filter { it.status == UserState.LOBBYLIST }.forEach {
                errorHandlingScope.launch {
                    it.sendLobbyList()
                }
            }
        }
    }

    fun getLobbyListJson(): String {
        //only lobbies with only 1 player (no joined player), no password, only name from users
        return jsonFormat.encodeToJsonElement(lobbyList.filter { it.joinedPlayerUUID == null }.map { it.data }
            .toTypedArray()).toString()
    }

    /**
     * another client joined the queue
     */
    fun onJoinQueue(serverClient: ServerClient) {
        queue.add(0, serverClient)
        println("user joined queue ${queue.size} ${serverClient.name}")

        if (queue.size >= 2) {
            onCreateLobby(Pair(queue.removeLast(), queue.removeLast()))
        }
    }

    /**
     * lobby was created with 2 players
     */
    private fun onCreateLobby(clientPair: Pair<ServerClient, ServerClient>) {
        println("created lobby")
        //create lobby, lobbyleader is random, send users that lobby was created

        val clients = if (Random.nextInt(0, 1) == 0) {
            Pair(clientPair.first, clientPair.second)
        } else {
            Pair(clientPair.second, clientPair.first)
        }

        val lobbyName = "${clients.first.name}'s Lobby"

        val lobbyData = LobbyData(
            UUID.randomUUID().toString(),
            lobbyName,
            clients.first.name,
            clients.second.name,
            false,
            getDefault()
        )

        val lobby = Lobby(LobbySettings(lobbyName, null), lobbyData, clients.first, clients.second)

        lobbyList.add(lobby)

        //send to user
        errorHandlingScope.launch {
            clientPair.first.onCreatedLobby(lobby)
            clientPair.second.joinedLobby(lobby)
        }
    }

    fun onJoinLobby(id: String): Lobby? {
        lobbyList.find { it.data.id == id }?.also {
            if (it.joinedPlayerUUID != null) {
                return null
            }
            return it
        }
        return null
    }

    fun onLobbyReady(lobby: Lobby) {
        errorHandlingScope.launch {
            lobby.joinedPlayerUUID?.also { joinedPlayer ->
                if (lobby.createdPlayerUUID.status == UserState.LOBBY_READY && joinedPlayer.status == UserState.LOBBY_READY) {
                    //launch game, remove lobby
                    lobby.createdPlayerUUID.onStartGame(lobby.data.gameSettings)
                    joinedPlayer.onStartGame(lobby.data.gameSettings)
                    lobbyList.remove(lobby)
                    gameList.add(Game(lobby.createdPlayerUUID, joinedPlayer, lobby.data.gameSettings))
                }
            }
        }
    }

    fun onError(serverClient: ServerClient) {
        removeClient(serverClient)
    }

    fun onDisconnect(serverClient: ServerClient) {
        removeClient(serverClient)
    }

    private fun removeClient(serverClient: ServerClient) {
        lobbyList.find { it.joinedPlayerUUID == serverClient || it.createdPlayerUUID == serverClient }?.apply {

            if (joinedPlayerUUID == serverClient) {
                joinedPlayerUUID = null
                errorHandlingScope.launch {
                    createdPlayerUUID.lobbyUpdate(this@apply)
                }
            } else if (createdPlayerUUID == serverClient) {
                joinedPlayerUUID?.also {
                    createdPlayerUUID = it
                    joinedPlayerUUID = null
                    errorHandlingScope.launch {
                        createdPlayerUUID.lobbyUpdate(this@apply)
                    }
                } ?: run {
                    lobbyList.remove(this@apply)
                }
            }
        }

        queue.remove(serverClient)
        clients.remove(serverClient)
        println("removeClient ${clients.size}")

    }

    fun removeFromQueue(serverClient: ServerClient) {
        queue.remove(serverClient)
    }
}