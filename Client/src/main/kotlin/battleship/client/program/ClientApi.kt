package battleship.client.program

import battleship.server.data.DataPacket
import battleship.server.data.UserState
import battleship.server.program.BroadcastPort
import battleship.server.program.OpenForConnections
import battleship.server.program.jsonFormat
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.observer.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonPrimitive

object ClientApi {

    public var host: String = ""
        private set

    public var port: Int = 0
        private set

    private var client: Client? = null

    private val errorHandler = CoroutineExceptionHandler { _, error -> }
    private val errorHandlingScope = CoroutineScope(errorHandler)

    private val foundServers = mutableListOf<Pair<String, Int>>()

    private val httpClient = HttpClient(CIO) {
        expectSuccess = false
        ResponseObserver { response ->
            println("HTTP status: ${response.status.value}")
        }
        install(WebSockets)
        install(ContentNegotiation) {
            json()
        }
        defaultRequest {
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            contentType(ContentType.Application.Json)
        }
    }

    fun connectToServer(host: String, port: Int, secure: Boolean) {
        errorHandlingScope.launch {
            client?.socket?.close()
            client = null

            this@ClientApi.host = host
            this@ClientApi.port = port

            try {
                if (secure) {
                    httpClient.wss(method = HttpMethod.Get, host = host, port = port, path = "/connect", getRequest()) {
                        handleConnection(this)
                    }
                } else {
                    httpClient.ws(method = HttpMethod.Get, host = host, port = port, path = "/connect", getRequest()) {
                        handleConnection(this)
                    }
                }
            } catch (e: Exception) {
                Logic.onConnectionError(null)
            }
        }
    }

    fun isConnected(): Boolean {
        return client != null
    }

    private fun getRequest(): HttpRequestBuilder.() -> Unit = {
        header("uuid", Logic.userSettings.uuid)
        header("name", Logic.userSettings.userName)
    }

    private suspend fun handleConnection(socket: DefaultClientWebSocketSession) {
        client = Client(socket)

        Logic.userState = UserState.START_SCREEN

        try {
            for (frame in socket.incoming) {
                client?.receive(frame)
            }
        } catch (e: ClosedReceiveChannelException) {
            Logic.onDisconnected(e)
        } catch (e: Error) {
            Logic.onConnectionError(e)
        }
    }

    private var socket: BoundDatagramSocket? = null

    /**
     * search for servers
     */
    fun searchLocalServer(onFoundNewServer: (List<Pair<String, Int>>) -> Unit) {
        foundServers.clear()
        socket = aSocket(ActorSelectorManager(Dispatchers.IO)).udp()
            .bind(InetSocketAddress("127.0.0.1", BroadcastPort)) {
                broadcast = true
                reuseAddress = true
            }

        socket?.openReadChannel()
        errorHandlingScope.launch {
            while (true) {
                socket?.incoming?.receive()?.also {
                    val text = it.packet.readText()
                    val json = Json.decodeFromString<JsonObject>(text)
                    if (json["status"]?.jsonPrimitive?.content == OpenForConnections) {
                        val host = it.address.toString().replace("/", "").split(":").first()
                        val pair = Pair(host, json["port"] as Int)
                        if (!foundServers.contains(pair)) {
                            foundServers.add(pair)
                            withContext(Dispatchers.IO) {
                                onFoundNewServer.invoke(foundServers)
                            }
                        }
                    }
                }
                delay(500)
            }
            //TODO remove server when not found for some time
        }
    }

    fun stopSearchLocalServer() {
        socket?.close()
    }


    fun sendData(dataPacket: DataPacket) {
        errorHandlingScope.launch {
            client?.socket?.send(Frame.Text(jsonFormat.encodeToJsonElement(dataPacket).toString()))
        }
    }

}