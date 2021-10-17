package battleship.client.gameviews

import processing.core.PVector
import battleship.client.elements.Image
import battleship.client.program.GameLogic
import battleship.client.interfaces.IViewGroup
import battleship.client.resources.Images
import battleship.server.data.Position
import battleship.server.data.Ship
import battleship.server.data.Orientation
import battleship.server.data.ShootResponse

//initially squared
class GameField(position: PVector, size: Float) : IViewGroup(position, size = PVector(size, size)) {

    var clickedField: ((pos: Position) -> Unit)? = null
    var mouseOverField: ((pos: Position, center: PVector) -> Unit)? = null

    private val fieldSize = if (GameLogic.gameSettings.fieldWidth > GameLogic.gameSettings.fieldHeight) {
        size / GameLogic.gameSettings.fieldWidth
    } else {
        size / GameLogic.gameSettings.fieldHeight
    }

    val fieldMatrix = Array(GameLogic.gameSettings.fieldWidth) {
        Array(GameLogic.gameSettings.fieldHeight) {
            Field(
                PVector(0f, 0f),
                fieldSize
            )
        }
    }

    init {
        //generate fields
        for (x in 0 until GameLogic.gameSettings.fieldWidth) {
            for (y in 0 until GameLogic.gameSettings.fieldHeight) {

                val yP = y * fieldSize
                val xP = x * fieldSize

                fieldMatrix[x][y] = Field(PVector(xP, yP), fieldSize).apply {
                    mouseOver = {
                        if (it) {
                            mouseOverField?.invoke(Position(x, y), PVector(xP + fieldSize / 2f, yP + fieldSize / 2f))
                        }
                    }
                    clickedLeft = {
                        clickedField?.invoke(Position(x, y))
                    }
                }

                addView(fieldMatrix[x][y])
            }
        }
    }

    //create ship view
    fun createShipView(size: Int): ShipView {
        return ShipView(Ship(size, Position(0, 0), Orientation.HORIZONTAL), fieldSize).apply {
            isEnabled = false
        }.also {
            addView(it)
        }
    }

    fun createShipView(ship: Ship): ShipView {
        return ShipView(ship, fieldSize).also {
            addView(it)
        }
    }

    fun showShootResponse(shootResponse: ShootResponse) {
        addView(
            Image(
                fieldMatrix[shootResponse.position.x][shootResponse.position.y].position,
                if (shootResponse.hit) Images.Shot_Hit else Images.Shot_Missed,
                PVector(fieldSize, fieldSize)
            )
        )
    }
}