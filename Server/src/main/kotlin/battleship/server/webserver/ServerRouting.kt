package battleship.server.webserver

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.cio.websocket.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import battleship.server.program.respondInformation
import java.time.Duration

fun Application.configureServer(server: ServerApi) {
    install(ContentNegotiation) {
        json()
    }
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    routing {

        get("/") {
            call.respondText("Hello, world! 1.1")
        }


        /**
         * connect client to server
         */
        webSocket("/connect") {
            println("webSocket called")
            val client = server.connect(this, call.request.headers["uuid"], call.request.headers["name"])?.apply {
                try {
                    for (frame in incoming) {
                        receive(frame)
                    }
                } catch (e: ClosedReceiveChannelException) {
                    // Connection is lost
                    server.onDisconnect(this)
                    println("ClosedReceiveChannelException $e")
                } catch (e: Error) {
                    server.onError(this)
                    println("some error $e")
                }
            }

            println("close")
            client?.also {
                server.onDisconnect(it)
            }
            this.close(CloseReason(CloseReason.Codes.NORMAL, "todo"))
        }


        /**
         * connect client to server
         */
        post("/bye") {
            call.respondInformation(server.disconnect(call.request.headers["uuid"]))
        }
    }
}


