package battleship.server.data

import kotlinx.serialization.Serializable

@Serializable
enum class Orientation {
    HORIZONTAL,
    VERTICAL,
}