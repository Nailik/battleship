package battleship.client.interfaces

import processing.core.PVector
import battleship.client.program.Sketch
import battleship.client.program.minus

abstract class IDraggable(position: PVector, size: PVector) : IClickable(position, null, size) {

    //TODO save position where drag started and adjust position because mouse can be too fast for fps

    var mouseDragged: ((distance: PVector) -> Unit)? = null

    var enableDrag = false

    override fun update() {
        super.update()

        if(enableDrag && isMouseOver && Sketch.isMouseLeftPressed){
            val distance = Sketch.oldMousePosition - Sketch.mousePosition
            position -= distance / Sketch.scaleFactor
        }
    }

}