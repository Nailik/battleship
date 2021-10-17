package battleship.client.gameviews

import processing.core.PConstants
import processing.core.PVector
import battleship.client.elements.Image
import battleship.client.program.Sketch
import battleship.client.program.plus
import battleship.client.resources.Images
import battleship.server.data.Position
import battleship.server.data.Ship
import battleship.server.data.Orientation

class ShipView(var ship: Ship, private val fieldSize: Float) : Image(
    PVector(0f, 0f), when(ship.shipType){
            2 -> Images.Ship_2_H
            3 -> Images.Ship_3_H
            4 -> Images.Ship_4_H
            5 -> Images.Ship_5_H
            else -> Images.Ship_2_H
        }, PVector((fieldSize - 5) * ship.shipType, fieldSize - 5)
) {

    init {
        drawMode = PConstants.CENTER
        updateOrientation(ship.orientation)
        updateFieldPosition(ship.centerPosition)
    }

    fun updateFieldPosition(newPosition: Position) {
        position = if (ship.shipType.mod(2) == 0) {
            when (ship.orientation) {
                Orientation.HORIZONTAL -> PVector(
                    fieldSize * (newPosition.x + 0.5f) + fieldSize / 2f,
                    fieldSize * newPosition.y + fieldSize / 2f
                )
                Orientation.VERTICAL -> PVector(
                    fieldSize * newPosition.x + fieldSize / 2f,
                    fieldSize * (newPosition.y + 0.5f) + fieldSize / 2f
                )
            }
        } else {
            PVector(fieldSize * newPosition.x + fieldSize / 2f, fieldSize * newPosition.y + fieldSize / 2f)
        }
        ship.centerPosition = newPosition
        //    position = fieldCenterPosition
    }

    fun updateOrientation(orientation: Orientation?) {
        orientation?.also {
            ship.orientation = it
            rotation = when (it) {
                Orientation.HORIZONTAL -> 0f
                Orientation.VERTICAL -> 90f
            }
            updateImage()
            updateFieldPosition(ship.centerPosition)
        }
    }

    fun updateSize(shipLength: Int) {
        ship.shipType = shipLength
        updateImage()
        size = PVector((fieldSize - 5) * ship.shipType, fieldSize - 5)
    }

    private fun updateImage(){
        pImage = when (ship.orientation) {
            Orientation.HORIZONTAL -> when(ship.shipType) {
                2 -> Images.Ship_2_H
                3 -> Images.Ship_3_H
                4 -> Images.Ship_4_H
                5 -> Images.Ship_5_H
                else -> Images.Ship_2_H
            }
            Orientation.VERTICAL -> when(ship.shipType) {
                2 -> Images.Ship_2_V
                3 -> Images.Ship_3_V
                4 -> Images.Ship_4_V
                5 -> Images.Ship_5_V
                else -> Images.Ship_2_V
            }
        }
    }

    override fun checkIsMouseOver(): Boolean {
        val finalPosition = position + offset

        return if (ship.orientation == Orientation.HORIZONTAL) {
            super.checkIsMouseOver()
        }else{
            Sketch.mousePosition.x >= (finalPosition.x - size.y / 2f) * (Sketch.scaleFactor) + 1 &&
                    Sketch.mousePosition.x <= (finalPosition.x + size.y / 2f) * (Sketch.scaleFactor) - 1 &&
                    Sketch.mousePosition.y >= (finalPosition.y - size.x / 2f) * (Sketch.scaleFactor) + 1 &&
                    Sketch.mousePosition.y <= (finalPosition.y + size.x / 2f) * (Sketch.scaleFactor) - 1
        }
    }

}