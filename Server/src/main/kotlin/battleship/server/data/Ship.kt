package battleship.server.data

import kotlinx.serialization.Serializable

@Serializable
data class Ship(var shipType: Int,
                //if no center peace it's one to the left
                var centerPosition: Position,
                var orientation: Orientation) {

    /**
     * returns a list of positions that are used by the ship
     */
    fun getListFields(): List<Position> {
        val list = mutableListOf<Position>()
        val add = if (shipType.mod(2) == 0) 1 else 0

        when (orientation) {
            Orientation.HORIZONTAL -> {
                for (i in (centerPosition.x - shipType / 2 + add)..(centerPosition.x + shipType / 2)) {
                    list.add(Position(i, centerPosition.y))
                }
            }
            Orientation.VERTICAL -> {
                for (i in (centerPosition.y - shipType / 2 + add)..(centerPosition.y + shipType / 2)) {
                    list.add(Position(centerPosition.x, i))
                }
            }
        }

        return list
    }


}


