package battleship.server.program

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*

data class Response(val httpStatusCode: HttpStatusCode, val information: Any)

suspend inline fun ApplicationCall.respondInformation(response: Response) {
    return this.respond(response.httpStatusCode, response.information)
}

