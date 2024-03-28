package battleship.server.program

//import mu.KotlinLogging
import battleship.server.webserver.ServerApi
import battleship.server.webserver.ServerSettings

//private val logger = mu.KotlinLogging.logger {}
/**
 * start class of server
 */
fun main() {
    ServerApi.startServer(ServerSettings(-1, -1, false, DefaultGamePort))
}