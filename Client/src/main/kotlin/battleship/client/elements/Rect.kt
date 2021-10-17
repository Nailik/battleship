package battleship.client.elements

import processing.core.PVector
import battleship.client.program.Sketch
import battleship.client.interfaces.IView
import battleship.client.resources.Colors

open class Rect(position: PVector, size: PVector, var color: Colors) : IView(position, null, size) {

    override fun draw(position: PVector) {
        Sketch.noStroke()
        Sketch.fill(color.red(), color.green(), color.blue(), currentAlpha)
        Sketch.rect(position.x, position.y, size.x, size.y)
    }

}